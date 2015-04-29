package com.artsuo.teachersassistant.activities;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.artsuo.teachersassistant.backend.Constants;
import com.artsuo.teachersassistant.backend.DataAccessor;
import com.artsuo.teachersassistant.backend.Utils;
import com.example.teachersassistant.R;

public class EditActivity extends Activity {
	
	private static final int SELECT_IMAGE = 100;
	private EditText pageTitle;
	private ImageView addImage;
	private EditText pageText;
	private Button doneButton;
	private Button nextButton;
	private Button prevButton;
	private Button deleteButton;
	private SharedPreferences preferences;
	private Uri selectedImageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		init();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_edit, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_delete_presentation:
			deletePresentation();
			break;
		}
		return true;
	}
	
	private void init() {
		// TO DO: Add functionality to buttons, connect with SharedPreferences and DataAccessor
		this.preferences = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
		this.pageTitle = (EditText)findViewById(R.id.editPageTitle);
		this.addImage = (ImageView)findViewById(R.id.addImage);
		
		this.pageText = (EditText)findViewById(R.id.editPageText);
		this.doneButton = (Button)findViewById(R.id.doneButton);
		this.nextButton = (Button)findViewById(R.id.nextPageButton);
		this.prevButton = (Button)findViewById(R.id.prevPageButton);
		this.deleteButton = (Button)findViewById(R.id.deletePageButton);
		this.selectedImageUri = null;
		
		addImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseImage();
			}
		});
		
		doneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				done();
			}
		});
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextPage();
			}
		});
		prevButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				prevPage();
			}
		});
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deletePage();
			}
		});
		if (pageContainsData()) {
			populateFields();
		}
	}
	
	private String getPresentationFilename() {
		return preferences.getString(Constants.PREFERENCES_CURRPRES_NAME, "");
	}
	
	private int getCurrentPageNr() {
		return preferences.getInt(Constants.PREFERENCES_CURRPAGE_NR, 1);
	}
	
	private void chooseImage() {
		Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
		imagePickerIntent.setType("image/*");
		startActivityForResult(imagePickerIntent, SELECT_IMAGE);
	}
	
	private String getPageDataString() {
		return "Page" + getCurrentPageNr() + 
				Constants.FILE_FIELD_SEPARATOR + pageTitle.getText().toString() + 
				Constants.FILE_FIELD_SEPARATOR + getImageUriString() + 
				Constants.FILE_FIELD_SEPARATOR + Utils.multiLineToSingleLine(pageText.getText().toString());
	}
	
	private String getImageUriString() {
		String returnString = "";
		if (selectedImageUri != null) {
			returnString += selectedImageUri.toString();
		}
		return returnString;
	}
	
	private void populateFields() {
		String[] data = DataAccessor.getInstance().readPage(this, getPresentationFilename(), 
				getCurrentPageNr());
		if (data.length < 2) {
			// Only PageNO found, Page is empty
			// 0: PageNO
			// 1: Title
			pageTitle.setText("");
			// 2: ImageUri
			selectedImageUri = null;
			// 3: Text
			pageText.setText("");
		} else if (data.length < 3) {
			// Only PageNO and Title found
			// 0: PageNO
			// 1: Title
			pageTitle.setText(data[1]);
			// 2: ImageUri
			selectedImageUri = null;
			// 3: Text
			pageText.setText("");
		} else if (data.length < 4) {
			// Only PageNO, Title and ImageURI found
			// 0: PageNO
			// 1: Title
			pageTitle.setText(data[1]);
			// 2: ImageUri
			selectedImageUri = Uri.parse(data[2]);
			try {
				addImage.setImageBitmap(DataAccessor.getInstance().decodeUri(this, selectedImageUri));
			} catch (FileNotFoundException ex) {
				
			}
			// 3: Text
			pageText.setText("");
		} else {
			// All found, populate all
			// 0: PageNO
			// 1: Title
			pageTitle.setText(data[1]);
			// 2: ImageUri
			selectedImageUri = Uri.parse(data[2]);
			try {
				addImage.setImageBitmap(DataAccessor.getInstance().decodeUri(this, selectedImageUri));
			} catch (FileNotFoundException ex) {
							
			}
			// 3: Text
			pageText.setText(Utils.singleLineToMultiLine(data[3]));
		}
	}
	
	private void clearFields() {
		pageTitle.setText("");
		selectedImageUri = null;		//	See below: (Option 2: keep the previous image?)
		//addImage.setImageBitmap();	// TO DO: Set to a default image, indicating "Choose image"
		addImage.setImageResource(R.drawable.choose_image);
		pageText.setText("");
	}
	
	private void done() {
		saveCurrentPageData();
		this.startActivity(new Intent(this, MainActivity.class));
	}
	
	private void nextPage() {
		saveCurrentPageData();
		increasePageNr();
		// Check if Page data exists (with the new PageNO), if so, populate, if not, clear fields
		if (pageContainsData()) {
			populateFields();
		} else {
			clearFields();
		}
	}
	
	private void prevPage() {
		saveCurrentPageData();
		decreasePageNr();
		// Check if Page data exists (with the new PageNO), if so, populate, if not, clear fields
		if (pageContainsData()) {
			populateFields();
		} else {
			clearFields();
		}
	}
	
	//If current page is saved (data exists), delete data, move to prev (if exists), 
	//if not, go to menu (MainActivity)
	private void deletePage() {
		if (pageContainsData()) {
			DataAccessor.getInstance().deletePage(this, getPresentationFilename(), getCurrentPageNr());
			clearFields();
			if (getCurrentPageNr() > 1) {
				decreasePageNr();
			} else {
				increasePageNr();
			}
			if (pageContainsData()) {
				populateFields();
			} else {
				this.startActivity(new Intent(this, MainActivity.class));
			}
		} else {
			clearFields();
		}
	}
	
	private void deletePresentation() {
		//DataAccessor.getInstance().deletePresentation(this, getPresentationFilename());
		clearFields();
		// return to main menu
		if (DataAccessor.getInstance().deletePresentation(this, getPresentationFilename())) {
			Toast.makeText(this, R.string.toast_presentation_deleted, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "File not deleted", Toast.LENGTH_LONG).show();
		}
		this.startActivity(new Intent(this, MainActivity.class));
	}
	
	// Save current page data
	private void saveCurrentPageData() {
		//if (!pageTitle.getText().toString().isEmpty() && !pageText.getText().toString().isEmpty()) {
			if (pageContainsData()) {
				DataAccessor.getInstance().replaceExistingPageData(this, getPresentationFilename(),
						getCurrentPageNr(), getPageDataString());
			} else {
				DataAccessor.getInstance().savePage(this, getPresentationFilename(), getPageDataString());
			}
		//} else {
			//Toast.makeText(EditActivity.this, "Please enter a Title and Description", Toast.LENGTH_SHORT).show();
		//}
	}
	
	private boolean pageContainsData() {
		if (DataAccessor.getInstance().dataExists(this, getPresentationFilename(), 
				getCurrentPageNr())) {
			return true;
		} 
		return false;
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case SELECT_IMAGE:
	        if(resultCode == RESULT_OK){ 
	        	try {
	        		selectedImageUri = imageReturnedIntent.getData();
		            addImage.setImageBitmap(DataAccessor.getInstance().decodeUri(this, selectedImageUri));
	        	} catch (FileNotFoundException ex) {
	        	}
	        }
	    }
	}
	
	private void increasePageNr() {
		SharedPreferences.Editor editor = preferences.edit();
		int currentPageNr = getCurrentPageNr();
		editor.putInt(Constants.PREFERENCES_CURRPAGE_NR, currentPageNr + 1);
		editor.commit();
	}
	
	private void decreasePageNr() {
		int currentPageNr = getCurrentPageNr();
		if (currentPageNr > 1) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt(Constants.PREFERENCES_CURRPAGE_NR, currentPageNr - 1);
			editor.commit();
		}
	}
}
