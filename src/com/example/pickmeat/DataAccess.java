package com.example.pickmeat;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DataAccess {
	private static final String DATABASE_NAME = "lift_database.db";
	private static final int DATABASE_VERSION = 3;
	
	public class DataHelper extends SQLiteOpenHelper {

	  public static final String TABLE_LIFT = "lift";
	  public static final String LIFT_COLUMN_ID = "_id";
	  public static final String LIFT_COLUMN_TIME = "lift_time";
	  public static final String LIFT_COLUMN_FROM = "lift_from";
	  public static final String LIFT_COLUMN_TO = "lift_to";
	  public static final String LIFT_COLUMN_LIFTEE = "liftee";
	  public static final String LIFT_COLUMN_LIFTOR = "liftor";
	  public static final String LIFT_COLUMN_TYPE = "lift_type";


	  // Database creation sql statement
	  private static final String LIFT_DATABASE_CREATE = "create table " + TABLE_LIFT + "(" 
		  + LIFT_COLUMN_ID + " integer primary key autoincrement, " 
		  + LIFT_COLUMN_TIME + " text not null, "
		  + LIFT_COLUMN_FROM + " text, "
		  + LIFT_COLUMN_TO + " text, "
		  + LIFT_COLUMN_LIFTEE + " text, "
		  + LIFT_COLUMN_LIFTOR + " text, "
		  + LIFT_COLUMN_TYPE + " text"
	      + ");";

	  DataSource source;
		  
	  public DataHelper(Context context, DataSource src) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    source = src;
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(LIFT_DATABASE_CREATE);
		source.fillData(database);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(DataHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    dropTables(db);
	    onCreate(db);
	  }
	  
	  private void dropTables(SQLiteDatabase db)
	  {
		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIFT);
	  }
	} 

