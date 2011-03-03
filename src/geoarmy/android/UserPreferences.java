package geoarmy.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreferences {
	
	private static SharedPreferences prefs = null;
	private static Editor editor = null;
	public static final String PREFERENCESNAME = "GeocacheResponder";
	
	public UserPreferences() {}

    public static String getUsername(Context context) {
    	prefs = context.getSharedPreferences(PREFERENCESNAME, Context.MODE_PRIVATE);
    	return prefs.getString(account.USERNAME, "user");
    }
    
    public static String getPassword(Context context) {
    	prefs = context.getSharedPreferences(PREFERENCESNAME, Context.MODE_PRIVATE);
    	return prefs.getString(account.PASSWORD, "password");
    }
    
    public static void setUsername(String username) {
    	editor = prefs.edit();
    	editor.putString(account.USERNAME, username);
		editor.commit();
    }
    
    public static void setPassword(String password) {
    	editor = prefs.edit();
    	editor.putString(account.PASSWORD, password);
		editor.commit();
    }

	public static void setLoggedIn(boolean loggedIn) {
    	editor = prefs.edit();
    	editor.putBoolean(account.LOGGEDIN, loggedIn);
		editor.commit();
	}

	public static boolean getLoggedIn(Context context) {
		prefs = context.getSharedPreferences(PREFERENCESNAME, Context.MODE_PRIVATE);
    	return prefs.getBoolean(account.LOGGEDIN, false);
	}
}