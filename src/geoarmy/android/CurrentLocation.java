package geoarmy.android;

import java.util.ArrayList;
import java.util.List;

import geoarmy.android.locationList;
import geoarmy.android.location;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CurrentLocation extends MapActivity {
	
	private static final String TAG = "MapActivity";
	
	MapController mapController;
	MapView mapView;
	StaticItemizedOverlay treasureOverlay;
	DynamicItemizedOverlay hunterOverlay;
	Context mContext;
	TextView txt_lat, txt_lng, txt_geocount, txt_compass;
	int latitude = 51618120, longitude = -1279020;
	String currentStatus;
	UserPreferences userPrefs;
	char degree = '\u00B0';
	View splashpanel;
	
	/** Compass stuff **/
	private static SensorManager mySensorManager;
	@SuppressWarnings("unused")
	private boolean sensorrunning;
	private String[] headings = {"North","North by North East","North East","North East by East","East","South East by East","South East","South by South East","South","South by South West","South West","South West by West","West","North West by West","North West","North by North West"};
	/** Menu references **/
	private static final int ACCOUNT_ID = R.id.account;
	private static final int REFRESH_ID = R.id.refresh;
	private static final int CENTER_ID  = R.id.center;
	private static final int LIST_ID    = R.id.geocachelist;
    private ProgressDialog m_ProgressDialog = null; 
 
    public static locationList currentLocationList = new locationList();
    final Context context = CurrentLocation.this;
	final Handler mHandler = new Handler();
	ProgressBar progressBar;
	TextView loadingText;
	
	private Load task=null;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle bundle) {
    	Log.d(TAG, "onCreate");
        super.onCreate(bundle);
        setContentView(R.layout.main); // bind the layout to the activity
        
        /**Set splash panel to inflate!**/
        splashpanel = ((ViewStub) findViewById(R.id.stub_import)).inflate();
        loadingText = (TextView)findViewById(R.id.loadingText);
        loadingText.setText("Loading...");
        progressBar = (ProgressBar)findViewById(R.id.progressbar_Horizontal);
        progressBar.setProgress(0);
     
        // lat-lng text
        txt_lng = (TextView) findViewById(R.id.location_text_lng);
        txt_lat = (TextView) findViewById(R.id.location_text_lat); 
        // geocache counter
        txt_geocount = (TextView) findViewById(R.id.status_text_00);
        // Compass text
        txt_compass = (TextView) findViewById(R.id.status_text_01);
        
        /** Splash handler stuffs **/
        task=(Load)getLastNonConfigurationInstance();
    	if (task==null) {
    		task=new Load(this);
    		task.execute();
    	} else  {
    		task.attach(this);
    		updateProgress(task.getProgress());
    		if (task.getProgress()>=100) {
    			markAsDone();
    		}
    	}        
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	task.detach();
    	return(task);
    }

    void updateProgress(int progress) {
    	if (progress <= 25) {
    		loadingText.setText("Attempting login");  
    	} else if (progress <= 50) {
    		loadingText.setText("Creating maps");
    	} else if (progress <= 75) {
    		loadingText.setText("Initializing compass");
    	} else if (progress <= 100) {
    		loadingText.setText("Drawing maps");
    	} 
    	progressBar.setProgress(progress);
    }

    void markAsDone() {
    	//findViewById(R.id.completed).setVisibility(View.VISIBLE);
    }
    
    private class Load extends AsyncTask<Void, Void, Void> {
		CurrentLocation activity=null;
		int progress=0;
	
    	protected void onPreExecute() {
			/** Set up GPS pinger **/
            activity.setGPSPing();	
			publishProgress();
    	}
    	
        protected void onPostExecute(final Void unused) {
			mapView.invalidate();
        	/** Geocaches, this breaks in the main 
             *  part of the thread, possibly because
             *  it spawns anther thread? who knows
             *  so this is why its here.. **/
        	getGeocaches();
        	drawGeocaches(currentLocationList);	

        	splashpanel.setVisibility(View.INVISIBLE);
        }

		protected Void doInBackground(Void... unused) {
			Looper.prepare();
            /** Login             **/
            login();
			publishProgress();
            /** Create maps       **/
            createMap();
			publishProgress();
            /** Set up Compass    **/
            initCompass();
			publishProgress();

			return null;
		}
		
		protected void onProgressUpdate(Void... unused) {
			//activity.updateProgress("haha");
			//progressBar.setProgress(values[0]);
			progress += 25;
			activity.updateProgress(progress);
		}
		
		Load(CurrentLocation activity) {
			attach(activity);
		}
		
		int getProgress() {
			return(progress);
		}
		
		void detach() {
			activity=null;
		}

		void attach(CurrentLocation activity) {
			this.activity=activity;
		}
    }
    
    void updateProgress(String sometext) {
    	loadingText.setText(sometext);
    }

    private void login() {
        String username = UserPreferences.getUsername(context);
        String password = UserPreferences.getPassword(context);
        NetworkTools.authenticate(username, password, mHandler, context, false);
        if (UserPreferences.getLoggedIn(context)) {
        	Toast.makeText(context, "Successfully logged into your account", Toast.LENGTH_LONG).show();
        } else {
        	Toast.makeText(context, "Failed to log into your account", Toast.LENGTH_LONG).show();
        }
        
    }
    
    private void createMap() {
        // create a map view
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        mapController.setZoom(17);
        mapView.setSatellite(false);
    }
    
    public static locationList getCurrentLocationList() {
    	Log.d(TAG, "getCurrentLocationList");
    	currentLocationList.length();
    	return currentLocationList;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.d(TAG, "onCreateOptionsMenu");
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d(TAG, "onOptionsItemSelected");
        // Handle all of the possible menu actions.
    	switch (item.getItemId()) {
        case ACCOUNT_ID:
            editAccount();
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
        }
        return super.onOptionsItemSelected(item);
    }
    
    private final void listView() {
    	Log.d(TAG, "listView");
    	Intent i = new Intent(this, geocacheListActivity.class);
    	startActivity(i);
    }
     
    private final void editAccount() {
    	Log.d(TAG, "editAccount");
    	Intent i = new Intent(this, account.class);
    	startActivity(i);
    }
    
    private final void centerMap() {
    	Log.d(TAG, "centerMap");
    	GeoPoint point = new GeoPoint(latitude,longitude);
    	mapController.animateTo(point);
	}
    
    public void onGeocachesResult(locationList mylocationList) {
    	Log.d(TAG, "onGeocachesResult");
    	currentLocationList.clearLocations();
    	currentLocationList = mylocationList;
    	drawGeocaches(currentLocationList);
    	m_ProgressDialog.dismiss();
    }
    
    public void onNetworkError(String error) {
    	Log.d(TAG, "onNetworkError");
    	if (!(m_ProgressDialog == null) && m_ProgressDialog.isShowing()) {
    		m_ProgressDialog.dismiss();
    	}
    	MessageTools.alert("Sorry a network error has occured", context);
    }
    
    private final void getGeocaches() {
    	Log.d(TAG, "getGeocaches");
    	if (!UserPreferences.getLoggedIn(context)) {
    		Toast.makeText(context, "You are not logged in", Toast.LENGTH_LONG).show();
    		return;
    	}
    	if (NetworkTools.gpsEnabled(context)) {
	       m_ProgressDialog = ProgressDialog.show(CurrentLocation.this,    
	                "Please wait...", "Retrieving geocache data ...", true);
	        final Handler mHandler = new Handler();
	    	double parsedLat = latitude;
	    	parsedLat = parsedLat / 1000000;
	    	double parsedLon = longitude;
	    	parsedLon = parsedLon / 1000000;
	    	//get geocache points	
	    	NetworkTools.getLocations(parsedLat, parsedLon, mHandler, context);
    	} else {
	    	enableGPS();
    	}
    }
    
    
    private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
    	Log.d(TAG, "gps2m");
       float pk = (float) (180/3.14169);
       float a1 = lat_a / pk;
       float a2 = lng_a / pk;
       float b1 = lat_b / pk;
       float b2 = lng_b / pk;
       float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
       float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
       float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);
       double tt = Math.acos(t1 + t2 + t3);
       return 6366000*tt;
    }

    private final void drawGeocaches(locationList mylocationList) {
    	Log.d(TAG, "drawGeocaches");
    	 if (mylocationList == null) {
    		 return;
    	 }
        List<Overlay> mapOverlays = mapView.getOverlays();
        mapOverlays.clear();
        //Add geocache markers
        //
        Drawable treasurePin = this.getResources().getDrawable(R.drawable.ruby);
        GeoPoint treasurepoint;
        
    	treasureOverlay = new StaticItemizedOverlay(treasurePin, this); 
    	OverlayItem overlayTreasure;

    	// our location
        GeoPoint point = new GeoPoint(latitude, longitude);
    	
    	int len = mylocationList.length();
    	
    	if (len < 0) {
    		return;
    	}
    	
    	ArrayList<location> localArray = mylocationList.getLocations();
    	location localLocation = new location();
    	    	
    	int myLat = 0;
    	int myLon = 0;
    	double closestTreasureDistance = 99999999;
    	GeoPoint closestPoint = null;
    	
    	for (int i = 0; i < len; i++) {
    		localLocation = localArray.get(i);
    		myLat = (int) (Double.parseDouble(localLocation.getLat())*1E6);
    		myLon = (int) (Double.parseDouble(localLocation.getLon())*1E6);
    		
    		treasurepoint = new GeoPoint(myLat, myLon);
    		double distance = gps2m(treasurepoint.getLatitudeE6(), treasurepoint.getLongitudeE6(), point.getLatitudeE6(), point.getLongitudeE6());
    		if(distance < closestTreasureDistance)
    		{
    			closestTreasureDistance = distance;
    			closestPoint = treasurepoint;
    		}
    			
        	overlayTreasure = new OverlayItem(treasurepoint, localLocation.getName(), "Point: " + treasurepoint);
        	treasureOverlay.addOverlay(overlayTreasure, localLocation);
        	mapOverlays.add(treasureOverlay); // add new marker
         }
    	
    	txt_geocount.setText("Geocache count: " + len);
    	drawMarker(point);
        mapController.setCenter(point);
        
    	//int zoomLevel = 17;
    	if(closestPoint != null){
    	    mapController.zoomToSpan(
    	            (point.getLatitudeE6() > closestPoint.getLatitudeE6()
    	                ? point.getLatitudeE6() - closestPoint.getLatitudeE6()
    	                : closestPoint.getLatitudeE6() - point.getLatitudeE6()),
    	            (point.getLongitudeE6() > closestPoint.getLongitudeE6()
    	                ? point.getLongitudeE6() - closestPoint.getLongitudeE6()
    	                : closestPoint.getLongitudeE6() - point.getLongitudeE6()));
    	            mapController.animateTo(
    	                new GeoPoint(
    	                		point.getLatitudeE6() - ((point.getLatitudeE6() - closestPoint.getLatitudeE6())/2),
    	                    point.getLongitudeE6() - ((point.getLongitudeE6() - closestPoint.getLongitudeE6())/2)
    	                ));

        	//mapController.setZoom(zoomLevel);
    	}
    }
    
    @SuppressWarnings("unused")
	private final void hideMenu() {
    	Log.d(TAG, "hideMenu");
    	
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	Log.v(TAG, "isRouteDisplayed");
    	return false;
    }
    
    private void initCompass() {
    	Log.d(TAG, "initCompass");
    	 mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         List<Sensor> mySensors = mySensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
      
         if(mySensors.size() > 0){
          mySensorManager.registerListener(mySensorEventListener, mySensors.get(0), SensorManager.SENSOR_DELAY_UI);
          sensorrunning = true;
          //Toast.makeText(this, "Start ORIENTATION Sensor", Toast.LENGTH_LONG).show();
        
         }
         else{
          //Toast.makeText(this, "No ORIENTATION Sensor", Toast.LENGTH_LONG).show();
          sensorrunning = false;
         }
    }


    private SensorEventListener mySensorEventListener = new SensorEventListener(){
    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		Log.v(TAG, "onAccuracyChanged");
    		// TODO Auto-generated method stub
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		Log.v(TAG, "onSensorChanged");
    		float  fHeading = (float)event.values[0];
    		int    iHeading = (int) Math.round(fHeading);
    		String sHeading = String.format("%03d", iHeading);

    		int direction = (int) Math.round((iHeading/22.5));
    		if (direction > 15 || direction < 0) {
    			direction = 0;
    		}
        	txt_compass.setText("Heading: "+sHeading+""+degree+" - "+headings[direction]);
    	}
    };
    
	private void drawMarker(GeoPoint point) {
		// dirty hack to stop the thing crashing after reload
		if (mapView == null) {
			return;
		}
		List<Overlay> mapOverlays = mapView.getOverlays();
		int hunterIdx = mapOverlays.indexOf(hunterOverlay);
		if (hunterIdx >= 0 && !mapOverlays.isEmpty()) {
			mapOverlays.remove(hunterIdx);
		}
        Drawable userPin = context.getResources().getDrawable(R.drawable.center_marker_male);
        hunterOverlay = new DynamicItemizedOverlay(userPin, context);
        OverlayItem overlayitem = new OverlayItem(point, "Hello GeoArmy recruit", "Point: " + point);
        hunterOverlay.addOverlay(overlayitem);
        mapOverlays.add(hunterOverlay); // add new marker
	}
    
    public void enableGPS() {
        /** Check if GPS is enabled, if not prompt the user to enable it **/
        if (!NetworkTools.gpsEnabled(context)) {
        	//MessageTools.createGpsDisabledAlert(context);
        	Toast.makeText(context, "GPS is not enabled!", Toast.LENGTH_LONG).show();
        }
    }
    
    public void setGPSPing() {
    	Log.d("setgpsping", "SETTING PINGOID");
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 0, new GeoUpdateHandler());
    }
    
    public class GeoUpdateHandler implements LocationListener {

    	public void onLocationChanged(Location location) {
    		Log.d(TAG, "onLocationChanged");
           	if (location != null) {
    			latitude = (int) (location.getLatitude()*1E6);
    			longitude = (int) (location.getLongitude()*1E6);
    		 
	    		GeoPoint point = new GeoPoint(latitude,longitude);
	
	    		String currentLat = "Lat: " + location.getLatitude();
	    		String currentLng = "Lng: " + location.getLongitude();
	    		txt_lat.setText(currentLat);
	    		txt_lng.setText(currentLng);
	    		drawMarker(point);
	    		//mapController.animateTo(point); always keep hunter in middle of map
    		}
       	}
  	  	
    	public void onProviderDisabled(String provider) {
    		Log.d(TAG, "onProviderDisabled");
    	}
    	
    	public void onProviderEnabled(String provider) {
    		Log.d(TAG, "onProviderEnabled");
    	}
    	
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    		Log.d(TAG, "onStatusChanged");
    	}
    }
}