public class DataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private DataHelper dbHelper;
	  
	  private String[] liftAllColumns = { 
			  DataHelper.LIFT_COLUMN_ID,
			  DataHelper.LIFT_COLUMN_TIME,
			  DataHelper.LIFT_COLUMN_FROM,
			  DataHelper.LIFT_COLUMN_TO,
			  DataHelper.LIFT_COLUMN_LIFTEE,
			  DataHelper.LIFT_COLUMN_LIFTOR,
			  DataHelper.LIFT_COLUMN_TYPE};
	  
	  public DataSource(Context context) {
	    dbHelper = new DataHelper(context, this);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }
	  public LiftItem dummyItem;
	  
	  public void fillData(SQLiteDatabase databse)
	  {
		  database = databse;
		  fillLift();
	  }
	  	  
	  public void fillLift()
	  {
			  createLiftItem("6:30 PM", "Microsoft", "Miyapur", "Kamal", "", "Free");
			  createLiftItem("6:40 PM", "Microsoft", "Hafeezpet", "Pankaj", "", "Free");
			  createLiftItem("6:50 PM", "Microsoft", "Kondapur", "Manoj", "Picked", "Dutch");
			  createLiftItem("6:60 PM", "Microsoft", "Miyapur", "Anil", "Picked", "Free");
			  createLiftItem("6:10 PM", "Microsoft", "Kondapur", "Mahiram", "", "Free");
			  createLiftItem("7:30 PM", "Microsoft", "Miyapur", "Mukesh", "", "Free");
			  createLiftItem("8:30 PM", "Microsoft", "Miyapur", "Ganpat", "", "Dutch");
	  }
	  
	  public LiftItem createLiftItem(
			  String time,
			  String from,
			  String to,
			  String liftee,
			  String liftor,
			  String type
			  ) {
		  
		    ContentValues values = new ContentValues();
		    values.put(DataHelper.LIFT_COLUMN_TIME, time);
		    values.put(DataHelper.LIFT_COLUMN_FROM, from);
		    values.put(DataHelper.LIFT_COLUMN_TO, to);
		    values.put(DataHelper.LIFT_COLUMN_LIFTEE, liftee);
		    values.put(DataHelper.LIFT_COLUMN_LIFTOR, liftor);
		    values.put(DataHelper.LIFT_COLUMN_TYPE, type);
		    
		    long insertId = database.insert(DataHelper.TABLE_LIFT, null,
		        values);
		    Cursor cursor = database.query(DataHelper.TABLE_LIFT,
		        liftAllColumns, DataHelper.LIFT_COLUMN_ID + " = " + insertId, null,
		        null, null, null);
		    cursor.moveToFirst();
		    LiftItem newLiftItem = cursorToLift(cursor);
		    cursor.close();
		    return newLiftItem;
	  }

	  public void deleteLiftItem(LiftItem liftItem) {
	    long id = liftItem.getId();
	    System.out.println("Lift item deleted with id: " + id);
	    database.delete(DataHelper.TABLE_LIFT, DataHelper.LIFT_COLUMN_ID
	        + " = " + id, null);
	  }

	  public void acceptLiftItem(long lift_id, String liftor) {
		  LiftItem liftItem = getLiftItem(lift_id);
		    System.out.println("Lift item accepted with id: " + liftItem.getId());
		    ContentValues values = new ContentValues();
		    values.put(DataHelper.LIFT_COLUMN_TIME, liftItem.getTime());
		    values.put(DataHelper.LIFT_COLUMN_FROM, liftItem.getFrom());
		    values.put(DataHelper.LIFT_COLUMN_TO, liftItem.getTo());
		    values.put(DataHelper.LIFT_COLUMN_LIFTEE, liftItem.getLiftee());
		    values.put(DataHelper.LIFT_COLUMN_LIFTOR, liftor);
		    values.put(DataHelper.LIFT_COLUMN_TYPE, liftItem.getTime());
		    database.update(DataHelper.TABLE_LIFT, values, DataHelper.LIFT_COLUMN_ID + " = " + liftItem.getId(), null);
	  }

	  public List<LiftItem> getLiftsByCriteria(String selectionCriteria) {
		    List<LiftItem> liftItems = new ArrayList<LiftItem>();

		    try {
			    Cursor cursor = database.query(DataHelper.TABLE_LIFT,
			        liftAllColumns, selectionCriteria, null, null, null, null);
		
			    cursor.moveToFirst();
			    while (!cursor.isAfterLast()) {
			      LiftItem liftItem = cursorToLift(cursor);
			      liftItems.add(liftItem);
			      cursor.moveToNext();
			    }
			    // make sure to close the cursor
			    cursor.close();
			 }
			 catch (Exception ex)
			 {
				 return null;
			 }
		    return liftItems;
		  }

	  public ArrayList<String> getAllLocations() {
		  String[] locationColumns = { 
				  DataHelper.LIFT_COLUMN_TO};
		  ArrayList<String> locations = new ArrayList<String>();
		  
		    try {
			    Cursor cursor = database.query(true, DataHelper.TABLE_LIFT, locationColumns, null, null, null, null, null, null, null);
		
			    cursor.moveToFirst();
			    while (!cursor.isAfterLast()) {
			    	locations.add(cursor.getString(0));
			    	cursor.moveToNext();
			    }
			    // make sure to close the cursor
			    cursor.close();
			 }
			 catch (Exception ex)
			 {
				 locations.add("EMPTY");
				 return null;
			 }
		    return locations;
	  }
	  
	  public List<LiftItem> getAllLiftItems() {
	    List<LiftItem> liftItems = new ArrayList<LiftItem>();

	    try {
		    Cursor cursor = database.query(DataHelper.TABLE_LIFT,
		        liftAllColumns, null, null, null, null, null);
	
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      LiftItem liftItem = cursorToLift(cursor);
		      liftItems.add(liftItem);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		 }
		 catch (Exception ex)
		 {
			 return null;
		 }
	    return liftItems;
	  }
	  

	  public LiftItem getLiftItem(long id) {
		  LiftItem liftItem = null;
		    
		  try {
			    Cursor cursor = database.query(DataHelper.TABLE_LIFT,
			        liftAllColumns, DataHelper.LIFT_COLUMN_ID + " = " + id, null, null, null, null);
		
			    cursor.moveToFirst();
	
				if (!cursor.isAfterLast())
				{
			      liftItem = cursorToLift(cursor); 
			    }
				
			    // make sure to close the cursor
			    cursor.close();    
		  }
		  catch (Exception ex)
		  {
			  return null;
		  }
		  
	    return liftItem;
	  }

	  private LiftItem cursorToLift(Cursor cursor) {
	    LiftItem liftItem = 
	    	new LiftItem(
    			cursor.getLong(0),
    			cursor.getString(1),
    			cursor.getString(2),
    			cursor.getString(3),
    			cursor.getString(4),
    			cursor.getString(5),
    			cursor.getString(6)
    			);

	    return liftItem;
	  }

	  private Cursor execSql(String sql) {
		  Cursor c = null;

		  try {
			  c = database.rawQuery(sql, null);		  
		  }
		  catch(SQLException e) {
			  String x = e.toString();
		  }
		  return c;
	  }
	} 


public class LiftItem {
	  private long _id;
	  private String _time;
	  private String _from;
	  private String _to;
	  private String _liftee;
	  private String _liftor;
	  private String _type;
	  
	  public LiftItem(
		  long id,
		  String time,
		  String from,
		  String to,
		  String liftee,
		  String liftor,
		  String type)
	  {
		 _id = id;
		 _time = time;
		 _from = from;
		 _to = to;
		 _liftee = liftee;
		 _liftor = liftor;
		 _type = type;
	  }
	  
	  public long getId() { return _id; }
	  public String getTime() { return _time; }
	  public String getFrom() { return _from; }
	  public String getTo() { return _to; }
	  public String getLiftee() { return _liftee; }
	  public String getLiftor() { return _liftor; }
	  public String getType() { return _type; }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return _time;
	  }
	} 
}
