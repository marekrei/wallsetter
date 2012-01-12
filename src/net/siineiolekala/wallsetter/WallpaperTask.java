package net.siineiolekala.wallsetter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class WallpaperTask extends AsyncTask<Void,Void,Void>{
	
	private Context context;
	public WallpaperTask(Context context){
		this.context = context;
	}
	
	/**
	 * Checks whether there is a file at the specified URL or not.
	 * More into and source: http://stackoverflow.com/questions/4596447/java-check-if-file-exists-on-remote-server-using-its-url
	 * @param URLName The URL for checking
	 * @return true if the file exists, false otherwise.
	 */
	public static boolean exists(String URLName){
		try {
			HttpURLConnection.setFollowRedirects(true);
			// note : you may also need
			// HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Connects to the server and updates the wallpaper.
	 */
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("WallSetter", "Trying to set the wallpaper");
		try {
			final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			String url = "http://www.siineiolekala.net/wallsetter/api.php?image&privateKey=" + sharedPrefs.getString("privateKey", null);
			Log.d("WallSetter", "Getting image : " + url);
			if(exists(url)){
				URL fileUrl = new URL(url);
				InputStream stream = fileUrl.openStream();
				wallpaperManager.setStream(stream);
				Log.d("WallSetter", "Finished setting the wallpaper");
			}
			else {
				Log.d("WallSetter", "The wallpaper file does not exist on the server");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
