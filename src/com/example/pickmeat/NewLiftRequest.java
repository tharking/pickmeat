
package com.example.pickmeat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.pickmeat.DataAccess.DataSource;
import com.example.pickmeat.DataAccess.Setting;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class NewLiftRequest extends Activity  implements LocationListener {

	DataSource datasource;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location location;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_lift_request);
		
		DataAccess dataAccess = new DataAccess();
    	datasource = dataAccess.new DataSource(this);
        datasource.open();

        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        Criteria crta = new Criteria(); 
        crta.setAccuracy(Criteria.ACCURACY_FINE); 
        crta.setAltitudeRequired(false); 
        crta.setBearingRequired(false); 
        crta.setCostAllowed(true); 
        crta.setPowerRequirement(Criteria.POWER_LOW); 
        String provider = locationManager.getBestProvider(crta, true); 

        // String provider = LocationManager.GPS_PROVIDER; 
        location = locationManager.getLastKnownLocation(provider);
        if (location==null){
        	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location==null){
        	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location==null){
        	Log.d("Location","is null");
			new AlertDialog.Builder(NewLiftRequest.this).setTitle("Error")
            .setMessage("Unable to fetch location")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                         dialog.cancel();
                }
            }).show();
			return;
        }
        locationManager.requestLocationUpdates(provider, 1000, 0, this); 		
        
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
	        		datasource.createLiftItem(newTime, location.getLatitude(), location.getLongitude(), to.getText().toString(), datasource.getSetting(Setting.UserName), "", "Free");
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


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}