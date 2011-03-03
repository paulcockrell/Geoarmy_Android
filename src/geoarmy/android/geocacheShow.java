package geoarmy.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class geocacheShow extends Activity {
	TextView geocacheId, geocacheName, geocacheLat, geocacheLon, geocacheNotes;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
        geocacheId = (TextView) findViewById(R.id.geocacheId);
        geocacheName = (TextView) findViewById(R.id.geocacheName);
        geocacheLat = (TextView) findViewById(R.id.geocacheLat);
        geocacheLon = (TextView) findViewById(R.id.geocacheLon);
        geocacheNotes = (TextView) findViewById(R.id.geocacheNotes);
		Bundle bundle = getIntent().getExtras();        
		String gId = bundle.getString("gId");
		String gName = bundle.getString("gName");
		String gLat = bundle.getString("gLat");
		String gLon = bundle.getString("gLon");
		String gNotes = bundle.getString("gNotes");
		this.geocacheId.setText("ID: " + gId);
		this.geocacheName.setText("Name: " + gName);
		this.geocacheLat.setText("Lat: " + gLat);
		this.geocacheLon.setText("Lon: " + gLon);
		this.geocacheNotes.setText("Lon: " + gNotes);
	}
}