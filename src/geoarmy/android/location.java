package geoarmy.android;

public class location {

	private Integer id;
	private String name;
	private String lat;
	private String lon;
	private String notes;
	
	public location() {}
	
	public location(Integer id, String name, String lat, String lon, String notes) {
		this.id  = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.notes = notes;
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

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getNotes() {
		return notes;
	}
	
}