package GemhDailyDb;

public class LatLongInfo {

	private String latitude = null; 						//The latitude coordinate
	private String longitude = null;						//The longitude coordinate
	private boolean found = false;							//True if found, false if not
	
	public LatLongInfo(String latitude,String longitude, boolean found) {
		
		this.latitude = latitude;
		this.longitude = longitude;
		this.found = found;
		
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public boolean getFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}
	
}