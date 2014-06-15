package com.example.pickmeat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.App42BadParameterException;
import com.shephertz.app42.paas.sdk.android.App42NotFoundException;
import com.shephertz.app42.paas.sdk.android.storage.OrderByType;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.Storage.JSONDocument;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DataAccessApp42 {
	private static final int DATABASE_VERSION = 1;
	private static final String API_KEY = "cfe6d802440911e5644d48381ea779465d738e92608f9fbfc52f6a06e081788f";
	private static final String SECRET_KEY = "7de5aa1dfe6ada125d5c00ddb0dfb2c8cbc3c0bf3ac3c9cf21ad82c6c718dfd3";
	private static final String DATABASE_NAME = "PickMeAt";
	private static final String COLLECTION_NAME = "Lifts"+DATABASE_VERSION;

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
	
	public static ArrayList getJSONObjectsFromJSONDocuments(
			ArrayList<Storage.JSONDocument> input) {
		ArrayList retValue = new ArrayList<JSONObject>();
		for (int i = 0; i < input.size(); i++) {
			JSONObject obj;
			try {
				obj = new JSONObject(input.get(i).jsonDoc);
				retValue.add(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return retValue;
	}
	public class DataHelperApp42 {

	  public static final String LIFT_COLUMN_ID = "lift_id";
	  public static final String LIFT_COLUMN_TIME = "lift_time";
	  public static final String LIFT_COLUMN_FROM_LAT = "lift_from_lat";
	  public static final String LIFT_COLUMN_FROM_LONG = "lift_from_long";
	  public static final String LIFT_COLUMN_TO = "lift_to";
	  public static final String LIFT_COLUMN_LIFTEE = "liftee";
	  public static final String LIFT_COLUMN_LIFTEE_ID = "liftee_id";
	  public static final String LIFT_COLUMN_LIFTOR = "liftor";
	  public static final String LIFT_COLUMN_LIFTOR_ID = "liftor_id";
	  public static final String LIFT_COLUMN_TYPE = "lift_type";


	  
	  DataSourceApp42 source;
		  
	  public DataHelperApp42(Context context, DataSourceApp42 src) {
	    source = src;
	  }

	  public void onUpgrade(int oldVersion, int newVersion) {
	    Log.w(DataHelperApp42.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    //TBD: Add logic to delete old collections after upgrade
	    //dropTables(db);
	    //onCreate(db);
	  }
	  
  
	} 

public class DataSourceApp42 {

	  private StorageService storageService; 
	  
	  private String[] liftAllColumns = { 
			  DataHelperApp42.LIFT_COLUMN_ID,
			  DataHelperApp42.LIFT_COLUMN_TIME,
			  DataHelperApp42.LIFT_COLUMN_FROM_LAT,
			  DataHelperApp42.LIFT_COLUMN_FROM_LONG,
			  DataHelperApp42.LIFT_COLUMN_TO,
			  DataHelperApp42.LIFT_COLUMN_LIFTEE,
			  DataHelperApp42.LIFT_COLUMN_LIFTEE_ID,
			  DataHelperApp42.LIFT_COLUMN_LIFTOR,
			  DataHelperApp42.LIFT_COLUMN_LIFTOR_ID,
			  DataHelperApp42.LIFT_COLUMN_TYPE};
 
	  public DataSourceApp42(Context context) {
		App42API.initialize(context, API_KEY, SECRET_KEY);
		storageService = App42API.buildStorageService(); 
	  }

	  public void open() {
// fill dummy data, if nothing exists
		  
		Query q1 = QueryBuilder.build(DataHelperApp42.LIFT_COLUMN_LIFTOR, "", Operator.EQUALS);   
		List<DataAccessApp42.LiftItem> liftitems = null;
		try {
				liftitems = getLiftsByCriteriaWithPaging(q1, 1, 0);
				System.out.println("Lift items count:" + liftitems.size());
				if (liftitems.size() == 0){
					fillData();
				}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	  }

	  public void close() {
		  return;
	  }
	  
	  public void fillData() throws JSONException
	  {
		  fillLift(17.366, 78.476);
	  }
	  
	  public void fillLift(Double lat, Double lon) throws JSONException
	  {
	  	  Calendar rightNow = Calendar.getInstance();
	  	  rightNow.add(Calendar.MINUTE, 20);
	  	  createLiftItem(rightNow, lat, lon, "Miyapur", "Kamal", "Kamal_ID", "", "", "Free");
	  	  rightNow.add(Calendar.MINUTE, 20);
	  	  createLiftItem(rightNow, lat, lon, "Hafeezpet", "Pankaj", "Pankaj_ID", "", "", "Free");
	  	  rightNow.add(Calendar.MINUTE, 20);
		  createLiftItem(rightNow, lat, lon, "Kondapur", "Manoj", "Manoj_ID", "", "", "Dutch");
	  	  rightNow.add(Calendar.MINUTE, 20);
		  createLiftItem(rightNow, lat, lon, "Miyapur", "Anil", "Anil_ID", "", "", "Free");
	  	  rightNow.add(Calendar.MINUTE, 20);
		  createLiftItem(rightNow, lat, lon, "Kondapur", "Mahiram", "Mahiram_ID", "", "", "Free");
	  	  rightNow.add(Calendar.MINUTE, 20);
		  createLiftItem(rightNow, lat, lon, "Miyapur", "Mukesh", "Mukesh_ID", "", "", "Free");
	  	  rightNow.add(Calendar.MINUTE, 20);
		  createLiftItem(rightNow, lat, lon, "Miyapur", "Ganpat", "Ganpat_ID", "", "", "Dutch");
	  }


	  public LiftItem createLiftItem(
			  Calendar time,
			  double from_lat,
			  double from_long,
			  String to,
			  String liftee,
			  String liftee_id,
			  String liftor,
			  String liftor_id,
			  String type
			  ) throws JSONException {
		  
		  	JSONObject values = new JSONObject();
		    values.put(DataHelperApp42.LIFT_COLUMN_ID, liftee_id+liftor_id+getStringFromDate(Calendar.getInstance()));
		    values.put(DataHelperApp42.LIFT_COLUMN_TIME, getStringFromDate(time));
		    values.put(DataHelperApp42.LIFT_COLUMN_FROM_LAT, from_lat);
		    values.put(DataHelperApp42.LIFT_COLUMN_FROM_LONG, from_long);
		    values.put(DataHelperApp42.LIFT_COLUMN_TO, to);
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTEE, liftee);
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTEE_ID, liftee_id);
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTOR, liftor);
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTOR_ID, liftor_id);
		    values.put(DataHelperApp42.LIFT_COLUMN_TYPE, type);
		    
		    Storage response = storageService.insertJSONDocument(DATABASE_NAME, COLLECTION_NAME, values);
		    if (response.isResponseSuccess()) {
				return JSONObjectToLift(values);
			} else {
				return null;
			}
	  }

	  public void deleteLiftItem(String lift_id) {
	    Storage response = storageService.findDocumentByKeyValue(DATABASE_NAME, COLLECTION_NAME, DataHelperApp42.LIFT_COLUMN_ID, lift_id);
	    if (response.isResponseSuccess()) {
			ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
			App42Response app42response = (App42Response) storageService.deleteDocumentById(DATABASE_NAME, COLLECTION_NAME,jsonDocList.get(0).getDocId());
			if (app42response.isResponseSuccess()) {
				System.out.println("Lift item deleted with id: " + lift_id);
			}
		} 
	  }

	  public void acceptLiftItem(String lift_id, String liftor, String liftor_id) throws JSONException {
		  	LiftItem liftItem = getLiftItem(lift_id);
		    System.out.println("Lift item accepted with id: " + liftItem.getId());
		  	JSONObject values = new JSONObject();
		    values.put(DataHelperApp42.LIFT_COLUMN_ID, liftItem.getId());
		    values.put(DataHelperApp42.LIFT_COLUMN_TIME, liftItem.getTimeString());
		    values.put(DataHelperApp42.LIFT_COLUMN_FROM_LAT, liftItem.getFromLat());
		    values.put(DataHelperApp42.LIFT_COLUMN_FROM_LONG, liftItem.getFromLong());
		    values.put(DataHelperApp42.LIFT_COLUMN_TO, liftItem.getTo());
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTEE, liftItem.getLiftee());
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTEE_ID, liftItem.getLifteeId());
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTOR, liftor);
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTOR_ID, liftor_id);
		    values.put(DataHelperApp42.LIFT_COLUMN_TYPE, liftItem.getType());
		    
		    Storage response = storageService.updateDocumentByKeyValue(DATABASE_NAME, COLLECTION_NAME, DataHelperApp42.LIFT_COLUMN_ID, lift_id, values);
	  }

	  public void denyLiftItem(String lift_id) throws JSONException {
		  	LiftItem liftItem = getLiftItem(lift_id);
		    System.out.println("Lift item denied with id: " + liftItem.getId());
		  	JSONObject values = new JSONObject();
		    values.put(DataHelperApp42.LIFT_COLUMN_ID, liftItem.getId());
		    values.put(DataHelperApp42.LIFT_COLUMN_TIME, liftItem.getTimeString());
		    values.put(DataHelperApp42.LIFT_COLUMN_FROM_LAT, liftItem.getFromLat());
		    values.put(DataHelperApp42.LIFT_COLUMN_FROM_LONG, liftItem.getFromLong());
		    values.put(DataHelperApp42.LIFT_COLUMN_TO, liftItem.getTo());
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTEE, liftItem.getLiftee());
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTEE_ID, liftItem.getLifteeId());
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTOR, "");
		    values.put(DataHelperApp42.LIFT_COLUMN_LIFTOR_ID, "");
		    values.put(DataHelperApp42.LIFT_COLUMN_TYPE, liftItem.getType());
		    
		    Storage response = storageService.updateDocumentByKeyValue(DATABASE_NAME, COLLECTION_NAME, DataHelperApp42.LIFT_COLUMN_ID, lift_id, values);
	  }

	  public List<LiftItem> getLiftsByCriteria(Query query) throws JSONException {
		  List<LiftItem> liftItems = new ArrayList<LiftItem>();
		  try {
			  Storage response = storageService.findDocumentsByQuery(DATABASE_NAME, COLLECTION_NAME, query);
			  if (response.isResponseSuccess()){
					ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
			    	ArrayList<JSONObject> jsonObjList = getJSONObjectsFromJSONDocuments(jsonDocList);
			    	for(int i=0;i<jsonObjList.size();i++)  {
				    	LiftItem liftItem = JSONObjectToLift(jsonObjList.get(i));
				    	System.out.println(liftItem);
					    liftItems.add(liftItem);
			    	}
			  }
		  } catch (App42NotFoundException e){
			  // do nothing, send empty list
		  }
		return liftItems;
	  }

	  public List<LiftItem> getLiftsByCriteriaWithPaging(Query query, int max, int offset) throws JSONException {
		  List<LiftItem> liftItems = new ArrayList<LiftItem>();
		  try {
			  Storage response = storageService.findDocumentsByQueryWithPaging(DATABASE_NAME, COLLECTION_NAME, query, max, offset);
			  if (response.isResponseSuccess()){
					ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
			    	ArrayList<JSONObject> jsonObjList = getJSONObjectsFromJSONDocuments(jsonDocList);
			    	for(int i=0;i<jsonObjList.size();i++)  {
				    	LiftItem liftItem = JSONObjectToLift(jsonObjList.get(i));
				    	System.out.println(liftItem);
					    liftItems.add(liftItem);
			    	}
			  }
		  } catch (App42NotFoundException e){
			  // do nothing, send empty list
		  }
		return liftItems;
	  }

	  public ArrayList<String> getAllLocations() {
		  ArrayList<String> locations = new ArrayList<String>();
		  return locations;
	  }
	  

	  public LiftItem getLiftItem(String id) throws JSONException {
	    Storage response = storageService.findDocumentByKeyValue(DATABASE_NAME, COLLECTION_NAME, DataHelperApp42.LIFT_COLUMN_ID, id);
	    if (response.isResponseSuccess()) {
	    	ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
	    	ArrayList<JSONObject> jsonObjList = getJSONObjectsFromJSONDocuments(jsonDocList);
	    	return JSONObjectToLift(jsonObjList.get(0));
	    }
	    return null;
	  }
	  
	  private LiftItem JSONObjectToLift(JSONObject values) throws JSONException {

		  LiftItem liftItem = 
	    	new LiftItem(
    			(String)values.get(DataHelperApp42.LIFT_COLUMN_ID),
    			DataAccessApp42.getCalendarFromString((String)values.get(DataHelperApp42.LIFT_COLUMN_TIME)),
    			(Double)values.get(DataHelperApp42.LIFT_COLUMN_FROM_LAT),
    			(Double)values.get(DataHelperApp42.LIFT_COLUMN_FROM_LONG),
    			(String)values.get(DataHelperApp42.LIFT_COLUMN_TO),
    			(String)values.get(DataHelperApp42.LIFT_COLUMN_LIFTEE),
    			(String)values.get(DataHelperApp42.LIFT_COLUMN_LIFTEE_ID),
    			(String)values.get(DataHelperApp42.LIFT_COLUMN_LIFTOR),
    			(String)values.get(DataHelperApp42.LIFT_COLUMN_LIFTOR_ID),
    			(String)values.get(DataHelperApp42.LIFT_COLUMN_TYPE)
    			);

	    return liftItem;
	  }
	} 


