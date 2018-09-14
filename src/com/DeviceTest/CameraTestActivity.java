package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.util.Log;

public class CameraTestActivity extends Activity {
	private static final int mRequestCode = 1000;
	private boolean isCanUse = true;
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.cameratest);

		ControlButtonUtil.initControlButtonView(this);
		isCameraCanUse();
		if(!isCanUse)
		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
	}
	    public  boolean isCameraCanUse() {  
		        Camera mCamera = null;  
		       try {  
		           mCamera = Camera.open();  
		           if(null == mCamera) isCanUse = false;
		      } catch (Exception e) {  
		    	  isCanUse = false;  
		      }  
		      
		        if (mCamera != null && isCanUse) {  
		           mCamera.release();  
		            mCamera = null;  
		        }  
		        return isCanUse;  
		   }  

	
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		if (paramMotionEvent.getAction() == MotionEvent.ACTION_DOWN && isCanUse) {
			Intent localIntent = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			startActivityIfNeeded(localIntent, 1000);
		}
		return super.onTouchEvent(paramMotionEvent);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}