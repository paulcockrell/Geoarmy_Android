package geoarmy.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GeocacheShow extends Activity {
	private static final CharSequence DECLARE_FOUND = "Found!";
	private static final CharSequence REMOVE_FOUND = "Remove found status";
	private static final CharSequence DECLARE_FAVORITE = "Favourite!";
	private static final CharSequence REMOVE_FAVORITE = "No longer Favourite";
	TextView geocacheId, geocacheName, geocacheLat, geocacheLon, geocacheNotes, geocacheOwner;
	TextView geocacheStatus, geocacheGeoType, geocacheTerrain, geocacheDifficulty, geocacheSize;
	TextView geocacheFound, geocacheFavorite;
	LinearLayout mainLinearLayout;
	Button foundButton, favoriteButton;
    
	location thisGeocache;
	
	private ProgressDialog m_ProgressDialog = null; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
	    final Context context = GeocacheShow.this;
		final Handler mHandler = new Handler();
	    foundButton  = (Button) findViewById(R.id.btnFound);
	    favoriteButton  = (Button) findViewById(R.id.btnFavorite);
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
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        
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
		// test dynamic add to linearlayour in scroll view, works but do we need it?
		// TextView textView = new TextView(this);
		// textView.setText("test dynamic text view");
		// this.mainLinearLayout.addView(textView);
		
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
	
	private void setButtonListeners() {       
		foundButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		try {  				
	    			declareGeocacheFound(v.getContext(), thisGeocache.getId(), thisGeocache.getFound());
	    	} catch (Exception e) {
	    			
	    		}
	    	}
		 });
		favoriteButton.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {
	    		try {  				
	    			declareGeocacheFavorite(v.getContext(), thisGeocache.getId(), thisGeocache.getFavorite());
	    	} catch (Exception e) {
	    			
	    		}
	    	}
		 });
	}

	public void setButtonText() {
		if (thisGeocache.getFound()) {
			foundButton.setText(REMOVE_FOUND);
			Drawable d = getResources().getDrawable(R.drawable.star);
			foundButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		} else if (!thisGeocache.getFound()) {
			foundButton.setText(DECLARE_FOUND);
			Drawable d = getResources().getDrawable(R.drawable.star);
			foundButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		}
		if (thisGeocache.getFavorite()) {
			favoriteButton.setText(REMOVE_FAVORITE);
			Drawable d = getResources().getDrawable(R.drawable.heart);
			favoriteButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		} else if (!thisGeocache.getFavorite()) {
			favoriteButton.setText(DECLARE_FAVORITE);
			Drawable d = getResources().getDrawable(R.drawable.heart);
			favoriteButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		}
	}
	
    public void onGeocacheResult(location geocache) {
    	thisGeocache = geocache;
    	drawScreen(thisGeocache);
    	setButtonText();
    	setButtonListeners();
    	m_ProgressDialog.dismiss();
    }
    
    public void onNetworkError(String error) {
    	if (!(m_ProgressDialog == null) && m_ProgressDialog.isShowing()) {
    		m_ProgressDialog.dismiss();
    	}
    	MessageTools.alert("Sorry a network error has occured", GeocacheShow.this);
    }

	public void onGeocacheActionResult(Boolean result, String action) {
	    final Context context = GeocacheShow.this;
		if (result) {
		       if (action == "addfound") {
		    	   thisGeocache.setFound(true);
		    	   this.geocacheFound.setText("Found: true");
		    	   Toast.makeText(context, "Added geocache to found!", Toast.LENGTH_LONG).show();
		        } else if (action == "addfavorite") {
		        	thisGeocache.setFavorite(true);
		        	this.geocacheFavorite.setText("Favorite: true");
		        	Toast.makeText(context, "Added geocache to favorites!", Toast.LENGTH_LONG).show();
		        } else if (action == "delfound") {
		        	thisGeocache.setFound(false);
		        	this.geocacheFound.setText("Found: false");
		        	Toast.makeText(context, "Removed geocache from found!", Toast.LENGTH_LONG).show();
		        } else if (action == "delfavorite") {
		        	thisGeocache.setFavorite(false);
		        	this.geocacheFavorite.setText("Favorite: false");
		        	Toast.makeText(context, "Removed geocache from favorites", Toast.LENGTH_LONG).show();
		        }
		        setButtonText();
		} else {
			Toast.makeText(context, "Failed to set geocache action, please try again", Toast.LENGTH_LONG).show();
		}
    	m_ProgressDialog.dismiss();
	}
    
}


