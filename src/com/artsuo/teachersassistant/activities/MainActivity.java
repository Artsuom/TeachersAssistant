package com.artsuo.teachersassistant.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.teachersassistant.R;

public class MainActivity extends Activity {

	private Button createButton;
	private Button openButton;
	private Button editButton;
	private Intent createIntent;
	private Intent openForViewIntent;
	private Intent openForEditIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void init() {
		createButton = (Button)findViewById(R.id.createButton);
		openButton = (Button)findViewById(R.id.openButton);
		editButton = (Button)findViewById(R.id.editButton);
		createIntent = new Intent(this, CreateActivity.class);
		openForViewIntent = new Intent(this, OpenForViewActivity.class);
		openForEditIntent = new Intent(this, OpenForEditActivity.class);
		
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				startNewActivity(createIntent);
			}
		});
		
		openButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startNewActivity(openForViewIntent);
			}
		});
		
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startNewActivity(openForEditIntent);
			}
		});
	}
	
	private void startNewActivity(Intent intent) {
		this.startActivity(intent);
	}

}
