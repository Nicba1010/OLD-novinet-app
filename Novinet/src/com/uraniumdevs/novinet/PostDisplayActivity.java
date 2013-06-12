package com.uraniumdevs.novinet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostDisplayActivity extends Activity {
	int id;
	TextView posttext, topbar;
	ProgressDialog dialog;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_post_display);
		// Init vars
		topbar = (TextView) findViewById(R.id.topbar);
		Intent sender = getIntent();
		id = sender.getExtras().getInt("Target");
		// Listeners
		topbar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		dialog = ProgressDialog.show(PostDisplayActivity.this,
                "Učitavanje članka", "Molim vas pričekajte ...", true, true);
		new PostReadTask().execute("");
	}

	public void addText(String text) {
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linlay1);
		TextView textview = new TextView(PostDisplayActivity.this);
		textview.setText(text);
		linearLayout1.addView(textview);
	}

	public void addImage(final String url) {
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linlay1);
		ImageView image = new ImageView(PostDisplayActivity.this);
		image.setAdjustViewBounds(true);
		try {
			Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					url).getContent());
			image.setImageBitmap(bitmap);
			linearLayout1.addView(image);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class PostReadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			vars.readPost = readPost();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				JSONArray jsonArray = new JSONArray(vars.readPost);
				Log.i(PostDisplayActivity.class.getName(), "Number of entries "
						+ jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String post_content = new String(jsonObject.getString(
							"post_content").getBytes("ISO-8859-1"), "UTF-8");
					String post_title = new String(jsonObject
							.getString("post_title").getBytes("ISO-8859-1"),
							"UTF-8");
					setTitle(post_title);
					post_content = removetags(post_content);
					String[] string3, string5, string6;
					String string4, videoID;
					String[] string2 = post_content.split("<img");
					for (int i1 = 0; i1 < string2.length; i1++) {
						string3 = string2[i1].split("/>");
						for (int i2 = 0; i2 < string3.length; i2++) {
							string4 = (string3[i2]);
							if (string4.contains("src")) {
								if (string4.contains(".jpg")
										|| string4.contains(".gif")
										|| string4.contains(".png")
										|| string4.contains(".bmp")
										|| string4.contains(".jpeg")) {
									addImage((string4.replace("src=\"", ""))
											.replace("\"", ""));
								} else if (string4.contains("youtube")) {
									string5 = string4.split("<iframe");
									for (int i3 = 0; i3 < string5.length; i3++) {
										string6 = (string5[i3].toString())
												.split("></iframe>");
										for (int i4 = 0; i4 < string6.length; i4++) {
											if (string6[i4]
													.contains("youtube.com/embed")) {
												videoID = ((((string6[i4].replace(
														"src=", ""))
														.replace(
																"http://www.youtube.com/embed/",
																"")).replace("\"",
														"")).replace(" ", ""));
												addYtImage(videoID);
											} else {
												addText(string6[i4]);
											}
										}
									}
								} else {
									addText(string4);
								}
							} else {
								addText(string4);
							}
						}
					}
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

		public String readPost() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(
					"http://novinet.in/get_posts.php?mode=0&id=" + id);
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

	public void addYtImage(final String id) {
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linlay1);
		ImageView image = new ImageView(PostDisplayActivity.this);
		image.setAdjustViewBounds(true);
		try {
			Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					"http://img.youtube.com/vi/" + id + "/hqdefault.jpg")
					.getContent());
			image.setImageBitmap(bitmap);
			image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://www.youtube.com/watch?v=" + id)));
				}
			});
			linearLayout1.addView(image);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String removetags(String post_content) {
		post_content = post_content.replaceAll("<a[^>]*>", "");
		post_content = post_content.replaceAll("</a>", "");
		post_content = post_content.replaceAll("<!--more-->", "");
		post_content = post_content.replaceAll("class=\"[^\"]*\"", "");
		post_content = post_content.replaceAll("alt=\"[^\"]*\"", "");
		post_content = post_content.replaceAll("width=\"[^\"]*\"", "");
		post_content = post_content.replaceAll("height=\"[^\"]*\"", "");
		post_content = post_content
				.replaceAll("allowfullscreen=\"[^\"]*\"", "");
		post_content = post_content.replaceAll("frameborder=\"[^\"]*\"", "");
		post_content = post_content.replaceAll("<p>", "\n");
		post_content = post_content.replaceAll("</p>", "");
		post_content = post_content.replaceAll("<em>", "");
		post_content = post_content.replaceAll("</em>", "");
		post_content = post_content.replaceAll("<strong>", "");
		post_content = post_content.replaceAll("</strong>", "");
		post_content = post_content.replaceAll("<h1>", "\n");
		post_content = post_content.replaceAll("</h1>", "\n");
		post_content = post_content.replaceAll("<h2>", "\n");
		post_content = post_content.replaceAll("</h2>", "\n");
		post_content = post_content.replaceAll("<h3>", "\n");
		post_content = post_content.replaceAll("</h3>", "\n");
		post_content = post_content.replaceAll("<h4>", "\n");
		post_content = post_content.replaceAll("</h4>", "\n");
		post_content = post_content.replaceAll("<h5>", "\n");
		post_content = post_content.replaceAll("</h5>", "\n");
		post_content = post_content.replaceAll("<h6>", "\n");
		post_content = post_content.replaceAll("</h6>", "\n");
		post_content = post_content.replaceAll("&nbsp;", "");
		return post_content;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_display, menu);
		return true;
	}

}
