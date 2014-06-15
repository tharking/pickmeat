package com.example.pickmeat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	private static final int DATABASE_VERSION = 14;
	public static final boolean DEBUG_MODE = true;
	
	  public enum Setting {
		  UserName,
		  UserID,
		  LastPickupLocation
	  }

	public class DataHelper extends SQLiteOpenHelper {

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
		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
	  }
	  
	} 

public class DataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private DataHelper dbHelper;
	  
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
	  
	  public void fillData(SQLiteDatabase databse)
	  {
		  database = databse;
		  fillSettings();
	  }
	  
	  public void fillSettings()
	  {
		  setSetting(Setting.UserName, "");
	  	  Calendar rightNow = Calendar.getInstance();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		  setSetting(Setting.UserID, dateFormat.format(rightNow.getTime())+Math.random());
		  setSetting(Setting.LastPickupLocation, "");
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
}
