
package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.DrawClock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

public class ClockTestActivity extends Activity {
    protected static final int MSG_CLOCK = 0x1234;

    private LinearLayout clock_pannel;

    private DrawClock clock;

    public Handler mHandler;

    private Thread mClockThread;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setTitle(getTitle() + "----("
                + getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.myclocktest);

        clock_pannel = (LinearLayout) findViewById(R.id.clock1);

        clock = new DrawClock(this);

        clock_pannel.addView(clock);

        mHandler = new Handler()
        {

            public void handleMessage(Message msg)

            {

                switch (msg.what)

                {

                    case ClockTestActivity.MSG_CLOCK:

                    {

                        clock_pannel.removeView(clock);

                        clock = new DrawClock(ClockTestActivity.this);

                        clock_pannel.addView(clock);

                    }

                        break;

                }

                super.handleMessage(msg);

            }

        };

        mClockThread = new LooperThread();

        mClockThread.start();
        ControlButtonUtil.initControlButtonView(this);
    }

    class LooperThread extends Thread

    {

        public void run()

        {

            super.run();

            try

            {

                do

                {

                    Thread.sleep(1000);

                    Message m = new Message();

                    m.what = ClockTestActivity.MSG_CLOCK;

                    ClockTestActivity.this.mHandler.sendMessage(m);

                } while (ClockTestActivity.LooperThread.interrupted() == false);

            }

            catch (Exception e)

            {

                e.printStackTrace();

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
