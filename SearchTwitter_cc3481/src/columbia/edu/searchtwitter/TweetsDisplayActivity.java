package columbia.edu.searchtwitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TweetsDisplayActivity extends Activity {
	public final static String SEARCH_FILTER_CONTENT = "SEARCH_FILTER_CONTENT";
	public final static String SEARCH_MODE = "SEARCH_MODE";
	private String SearchFilter;
	private int SearchMode;
	private ArrayList<Tweets> TweetsList1;
	private TableLayout TweetsTable; 
	private ArrayList<String> TweetList;
	private ArrayList<String> NameList;
	private ArrayList<String> HandleList;
	private ArrayList<Bitmap> PhotoList;
    DataBaseHelper DbHelper = null;  
    SQLiteDatabase Db = null; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweets_display);
		
		//receiver intent containing location words
		Bundle SearchBundle=getIntent().getExtras();
		SearchFilter = SearchBundle.getString(SEARCH_FILTER_CONTENT);
		this.setTitle("Users in "+SearchFilter);
		SearchMode = SearchBundle.getInt(SEARCH_MODE);
		
		TweetsList1 = new ArrayList<Tweets>();
		TweetsTable = (TableLayout)findViewById(R.id.tablelayout_tweets);
		TweetList = new ArrayList<String>();
		NameList = new ArrayList<String>();
		HandleList = new ArrayList<String>();
		PhotoList = new ArrayList<Bitmap>();
		
		//open or create database
	    DbHelper = DataBaseHelper.getInstance(this);  
	    Db= DbHelper.getReadableDatabase(); 
		
		if (SearchMode == 1) {	//first searching
			GetTweets gt1 = new GetTweets(this);
			gt1.execute(SearchFilter);
		}
		else {	//use cache
			GetCache gc1 = new GetCache(this);
			gc1.execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tweets_display, menu);
		return true;
	}
	
	
	public void parseJSONtoList(String JSONStr) {
		try {
			JSONArray JSONArray1 = new JSONObject(JSONStr).getJSONArray("results");
			for(int i = 0; i < JSONArray1.length() ; i++){ 
				JSONObject JSONObj = ((JSONObject)JSONArray1.opt(i)); 
				String Tweet = JSONObj.getString("text"); 
				String Name = JSONObj.getString("from_user_name"); 
				String Handle = "@"+JSONObj.getString("from_user"); 
				
				//get photos from network
				String PhotoUrl = JSONObj.getString("profile_image_url");
				try {    
		            URL PUrl = new URL(PhotoUrl);    
		        	HttpURLConnection c = (HttpURLConnection) PUrl.openConnection();
		            c.setDoInput(true);    
		            c.connect();    
		            InputStream is = c.getInputStream();    
		            Bitmap Photo = BitmapFactory.decodeStream(is);    
		            is.close();    

		            
		            
		            //display on tablelayout
		            Tweets Tweets1 = new Tweets(Tweet, Name, PhotoUrl, Handle, SearchFilter);
		            TweetsList1.add(Tweets1);
		            TweetList.add(Tweet);
		            NameList.add(Name);
		            HandleList.add(Handle);
		            PhotoList.add(Photo);
		        } catch (IOException e) {    
		              e.printStackTrace();    
		        }   
				
				
				
			} 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
				


	}
	
    private void addRow() {  

        for (int i=0; i<TweetList.size(); i++) {
            TableRow tableRow = new TableRow(this);  
            TextView TweetsView = new TextView(this);
            TextView NameView = new TextView(this);
            ImageView PhotoView = new ImageView(this);
            TextView HandleView = new TextView(this);
	        TweetsView.setText(TweetList.get(i));
	        NameView.setText(NameList.get(i));
	        HandleView.setText(HandleList.get(i));
	        PhotoView.setImageBitmap(PhotoList.get(i));

	        tableRow.addView(PhotoView);
	        tableRow.addView(NameView);
	        tableRow.addView(HandleView);
	        tableRow.addView(TweetsView);

	        TweetsTable.addView(tableRow);   
        }
    }  //end method
  

	
	//thread to get tweets
	class GetTweets extends AsyncTask<String, Integer, String>{
		private ProgressDialog mdialog; 
		private Context mcontext;
		
		public GetTweets(Context context){  
		        mcontext = context;  
		}  
		
	    protected void onPreExecute() {  
	        mdialog = new ProgressDialog(mcontext);  
	        mdialog.setTitle("Please wait...");  
	        mdialog.setMessage("Loading...");  
	        mdialog.show();  
	        super.onPreExecute();  
	    }  

		@Override
		protected String doInBackground(String... params) {
			//get place id
			String NoSpace = SearchFilter.replaceAll(" ", "%3A");
			String PIDURL = "http://api.twitter.com/1/geo/search.json?query="+NoSpace+"&max_results=1";
			HttpClient client1 = new DefaultHttpClient(); 
			HttpGet Req = new HttpGet(PIDURL);  
	        HttpResponse Response1 = null;
	        String ResponseStr = "";
	        String PID = "";
	        
	        try {
				Response1 = client1.execute(Req);
				if(Response1.getStatusLine().getStatusCode()==200){ 
					ResponseStr = EntityUtils.toString(Response1.getEntity());
				}
			} catch (ClientProtocolException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	        JSONObject JSONPID;
			try {
				JSONPID = new JSONObject(ResponseStr);
				JSONArray JArray = new JSONObject(JSONPID.getString("result")).getJSONArray("places");
				PID = ((JSONObject)JArray.opt(0)).getString("id");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
			
			//get Tweets by location
			String GETURL = "http://search.twitter.com/search.json?q=place:"+PID+"&result_type=mixed"; 
			
			//send GET request
			Req = new HttpGet(GETURL);  
			try {
				Response1 = client1.execute(Req);
				
		        //receive GET
		        if(Response1.getStatusLine().getStatusCode()==200){  
		        	ResponseStr = EntityUtils.toString(Response1.getEntity());
		        	parseJSONtoList(ResponseStr);
		        }
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

	    }//end method
		
	    @Override
	    protected void onPostExecute(String result) {
	    	TweetsDisplayActivity.this.addRow();
	        mdialog.dismiss(); 
	        for (int i=0; i<TweetsList1.size(); i++) {
	        	DbHelper.saveTweets(TweetsList1.get(i));
	        }
	        DbHelper.close();
	        super.onPostExecute(result);
	    }
	}//end async task
	
	//thread to get cache
	class GetCache extends AsyncTask<String, Integer, String>{
		private ProgressDialog mdialog; 
		private Context mcontext;
		
		public GetCache(Context context){  
		        mcontext = context;  
		}  
		
	    protected void onPreExecute() {  
	        mdialog = new ProgressDialog(mcontext);  
	        mdialog.setTitle("Please wait...");  
	        mdialog.setMessage("Loading...");  
	        mdialog.show();  
	        super.onPreExecute();  
	    }  

		@Override
		protected String doInBackground(String... params) {
			TweetsList1 = DbHelper.getFilteredTweets(SearchFilter);
			DbHelper.close();
			
			for (int i=0; i<TweetsList1.size(); i++) {
				TweetList.add(TweetsList1.get(i).getTweetText());
	            NameList.add(TweetsList1.get(i).getUser());
	            HandleList.add(TweetsList1.get(i).getHandle());
	            
	            String PhotoUrl = TweetsList1.get(i).getPhotoUrl();
	            try {    
		            URL PUrl = new URL(PhotoUrl);    
		        	HttpURLConnection c = (HttpURLConnection) PUrl.openConnection();
		            c.setDoInput(true);    
		            c.connect();    
		            InputStream is = c.getInputStream();    
		            Bitmap Photo = BitmapFactory.decodeStream(is);    
		            is.close();    
		            PhotoList.add(Photo);
		        } catch (IOException e) {    
		              e.printStackTrace();    
		        }  
			}//end if
			
			return null;
	    }//end method
		
	    @Override
	    protected void onPostExecute(String result) {
	    	TweetsDisplayActivity.this.addRow();
	        mdialog.dismiss(); 
	        super.onPostExecute(result);
	    }
	}//end async task



}//end activity

