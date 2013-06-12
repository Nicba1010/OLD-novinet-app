package com.uraniumdevs.novinet;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * Note that here we are inheriting ListActivity class instead of Activity class
 **/
public class AppListActivity extends ListActivity {

	/** Items entered by the user is stored in this ArrayList variable */
	ArrayList<String> list = new ArrayList<String>();

	/** Declaring an ArrayAdapter to set items to ListView */
	ArrayAdapter<String> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_listviewexampleactivity);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
		list.add("AFFAS");
		adapter.notifyDataSetChanged();
		setListAdapter(adapter);
	}
	
}