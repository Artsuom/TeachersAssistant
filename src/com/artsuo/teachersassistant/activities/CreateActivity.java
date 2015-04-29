package com.artsuo.teachersassistant.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.artsuo.teachersassistant.backend.Constants;
import com.example.teachersassistant.R;

public class CreateActivity extends Activity {
	
	private EditText presentationName;
	private Button doneButton;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		init();
	}
	
	private void init() {
		this.preferences = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
		this.presentationName = (EditText)findViewById(R.id.presentationName);
		this.doneButton = (Button)findViewById(R.id.createPresentationButton);
		
		doneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (presentationName.getText().length() > 0) {
					createPresentation();
				}
			}
		});
	}
	
	private void createPresentation() {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(Constants.PREFERENCES_CURRPRES_NAME, presentationName.getText().toString());
		editor.putInt(Constants.PREFERENCES_CURRPAGE_NR, 1);
		editor.commit();
		this.startActivity(new Intent(this, EditActivity.class));
	}
}
