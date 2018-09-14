package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.*;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.IPowerManager;
import android.os.ServiceManager;
public class BrightnessTestActivity extends Activity {
	private static final String TAG = BrightnessTestActivity.class
			.getSimpleName();
	static final int MAXIMUM_BRIGHTNESS = 255;
	static final int MINIMUM_BRIGHTNESS = 2;
	static final int MSG_TEST_BRIGHTNESS = 0;
	 private int mCurBrightness = -1;
	static final int ONE_STAGE = 2;
	MyHandler mHandler;
	TextView mText;
	TextView mTitle;
	TextView progressText;
	int mBrightness = 30;
	boolean increase = true;
	private static final int SEEK_BAR_RANGE = 10000;
	 private static final int MAXIMUM_BACKLIGHT = android.os.PowerManager.BRIGHTNESS_ON;
	 private float oldScreenBrightness;
	 private int mScreenBrightnessDim = 20;
	public BrightnessTestActivity() {
		mHandler = new MyHandler();

	}

	ProgressBar progressBar;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.brightnesstest);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS)
				+ ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		progressBar = (ProgressBar) findViewById(R.id.brightnessBar);
		progressBar.setClickable(false);
		progressBar.setMax(MAXIMUM_BACKLIGHT);
		//mSeekBar.setMax(SEEK_BAR_RANGE);
		progressText = (TextView) findViewById(R.id.progressText);
		ControlButtonUtil.initControlButtonView(this);
         WindowManager.LayoutParams lp = getWindow().getAttributes();
         oldScreenBrightness =  lp.screenBrightness ;
     	Log.d(TAG,oldScreenBrightness+ " _____________________- onPause()");
	}

	protected void onResume() {
		super.onResume();
		this.mHandler.sendEmptyMessage(MSG_TEST_BRIGHTNESS);
	}

	protected void onPause() {
		super.onPause();
		Log.d(TAG, " _____________________- onPause()");
		this.mHandler.removeMessages(MSG_TEST_BRIGHTNESS);
	}
	@Override
	protected void onDestroy() {
		try {
            IPowerManager power = IPowerManager.Stub.asInterface(
                    ServiceManager.getService("power"));
            if (power != null) {
                power.setTemporaryScreenBrightnessSettingOverride(140);
            }
        } catch (RemoteException doe) {
        }
		Log.d(TAG, oldScreenBrightness +" _____________________- onDestroy()");
		super.onDestroy();
	}
	private void setBrightness(int paramInt) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		
		/*float brightness = 0;
		
		int range = (MAXIMUM_BACKLIGHT - mScreenBrightnessDim);
        brightness = (paramInt*range)/SEEK_BAR_RANGE + mScreenBrightnessDim;
        mCurBrightness =(int) brightness;*/
		float brightness = (float) paramInt / MAXIMUM_BRIGHTNESS;
		lp.screenBrightness = (float) paramInt / MAXIMUM_BRIGHTNESS;
		System.out.println(mBrightness+"-------------------------------------------"+brightness);
		getWindow().setAttributes(lp);
	}

	class MyHandler extends Handler {
		MyHandler() {
		}

		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			int delay = 25;
			if (msg.what == MSG_TEST_BRIGHTNESS) {
				if (increase) {
					mBrightness += ONE_STAGE;
					if (mBrightness >= MAXIMUM_BRIGHTNESS) {
						mBrightness = MAXIMUM_BRIGHTNESS;
						increase = false;
						delay = 500;
					}
				} else {
					mBrightness -= ONE_STAGE;
					if (mBrightness <= MINIMUM_BRIGHTNESS) {
						mBrightness = MINIMUM_BRIGHTNESS;
						increase = true;
						delay = 500;
					}
				}
			//	System.out.println("==========================================="+mBrightness);
                try {
                    IPowerManager power = IPowerManager.Stub.asInterface(
                            ServiceManager.getService("power"));
                    if (power != null) {
                        power.setTemporaryScreenBrightnessSettingOverride(mBrightness);
                    }
                } catch (RemoteException doe) {
                }
				float brightness = mBrightness*100;
				
		         brightness = (brightness - mScreenBrightnessDim)
		                / (MAXIMUM_BACKLIGHT - mScreenBrightnessDim);
		     
		         brightness =(int)(brightness*SEEK_BAR_RANGE);
		         //System.out.println("------------SEEK_BAR_RANGE-------------------"+mBrightness);
				progressBar.setProgress(mBrightness);
				progressText.setText(mBrightness + "/255");
				//setBrightness(mBrightness);
				
				sendEmptyMessageDelayed(MSG_TEST_BRIGHTNESS, delay);

			}
		}

	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}
