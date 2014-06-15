
package com.example.pickmeat;

import com.example.pickmeat.DataAccess.DataSource;
import com.example.pickmeat.DataAccess.Setting;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	DataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
        
		DataAccess dataAccess = new DataAccess();
    	datasource = dataAccess.new DataSource(this);
        datasource.open();
        
        hookSaveButtonEvents();
		setSettingValuesFromDatabase();
	}

	private void setSettingValuesFromDatabase() {
		EditText userName = (EditText) findViewById(R.id.editTextUserName);
		userName.setText(datasource.getSetting(Setting.UserName));

		EditText lastPickupLocation = (EditText) findViewById(R.id.editTextLastPickupLocation);
		lastPickupLocation.setText(datasource.getSetting(Setting.LastPickupLocation));
		lastPickupLocation.setVisibility(View.GONE);
		
		EditText userID = (EditText) findViewById(R.id.editTextUserID);
		userID.setText(datasource.getSetting(Setting.UserID));
		userID.setVisibility(View.GONE);
	}

	private void hookSaveButtonEvents() {
		Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		EditText userName = (EditText) findViewById(R.id.editTextUserName);
        		if(userName.getText().toString().equalsIgnoreCase("")){
        			new AlertDialog.Builder(SettingsActivity.this).setTitle("Save Failed")
                    .setMessage("Please provide your user name")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                 dialog.cancel();
                        }
                    }).show();
        			return;
        		}
        		EditText lastPickupLocation = (EditText) findViewById(R.id.editTextLastPickupLocation);
       			EditText userID = (EditText) findViewById(R.id.editTextUserID);
        		if(userID.getText().toString().equalsIgnoreCase("")){
        			new AlertDialog.Builder(SettingsActivity.this).setTitle("Save Failed")
                    .setMessage("Please provide user ID")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                 dialog.cancel();
                        }
                    }).show();
        			return;
        		}

        		datasource.setSetting(Setting.LastPickupLocation, lastPickupLocation.getText().toString());
        		datasource.setSetting(Setting.UserName, userName.getText().toString());
        		datasource.setSetting(Setting.UserID, userID.getText().toString());
           		
        		Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.meal_settings, menu);
		return true;
	}

}