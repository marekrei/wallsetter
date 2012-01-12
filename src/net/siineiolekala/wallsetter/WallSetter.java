package net.siineiolekala.wallsetter;


import net.siineiolekala.wallsetter.R;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class WallSetter extends PreferenceActivity {
	private AlertDialog activeDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
 	
    	super.onCreate(savedInstanceState);     
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	/** If the keys are missing in the peferences, call the init() function to get them */
    	if(sharedPrefs.getString("c2dmKey", null) == null || sharedPrefs.getString("privateKey", null) == null || sharedPrefs.getString("publicKey", null) == null)
    		init(this);
    	
    	/** Creating the layout of the preferences screen */
        addPreferencesFromResource(R.xml.preferences);
        
        Preference loadNow = (Preference) findPreference("load_now");
        loadNow.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new WallpaperTask(preference.getContext()).execute();
				return true;
			}});
        
        Preference showPublicKey = (Preference) findPreference("show_public_key");
        showPublicKey.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
				AlertDialog.Builder dialog = new AlertDialog.Builder(preference.getContext());
				dialog.setMessage(sharedPrefs.getString("publicKey", preference.getContext().getString(R.string.public_key_missing))).setCancelable(true).setNeutralButton("Close", null);
				activeDialog = dialog.create();
				activeDialog.setTitle("Public key");
				activeDialog.setIcon(R.drawable.icon);
				activeDialog.show();
				return true;
			}});
        
        Preference reset = (Preference) findPreference("reset");
        reset.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				init(preference.getContext());
				return true;
			}});
        
        Preference showAbout = (Preference) findPreference("about");
        showAbout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(preference.getContext());
				dialogBuilder.setCancelable(true).setNeutralButton("Close", null)
							.setTitle(preference.getContext().getString(R.string.about_title)).setMessage(preference.getContext().getString(R.string.about_text));
				activeDialog = dialogBuilder.create();
				activeDialog.setIcon(R.drawable.icon);
				activeDialog.show();
				return true;
			}});
    }
    
    /**
     * Dismisses the active dialog if there is one. Needed in order to avoid ugly errors.
     * For example when your dialog is open and you switch orientation, the Activity is redrawn and the dialog gets "leaked"
     * Source and more info: http://groups.google.com/group/android-developers/browse_thread/thread/e9101bf64349492b#
     */
    @Override
    protected void onPause() {
        super.onPause();
        try{
        	if(activeDialog != null)
        		activeDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    /**
     * Starts the key generation process by sending a C2DM registration intent to Google.
     * The response is caught in C2DMReceiver, which then contacts the WallSetter server.
     * @param context the context of the main activity
     */
    public void init(Context context){
    	Log.d("WallSetter", "Sending a C2DM registration intent");
    	Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
        registrationIntent.putExtra("sender", "wallsetter@gmail.com");
        startService(registrationIntent);
    }
}