package com.example.pickmeat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.pickmeat.DataAccess;
import com.example.pickmeat.DataAccess.DataHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private DataAccess.DataSource datasource;
	private ArrayList<Location> locations = new ArrayList<Location>(); 
	private LiftListAdapter liftListAdapter;
	private ExpandableListView liftListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
   	
		DataAccess dataAccess = new DataAccess();
       	
        datasource = dataAccess.new DataSource(this);
        datasource.open(); 

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
        		+DataHelper.LIFT_COLUMN_FROM+" = '"+datasource.getSetting(DataAccess.Setting.UserLocation)+"' AND "
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
    	locations.add(new Location(lift.to, lifts));
    }

	private Lift CreateLift(DataAccess.LiftItem lift) {
		return new Lift(lift.getId(),
					lift.getTime(), 
					lift.getFrom(), 
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
