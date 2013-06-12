package com.uraniumdevs.novinet;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class Izbornik extends ListActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_izbornik);
		int inti=0;
		Intent PostList = new Intent(this, PostList.class);
		PostList.putExtra("Target", inti);
		final int result = 1;
		startActivityForResult(PostList, result);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.izbornik, menu);
		return true;
	}
}