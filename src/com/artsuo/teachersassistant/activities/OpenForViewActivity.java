package com.artsuo.teachersassistant.activities;

import com.artsuo.teachersassistant.backend.Constants;
import com.example.teachersassistant.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class OpenForViewActivity extends Activity {

	private String[] savedFiles;
	private ListView listSavedFiles;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_openforview);
		init();
	}
	
	private void init() {
		this.preferences = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
		this.savedFiles = getApplicationContext().fileList();
		for (int i = 0; i < savedFiles.length; i++) {
			savedFiles[i] = savedFiles[i].substring(0, savedFiles[i].length() - 4);
		}
		this.listSavedFiles = (ListView)findViewById(R.id.openForViewList);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, savedFiles);
		listSavedFiles.setAdapter(adapter);
		listSavedFiles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				openPresentationForEdit(savedFiles[position]);
			}
		});
	}
	
	private void openPresentationForEdit(String name) {
		if (name != null) {
			//String[] split = name.split(".");
			if (name != null) {
				if (name.length() > 0) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString(Constants.PREFERENCES_CURRPRES_NAME, name);
					editor.putInt(Constants.PREFERENCES_CURRPAGE_NR, 1);
					editor.commit();
					this.startActivity(new Intent(this, ViewActivity.class));
				}
			}
		} 
		
	}
}
