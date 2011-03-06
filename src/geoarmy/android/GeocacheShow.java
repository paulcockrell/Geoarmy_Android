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
        
		Bundle bundle = getIntent().getExtras();        
		final String gId = bundle.getString("gId");
	    
		m_ProgressDialog = ProgressDialog.show(GeocacheShow.this,    
	             "Please wait...", "Retrieving geocache data ...", true);
		
		NetworkTools.getGeocache(Integer.parseInt(gId), mHandler, context);
	}

	private void declareGeocacheFound(Context context, int gId, boolean foundState) {
		final Handler mHandler = new Handler();
		String action = "";
		if (foundState) {
			action = "delfound";
		} else {
			action = "addfound";
		}
		
		m_ProgressDialog = ProgressDialog.show(GeocacheShow.this,    
	             "Please wait...", "Declaring geocache found", true);
		NetworkTools.geocacheAction(gId, action, mHandler, context);
	}

	private void declareGeocacheFavorite(Context context, int gId, boolean favoriteState) {
		final Handler mHandler = new Handler();
		String action = "";
		if (favoriteState) {
			action = "delfavorite";
		} else {
			action = "addfavorite";
		}
		m_ProgressDialog = ProgressDialog.show(GeocacheShow.this,    
	             "Please wait...", "Declaring geocache as a favorite", true);
		NetworkTools.geocacheAction(gId, action, mHandler, context);
	}
	
	private void drawScreen(final location geocache) {
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
		this.geocacheFavorite.setText("Favorite: " + geocache.getFavorite());
		this.geocacheFound.setText("Found: " + geocache.getFound());
	}
	
	private void setButtonListeners(final location geocache) {
        final Button foundButton  = (Button) findViewById(R.id.btnFound);
        final Button favoriteButton  = (Button) findViewById(R.id.btnFavorite);
        
		foundButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		try {  				
	    			declareGeocacheFound(v.getContext(),geocache.getId(), geocache.getFound());
	    	} catch (Exception e) {
	    			
	    		}
	    	}
		 });
		favoriteButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		try {  				
	    			declareGeocacheFavorite(v.getContext(),geocache.getId(), geocache.getFavorite());
	    	} catch (Exception e) {
	    			
	    		}
	    	}
		 });
	}

    public void onGeocacheResult(location geocache) {
    	drawScreen(geocache);
    	setButtonListeners(geocache);
    	m_ProgressDialog.dismiss();
    }

	public void onGeocacheActionResult(Boolean result, String action) {
	    final Context context = GeocacheShow.this;
		if (result) {
		       if (action == "addfound") {
		    	   this.geocacheFound.setText("Found: true");
		        } else if (action == "addfavorite") {
		        	this.geocacheFavorite.setText("Favorite: true");
		        } else if (action == "delfound") {
		        	this.geocacheFound.setText("Found: false");
		        } else if (action == "delfavorite") {
		        	this.geocacheFavorite.setText("Favorite: false");
		        }
		} else {
			Toast.makeText(context, "Failed to set geocache action, please try again", Toast.LENGTH_LONG).show();
		}
    	m_ProgressDialog.dismiss();
	}
    
}


