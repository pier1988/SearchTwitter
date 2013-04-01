package columbia.edu.searchtwitter;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
@Athresh

**/
public class DataBaseHelper extends SQLiteOpenHelper {

	private static DataBaseHelper mInstance = null;
	static final String dbName = "TweetsDB";
	static final String TweetsTable = "tTweets";
	static final String colID = "TweetsID";
	static final String colTweetText = "TweetText";
	static final String colUser = "User";
	static final String colPhoteUrl = "PhoteUrl";
	static final String colHandle = "Handle";
	static final String colLocation = "Location";

	public DataBaseHelper(Context context) {
		super(context, dbName, null, 33);
	}
	
    static synchronized DataBaseHelper getInstance(Context context) {  
    	if (mInstance == null) {  
    		mInstance = new DataBaseHelper(context);  
    	}  
    	return mInstance;  
    }  

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + TweetsTable + " (" + colID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + colTweetText
				+ " TEXT, " + colUser + " TEXT, " + colPhoteUrl + " TEXT, "
				+ colHandle + " TEXT, " + colLocation + " TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TweetsTable);
		onCreate(db);
	}
	
	public void dropTable(SQLiteDatabase db) {

		db.execSQL("DROP TABLE IF EXISTS " + TweetsTable);
		onCreate(db);
	}

	void saveTweets(Tweets newTweets) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(colTweetText, newTweets.getTweetText());
		cv.put(colUser, newTweets.getUser());
		cv.put(colPhoteUrl, newTweets.getPhotoUrl());
		cv.put(colHandle, newTweets.getHandle());
		cv.put(colLocation, newTweets.getLocation());

		db.insert(TweetsTable, colHandle, cv);
		db.close();

	}

	ArrayList<Tweets> getAllTweets() {
		ArrayList<Tweets> allTweets = new ArrayList<Tweets>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM " + TweetsTable, null);

		cur.moveToFirst();
		while (!cur.isAfterLast()) {
			Tweets Tweets = cursorToTweets(cur);
			allTweets.add(Tweets);
			cur.moveToNext();
		}

		cur.close();
		return allTweets;

	}

	ArrayList<Tweets> getFilteredTweets(String filter) {
		ArrayList<Tweets> allTweets = new ArrayList<Tweets>();

		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TweetsTable + " Where "
				+ colLocation + " like '%" + filter + "%'";

		Cursor cur = db.rawQuery(query, null);

		cur.moveToFirst();
		while (!cur.isAfterLast()) {
			Tweets Tweets = cursorToTweets(cur);
			allTweets.add(Tweets);
			cur.moveToNext();
		}

		cur.close();
		return allTweets;

	}

	private Tweets cursorToTweets(Cursor cursor) {
		Tweets Tweets = new Tweets();
		Tweets.setTweetID(cursor.getInt(0));
		Tweets.setTweetText(cursor.getString(1));
		Tweets.setUser(cursor.getString(2));
		Tweets.setPhotoUrl(cursor.getString(3));
		Tweets.setHandle(cursor.getString(4));
		Tweets.setLocation(cursor.getString(5));
		return Tweets;
	}

	void deletTweets(int TweetsID) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TweetsTable, colID + "=?",
				new String[] { String.valueOf(TweetsID) });
		db.close();
	}

}
