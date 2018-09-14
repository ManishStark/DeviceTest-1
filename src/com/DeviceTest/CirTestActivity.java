package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.IOException;

import com.DeviceTest.helper.ControlButtonUtil;

import jp.co.ntt.east.hardware.IrRemoteController;
import jp.co.ntt.east.hardware.IrRemoteController.Data;
import jp.co.toshiba.newtion.cir.RemoteControl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CirTestActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	public Button btn_send;
	// public Button btn_stop;
	String TAG = "CirTest";
	int ret;
	// IrRemoteController ir_ctl = null;
	// IrRemoteController.Data[] data;

	private TextView resultView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cirtest);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		this.btn_send = (Button) findViewById(R.id.send);
		// this.btn_stop = (Button) findViewById(R.id.stop);
		this.btn_send.setOnClickListener(this);
		// this.btn_stop.setOnClickListener(this);

		// ir_ctl = IrRemoteController.getInstance();
		// Log.i("Cir", "init");
		// data = new IrRemoteController.Data[2];
		//
		// data[0] = new IrRemoteController.Data();
		//
		// data[0].setCarrier(8, 18);
		// data[0].setDuration(1080);
		// data[0].setParameter(9000, 4500, 560);
		// data[0].setPulse(0, 560, 560, 0, 1690, 560);
		//
		// data[1] = new IrRemoteController.Data();
		// data[1].setCarrier(8, 18);
		// data[1].setDuration(1080);
		// data[1].setParameter(9000, 2250, 560);
		// data[1].setPulse(0, 560, 560, 0, 1690, 560);

		ControlButtonUtil.initControlButtonView(this);
		resultView = (TextView) findViewById(R.id.CirResult);
		((TextView) findViewById(R.id.CirSendData))
				.setText("Send command:COMMAND_POWER(0x12)");
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		// if (v.getId() == R.id.send) {
		Log.d(TAG, "cir send");
		resultView.setText(onCirSend() ? "Pass!" : "Failed!");
		// } else if (v.getId() == R.id.stop) {
		// Log.d(TAG, "cir stop");
		// resultView.setText(onCirStop() ? "Pass!" : "Failed!");
		// }
		// v.setEnabled(false);
	}

	public boolean onCirSend() {
		return RemoteControl.sendCommand(RemoteControl.COMMAND_POWER);
	}

	//
	// public boolean onCirStop() {
	//
	// try {
	// ir_ctl.stop();
	// return true;
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// Log.e(TAG, "stop err!");
	// return false;
	// }
	//
	// }

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}
