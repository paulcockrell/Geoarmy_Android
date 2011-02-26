package geoarmy.android;

import java.util.ArrayList;

import geoarmy.android.location;

public class locationList {
	
	private ArrayList<location> locations = new ArrayList<location>();
	
	public locationList() {
		
	}
	
	public ArrayList<location> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<location> locations) {
		this.locations = locations;
	}
	
	public void addLocation(location location) {
		this.locations.add(location);
	}
	
	public int length() {
		return this.locations.size();
	}
	
}