public class LiftItem {
	  private String _id;
	  private Calendar _time;
	  private Double _from_lat;
	  private Double _from_long;
	  private String _to;
	  private String _liftee;
	  private String _liftee_id;
	  private String _liftor;
	  private String _liftor_id;
	  private String _type;
	  
	  public LiftItem(
		  String id,
		  Calendar time,
		  Double from_lat,
		  Double from_long,
		  String to,
		  String liftee,
		  String liftee_id,
		  String liftor,
		  String liftor_id,
		  String type)
	  {
		 _id = id;
		 _time = time;
		 _from_lat = from_lat;
		 _from_long = from_long;
		 _to = to;
		 _liftee = liftee;
		 _liftee_id = liftee_id;
		 _liftor_id = liftor_id;
		 _type = type;
	  }
	  
	  public String getId() { return _id; }
	  public Calendar getTime() { return _time; }
	  public String getTimeString() { return DataAccessApp42.getStringFromDate(_time); }
	  public Double getFromLat() { return _from_lat; }
	  public Double getFromLong() { return _from_long; }
	  public String getTo() { return _to; }
	  public String getLiftee() { return _liftee; }
	  public String getLifteeId() { return _liftee_id; }
	  public String getLiftor() { return _liftor; }
	  public String getLiftorId() { return _liftor_id; }
	  public String getType() { return _type; }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return _id;
	  }
	} 
}
