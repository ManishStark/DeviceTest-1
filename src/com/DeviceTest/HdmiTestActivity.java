package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.LcdTestView;
import android.os.UEventObserver;

public class HdmiTestActivity extends Activity {
    private final static String TAG = "HDMITEST";

    private final static int CHANGE_COLOR = 1;
    private final static int HDMI_SCAN = 2;
    private int[] TestColor = {Color.RED, Color.GREEN, Color.BLUE };
    private LcdTestView mTestView;
    private TextView mTitle;
    private TextView mResult;
    private TextView mShowTime;
    private int mTestNo;
    private boolean isStart = false;
    private File HdmiFile = null;
    private File HdmiState = null;
    private File HdmiDisplayEnable=null;
    private File HdmiDisplayMode=null;
    private File HdmiDisplayConnect=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.hdmitest);

        mTestView = (LcdTestView) findViewById(R.id.lcdtestview);
        mResult = (TextView) findViewById(R.id.result);
        mShowTime = (TextView) findViewById(R.id.TimeShow);
        mTestNo = 0;
        
        HdmiFile = new File("/sys/class/hdmi/hdmi-0/enable");
        HdmiState = new File("/sys/class/hdmi/hdmi-0/state");
        HdmiDisplayEnable=new File("/sys/class/display/HDMI/enable");
        //HdmiDisplayMode=new File("/sys/class/display/HDMI/mode");
        HdmiDisplayConnect=new File("sys/class/display/HDMI/connect");
        ControlButtonUtil.initControlButtonView(this);
        ((Button) findViewById(R.id.btn_Pass)).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(HDMI_SCAN, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(HDMI_SCAN);
        mHandler.removeMessages(CHANGE_COLOR);
    }
    
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CHANGE_COLOR:
                    if (mTestNo > TestColor.length - 1) {
                        finishHdmiTest();
                        return;
                    }
                    ControlButtonUtil.Hide();
                    mShowTime.setVisibility(View.VISIBLE);
                    mTestView.setVisibility(View.VISIBLE);
                    mResult.setText(R.string.HdmiStart);
                    mTestView.setBackgroundColor(TestColor[mTestNo++]);
                    sendEmptyMessageDelayed(CHANGE_COLOR, 1500);
                    break;
                case HDMI_SCAN:
                	this.removeMessages(HDMI_SCAN);
                    if (startHdmiTest()) {
                        mResult.setText(R.string.HdmiPrepare);
                        //setHdmiConfig(HdmiFile, true);
                        mTestNo = 0;
                        sendEmptyMessageDelayed(CHANGE_COLOR, 4000);
                    }else{
                        sendEmptyMessageDelayed(HDMI_SCAN, 500);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    
    public boolean startHdmiTest() {
        if (!isStart && isHdmiConnected(HdmiDisplayConnect)) {
            mResult.setText(R.string.HdmiPrepare);
            setHdmiConfig(HdmiFile, true);
            mTestNo = 0;
            isStart = true;
            return true;
        }
        mResult.setText(R.string.HdmiNoInsert);
        Log.i(TAG, "Hdmi no insert");
        return false;
    }
    
    public void finishHdmiTest() {
        ((Button) findViewById(R.id.btn_Pass)).setVisibility(View.VISIBLE);
        ControlButtonUtil.Show();
        isStart = false;
        mShowTime.setVisibility(View.GONE);
        mTestView.setVisibility(View.GONE);
        mResult.setText(R.string.HdmiResult);
//        setHdmiConfig(HdmiFile, false);
    }

    protected boolean isHdmiConnected(File file) {
        boolean isConnected = false;
        if (file.exists()) {
            try {
                FileReader fread = new FileReader(file);
                BufferedReader buffer = new BufferedReader(fread);
                String strPlug = "plug=1";
                String str = null;

                while ((str = buffer.readLine()) != null) {
                    int length = str.length();
                    //if ((length == 6) && (str.equals(strPlug))) {
                    if(str.equals("1")){
                        isConnected = true;
                        break;
                    } else {
                        isConnected = false;
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "IO Exception");
            }
        } else {
            Log.e(TAG, file + "isHdmiConnected : file no exist");
        }
        return isConnected;
    }

    protected void setHdmiConfig(File file, boolean enable) {
        if (SystemProperties.get("ro.board.platform", "none").equals("rk29xx")){
            if (file.exists()) {
                try {
                    String strDouble = "2";
                    String strChecked = "1";
                    String strUnChecked = "0";
                    RandomAccessFile rdf = null;
                    rdf = new RandomAccessFile(file, "rw");

                    if (enable) {
                        rdf.writeBytes(strChecked);
                    } else {
                        rdf.writeBytes(strUnChecked);

                    }

                } catch (IOException re) {
                    Log.e(TAG, "IO Exception");
                }
            } else {
                Log.i(TAG, "The File " + file + " is not exists");
            }
        } else{
            if (file.exists()) {
                try {
                    Log.d(TAG, "setHdmiConfig");
                    String strChecked = "1";
                    String strUnChecked = "0";

                    RandomAccessFile rdf = null;
                    rdf = new RandomAccessFile(file, "rw");
                    if (enable) {
                        rdf.writeBytes(strChecked);
                    } else {
                        rdf.writeBytes(strUnChecked);
                    }
                } catch (IOException re) {
                    Log.e(TAG, "IO Exception");
                    re.printStackTrace();
                }
            } else {
                Log.i(TAG, "The File " + file + " is not exists");
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if (paramMotionEvent.getAction() == MotionEvent.ACTION_DOWN && !isStart) {
    		mHandler.sendEmptyMessageDelayed(HDMI_SCAN, 500);
        }
        return super.onTouchEvent(paramMotionEvent);
    }
    

}
