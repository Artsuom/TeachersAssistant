package com.artsuo.teachersassistant.activities;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.artsuo.teachersassistant.backend.Constants;
import com.artsuo.teachersassistant.backend.DataAccessor;
import com.artsuo.teachersassistant.backend.GestureListener;
import com.artsuo.teachersassistant.backend.Utils;
import com.example.teachersassistant.R;

public class ViewActivity extends Activity {
	
	private static final int MIN_FLING_DISTANCE = 100;
	private SharedPreferences preferences;
	private TextView title;
	private ImageView image;
	private Uri selectedImageUri;
	private TextView text;
	private GestureDetector gestureDetector;
	private float y1;
	private float y2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		init();
	}
	
	private void init() {
		this.y1 = 0;
		this.y2 = 0;
		this.selectedImageUri = null;
		this.gestureDetector = new GestureDetector(this, new GestureListener());
		this.preferences = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
		this.title = (TextView)findViewById(R.id.viewPageTitle);
		this.image = (ImageView)findViewById(R.id.viewImage);
		this.text = (TextView)findViewById(R.id.viewPageText);
		if (pageContainsData()) {
			populateFields();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			y1 = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			y2 = event.getY();
			float deltaY = y1 - y2;
			if (Math.abs(deltaY) > MIN_FLING_DISTANCE) {
				if (y2 > y1) {
					prevPage();
				} else {
					nextPage();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	private void nextPage() {
		increasePageNr();
		// Check if Page data exists (with the new PageNO), if so, populate
		if (pageContainsData()) {
			populateFields();
		} else {
			decreasePageNr();
		}
	}
	
	private void prevPage() {
		decreasePageNr();
		// Check if Page data exists (with the new PageNO), if so, populate
		if (pageContainsData()) {
			populateFields();
		} else {
			increasePageNr();
		}
	}
	
	private void populateFields() {
		String[] data = DataAccessor.getInstance().readPage(this, getPresentationFilename(), 
				getCurrentPageNr());
		if (data.length < 2) {
			// Only PageNO found, Page is empty
			// 0: PageNO
			// 1: Title
			title.setText("");
			// 2: ImageUri
			selectedImageUri = null;
			// 3: Text
			text.setText("");
		} else if (data.length < 3) {
			// Only PageNO and Title found
			// 0: PageNO
			// 1: Title
			title.setText(data[1]);
			// 2: ImageUri
			selectedImageUri = null;
			// 3: Text
			text.setText("");
		} else if (data.length < 4) {
			// Only PageNO, Title and ImageURI found
			// 0: PageNO
			// 1: Title
			title.setText(data[1]);
			// 2: ImageUri
			selectedImageUri = Uri.parse(data[2]);
			try {
				image.setImageBitmap(DataAccessor.getInstance().decodeUri(this, selectedImageUri));
			} catch (FileNotFoundException ex) {
				
			}
			// 3: Text
			text.setText("");
		} else {
			// All found, populate all
			// 0: PageNO
			// 1: Title
			title.setText(data[1]);
			// 2: ImageUri
			selectedImageUri = Uri.parse(data[2]);
			try {
				image.setImageBitmap(DataAccessor.getInstance().decodeUri(this, selectedImageUri));
			} catch (FileNotFoundException ex) {
							
			}
			// 3: Text
			text.setText(Utils.singleLineToMultiLine(data[3]));
		}
	}
	/*
	private void clearFields() {
		title.setText("");
		//selectedImage = null;		//	See below: (Option 2: keep the previous image?)
		//addImage.setImageBitmap();	// TO DO: Set to a default image, indicating "Choose image"
		text.setText("");
	}
	*/
	private boolean pageContainsData() {
		if (DataAccessor.getInstance().dataExists(this, getPresentationFilename(), 
				getCurrentPageNr())) {
			return true;
		} 
		return false;
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
	
	private String getPresentationFilename() {
		return preferences.getString(Constants.PREFERENCES_CURRPRES_NAME, "");
	}
	
	private int getCurrentPageNr() {
		return preferences.getInt(Constants.PREFERENCES_CURRPAGE_NR, 1);
	}
}
