package geoarmy.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GeocacheShow extends Activity {
	TextView geocacheId, geocacheName, geocacheLat, geocacheLon, geocacheNotes, geocacheOwner;
	TextView geocacheStatus, geocacheGeoType, geocacheTerrain, geocacheDifficulty, geocacheSize;
	TextView geocacheFound, geocacheFavorite;
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
        geocacheStatus = (TextView) findViewById(R.id.geocacheStatus);
        geocacheGeoType = (TextView) findViewById(R.id.geocacheGeoType);
        geocacheTerrain = (TextView) findViewById(R.id.geocacheTerrain);
        geocacheDifficulty = (TextView) findViewById(R.id.geocacheDifficulty);
        geocacheSize = (TextView) findViewById(R.id.geocacheSize);
        geocacheFound = (TextView) findViewById(R.id.geocacheFound);
        geocacheFavorite = (TextView) findViewById(R.id.geocacheFavorite);
        
	    //final Button   foundButton  = (Button) findViewById(R.id.foundButton);
		Bundle bundle = getIntent().getExtras();        
		final String gId = bundle.getString("gId");
	    
		/*foundButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		try {  				
	    				declareGeocacheFound(v.getContext(),Integer.parseInt(gId.trim()));
	    	} catch (Exception e) {
	    			
	    		}
	    	}
		 });  */
		
		m_ProgressDialog = ProgressDialog.show(GeocacheShow.this,    
	             "Please wait...", "Retrieving geocache data ...", true);
		
		NetworkTools.getGeocache(Integer.parseInt(gId), mHandler, context);
	}

	private boolean declareGeocacheFound(Context context, int gId) {
		Toast.makeText(context, "Geocache declared as found!", Toast.LENGTH_LONG).show();
		return true;
	}
	
	private void drawScreen(final location geocache) {
		LinearLayout lnr = (LinearLayout) findViewById(R.id.mainLinearLayout);

    	this.geocacheId.setText("ID: " + geocache.getId());
		this.geocacheName.setText("Name: " + geocache.getName());
		this.geocacheLat.setText("Lat: " + geocache.getLat());
		this.geocacheLon.setText("Lon: " + geocache.getLon());
		this.geocacheNotes.setText("Notes: " + geocache.getNotes());
		this.geocacheOwner.setText("Owner: " + geocache.getOwner());
		this.geocacheSize.setText("Size: "+ geocache.getSize());
		this.geocacheStatus.setText("Status: " + geocache.getStatus());
		this.geocacheTerrain.setText("Terrain: " + geocache.getTerrain());
		this.geocacheDifficulty.setText("Difficulty: " + geocache.getDifficulty());
		
		if (geocache.getFavorite() == false) {
			Button btnFavorite = new Button(this);
			btnFavorite.setText("Add to favorites");
			lnr.addView(btnFavorite);
			
			btnFavorite.setOnClickListener(new Button.OnClickListener() {
		    	public void onClick(View v) {
		    		try {  				
		    				declareGeocacheFound(v.getContext(),geocache.getId());
		    	} catch (Exception e) {
		    			
		    		}
		    	}
			 });
		} else {
			this.geocacheFavorite.setText("You have added this geocache to your favorites");
		}
		
		if (geocache.getFound() == false) {
			Button btnFound = new Button(this);
			btnFound.setText("Set as found");
			lnr.addView(btnFound);
			
			btnFound.setOnClickListener(new Button.OnClickListener() {
		    	public void onClick(View v) {
		    		try {  				
		    				declareGeocacheFound(v.getContext(),geocache.getId());
		    	} catch (Exception e) {
		    			
		    		}
		    	}
			 });
		} else {
			this.geocacheFound.setText("You have found this geocache");
		}
	}

    public void onGeocacheResult(location geocache) {
    	m_ProgressDialog.dismiss();
    	drawScreen(geocache);
    }
    
}


