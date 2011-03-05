package geoarmy.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GeocacheShow extends Activity {
	TextView geocacheId, geocacheName, geocacheLat, geocacheLon, geocacheNotes, geocacheOwner;
	private ProgressDialog m_ProgressDialog = null; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
	    final Context context = GeocacheShow.this;
		final Handler mHandler = new Handler();
		
        geocacheId = (TextView) findViewById(R.id.geocacheId);
        geocacheName = (TextView) findViewById(R.id.geocacheName);
        geocacheLat = (TextView) findViewById(R.id.geocacheLat);
        geocacheLon = (TextView) findViewById(R.id.geocacheLon);
        geocacheNotes = (TextView) findViewById(R.id.geocacheNotes);
        geocacheOwner = (TextView) findViewById(R.id.geocacheOwner);
	    final Button   foundButton  = (Button) findViewById(R.id.foundButton);
		Bundle bundle = getIntent().getExtras();        
		final String gId = bundle.getString("gId");
	    
		foundButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		try {  				
	    				declareGeocacheFound(v.getContext(),Integer.parseInt(gId.trim()));
	    	} catch (Exception e) {
	    			/* Oppps!!! maybe log message */
	    		}
	    	}
		 });  
		
		m_ProgressDialog = ProgressDialog.show(GeocacheShow.this,    
	             "Please wait...", "Retrieving geocache data ...", true);
		
		NetworkTools.getGeocache(Integer.parseInt(gId), mHandler, context);
	}

	private boolean declareGeocacheFound(Context context, int gId) {
		Toast.makeText(context, "Geocache declared as found!", Toast.LENGTH_LONG).show();
		return true;
	}

    public void onGeocacheResult(location geocache) {
    	m_ProgressDialog.dismiss();
    	this.geocacheId.setText("ID: " + geocache.getId());
		this.geocacheName.setText("Name: " + geocache.getName());
		this.geocacheLat.setText("Lat: " + geocache.getLat());
		this.geocacheLon.setText("Lon: " + geocache.getLon());
		this.geocacheNotes.setText("Notes: " + geocache.getNotes());
		this.geocacheOwner.setText("Owner: " + geocache.getOwner());
    }
}