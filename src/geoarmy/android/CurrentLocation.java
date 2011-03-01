package geoarmy.android;

import java.util.ArrayList;
import java.util.List;

import geoarmy.android.locationList;
import geoarmy.android.location;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CurrentLocation extends MapActivity {
	MapController mapController;
	MapView mapView;
	StaticItemizedOverlay treasureOverlay;
	DynamicItemizedOverlay hunterOverlay;
	Context mContext;
	TextView txt_lat, txt_lng, txt_geocount, txt_gps;
	int latitude = 51618120, longitude = -1279020;
	String currentStatus;
	
	/** Menu references **/
	private static final int ACCOUNT_ID = R.id.account;
	private static final int RADAR_ID = R.id.compass;
	private static final int REFRESH_ID = R.id.refresh;
	private static final int CENTER_ID = R.id.center;
	private static final int LIST_ID   = R.id.geocachelist;
	private static final int SHOW_ID   = R.id.geocacheshow;
    private ProgressDialog m_ProgressDialog = null; 
    
    public static locationList currentLocationList;
    final Context context = CurrentLocation.this;
    
	/** preference variables **/
	private SharedPreferences prefs = null;
	public static final String PREFERENCESNAME = "GeocacheResponder";
	private static final String commandUrl = "geocaches";
	public static final String BASEURL = "http://www.geoarmy.net/";
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
       
        setContentView(R.layout.main); // bind the layout to the activity
        /** Check if gps is enabled, if not prompt the user to enable it **/
        if (!NetworkTools.gpsEnabled(context)) {
        	MessageTools.createGpsDisabledAlert(CurrentLocation.this);
        }
        
        /** Set up GPS pinger **/
        setGPSPing();
        
        // lat-lng text
        txt_lng = (TextView) findViewById(R.id.location_text_lng);
        txt_lat = (TextView) findViewById(R.id.location_text_lat); 
        // geocache counter
        txt_geocount = (TextView) findViewById(R.id.status_text_00);
        // satelite text
        txt_gps = (TextView) findViewById(R.id.status_text_01);
        
        // create a map view
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        mapController.setZoom(17);
        mapView.setSatellite(false);
        mapView.invalidate();
              
        // Now everything is initialised, lets draw the markers.
        getGeocaches();
    }
    
    public void setGPSPing() {
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new GeoUpdateHandler());
    }
    
    public static locationList getCurrentLocationList() {
    	return currentLocationList;
    }
    
	public void loadPreferences() {
		prefs = this.getSharedPreferences(PREFERENCESNAME, Context.MODE_PRIVATE);
		prefs.getString(account.USERNAME, "user");
		prefs.getString(account.PASSWORD, "password");
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
    	switch (item.getItemId()) {
        case ACCOUNT_ID:
            editAccount();
            //finish();
            break;
        case RADAR_ID:
        	compassView();
        	break;
        case REFRESH_ID:
            getGeocaches();
            break;
        case CENTER_ID:
        	centerMap();
        	break;
        case LIST_ID:
        	listView();
        	break;
        case SHOW_ID:
        	showView();
        	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private final void listView() {
    	Intent i = new Intent(this, geocacheListActivity.class);
    	startActivity(i);
    }
    
    private final void showView() {
    	Intent i = new Intent(this, geocacheShow.class);
    	startActivity(i);
    }
    
    private final void editAccount() {
    	Intent i = new Intent(this, account.class);
    	startActivity(i);
    }
    
    private final void compassView() {
    	Intent i = new Intent(this, compass.class);
    	startActivity(i);
    }
    
    private final void centerMap() {
    	GeoPoint point = new GeoPoint(latitude,longitude);
    	mapController.animateTo(point);
	}
    
    public void onGeocachesResult(locationList mylocationList) {
    	currentLocationList = mylocationList;
    	drawGeocaches(currentLocationList);
    	m_ProgressDialog.dismiss();
    }
    
    public void onNetworkError(String error) {
    	m_ProgressDialog.dismiss();
    	MessageTools.alert(error, context);
    }
    
    private final void getGeocaches() {
        m_ProgressDialog = ProgressDialog.show(CurrentLocation.this,    
                "Please wait...", "Retrieving geocache data ...", true);
        final Handler mHandler = new Handler();
    	double parsedLat = latitude;
    	parsedLat = parsedLat / 1000000;
    	double parsedLon = longitude;
    	parsedLon = parsedLon / 1000000;
    	//get geocache points	
    	NetworkTools.getLocations(BASEURL + commandUrl, parsedLat, parsedLon, mHandler, context);
    }
    
    private final void drawGeocaches(locationList mylocationList) {
        List<Overlay> mapOverlays = mapView.getOverlays();
        
        // Load preferences
        loadPreferences();
              
        //Add geocache markers
        //
        Drawable treasurePin = this.getResources().getDrawable(R.drawable.ruby);
        GeoPoint treasurepoint;
        
    	treasureOverlay = new StaticItemizedOverlay(treasurePin, this); 
    	OverlayItem overlayTreasure;

    	// our location
        GeoPoint point = new GeoPoint(latitude, longitude);
    	
    	int len = mylocationList.length();
    	
    	ArrayList<location> localArray = mylocationList.getLocations();
    	location localLocation = new location();
    	
    	
    	int myLat = 0;
    	int myLon = 0;
    	for (int i = 0; i < len; i++) {
    		localLocation = localArray.get(i);
    		myLat = (int) (Double.parseDouble(localLocation.getLat())*1E6);
    		myLon = (int) (Double.parseDouble(localLocation.getLon())*1E6);
    		treasurepoint = new GeoPoint(myLat, myLon);

        	overlayTreasure = new OverlayItem(treasurepoint, "Treasure!", "Point: " + treasurepoint);
        	treasureOverlay.addOverlay(overlayTreasure);
        	mapOverlays.add(treasureOverlay); // add new marker
         }
    	
    	txt_geocount.setText("Goecache count: " + len);

        //Add users marker
        //
        Drawable userPin = this.getResources().getDrawable(R.drawable.center_marker_male);
        hunterOverlay = new DynamicItemizedOverlay(userPin, this);

        OverlayItem overlayitem = new OverlayItem(point, "Hello Hunter", "Point: " + point);
        hunterOverlay.addOverlay(overlayitem);
        mapOverlays.add(hunterOverlay); // add new marker
        mapController.setCenter(point);
    }
    
	public void getGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new GeoUpdateHandler());
	}
	
    @SuppressWarnings("unused")
	private final void hideMenu() {
    	
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    public class GeoUpdateHandler implements LocationListener {
       	
    	public void onLocationChanged(Location location) {
    		
    		if (location != null) {
    			latitude = (int) (location.getLatitude()*1E6);
    			longitude = (int) (location.getLongitude()*1E6);
    		} 
    		GeoPoint point = new GeoPoint(latitude,longitude);

    		String currentLat = "Lat: " + location.getLatitude();
    		String currentLng = "Lng: " + location.getLongitude();
    		txt_lat.setText(currentLat);
    		txt_lng.setText(currentLng);
    		
    		currentStatus = "GPS: Active";
    		txt_gps.setText(currentStatus);

    		this.drawMarker(point);
    		//mapController.animateTo(point); always keep hunter in middle of map
       	}

    	
    	private void drawMarker(GeoPoint point) {		
    		List<Overlay> mapOverlays = mapView.getOverlays();
            OverlayItem overlayitem = new OverlayItem(point, "Hello Paul", "Point: " + point);
            hunterOverlay.addOverlay(overlayitem);
    		
            mapOverlays.add(hunterOverlay); // add new marker
			mapView.invalidate();
    	}
    	
    	public void onProviderDisabled(String provider) {
    	}
    	
    	public void onProviderEnabled(String provider) {
    	}
    	
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    	}
    }
}