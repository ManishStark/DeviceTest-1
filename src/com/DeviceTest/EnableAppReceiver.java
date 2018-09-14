package com.DeviceTest;

import android.app.ActivityManager;
import android.content.Context;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.net.Uri;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;




public class EnableAppReceiver extends BroadcastReceiver
{
    private final static String TAG = "EnableAppReceiver";
    public static final String PACKAGENAME = "packageName";
    public static final String CLASSNAME = "className";
    public static final String STATE = "state";
    private final static String ACTION_APP_STATE_CHANGE ="android.rockchip.devicetest.action.APP_STATE_CHANGE"; 
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Uri uri = intent.getData();
        Log.d(TAG,"action ================================= "+action);
        Bundle extras = intent.getExtras();
        String packageName = extras.getString(PACKAGENAME);
        String className = extras.getString(CLASSNAME);
        int state = extras.getInt(STATE);
        if(packageName == null || className == null || state < 0) {
            return;
        }
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return;
        }
        // check that device test app package is known to the PackageManager
        ComponentName cName = new ComponentName(packageName,
                className);

        try {
            pm.setComponentEnabledSetting(cName, state,
                    PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
           
        }
    } 
	
}
