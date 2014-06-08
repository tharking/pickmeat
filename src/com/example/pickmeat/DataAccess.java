package com.example.pickmeat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
	private static final int DATABASE_VERSION = 12;

	  public enum Setting {
		  UserName,
		  UserID,
		  LastPickupLocation
	  }

	public static String getStringFromDate(Calendar in_time){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(in_time.getTime());
	  }
	  
	public static String getHourMinuteStringFromDate(Calendar in_time){
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		return dateFormat.format(in_time.getTime());
	  }

	public static Calendar getCalendarFromString(String in_time){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = dateFormat.parse(in_time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	  }
	
	public class DataHelper extends SQLiteOpenHelper {

	  public static final String TABLE_LIFT = "lift";
	  public static final String LIFT_COLUMN_ID = "_id";
	  public static final String LIFT_COLUMN_TIME = "lift_time";
	  public static final String LIFT_COLUMN_FROM_LAT = "lift_from_lat";
	  public static final String LIFT_COLUMN_FROM_LONG = "lift_from_long";
	  public static final String LIFT_COLUMN_TO = "lift_to";
	  public static final String LIFT_COLUMN_LIFTEE = "liftee";
	  public static final String LIFT_COLUMN_LIFTOR = "liftor";
	  public static final String LIFT_COLUMN_TYPE = "lift_type";


	  // Database creation sql statement
	  private static final String LIFT_DATABASE_CREATE = "create table " + TABLE_LIFT + "(" 
		  + LIFT_COLUMN_ID + " integer primary key autoincrement, " 
		  + LIFT_COLUMN_TIME + " timestamp not null DEFAULT current_timestamp, "
		  + LIFT_COLUMN_FROM_LAT + " double, "
		  + LIFT_COLUMN_FROM_LONG + " double, "
		  + LIFT_COLUMN_TO + " text, "
		  + LIFT_COLUMN_LIFTEE + " text, "
		  + LIFT_COLUMN_LIFTOR + " text, "
		  + LIFT_COLUMN_TYPE + " text"
	      + ");";

	  public static final String TABLE_SETTINGS = "settings";
	  public static final String SETTINGS_COLUMN_ID = "id";
	  public static final String SETTINGS_COLUMN_TYPE = "type";
	  public static final String SETTINGS_COLUMN_VALUE = "value";
	  
	  // Database creation sql statement
	  private static final String SETTINGS_DATABASE_CREATE = "create table " + TABLE_SETTINGS + "("
		  + SETTINGS_COLUMN_ID + " integer primary key autoincrement, "
		  + SETTINGS_COLUMN_TYPE + " integer, "
		  + SETTINGS_COLUMN_VALUE + " text"
	      + ");";

	  
	  DataSource source;
		  
	  public DataHelper(Context context, DataSource src) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    source = src;
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(LIFT_DATABASE_CREATE);
		database.execSQL(SETTINGS_DATABASE_CREATE);
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
		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
	  }
	  
	} 

