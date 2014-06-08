package com.example.pickmeat;
import java.util.ArrayList;

public class LocationPool {
	public String location;
	public ArrayList<Lift> liftList = new ArrayList<Lift>();
		
    public LocationPool(){
        super();
    }

    public LocationPool(String location, ArrayList<Lift> liftList) {
	        super();
	        this.location = location;
	        this.liftList = liftList;
    }
}
