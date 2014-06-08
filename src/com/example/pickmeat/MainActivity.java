package com.example.pickmeat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.pickmeat.DataAccess;
import com.example.pickmeat.DataAccess.DataHelper;

import android.location.Criteria;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener {

	private DataAccess.DataSource datasource;
	private ArrayList<LocationPool> locations = new ArrayList<LocationPool>(); 
	private LiftListAdapter liftListAdapter;
	private ExpandableListView liftListView;

	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location location;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
   	
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
			new AlertDialog.Builder(MainActivity.this).setTitle("Error")
            .setMessage("Unable to fetch location")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                         dialog.cancel();
                }
            }).show();
			return;
        }
		Log.d("Location","lat "+location.getLatitude());
		Log.d("Location","lat "+location.getLongitude());

        
        locationManager.requestLocationUpdates(provider, 1000, 0, this); 		
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        loadData();
        
		//get reference to the ExpandableListView
		liftListView = (ExpandableListView) findViewById(R.id.LiftExpandableList);

        View header = (View)getLayoutInflater().inflate(R.layout.widget_add_lift_button, null);
        liftListView.addHeaderView(header);
		
		//create the adapter by passing your ArrayList data
		liftListAdapter = new LiftListAdapter(this, locations);
		//attach the adapter to the list
		liftListView.setAdapter(liftListAdapter);
		 
		//expand all Groups
		expandFirstGroup();

	}


	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		//loadData();
		//textView = (TextView) findViewById(R.id.textview1);
		//textView.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.d("MY_TAG","latitude disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.d("MY_TAG","latitude enable");
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.d("MY_TAG","latitude status");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	    	case R.id.action_settings:
	    		startActivity(new Intent(this, SettingsActivity.class));
	    		return true;
	    }
	  return false;
	}
    //method to expand all groups
    private void expandAll() {
      int count = liftListAdapter.getGroupCount();
      for (int i = 0; i < count; i++){
         liftListView.expandGroup(i);
      }
    }
    //method to expand all groups
    private void expandFirstGroup() {
      int count = liftListAdapter.getGroupCount();
      if(count > 0){
         liftListView.expandGroup(0);
      }
    }
    private void loadData(){
    	locations.clear();
    	Calendar onehourback = Calendar.getInstance();
    	onehourback.add(Calendar.MINUTE, -30);
    	Calendar twelvehourlater = Calendar.getInstance();
    	twelvehourlater.add(Calendar.HOUR, 12);
        List<DataAccess.LiftItem> liftitems = datasource.getLiftsByCriteria(DataHelper.LIFT_COLUMN_LIFTOR + " = '' AND "
			+DataHelper.LIFT_COLUMN_FROM_LAT+" > "+(location.getLatitude()-0.01)+" AND "
			+DataHelper.LIFT_COLUMN_FROM_LAT+" < "+(location.getLatitude()+0.01)+" AND "
			+DataHelper.LIFT_COLUMN_FROM_LONG+" > "+(location.getLongitude()-0.01)+" AND "
			+DataHelper.LIFT_COLUMN_FROM_LONG+" < "+(location.getLongitude()+0.01)+" AND "
      		+DataHelper.LIFT_COLUMN_TIME+ " > '"+DataAccess.getStringFromDate(onehourback)+"' AND "
        		+DataHelper.LIFT_COLUMN_TIME+ " < '"+DataAccess.getStringFromDate(twelvehourlater)+"'");
    	for(int i=0;i<liftitems.size();i++){
    		DataAccess.LiftItem liftItem = liftitems.get(i);
    		Lift lift = CreateLift(liftItem);
    		addLiftToLocation(lift);
    	}	
    }
    
    private void addLiftToLocation(Lift lift){
    	for(int i=0;i<locations.size();i++){
    		if (locations.get(i).location.equalsIgnoreCase(lift.to)){
    			locations.get(i).liftList.add(lift);
    			return;
    		}
    	}
    	ArrayList<Lift> lifts = new ArrayList<Lift>();
    	lifts.add(lift);
    	locations.add(new LocationPool(lift.to, lifts));
    }

	private Lift CreateLift(DataAccess.LiftItem lift) {
		return new Lift(lift.getId(),
					lift.getTime(), 
					lift.getFromLat(), 
					lift.getFromLong(), 
					lift.getTo(), 
					lift.getLiftee(), 
					lift.getLiftor(),
					lift.getType());
	} 
	
    /** Called when the user touches the button */
    public void addRequest(View view) {
	   	Intent intent = new Intent(MainActivity.this, NewLiftRequest.class);
    	startActivity(intent);
	}

    public void acceptLift(View view) {
    	int[] tag_array = (int [])view.getTag();
	   	int groupPosition = tag_array[0];
	   	int childPosition = tag_array[1];

	   	long lift_id_to_accept = liftListAdapter.locations.get(groupPosition).liftList.get(childPosition).lift_id;
	   	DataAccess.LiftItem liftitem_to_be_accepted = datasource.getLiftItem(lift_id_to_accept);  
	   	datasource.acceptLiftItem(lift_id_to_accept, "Kamal");
    	liftListAdapter.locations.get(groupPosition).liftList.remove(childPosition);
    	if (liftListAdapter.locations.get(groupPosition).liftList.size()==0){
    		liftListAdapter.locations.remove(groupPosition);
    	}
    	liftListAdapter.notifyDataSetInvalidated();
	}

	
    @Override
    protected void onResume() {
      datasource.open();
      super.onResume();
    }

    @Override
    protected void onPause() {
      datasource.close();
      super.onPause();
    }

}