public class DataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private DataHelper dbHelper;
	  
	  private String[] liftAllColumns = { 
			  DataHelper.LIFT_COLUMN_ID,
			  DataHelper.LIFT_COLUMN_TIME,
			  DataHelper.LIFT_COLUMN_FROM_LAT,
			  DataHelper.LIFT_COLUMN_FROM_LONG,
			  DataHelper.LIFT_COLUMN_TO,
			  DataHelper.LIFT_COLUMN_LIFTEE,
			  DataHelper.LIFT_COLUMN_LIFTOR,
			  DataHelper.LIFT_COLUMN_TYPE};

	  private String[] settingsAllColumns = { 
			  DataHelper.SETTINGS_COLUMN_ID,
			  DataHelper.SETTINGS_COLUMN_TYPE,
			  DataHelper.SETTINGS_COLUMN_VALUE,
	      };  
	  
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
		  fillSettings();
	  }
	  
	  
	  public void fillLift()
	  {
		  	  Calendar rightNow = Calendar.getInstance();
		  	  rightNow.add(Calendar.MINUTE, 20);
		  	  createLiftItem(rightNow, 17.366, 78.476, "Miyapur", "Kamal", "", "Free");
		  	  rightNow.add(Calendar.MINUTE, 20);
		  	  createLiftItem(rightNow, 17.366, 78.476, "Hafeezpet", "Pankaj", "", "Free");
		  	  rightNow.add(Calendar.MINUTE, 20);
			  createLiftItem(rightNow, 17.366, 78.476, "Kondapur", "Manoj", "", "Dutch");
		  	  rightNow.add(Calendar.MINUTE, 20);
			  createLiftItem(rightNow, 17.366, 78.476, "Miyapur", "Anil", "", "Free");
		  	  rightNow.add(Calendar.MINUTE, 20);
			  createLiftItem(rightNow, 17.366, 78.476, "Kondapur", "Mahiram", "", "Free");
		  	  rightNow.add(Calendar.MINUTE, 20);
			  createLiftItem(rightNow, 17.366, 78.476, "Miyapur", "Mukesh", "", "Free");
		  	  rightNow.add(Calendar.MINUTE, 20);
			  createLiftItem(rightNow, 17.366, 78.476, "Miyapur", "Ganpat", "", "Dutch");
	  }
	  public void fillSettings()
	  {
		  setSetting(Setting.UserName, "KamalC");
		  setSetting(Setting.UserID, "kachoudh");
		  setSetting(Setting.LastPickupLocation, "Kondapur");
	  }
	  public void setSetting(Setting setting, String value) {
		  // clear any existing setting first
		  if (getSetting(setting) != null)
			  deleteSetting(setting);
		  
		  // now add the setting
		  ContentValues values = new ContentValues();
		    values.put(DataHelper.SETTINGS_COLUMN_TYPE, setting.ordinal());
		    values.put(DataHelper.SETTINGS_COLUMN_VALUE, value);
		    
		    database.insert(DataHelper.TABLE_SETTINGS, null, values);
	  }
	  
	  public String getSetting(Setting setting) {	  
		  String value = null;
		    
		  try {
			    Cursor cursor = database.query(DataHelper.TABLE_SETTINGS,
			    	settingsAllColumns, DataHelper.SETTINGS_COLUMN_TYPE + " = " + setting.ordinal(), null, null, null, null);
		
			    cursor.moveToFirst();
	
				if (!cursor.isAfterLast())
				{
					value = cursor.getString(2); 
			    }
				
			    // make sure to close the cursor
			    cursor.close();    
		  }
		  catch (Exception ex)
		  {
			  return null;
		  }
		  
	    return value;
	 }
	  
	  public void deleteSetting(Setting setting) {
	    database.delete(DataHelper.TABLE_SETTINGS, DataHelper.SETTINGS_COLUMN_TYPE
	        + " = " + setting.ordinal(), null);
	  }
  
	  public LiftItem createLiftItem(
			  Calendar time,
			  double from_lat,
			  double from_long,
			  String to,
			  String liftee,
			  String liftor,
			  String type
			  ) {
		  
		    ContentValues values = new ContentValues();
		    values.put(DataHelper.LIFT_COLUMN_TIME, getStringFromDate(time));
		    values.put(DataHelper.LIFT_COLUMN_FROM_LAT, from_lat);
		    values.put(DataHelper.LIFT_COLUMN_FROM_LONG, from_long);
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
		    values.put(DataHelper.LIFT_COLUMN_TIME, liftItem.getTimeString());
		    values.put(DataHelper.LIFT_COLUMN_FROM_LAT, liftItem.getFromLat());
		    values.put(DataHelper.LIFT_COLUMN_FROM_LONG, liftItem.getFromLong());
		    values.put(DataHelper.LIFT_COLUMN_TO, liftItem.getTo());
		    values.put(DataHelper.LIFT_COLUMN_LIFTEE, liftItem.getLiftee());
		    values.put(DataHelper.LIFT_COLUMN_LIFTOR, liftor);
		    values.put(DataHelper.LIFT_COLUMN_TYPE, liftItem.getType());
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
    			DataAccess.getCalendarFromString(cursor.getString(1)),
    			cursor.getDouble(2),
    			cursor.getDouble(3),
    			cursor.getString(4),
    			cursor.getString(5),
    			cursor.getString(6),
    			cursor.getString(7)
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
	  private Calendar _time;
	  private double _from_lat;
	  private double _from_long;
	  private String _to;
	  private String _liftee;
	  private String _liftor;
	  private String _type;
	  
	  public LiftItem(
		  long id,
		  Calendar time,
		  double from_lat,
		  double from_long,
		  String to,
		  String liftee,
		  String liftor,
		  String type)
	  {
		 _id = id;
		 _time = time;
		 _from_lat = from_lat;
		 _from_long = from_long;
		 _to = to;
		 _liftee = liftee;
		 _liftor = liftor;
		 _type = type;
	  }
	  
	  public long getId() { return _id; }
	  public Calendar getTime() { return _time; }
	  public String getTimeString() { return DataAccess.getStringFromDate(_time); }
	  public double getFromLat() { return _from_lat; }
	  public double getFromLong() { return _from_long; }
	  public String getTo() { return _to; }
	  public String getLiftee() { return _liftee; }
	  public String getLiftor() { return _liftor; }
	  public String getType() { return _type; }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return DataAccess.getStringFromDate(_time);
	  }
	} 
}
