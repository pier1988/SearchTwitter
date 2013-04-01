package columbia.edu.searchtwitter;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class SearchTwitter extends Activity implements OnClickListener {
	private Button ButtonSearch;
	private EditText SearchFilter;
	public final static String SEARCH_FILTER_CONTENT = "SEARCH_FILTER_CONTENT";
	public final static String SEARCH_MODE = "SEARCH_MODE";
	private ArrayList<String> History;
    DataBaseHelper DbHelper = null;  
    SQLiteDatabase Db = null; 
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_twitter);
		ButtonSearch = (Button) this.findViewById(R.id.Button_Search);
		SearchFilter = (EditText)findViewById(R.id.EditText_SearchFilter);
		History = new ArrayList<String>();
	    DbHelper = DataBaseHelper.getInstance(this);  
	    Db= DbHelper.getReadableDatabase(); 
	    DbHelper.dropTable(Db); //clear the old cache
		
		ButtonSearch.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_twitter, menu);
		return true;
	}
	
	@Override
    protected void onPause() {
		DbHelper.close();
        super.onPause();
    }
	
	@Override
    protected void onDestroy() {
		DbHelper.close();
        super.onDestroy();
    }
	
    @Override
    protected void onResume() {
        super.onResume();
	    DbHelper = DataBaseHelper.getInstance(this);  
	    Db= DbHelper.getReadableDatabase(); 
    }

	
	@Override
	public void onClick(View v) {
		Intent SearchIntent = new Intent(SearchTwitter.this, TweetsDisplayActivity.class);
		Bundle SearchBundle = new Bundle();
		
		//get user input from edittext and send as an intent
		String SearchString = SearchFilter.getText().toString();
		
		if (History.contains(SearchString)) {
			SearchBundle.putInt(SEARCH_MODE, 0);
		}//end if
		else {
			History.add(SearchString);
			SearchBundle.putInt(SEARCH_MODE, 1);
		} //end else
		SearchBundle.putString(SEARCH_FILTER_CONTENT, SearchString);
		SearchIntent.putExtras(SearchBundle);
		startActivity(SearchIntent);
	}

}
