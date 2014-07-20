package com.hmjcompany.give2gether.async;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.giv2gether.R;

public class ImageViewThread extends AsyncTask<String, Void, Bitmap> {

	Bitmap bmp;
	ImageView image;
	
	public ImageViewThread(ImageView image) {
		this.image = image;
	}
	
	protected Bitmap doInBackground(String... params) {
		try {
			URL url = new URL(params[0]);
			bmp = BitmapFactory.decodeStream(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if(result != null) {
			image.setImageBitmap(result);
		} else {
			image.setImageResource(R.drawable.image_loading);
		}
	}

}
