package geoarmy.android;

import geoarmy.android.UserPreferences;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class account extends Activity {
	 private ProgressDialog m_ProgressDialog = null;
	 public static final String USERNAME = "username";
	 public static final String PASSWORD = "password";
	 public static final String LOGGEDIN = "loggedin";
	 
	/**
	 * Called when the activity is first created. Responsible for initialising the UI.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.editaccount);
	    
	    final EditText userName      = (EditText) findViewById(R.id.username);
	    final EditText password      = (EditText) findViewById(R.id.password);
	    final Button   updateButton  = (Button) findViewById(R.id.updateButton);
	    final Button   closeButton  = (Button) findViewById(R.id.closeButton);
	    
	    userName.setText(UserPreferences.getUsername(account.this));
	    password.setText(UserPreferences.getPassword(account.this));
	    
	    updateButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		try {
	    				String user   = userName.getText().toString();
	    				String pword  = password.getText().toString();
	    				
	    				/* basic input filtering */
	    				if (validateForm(v.getContext(), user, pword)) {  
    						/* store data in a shared preference */
    						UserPreferences.setUsername(user);
	    					UserPreferences.setPassword(pword);
	    					
	    					authenticateUser(user, pword);
	    				}
	    		} catch (Exception e) {
	    			/* Oppps!!! maybe log message */
	    		}
	    	}
	    });  
	    
	    closeButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
	    });
	}
	
	private boolean validateForm(Context context, String username, String password) {
		if (username.trim().length() == 0 || password.trim().length() == 0)
		{
			Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	private boolean authenticateUser(String username, String password) {
		boolean authenticated = false;
		final Context context = account.this;
	    final Handler mHandler = new Handler();
		try {
			m_ProgressDialog = ProgressDialog.show(account.this,    
	                "Please wait...", "Validating account details...", true);
			authenticated = NetworkTools.authenticate(username, password, mHandler, context, true);    				    				
		} catch (Exception e) {
			// Oppps!!! maybe log message 
			return authenticated;
		}
		return authenticated;
	}
	
	
	 
    public void onAuthenticationResult(boolean result) {
    	m_ProgressDialog.dismiss();
    	if (result) {
    		Toast.makeText(account.this, "Account validated", Toast.LENGTH_LONG).show();
    	} else {
    		Toast.makeText(account.this, "Invalid account details", Toast.LENGTH_LONG).show();
    	}
    }
}