package com.example.pickmeat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity {

	protected int _splashTime = 5000; 
    
    private Thread splashTread;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);

    	//Remove notification bar
    	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        
        
        final SplashScreen sPlashScreen = this; 
        
        // thread for displaying the SplashScreen
        splashTread = new Thread() {
            @Override
            public void run() {
                try {                       
                    synchronized(this){
                            wait(_splashTime);
                    }
                    
                } catch(InterruptedException e) {} 
                finally {
                    finish();
                    
                    Intent i = new Intent();
                    i.setClass(sPlashScreen, MainActivity.class);
                    startActivity(i);
                    
                    //stop();
                }
            }
        };
        
        splashTread.start();
    }
}

