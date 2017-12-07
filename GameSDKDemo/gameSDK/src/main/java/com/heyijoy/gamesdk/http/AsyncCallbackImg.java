package com.heyijoy.gamesdk.http;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class AsyncCallbackImg implements AsyncImageLoader.ImageCallback{
	private ImageView imageView ;
	
	public AsyncCallbackImg(ImageView imageView) {
		super();
		this.imageView = imageView;
	}

	@Override
	public void imageLoaded(Drawable imageDrawable) {
		imageView.setImageDrawable(imageDrawable);
	}

}

