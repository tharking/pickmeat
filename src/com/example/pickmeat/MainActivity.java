package com.example.pickmeat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import com.example.pickmeat.DataAccessApp42;
import com.example.pickmeat.DataAccess.DataSource;
import com.example.pickmeat.DataAccess.Setting;
import com.example.pickmeat.DataAccessApp42.DataHelperApp42;
import com.example.pickmeat.DataAccessApp42.DataSourceApp42;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;

import android.location.Criteria;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener {

	private DataAccessApp42.DataSourceApp42 datasourceapp42;
	DataSource datasource;
	private ArrayList<LocationPool> pendinglocations = new ArrayList<LocationPool>(); 
	private ArrayList<LocationPool> yourlocations = new ArrayList<LocationPool>(); 
	private ArrayList<LocationPool> acceptedlocations = new ArrayList<LocationPool>(); 
	private LiftListAdapter pendingLiftListAdapter, yourLiftListAdapter, acceptedLiftListAdapter;
	private ExpandableListView pendingLiftListView, yourLiftListView, acceptedLiftListView;
	private LinearLayout pendingHeader, yourHeader, acceptedHeader, progressBar;

	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location location;
	private Context context;
	
	public static void initiateDataSource(final DataSourceApp42 datasourceapp42){
		Runnable runnable = new Runnable() {
	        public void run() {     	
	            datasourceapp42.open(); 	       	
	        }
		};
      Thread mythread = new Thread(runnable);
      mythread.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DataAccess dataAccess = new DataAccess();
    	datasource = dataAccess.new DataSource(this);
        datasource.open();

		DataAccessApp42 dataAccessApp42 = new DataAccessApp42();
        datasourceapp42 = dataAccessApp42.new DataSourceApp42(this);
		initiateDataSource(datasourceapp42);
   
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
        else {
        	Log.d("Location","lat "+location.getLatitude());
        	Log.d("Location","lat "+location.getLongitude());
        }

        
        locationManager.requestLocationUpdates(provider, 1000, 0, this); 		
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        yourHeader = (LinearLayout) findViewById(R.id.yourrequestsheader);
        yourLiftListView = (ExpandableListView) findViewById(R.id.YourLiftExpandableList);
		yourLiftListAdapter = new LiftListAdapter(this, yourlocations, LiftListAdapter.CANCEL_MODE);
		yourLiftListView.setAdapter(yourLiftListAdapter);

		acceptedHeader = (LinearLayout) findViewById(R.id.acceptedrequestsheader);
		acceptedLiftListView = (ExpandableListView) findViewById(R.id.AcceptedLiftExpandableList);
		acceptedLiftListAdapter = new LiftListAdapter(this, acceptedlocations, LiftListAdapter.DENY_MODE);
		acceptedLiftListView.setAdapter(acceptedLiftListAdapter);

		pendingHeader = (LinearLayout) findViewById(R.id.pendingrequestsheader);
		pendingLiftListView = (ExpandableListView) findViewById(R.id.PendingLiftExpandableList);
		pendingLiftListAdapter = new LiftListAdapter(this, pendinglocations, LiftListAdapter.ACCEPT_MODE);
		pendingLiftListView.setAdapter(pendingLiftListAdapter);

        loadData();

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
//      int count = liftListAdapter.getGroupCount();
//      for (int i = 0; i < count; i++){
//         liftListView.expandGroup(i);
//      }
    }
    //method to expand all groups
    private void expandFirstGroup() {
//      int count = liftListAdapter.getGroupCount();
//      if(count > 0){
//         liftListView.expandGroup(0);
//      }
    }
    private void loadData(){
    	final MainActivity callback = this;
    	final Handler callingThreadHandler = new Handler();
    	progressBar.setVisibility(View.VISIBLE);
    	new Thread() {
			@Override
			public void run() {
				try {
			    	Calendar onehourback = Calendar.getInstance();
			    	onehourback.add(Calendar.MINUTE, -30);
			    	Calendar twelvehourlater = Calendar.getInstance();
			    	twelvehourlater.add(Calendar.HOUR, 12);
			    	Query q0 = QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_LIFTEE_ID, datasource.getSetting(Setting.UserID), Operator.NOT_EQUALS);   
			    	Query q1 = QueryBuilder.compoundOperator(q0, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_LIFTOR, "", Operator.EQUALS));   
			    	Query q2 = QueryBuilder.compoundOperator(q1, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_FROM_LAT, (location.getLatitude()-0.01), Operator.GREATER_THAN));   
			    	Query q3 = QueryBuilder.compoundOperator(q2, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_FROM_LAT, (location.getLatitude()+0.01), Operator.LESS_THAN));   
			    	Query q4 = QueryBuilder.compoundOperator(q3, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_FROM_LONG, (location.getLongitude()-0.01), Operator.GREATER_THAN));   
			    	Query q5 = QueryBuilder.compoundOperator(q4, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_FROM_LONG, (location.getLongitude()+0.01), Operator.LESS_THAN));
			    	Query q6 = QueryBuilder.compoundOperator(q5, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_TIME, DataAccessApp42.getStringFromDate(onehourback), Operator.GREATER_THAN));
			    	Query q7 = QueryBuilder.compoundOperator(q6, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_TIME, DataAccessApp42.getStringFromDate(twelvehourlater), Operator.LESS_THAN));
					final List<DataAccessApp42.LiftItem> pendingliftitems = datasourceapp42.getLiftsByCriteria(q7);

			    	Query q8 = QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_LIFTEE_ID, datasource.getSetting(Setting.UserID), Operator.EQUALS);   
			    	q1 = QueryBuilder.compoundOperator(q8, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_TIME, DataAccessApp42.getStringFromDate(onehourback), Operator.GREATER_THAN));
					final List<DataAccessApp42.LiftItem> yourliftitems = datasourceapp42.getLiftsByCriteria(q1);
					
			    	Query q9 = QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_LIFTOR_ID, datasource.getSetting(Setting.UserID), Operator.EQUALS);   
			    	q1 = QueryBuilder.compoundOperator(q9, Operator.AND, QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_TIME, DataAccessApp42.getStringFromDate(onehourback), Operator.GREATER_THAN));
					final List<DataAccessApp42.LiftItem> acceptedliftitems = datasourceapp42.getLiftsByCriteria(q1);
					
					callingThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.onLoadData(pendingliftitems, yourliftitems, acceptedliftitems);
						}
					});
				} catch (final Exception ex) {
					//
					ex.printStackTrace();
				}
			}
		}.start();
    	
    }
    
    private void onLoadData(List<DataAccessApp42.LiftItem> pendingliftitems,
    		List<DataAccessApp42.LiftItem> yourliftitems,
    		List<DataAccessApp42.LiftItem> acceptedliftitems
    		){
		yourlocations.clear();
    	for(int i=0;i<yourliftitems.size();i++){
    		DataAccessApp42.LiftItem liftItem = yourliftitems.get(i);
    		Lift lift = CreateLift(liftItem);
    		addLiftToLocation(lift, yourlocations);
    	}	
    	yourLiftListAdapter.notifyDataSetInvalidated();
    	if (yourlocations.size() > 0){
    		yourHeader.setVisibility(View.VISIBLE);
    	} else {
    		yourHeader.setVisibility(View.GONE);
    	}
		acceptedlocations.clear();
    	for(int i=0;i<acceptedliftitems.size();i++){
    		DataAccessApp42.LiftItem liftItem = acceptedliftitems.get(i);
    		Lift lift = CreateLift(liftItem);
    		addLiftToLocation(lift, acceptedlocations);
    	}	
    	acceptedLiftListAdapter.notifyDataSetInvalidated();
    	if (acceptedlocations.size() > 0){
    		acceptedHeader.setVisibility(View.VISIBLE);
    	} else {
    		acceptedHeader.setVisibility(View.GONE);
    	}
		pendinglocations.clear();
    	for(int i=0;i<pendingliftitems.size();i++){
    		DataAccessApp42.LiftItem liftItem = pendingliftitems.get(i);
    		Lift lift = CreateLift(liftItem);
    		addLiftToLocation(lift, pendinglocations);
    	}	
    	pendingLiftListAdapter.notifyDataSetInvalidated();
    	if (pendinglocations.size() > 0){
    		pendingHeader.setVisibility(View.VISIBLE);
    	} else {
    		pendingHeader.setVisibility(View.GONE);
    	}
    	progressBar.setVisibility(View.GONE);
    }
    
    private void addLiftToLocation(Lift lift, ArrayList<LocationPool> locations){
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

	private Lift CreateLift(DataAccessApp42.LiftItem lift) {
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
    	final MainActivity callback = this;
    	final Handler callingThreadHandler = new Handler();
		final int[] tag_array = (int [])view.getTag();
    	progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				try {
				   	final int groupPosition = tag_array[0];
				   	final int childPosition = tag_array[1];

				   	String lift_id_to_accept = pendingLiftListAdapter.locations.get(groupPosition).liftList.get(childPosition).lift_id;
				   	try {
						datasourceapp42.acceptLiftItem(lift_id_to_accept, datasource.getSetting(Setting.UserName), datasource.getSetting(Setting.UserID));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					callingThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.loadData();
						}
					});

				} catch (final Exception ex) {
					//
					ex.printStackTrace();
				}
			}
		}.start();
    	
    }

    public void cancelLift(View view) {
    	final MainActivity callback = this;
    	final Handler callingThreadHandler = new Handler();
		final int[] tag_array = (int [])view.getTag();
    	progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				try {
				   	final int groupPosition = tag_array[0];
				   	final int childPosition = tag_array[1];

				   	String lift_id_to_cancel = yourLiftListAdapter.locations.get(groupPosition).liftList.get(childPosition).lift_id;
					datasourceapp42.deleteLiftItem(lift_id_to_cancel);
					callingThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.loadData();
						}
					});

				} catch (final Exception ex) {
					//
					ex.printStackTrace();
				}
			}
		}.start();
    	
    }
	
    public void denyLift(View view) {
    	final MainActivity callback = this;
    	final Handler callingThreadHandler = new Handler();
		final int[] tag_array = (int [])view.getTag();
    	progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				try {
				   	final int groupPosition = tag_array[0];
				   	final int childPosition = tag_array[1];

				   	String lift_id_to_deny = acceptedLiftListAdapter.locations.get(groupPosition).liftList.get(childPosition).lift_id;
				   	try {
						datasourceapp42.denyLiftItem(lift_id_to_deny);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					callingThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callback.loadData();
						}
					});

				} catch (final Exception ex) {
					//
					ex.printStackTrace();
				}
			}
		}.start();
    	
    }

    @Override
    protected void onResume() {
//      datasource.open();
      super.onResume();
    }

    @Override
    protected void onPause() {
//      datasource.close();
      super.onPause();
    }

}
