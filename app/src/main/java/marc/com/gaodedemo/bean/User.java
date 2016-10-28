package marc.com.gaodedemo.bean;

/**
 * Created by Broderick on 2016/10/28.
 */

public class User {
	String latitude;
	String longitude;

	public User(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
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
}
