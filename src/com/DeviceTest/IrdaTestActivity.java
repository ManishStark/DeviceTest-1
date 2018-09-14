package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.SystemUtil;
import com.rockchip.dmi.DmiInfo;
import com.rockchip.dmi.DmiUtil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class IrdaTestActivity extends Activity implements OnClickListener {
	private Button sirSend = null;
	private Button sirReceive = null;
	private Button firSend = null;
	private Button firReceive = null;
	private ProgressBar progressBar = null;

	Handler handler, myHandler;
	private static final int TEST_SIR_SEND = 0;
	private static final int TEST_SIR_RECEIVE = 1;
	private static final int TEST_FIR_SEND = 2;
	private static final int TEST_FIR_RECEIVE = 3;
	boolean sirSendPass = false;
	boolean sirReceivePass = false;
	boolean firSendPass = false;
	boolean firReceivePass = false;
	
	private final static String TEST_IRDA_PATH = DeviceTest.DATA_PATH
	+ "irda_test";
	File testIrdaFile = new File(TEST_IRDA_PATH);

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.irdatest);

		sirSend = (Button) findViewById(R.id.sir_send);
		sirReceive = (Button) findViewById(R.id.sir_receive);
		firSend = (Button) findViewById(R.id.fir_send);
		firReceive = (Button) findViewById(R.id.fir_receive);

		sirSend.setTag(sirSend.getText());
		sirReceive.setTag(sirReceive.getText());
		firSend.setTag(firSend.getText());
		firReceive.setTag(firReceive.getText());

		sirSend.setOnClickListener(this);
		sirReceive.setOnClickListener(this);
		firSend.setOnClickListener(this);
		firReceive.setOnClickListener(this);

		progressBar = (ProgressBar) findViewById(R.id.irda_progress);

		ControlButtonUtil.initControlButtonView(this);
		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);


		InputStream is = null;
		OutputStream os = null;
		try {
			is = getAssets().open("irda_test");
			os = new FileOutputStream(testIrdaFile);
			int data = 0;
			while (true) {
				data = is.read();
				if (data < 0) {
					break;
				}
				os.write(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
				if (testIrdaFile.exists()) {
					SystemUtil.execRootCmd("chmod 777 " + testIrdaFile);
				}
			} catch (Exception e) {
			}
		}

		
		
		myHandler = new Handler();

		new Thread(new Runnable() {

			public void run() {
				Looper.prepare();
				handler = new Handler() {

					public void handleMessage(Message msg) {
						myHandler.post(new Runnable() {

							public void run() {
								sirSend.setEnabled(false);
								sirReceive.setEnabled(false);
								firSend.setEnabled(false);
								firReceive.setEnabled(false);
							}
						});

						final boolean result;
						final TextView resultView;
						switch (msg.arg1) {
						case TEST_SIR_SEND:
							result = SystemUtil.execShellCmdForStatue(TEST_IRDA_PATH + " --sir-send") == 0;
							resultView = sirSend;
							if (result) {
								sirSendPass = true;
							}
							break;
						case TEST_SIR_RECEIVE:
							result = SystemUtil.execShellCmdForStatue(TEST_IRDA_PATH + " --sir-receive") == 0;
							resultView = sirReceive;
							if (result) {
								sirReceivePass = true;
							}
							break;
						case TEST_FIR_SEND:
							result = SystemUtil.execShellCmdForStatue(TEST_IRDA_PATH + " --fir-send") == 0;
							resultView = firSend;
							if (result) {
								firSendPass = true;
							}
							break;
						case TEST_FIR_RECEIVE:
							result = SystemUtil.execShellCmdForStatue(TEST_IRDA_PATH + " --fir-receive") == 0;
							resultView = firReceive;
							if (result) {
								firReceivePass = true;
							}
							break;

						default:
							result = false;
							resultView = null;
							break;
						}

						myHandler.post(new Runnable() {

							public void run() {

								sirSend.setEnabled(true);
								sirReceive.setEnabled(true);
								firSend.setEnabled(true);
								firReceive.setEnabled(true);

								progressBar.setVisibility(View.INVISIBLE);
								resultView.setText(resultView.getTag()
										.toString()
										+ ":"
										+ (result ? "Success" : "Failed"));
								if (sirSendPass && sirReceivePass
										&& firSendPass && firReceivePass) {
									findViewById(R.id.btn_Pass).performClick();
								}
							}
						});
					}
				};

				Looper.loop();
			}
		}).start();

		

	}

	protected void onStop() {
		super.onStop();

		SystemUtil.killProcessByPath(TEST_IRDA_PATH);
		
		if (testIrdaFile.exists()) {
			testIrdaFile.delete();
		}
	}
	
	protected void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	public void onClick(View v) {
		progressBar.setVisibility(View.VISIBLE);

		Message message = handler.obtainMessage();
		if (v == sirSend || v == sirReceive) {
			if (v == sirSend) {
				message.arg1 = TEST_SIR_SEND;
			} else {
				message.arg1 = TEST_SIR_RECEIVE;
			}
		} else {
			if (v == firSend) {
				message.arg1 = TEST_FIR_SEND;
			} else {
				message.arg1 = TEST_FIR_RECEIVE;
			}
		}
		((TextView) v).setText(v.getTag().toString() + ":Testing");

		handler.sendMessage(message);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

}
