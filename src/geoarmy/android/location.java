package geoarmy.android;

public class location {

	private Integer id;
	private String name;
	private String lat;
	private String lon;
	
	public location() {}
	
	public location(Integer id, String name, String lat, String lon) {
		this.id  = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
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