package net.siineiolekala.wallsetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class C2DMReceiver extends BroadcastReceiver {
	
	/**
	 * The overridden method for receiving messages.
	 * Checks whether the message is a registration reply or an actual message and
	 * calls the appropriate function.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.d("WallSetter", "Received a message");
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
	        handleRegistration(context, intent);
	    } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	        handleMessage(context, intent);
	    }
	 }

	/**
	 * Gets the registration reply, checks if it succeeded,
	 * saves the c2dm key into preferences and creates a new RegistrationTask for 
	 * contacting the WallSetter server.
	 */
	private void handleRegistration(Context context, Intent intent) {
	    String registration_id = intent.getStringExtra("registration_id");
	    String error = intent.getStringExtra("error");
	    if (error != null) {
	        // Registration failed, should try again later.
	    	Log.d("WallSetter", "Registration failed : " + error);
	    } else if (intent.getStringExtra("unregistered") != null) {
	    	Log.d("WallSetter", "Unregistered");
	    } else if (registration_id != null) {
	    	Log.d("WallSetter", "Received a new C2DM key");
	    	Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString("c2dmKey", registration_id);
    		editor.commit();
    		new RegistrationTask(context).execute(registration_id);
	    }
	}

	/**
	 * Gets the message and performs the wallpaper update by creating a new WallpaperTask.
	 */
	private void handleMessage(Context context, Intent intent){
		Log.d("WallSetter", "Received a message : " + intent.getStringExtra("message"));
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(sharedPrefs.getBoolean("perform_updates", false)){
			new WallpaperTask(context).execute();
		}
		else {
			Log.d("WallSetter", "WallSetter has been disabled");
		}
	}
}