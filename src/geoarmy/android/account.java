package geoarmy.android;

import geoarmy.android.UserPreferences;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class account extends Activity {
	 public static final String TAG = "account";
	 private ProgressDialog m_ProgressDialog = null;
	 public static final String USERNAME = "username";
	 public static final String PASSWORD = "password";

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
	    
	    userName.setText(UserPreferences.getUsername(account.this));
	    password.setText(UserPreferences.getPassword(account.this));
	    
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
	    				UserPreferences.setUsername(user);
	    				UserPreferences.setPassword(pword);
   				
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
	    			m_ProgressDialog = ProgressDialog.show(account.this,    
	    	                "Please wait...", "Retrieving data ...", true);
	    			// get variables that are in the text fields, they may not have been saved yet as we are running a test
    				String username      = userName.getText().toString();
    				String passwd        = password.getText().toString();
 
	    			NetworkTools.attemptAuth(username, passwd, mHandler, context);    				    				
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
    	m_ProgressDialog.dismiss();
    }
    
    public void onCookieResult(String result) {
    	final TextView test = (TextView) findViewById(R.id.lbl_debug);
    	if (result.length()>0) {
    		test.setText(result);
    	} else {
    		test.setText("Err no cookie data yo!");
    	}
    	m_ProgressDialog.dismiss();
    }
}