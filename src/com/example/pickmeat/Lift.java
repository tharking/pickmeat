package com.example.pickmeat;

public class Lift {
		public long lift_id;
	    public String time;
	    public String from;
	    public String to;
	    public String liftee;
	    public String liftor;
	    public String type;
	    
	    public Lift(){
	        super();
	    }

	    public Lift(long lift_id, String time, String from, String to, String liftee, String liftor, String type) {
	        super();
	        this.lift_id = lift_id;
	        this.time = time;
	        this.from = from;
	        this.to = to;
	        this.liftee = liftee;
	        this.liftor = liftor;
	        this.type = type;
	    }

}
