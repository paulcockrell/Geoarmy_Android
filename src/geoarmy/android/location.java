package geoarmy.android;

public class location {

	private String lat;
	private String lon;
	
	public location() {}
	
	public location(String lat, String lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	public String getLat() {
		return lat;
	}
	
	public void setLon(String lon) {
		this.lon = lon;
	}
	
	public String getLon() {
		return lon;
	}
	
}