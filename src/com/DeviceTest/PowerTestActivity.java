package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.SystemUtil;
import com.rockchip.newton.UserModeManager;

import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PowerTestActivity extends Activity {
	TextView mBattery;
	private BroadcastReceiver mBatteryInfoReceiver;
	TextView mChargeStatus;
	TextView mVoltage;
	TextView mCurrent;
	TextView mCapacity;
	TextView mPlug;
	private static final String CURRENT_PATH = "/sys/class/power_supply/*battery/current_now";

	boolean stop = false;

	int mLastVoltage = -1;

	TextView pluginView;
	TextView unplugView;
	boolean pluginPass = false;
	boolean unplugPass = false;

	public PowerTestActivity() {

		this.mBatteryInfoReceiver = new MyBroadcastReceiver();

	}

	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.powertest);
		ControlButtonUtil.initControlButtonView(this);

		this.mChargeStatus = (TextView) findViewById(R.id.chargeStatusText);
		this.mVoltage = (TextView) findViewById(R.id.voltageText);
		this.mCurrent = (TextView) findViewById(R.id.currentText);
		this.mCapacity = (TextView) findViewById(R.id.capacityText);
		this.mPlug = (TextView) findViewById(R.id.plugText);

//		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
		pluginView = (TextView) findViewById(R.id.pluginTest);
		unplugView = (TextView) findViewById(R.id.unplugTest);
	}

	
	protected void onResume() {
		super.onResume();
		stop = false;
		IntentFilter localIntentFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(this.mBatteryInfoReceiver, localIntentFilter);
	}

	
	public void onPause() {
		super.onPause();
		stop = true;
		BroadcastReceiver localBroadcastReceiver = this.mBatteryInfoReceiver;
		unregisterReceiver(localBroadcastReceiver);

	}

	class MyBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context paramContext, Intent intent) {
			if (stop) {
				return;
			}
			String action = intent.getAction();

			if (!Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				return;

			}

			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);

			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);

			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
			Log.e("Jeffy", "plugged:" + plugged);
			// int current = -1;
			// try {
			// DataInputStream dis = new DataInputStream(new FileInputStream(
			// CURRENT_PATH));
			// current = Integer.parseInt(dis.readLine());
			// dis.close();
			// } catch (FileNotFoundException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			int current = -1;
			try {
				String currentStr = SystemUtil.execScriptCmd("cat "
						+ CURRENT_PATH, DeviceTest.TEMP_FILE_PATH, true);
				if (currentStr.length() > 0) {
					current = Integer.parseInt(currentStr);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			String statusString = "";

			switch (status) {
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				statusString = "Unknown";
				break;
			case BatteryManager.BATTERY_STATUS_CHARGING:
				statusString = "Charging";

				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				statusString = "Discharging";
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				statusString = "Not Charging";
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				statusString = "Full";
				break;
			}
			mChargeStatus.setText(getString(R.string.ChargeState) + statusString);

			mVoltage.setText(getString(R.string.Voltage) + voltage + "mV");

			if (current != -1) {
				mCurrent.setText("Current:" + (current / 1000) + "mA");
			} else {
				mCurrent.setVisibility(View.GONE);
			}

			mCapacity.setText(getString(R.string.Capacity) + (level * 100 / scale) + "%");

			boolean acPlugin = false;
			String pluggedStr = "";
			switch (plugged) {
			case BatteryManager.BATTERY_PLUGGED_AC:
				acPlugin = true;
				pluggedStr = "AC";
				break;
			case BatteryManager.BATTERY_PLUGGED_USB:
				pluggedStr = "USB";
				break;

			default:
				pluggedStr = "Unplugged";
				break;
			}
			mPlug.setText(getString(R.string.Plug) + pluggedStr);

//			if (mLastVoltage > 0) {
//				if (acPlugin) {
//					if (voltage > mLastVoltage) {
//						if (!pluginPass) {
//							pluginPass = true;
//							pluginView.setText(pluginView.getText() + "Pass");
//						}
//					}
//				} else {
//					if (voltage < mLastVoltage) {
//						if (!unplugPass) {
//							unplugPass = true;
//							unplugView.setText(unplugView.getText() + "Pass");
//						}
//					}
//				}
//			}
//
//			if (pluginPass && unplugPass) {
//				findViewById(R.id.btn_Pass).performClick();
//			}

//			mLastVoltage = voltage;
		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}
