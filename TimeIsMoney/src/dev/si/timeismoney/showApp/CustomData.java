package dev.si.timeismoney.showApp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class CustomData {
	private Drawable imageData_;
	private String textData_;
	private String packageName_;

	public void setImagaData(Drawable icon) {
		imageData_ = icon;
	}

	public Drawable getImageData() {
		return imageData_;
	}

	public void setTextData(String text) {
		textData_ = text;
	}

	public String getTextData() {
		return textData_;
	}
	
	public void setPackageName(String packageName) {
		packageName_ = packageName; 
	}
	
	public String getPackageName() {
		return packageName_;
	}
}
