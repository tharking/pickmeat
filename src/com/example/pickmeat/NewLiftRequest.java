
package com.example.pickmeat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.pickmeat.DataAccess.DataSource;
import com.example.pickmeat.DataAccess.Setting;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class NewLiftRequest extends Activity {

	DataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_lift_request);
		
		DataAccess dataAccess = new DataAccess();
    	datasource = dataAccess.new DataSource(this);
        datasource.open();
        
        ArrayList<String> locations = datasource.getAllLocations();
        String[] data = locations.toArray(new String[locations.size()]);
    	ArrayAdapter<?> locationAdapter = new ArrayAdapter<Object>(this, android.R.layout.simple_dropdown_item_1line, data);
		AutoCompleteTextView edtTitle = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        edtTitle.setAdapter(locationAdapter);
        edtTitle.setThreshold(1);

		TimePicker myTimePicker = (TimePicker) findViewById(R.id.editTextTimePicker);
	    myTimePicker.setIs24HourView(true);
//	    myTimePicker.setCurrentHour(currentHour);
//	    myTimePicker.setCurrentMinute(currentHour);
        hookSaveButtonEvents();
	}


	private void hookSaveButtonEvents() {
		Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		AutoCompleteTextView to = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        		TimePicker myTimePicker = (TimePicker) findViewById(R.id.editTextTimePicker);
        		Calendar rightNow = Calendar.getInstance();
        		Calendar newTime = Calendar.getInstance();
        		newTime.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DATE), myTimePicker.getCurrentHour(), myTimePicker.getCurrentMinute());
        		if(newTime.compareTo(rightNow) < 1){
        			rightNow.add(Calendar.DAY_OF_MONTH, 1);
        			newTime.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DATE), myTimePicker.getCurrentHour(), myTimePicker.getCurrentMinute());
        		}
        		if(to.getText().toString().equalsIgnoreCase("")){
        			new AlertDialog.Builder(NewLiftRequest.this).setTitle("Unable to save request")
                    .setMessage("Please enter where you want to go")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                 dialog.cancel();
                        }
                    }).show();
        		}
        		else {
	        		datasource.createLiftItem(newTime, datasource.getSetting(Setting.UserLocation), to.getText().toString(), datasource.getSetting(Setting.UserName), "", "Free");
	        		Intent intent = new Intent(NewLiftRequest.this, MainActivity.class);
		        	startActivity(intent);
        		}
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