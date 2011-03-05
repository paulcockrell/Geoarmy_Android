package geoarmy.android;

public class location {

	private Integer id;
	private String name;
	private String lat;
	private String lon;
	private String notes;
	private String owner;
	private String status;
	private String geo_type;
	private String terrain;
	private String difficulty;
	private String size;
	private boolean found;
	private boolean favorite;
	
	public location() {}
	
	public location(Integer id, String name, String lat, String lon, String notes
			, String owner, String status, String geo_type, String terrain, String difficulty, String size,
			boolean found, boolean favorite) {
		this.id  = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.notes = notes;
		this.owner = owner;
		this.status = status;
		this.geo_type = geo_type;
		this.terrain = terrain;
		this.difficulty = difficulty;
		this.size = size;
		this.found = found;
		this.favorite = favorite;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setGeoType(String geo_type) {
		this.geo_type = geo_type;
	}
	
	public String getGeoType() {
		return geo_type;
	}
	
	public void setTerrain(String terrain) {
		this.terrain = terrain;
	}
	
	public String getTerrain() {
		return terrain;
	}
	
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	
	public String getDifficulty() {
		return difficulty;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getSize() {
		return size;
	}
	
	public void setFound(boolean found) {
		this.found = found;
	}
	
	public boolean getFound() {
		return found;
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	public boolean getFavorite() {
		return favorite;
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