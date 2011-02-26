package geoarmy.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class account extends Activity {
	 public static final String TAG = "account";
	 private SharedPreferences prefs = null;
	 private Editor editor = null;
	 public static final String BASEURL = "http://www.geoarmy.net/";
	 public static final String USERNAME = "username";
	 public static final String PASSWORD = "password";
	 public static final String PREFERENCESNAME = "GeocacheResponder";
	/**
	 * Called when the activity is first created. Responsible for initialising the UI.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.editaccount);
	    
	    final EditText userName      = (EditText) findViewById(R.id.username);
	    final EditText password      = (EditText) findViewById(R.id.password);
	    final TextView test          = (TextView) findViewById(R.id.lbl_test);
	    final Button   testButton    = (Button) findViewById(R.id.testButton);
	    final Button   saveButton    = (Button) findViewById(R.id.saveButton);
	    final Handler mHandler = new Handler();
	    
	    prefs = this.getSharedPreferences(PREFERENCESNAME, Context.MODE_PRIVATE);
	    editor = prefs.edit();
	    
	    userName.setText(prefs.getString(account.USERNAME, "user"));
	    password.setText(prefs.getString(account.PASSWORD, "password"));
	    
	    saveButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		try {
	    				String user   = userName.getText().toString();
	    				String pword  = password.getText().toString();
	    				
	    				/* basic input filtering */
	    				if (user.trim().length() == 0 || pword.trim().length() == 0)
	    				{
	    					AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
	    					AlertDialog ad = adb.create();
	    					ad.setMessage("All fields are required.");
	    					ad.show();
	    					return;
	    				}
	    				
	    				/* store data in a shared preference */
	    				editor.putString(account.USERNAME, user);
	    				editor.putString(account.PASSWORD, pword);
	    				editor.commit();
	    				
	    				finish();
	    		} catch (Exception e) {
	    			/* Oppps!!! maybe log message */
	    		}
	    	}
	    });
	    
	    testButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		final Context context = account.this;
	    		test.setText("Please wait, contacting server...");
	    		try {
	    			// get variables that are in the text fields, they may not have been saved yet as we are running a test
    				String username      = userName.getText().toString();
    				String passwd        = password.getText().toString();
    				String tokenUrl      = "login/get_auth_token";
    				String commandUrl    = "login/login";
    				// create instance of ConnectionTester
	    			NetworkTools nt = new NetworkTools();
	    			String token = nt.getToken(BASEURL + tokenUrl);
	    			NetworkTools.authenticate(BASEURL + commandUrl, username, passwd, token, mHandler, context);    				    				
	    		} catch (Exception e) {
	    			// Oppps!!! maybe log message 
	    			test.setText("Error performing test, please try again");
	    		}
	    	}
	    });
	   
	}
	 
    public void onAuthenticationResult(boolean result) {
    	final TextView test = (TextView) findViewById(R.id.lbl_test);
    	if (result) {
    		test.setText("Account details have been verified, please save the changes.");
    	} else {
    		test.setText("Invalid account details, please try again.");
    	}
    }
}