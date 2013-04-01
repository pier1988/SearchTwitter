package columbia.edu.searchtwitter;

import android.graphics.Bitmap;

public class Tweets {
	
	private int TweetID;
	private String TweetText;
	private String User;
	private String PhotoUrl;
	private String Handle;
	private String Location;
	
	public Tweets() {
		TweetID = 0;
		TweetText = "";
		User = "";
		PhotoUrl = "";
		Handle = "";
		Location = "";
	}
	
	public Tweets(String tt, String u, String p, String h, String location) {
		TweetID = 0;
		TweetText = tt;
		User = u;
		PhotoUrl = p;
		Handle = h;
		Location = location;
	}
	
	public void setTweetID(int i) {
		TweetID = i;
	}
	
	public void setTweetText(String s) {
		TweetText = s;
	}
	
	public String getTweetText() {
		return TweetText;
	}
	
	public void setUser(String s) {
		User = s;
	}
	
	public String getUser() {
		return User;
	}
	
	public void setPhotoUrl(String s) {
		PhotoUrl = s;
	}
	
	public String getPhotoUrl() {
		return PhotoUrl;
	}
	
	public void setHandle(String s) {
		Handle = s;
	}
	
	public String getHandle() {
		return Handle;
	}
	
	public void setLocation(String s) {
		Location = s;
	}
	
	public String getLocation() {
		return Location;
	}
	
	


}
