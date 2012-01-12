package net.siineiolekala.wallsetter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class RegistrationTask extends AsyncTask<String,Void,Void>{
	private Context context;
	public RegistrationTask(Context context){
		this.context = context;
	}
	
	/**
	 * Sends a query to the WallSetter server with the C2DM key to register.
	 * The server replies back with the public and private keys, which are
	 * then saved to the preferences.
	 */
	@Override
	protected Void doInBackground(String... params) {
		Log.d("WallSetter", "Sending WallSetter registration");
		   try {
			   String c2dmKey = params[0];
			   SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			   String url = "http://www.siineiolekala.net/wallsetter/api.php?register&c2dmKey=" + c2dmKey;
			   if(sharedPrefs.getString("privateKey", null) != null)
				   url += "&oldPrivateKey=" + sharedPrefs.getString("privateKey", null);
			    URL fileUrl = new URL(url);
			    BufferedReader in = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
			    
			    Editor editor = sharedPrefs.edit();
			    String publicKey = in.readLine();
			    String privateKey = in.readLine();
			    editor.putString("publicKey", publicKey);
			    editor.putString("privateKey", privateKey);
			    editor.commit();
			    in.close();
			    Log.d("WallSetter", "Received new public key: " + publicKey);
			} catch (Exception e) {
				Log.d("WallSetter", e.getMessage());
			}
		return null;
	}
}
