package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.os.Environment;
//import android.hardware.Usb;
import android.util.Log;

public class MrioUSBTestActivity extends Activity {
	private static final String TAG = "USBConnectPCTestActivity";
	private BroadcastReceiver mUsbStateReceiver;
	TextView mUsbPluginText;
	TextView mUsbUnplugText;
	boolean pluginPass = false;
	boolean unplugPass = false;
	boolean stop = false;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "=========onCreate============");
		super.onCreate(savedInstanceState);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.mriousbtest);
		ControlButtonUtil.initControlButtonView(this);

		mUsbStateReceiver = new UsbConnectedBroadcastReceiver();

		mUsbPluginText = (TextView) findViewById(R.id.pluginTest);

		mUsbUnplugText = (TextView) findViewById(R.id.unplugTest);
		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
	}

	
	protected void onResume() {
		super.onResume();
		stop = false;
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_UMS_CONNECTED);
		intentFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);
		registerReceiver(mUsbStateReceiver, intentFilter);
	}

	
	public void onPause() {
		super.onPause();
		stop = true;
		unregisterReceiver(mUsbStateReceiver);
	}

	class UsbConnectedBroadcastReceiver extends BroadcastReceiver {
		
		public void onReceive(Context context, Intent intent) {
			if(stop) {
				return;
			}
			if (intent.getAction().equals(Intent.ACTION_UMS_CONNECTED)) {
				if(!pluginPass) {
					pluginPass = true;
					mUsbPluginText.setText(mUsbPluginText.getText() + "Pass");
				}
			} else if (intent.getAction()
					.equals(Intent.ACTION_UMS_DISCONNECTED)) {
				if(!unplugPass) {
					unplugPass = true;
					mUsbUnplugText.setText(mUsbUnplugText.getText() + "Pass");
				}
			}
			if (pluginPass && unplugPass) {
				findViewById(R.id.btn_Pass).performClick();
			}
		}
	};

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}
