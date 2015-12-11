package org.shepherd.breathewell.settings.option;

import android.graphics.Bitmap;

public class PreferenceOption {
	
	private String mTitle;
    private String mValue;
	private int mImageResource;
    private Bitmap mBitmap;
	
	public PreferenceOption(String title){
		mTitle = title;
        mValue = null;
		mImageResource = -1;
        mBitmap = null;
	}

    public PreferenceOption(String title, Bitmap b){
        mTitle = title;
        mValue = null;
        mImageResource = -1;
        mBitmap = b;
    }

    public PreferenceOption(String title,String value){
        mTitle = title;
        mValue = value;
        mImageResource = -1;
        mBitmap = null;
    }
	
	public PreferenceOption(String title, int imageResource){
		mTitle = title;
        mValue = null;
		mImageResource = imageResource;
        mBitmap = null;
	}

    public PreferenceOption(String title, String value, int imageResource){
        mTitle = title;
        mValue = value;
        mImageResource = imageResource;
    }

    public PreferenceOption(String title, String value, Bitmap b){
        mTitle = title;
        mValue = value;
        mImageResource = -1;
        mBitmap = b;
    }

    public String getValue(){
        return mValue;
    }

	public String getTitle(){
		return mTitle;
	}
	
	public int getImageResource(){
		return mImageResource;
	}

    public Bitmap getBitmap(){
        return mBitmap;
    }
	

}
