package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.RemoteException;
//import android.view.IWindowManager;
//import android.view.IWindowManager.Stub;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.widget.TextView;
import java.util.HashMap;

import com.DeviceTest.helper.ControlButtonUtil;

import android.util.Log;

public class KeyboardTestActivity extends Activity {
	static final int Key_Status_Down = 1;
	static final int Key_Status_Null = 0;
	static final int Key_Status_Up = 2;

	static final String TAG = "KeyboardTestActivity";

	private int[] mButtonIds;
	private HashMap<Integer, Integer> mButtonMaps = new HashMap();
	private HashMap<Integer, Integer> mButtonStatus = new HashMap();
	private int[] mKeyCodes;
	private View v = null;
	private WindowManager wm = null;
	KeyguardLock kl = null;
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.keyboadtest);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS)
				+ ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		//(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		ControlButtonUtil.initControlButtonView(this);
		initButtonsMaps();

		v = new View(KeyboardTestActivity.this);
		wm = (WindowManager)KeyboardTestActivity.this.getSystemService(WINDOW_SERVICE);
        KeyguardManager km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");
//		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "=============onKeyDown==========");
		return super.onKeyDown(keyCode, event);
	}

	protected void onResume() {
		super.onResume();
		
        kl.disableKeyguard(); 
        
		//addWindow();
	}
	@Override
	protected void onPause() {
		super.onPause();
		//removeWindow();
		kl.reenableKeyguard();
	}
	private void initButtonsMaps() {
		Log.d(TAG, "===========initButtonsMaps======");
		int[] resId = { R.id.bt_sounddown, R.id.bt_soundup, R.id.bt_home,
				R.id.bt_menu, R.id.bt_back };
		mButtonIds = resId;
		int[] keycode = { 25, 24, 3, 82, 4 };
		mKeyCodes = keycode;

		int i = 0;
		int j = mButtonIds.length;

		Log.d(TAG, "I=" + i + " j = " + j);

		for (i = 0; i < j; i++) {
			Integer key = Integer.valueOf(mKeyCodes[i]);
			Integer value = Integer.valueOf(mButtonIds[i]);
			mButtonMaps.put(key, value);
		}
		resetButtonBackground();
	}

	private boolean isTestKey(int keycode) {
		int j = mKeyCodes.length;
		int i = 0;
		while (i < j) {
			if (keycode == mKeyCodes[i]) {
				return true;
			} else {
				i++;
			}
		}
		return false;
	}

	private void resetButtonBackground() {
		Log.d(TAG, "resetButtonBackground()... ...");
		int i = mButtonIds.length;
		int j = 0;
		while (true) {
			if (j >= i)
				return;
			int k = mButtonIds[j];
			findViewById(k).setBackgroundColor(Color.rgb(255, 255, 255));
			((TextView)findViewById(k)).setTextColor(Color.BLACK);
			j += 1;
		}
	}

	private void setButtonBackgroundDown(int resId) {
		Log.d(TAG, "=====613========setButtonBackgroundDown");
		findViewById(resId).setBackgroundColor(Color.BLUE);
	}

	private void setButtonBackgroundUp(int resId) {
		Log.d(TAG, "======setButtonBackgroundUp");
		findViewById(resId).setBackgroundColor(Color.GREEN);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		int actionCode = event.getAction();
		Log.d(TAG, "KeyCode = " + keyCode);
		Log.d(TAG, "actionCode = " + actionCode);
		if (!isTestKey(keyCode))
			return super.dispatchKeyEvent(event);
		int value = 0;
		Integer key = Integer.valueOf(keyCode);
		value = mButtonMaps.get(key).intValue();
		Log.d(TAG, "==================   value = " + value);
		switch (actionCode) {
		    
		case 0:
			setButtonBackgroundDown(value);
			mButtonStatus.put(key, Integer.valueOf(1));
			break;

		case 1:
			setButtonBackgroundUp(value);
			mButtonStatus.put(key, Integer.valueOf(1));
			if(event.getKeyCode()== KeyEvent.KEYCODE_HOME){
                Log.d(TAG,getWindow().getAttributes().type+ " _____________---- onKeyEEEE(),   " + event.getKeyCode());
                return true;
               }else if(event.getKeyCode()== KeyEvent.KEYCODE_BACK){
                   Log.d(TAG,getWindow().getAttributes().type+ " _____________---- onKeyEEEE(),   " + event.getKeyCode());
                   return true;
               }
			break;

		default:
			break;
		}

//		if (mButtonStatus.size() == mButtonIds.length) {
//			findViewById(R.id.btn_Pass).performClick();
//		}
		return true;
	}
	
	private void addWindow(){
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		 params.type = WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG;
//		 params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//		 params.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
//                 |  WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		params.width = 1;//WindowManager.LayoutParams.FILL_PARENT;
		params.height = 1;//WindowManager.LayoutParams.FILL_PARENT;
		params.format=PixelFormat.TRANSLUCENT;				
		params.gravity=Gravity.LEFT|Gravity.TOP;
	   //����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
		params.x = 0;
		params.y = 0;
		wm.addView(v, params);
		v.requestFocus();
		v.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCodee, KeyEvent event) {
				Log.d(TAG, " _____________---- onKey(),   " + event.getKeyCode());
				int keyCode = event.getKeyCode();
				int actionCode = event.getAction();
				Log.d(TAG, "KeyCode = " + keyCode);
				Log.d(TAG, "actionCode = " + actionCode);
				if (!isTestKey(keyCode))
					return false;
				int value = 0;
				Integer key = Integer.valueOf(keyCode);
				value = mButtonMaps.get(key).intValue();
				Log.d(TAG, "==================   value = " + value);
				switch (actionCode) {
				case 0:
					setButtonBackgroundDown(value);
					mButtonStatus.put(key, Integer.valueOf(1));
					break;

				case 1:
					setButtonBackgroundUp(value);
					mButtonStatus.put(key, Integer.valueOf(1));
					break;
				default:
					break;
				}
				return true;
			
			}
		});		
	}	
	private void removeWindow(){
		wm.removeView(v);
	}
}