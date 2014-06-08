
package com.example.pickmeat;

import java.util.ArrayList;

import com.example.pickmeat.DataAccess.DataSource;

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

		EditText from = (EditText) findViewById(R.id.editTextFrom);
		from.setText("Microsoft");
		EditText liftee = (EditText) findViewById(R.id.editTextLiftee);
		liftee.setText("Kamal");

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
        		EditText from = (EditText) findViewById(R.id.editTextFrom);
        		AutoCompleteTextView to = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        		EditText liftee = (EditText) findViewById(R.id.editTextLiftee);
        		TimePicker myTimePicker = (TimePicker) findViewById(R.id.editTextTimePicker);
        		String time = myTimePicker.getCurrentHour().toString()+":"+myTimePicker.getCurrentMinute().toString();        
        		if(from.getText().toString().equalsIgnoreCase("")){
        			new AlertDialog.Builder(NewLiftRequest.this).setTitle("Unable to save request")
                    .setMessage("Please provide pick up from location")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                 dialog.cancel();
                        }
                    }).show();
        		}
        		else if(to.getText().toString().equalsIgnoreCase("")){
        			new AlertDialog.Builder(NewLiftRequest.this).setTitle("Unable to save request")
                    .setMessage("Please enter where you want to go")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                 dialog.cancel();
                        }
                    }).show();
        		}
        		else if(liftee.getText().toString().equalsIgnoreCase("")){
        			new AlertDialog.Builder(NewLiftRequest.this).setTitle("Unable to save request")
                    .setMessage("Please provide your name")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                 dialog.cancel();
                        }
                    }).show();
        			return;
        		}
        		else {
	        		datasource.createLiftItem(time, from.getText().toString(), to.getText().toString(), liftee.getText().toString(), "", "Free");
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