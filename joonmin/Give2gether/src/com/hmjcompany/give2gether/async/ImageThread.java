package com.hmjcompany.give2gether.async;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageThread extends AsyncTask<String, Void, Bitmap> {

	Bitmap bmp;
	
	protected Bitmap doInBackground(String... params) {
		try {
			URL url = new URL(params[0]);
			bmp = BitmapFactory.decodeStream(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bmp;
	}

}
