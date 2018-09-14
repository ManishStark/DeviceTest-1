package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.SystemUtil;
import com.rockchip.newton.UserModeManager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PermissionInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LedTestActivity extends Activity implements OnClickListener {
	NotificationManager notificationManager;
	private static final int NOTIFY_ID = 1000;
//
//	private static final String POWER_LED_PATH = "/sys/class/leds/power_led/";
//	private static final String POWER_LED_BRIGHT = POWER_LED_PATH
//			+ "brightness";
//	// private static final String POWER_LED_FREQ = POWER_LED_PATH + "freq";
//	private static final String POWER_LED_PERIOD = POWER_LED_PATH + "period";


	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.ledtest);

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Button redButton = (Button) findViewById(R.id.red_btn);
		Button yellowButton = (Button) findViewById(R.id.green_btn);
		Button blueButton = (Button) findViewById(R.id.blue_btn);
//		final Button powerButton = (Button) findViewById(R.id.power_btn);
//
//		powerButton.setText("PowerLed");
//		powerButton.setOnClickListener(new OnClickListener() {
//
//			
//			public void onClick(View v) {
//				String cmd = "echo 255 > " + POWER_LED_BRIGHT + "\necho 255 > "
//						+ POWER_LED_PERIOD;
//				SystemUtil.execScriptCmd(cmd,
//						DeviceTest.TEMP_FILE_PATH, true);
//				powerButton.setEnabled(false);
//			}
//		});

		redButton.setOnClickListener(this);
		yellowButton.setOnClickListener(this);
		blueButton.setOnClickListener(this);

		ControlButtonUtil.initControlButtonView(this);

		Notification notification;

		notification = new Notification();
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledARGB = Color.RED;
		notification.ledOffMS = 0;
		notification.ledOnMS = 1;
		redButton.setTag(notification);

		notification = new Notification();
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledARGB = Color.GREEN;
		notification.ledOffMS = 0;
		notification.ledOnMS = 1;
		yellowButton.setTag(notification);

		notification = new Notification();
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledARGB = Color.BLUE;
		notification.ledOffMS = 0;
		notification.ledOnMS = 1;
		blueButton.setTag(notification);

	}

	
	protected void onStop() {
		super.onStop();
		notificationManager.cancel(NOTIFY_ID);
//		SystemUtil.execScriptCmd("echo 0 > " + POWER_LED_PERIOD,
//				DeviceTest.TEMP_FILE_PATH, true);
	}

	
	protected void onResume() {
		super.onResume();
	}

	
	public void onPause() {
		super.onPause();
	}

	
	public void onClick(View v) {
		notificationManager.notify(NOTIFY_ID, (Notification) v.getTag());
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

}
