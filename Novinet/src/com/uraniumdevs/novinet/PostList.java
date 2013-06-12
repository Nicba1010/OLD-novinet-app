package com.uraniumdevs.novinet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PostList extends ListActivity {

	ArrayList<String> list = new ArrayList<String>();
	ArrayList<Integer> ids = new ArrayList<Integer>();
	ArrayAdapter<String> adapter;
	public int page;
	ProgressDialog dialog;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		setContentView(R.layout.activity_post_list);

		Intent sender = getIntent();
		page = sender.getExtras().getInt("page");
		Toast.makeText(getApplicationContext(),
				"Stranica " + Integer.toString(page + 1), Toast.LENGTH_LONG)
				.show();
		dialog = ProgressDialog.show(PostList.this,
                "Uèitavanje èlanaka", "Molim vas prièekajte ...", true, true);
		new PostListTask().execute("");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(this, Integer.toString(ids.get(position)),
				Toast.LENGTH_SHORT).show();
		Intent PostList = new Intent(this, PostDisplayActivity.class);
		PostList.putExtra("Target", ids.get(position));
		final int result = 1;
		startActivityForResult(PostList, result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.post_list, menu);
		if (page == 0) {
			menu.getItem(0).setEnabled(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent PageSet = new Intent(this, PostList.class);
		switch (item.getItemId()) {
		case R.id.next:
			page++;
			PageSet.putExtra("page", page);
			final int result = 1;
			startActivityForResult(PageSet, result);
			finish();
			return true;
		case R.id.prev:
			page--;
			PageSet.putExtra("page", page);
			final int result1 = 1;
			startActivityForResult(PageSet, result1);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class PostListTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			vars.listPost = listPost();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				JSONArray jsonArray = new JSONArray(vars.listPost);
				Log.i(PostDisplayActivity.class.getName(), "Number of entries "
						+ jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String title = new String(jsonObject
							.getString("post_title").getBytes("ISO-8859-1"),
							"UTF-8");
					int ID = new Integer(jsonObject.getInt("ID"));
					list.add(title);
					ids.add(ID);
					adapter.notifyDataSetChanged();
					setListAdapter(adapter);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

		public String listPost() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(
					"http://novinet.in/get_posts.php?mode=1&page=" + page);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					Log.e(PostDisplayActivity.class.toString(),
							"Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return builder.toString();
		}
	}
}