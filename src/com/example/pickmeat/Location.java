package com.example.pickmeat;
import java.util.ArrayList;

public class Location {
	public String location;
	public ArrayList<Lift> liftList = new ArrayList<Lift>();
		
    public Location(){
        super();
    }

    public Location(String location, ArrayList<Lift> liftList) {
	        super();
	        this.location = location;
	        this.liftList = liftList;
    }
}
