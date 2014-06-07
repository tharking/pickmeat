
package com.example.pickmeat;

import com.example.pickmeat.DataAccess.DataSource;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class NewLiftRequest extends Activity {

	DataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_lift_request);
		
		DataAccess dataAccess = new DataAccess();
    	datasource = dataAccess.new DataSource(this);
        datasource.open();
        
        hookSaveButtonEvents();
	}


	private void hookSaveButtonEvents() {
		Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		EditText from = (EditText) findViewById(R.id.editTextFrom);
        		EditText to = (EditText) findViewById(R.id.editTextTo);
        		EditText liftee = (EditText) findViewById(R.id.editTextLiftee);
        		EditText time = (EditText) findViewById(R.id.editTextTime);
        		datasource.createLiftItem(time.getText().toString(), from.getText().toString(), to.getText().toString(), liftee.getText().toString(), "", "Free");
        		
        		Intent intent = new Intent(NewLiftRequest.this, MainActivity.class);
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