package com.example.pickmeat;

import java.util.Calendar;

public class Lift {
		public String lift_id;
	    public Calendar time;
	    public Double from_lat;
	    public Double from_long;
	    public String to;
	    public String liftee;
	    public String liftor;
	    public String type;
	    
	    public Lift(){
	        super();
	    }

	    public Lift(String lift_id, Calendar time, Double from_lat, Double from_long, String to, String liftee, String liftor, String type) {
	        super();
	        this.lift_id = lift_id;
	        this.time = time;
	        this.from_lat = from_lat;
	        this.from_long = from_long;
	        this.to = to;
	        this.liftee = liftee;
	        this.liftor = liftor;
	        this.type = type;
	    }

}
