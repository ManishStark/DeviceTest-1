package com.DeviceTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ServiceManager;
import android.os.StatFs;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemProperties;
import android.text.format.Formatter;
import android.util.Log;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.StorageEventListener;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.os.storage.VolumeInfo;
import java.util.Collections;
import android.os.storage.DiskInfo;
import java.util.List;


public class UsbHostTestActivity extends Activity {
    private static final String TAG = "UsbHostTestActivity";
    private static final String TEST_STRING = "Rockchip UsbHostTest File";
  private static  String USB_PATH ;
//   private static final String SDCARD_PATH = "/mnt/external_sd";
//  private final static String SDCARD_PATH = "/storage/sdcard1";
    private static final int BACK_TIME = 2000;
    private static final int SEND_REND_WRITE_SD = 3;
    private static final int R_PASS = 1;
    private static final int R_FAIL = 2;
    private String sdcard_path = null;
    private StringBuilder sBuilder;
   // private SdcardReceiver sdcardReceiver = null;
    public String SUCCESS;
    public String FAIL;
    private boolean isFindSd = false;
    private static StorageManager mStorageManager = null;
    private static boolean is_Usb=false;
    TextView mResult;
    private static boolean isSDFirstTest =true;
    public static String flash_dir = Environment.getExternalStorageDirectory().getPath();
    private static String usb_dir; 
    private static StatFs stat=null;       

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().addFlags(1152);
        setContentView(R.layout.usbhosttest);
        isSDFirstTest =true;
        if (mStorageManager == null) {
            mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        }
        this.mResult = (TextView) findViewById(R.id.sdresultText);
        this.mResult.setVisibility(View.VISIBLE);
        this.mResult.setGravity(17);

        ControlButtonUtil.initControlButtonView(this);
        findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_Fail).setVisibility(View.INVISIBLE);
        SUCCESS = getString(R.string.success);
        FAIL = getString(R.string.fail);
      
    }

    @Override
    protected void onResume() {
        super.onResume();
        init_StoragePath(this);
       // mStorageManager.registerListener(mStorageListener);
        StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
        Log.d(TAG, " storageVolumes.length= " + storageVolumes.length);
        for(int i=0;i< storageVolumes.length;i++)
            {
                Log.d(TAG, " storageVolumes["+i+"].getPath()=" +storageVolumes[i].getPath());
            }
        /*if(storageVolumes.length >= 2){
            sdcard_path = storageVolumes[1].getPath();*/
      
        //SDCARD_PATH=sdcard_path;
        //Log.d(TAG, "  storageVolumes[1].getPath()= " + SDCARD_PATH + "   "+","+  Environment.getExternalStorageDirectory());
            //Log.d(TAG, "  storageVolumes[0].getPath()= " + storageVolumes[0].getPath());
        //}
        sBuilder = new StringBuilder();
        //String sdState = getSdCardState();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            isFindSd=true;

            mResult.setText(getString(R.string.resume_findSD));
            mHandler.sendEmptyMessageDelayed(SEND_REND_WRITE_SD, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStorageManager != null) {
          //  mStorageManager.unregisterListener(mStorageListener);
        }
    }
    

    public void testSdcard() {
        try {
                if (USB_PATH==null)
    {
        sBuilder.append(getString(R.string.UsbHostTestFail)).append("\n");
        mResult.setText(sBuilder.toString());
        mHandler.sendEmptyMessageDelayed(R_FAIL, 3000);
        isFindSd = false;
        return;
    }
           // String externalVolumeState = mStorageManager.getVolumeState(SDCARD_PATH);
            //if (!externalVolumeState.equals(Environment.MEDIA_MOUNTED)) {
              //  sBuilder.append(getString(R.string.SdCardFail)).append("\n");
                //mResult.setText(sBuilder.toString());
                //mHandler.sendEmptyMessageDelayed(R_FAIL, 3000);
                //isFindSd = false;
                //return;
           // }
        } catch (Exception rex) {
             
            rex.printStackTrace();
            isFindSd = false;
            mHandler.sendEmptyMessageDelayed(R_FAIL, 3000);
            return;
        }

        File pathFile = new File(USB_PATH);
        Log.d(TAG, "pathFile = " + pathFile.toString());
          if (!pathFile.exists()) {
        sBuilder.append(getString(R.string.UsbHostTestFail)).append("\n");
        mResult.setText(sBuilder.toString());
        mHandler.sendEmptyMessageDelayed(R_FAIL, 3000);
        isFindSd = false;
        return;
          }
       try {
             stat = new StatFs(pathFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
             sBuilder.append(getString(R.string.UsbHostTestFail)).append("\n");
        mResult.setText(sBuilder.toString());
        mHandler.sendEmptyMessageDelayed(R_FAIL, 3000);
        isFindSd = false;
        return;
        }
        
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        String totalSize = Formatter.formatFileSize(this, totalBlocks
                * blockSize);
            if ("0.00 B".equals(totalSize)) {
                sBuilder.append(getString(R.string.UsbHostTestFail)).append("\n");
        mResult.setText(sBuilder.toString());
        mHandler.sendEmptyMessageDelayed(R_FAIL, 3000);
        isFindSd = false;
         return; 
            }
        String prix = getString(R.string.UsbFind);
        sBuilder.append(prix + totalSize).append("\n");
        try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        testReadAndWrite();
       
    }

    public void testReadAndWrite() {
        if (isFindSd && dotestReadAndWrite()) {
            sBuilder.append(getString(R.string.UsbHostTestTitle) + SUCCESS);
            ((Button)findViewById(R.id.btn_Skip)).setClickable(false);
            //((Button)findViewById(R.id.btn_Pass)).setClickable(false);
            ((Button)findViewById(R.id.btn_Fail)).setClickable(false);
            mHandler.sendEmptyMessageDelayed(R_PASS, BACK_TIME);
        } else {
            sBuilder.append(getString(R.string.UsbHostTestTitle) + FAIL);
            ((Button)findViewById(R.id.btn_Skip)).setClickable(false);
            ((Button)findViewById(R.id.btn_Pass)).setClickable(false);
            //((Button)findViewById(R.id.btn_Fail)).setClickable(false);
            mHandler.sendEmptyMessageDelayed(R_FAIL, BACK_TIME);
        }

        mResult.setText(sBuilder.toString());
    }

    private boolean dotestReadAndWrite() {
       // String directoryName = Environment.getExternalStorageDirectory().toString()+ "/test";
        String directoryName = USB_PATH+ "/test";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                sBuilder.append(getString(R.string.MakeDir) + FAIL).append("\n");
                return false;
            } else {
                sBuilder.append(getString(R.string.MakeDir) + SUCCESS).append(
                        "\n");
            }
        }
        File f = new File(directoryName, "SDCard.txt");
        try {
            // Remove stale file if any
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {
                sBuilder.append(getString(R.string.CreateFile) + FAIL).append(
                        "\n");
                return false;
            } else {
                sBuilder.append(getString(R.string.CreateFile) + SUCCESS).append(
                        "\n");

                doWriteFile(f.getAbsoluteFile().toString());

                if (doReadFile(f.getAbsoluteFile().toString()).equals(
                        TEST_STRING)) {
                    sBuilder.append(getString(R.string.Compare)).append(SUCCESS).append(
                            "\n");
                } else {
                    sBuilder.append(getString(R.string.Compare)).append(FAIL).append(
                            "\n");
                    return false;
                }
            }

            sBuilder.append(getString(R.string.FileDel)).append(
                    (f.delete() ? SUCCESS : FAIL)).append("\n");
            sBuilder.append(getString(R.string.DirDel)).append(
                    (directory.delete() ? SUCCESS : FAIL)).append("\n");
            return true;
        } catch (IOException ex) {
            Log.e(TAG, "isWritable : false (IOException)!");
            return false;
        }
    }

    public void doWriteFile(String filename) {
        try {
            sBuilder.append(getString(R.string.WriteData)).append("\n");
            OutputStreamWriter osw = new OutputStreamWriter(
                                                            new FileOutputStream(
                                                                                 filename));
            osw.write(TEST_STRING, 0, TEST_STRING.length());
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String doReadFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader
                    (new FileInputStream(filename)));
            String data = null;
            StringBuilder temp = new StringBuilder();
            sBuilder.append(getString(R.string.ReadData)).append("\n");
            while ((data = br.readLine()) != null) {
                temp.append(data);
            }
            br.close();
            Log.e(TAG, "Readfile " + temp.toString());
            return temp.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    public class SdcardReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReveive ..... " + intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
                 Log.e(TAG, "239 ..... " + intent.getAction()); 
                testSdcard();
                testReadAndWrite();
            }
        }
    }
   */
/*StorageEventListener mStorageListener = new StorageEventListener() {
        @Override
        public void onStorageStateChanged(String path, String oldState, String newState) {
             Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxx","yyyyyyyyyyyyyyyyyyyyyyyyyyyy");
            if (path.equals(SDCARD_PATH) && newState.equals(Environment.MEDIA_MOUNTED)) {
                isFindSd=true;

                mResult.setText(getString(R.string.resume_findSD));
                mHandler.sendEmptyMessageDelayed(SEND_REND_WRITE_SD, 100);
            testReadAndWrite();
            }
      }
  };*/
    
    public void TestResult(int result) { 
        if (result == R_PASS) {
           if(isSDFirstTest){
               isSDFirstTest=false;
               ((Button) findViewById(R.id.btn_Pass)).performClick();
               isFindSd = false;
           }
            
        } else if (result == R_FAIL) {
            if(isSDFirstTest){
                isSDFirstTest=false;
                ((Button) findViewById(R.id.btn_Fail)).performClick();
                isFindSd = false;
            }
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case R_PASS:
                    TestResult(R_PASS);
                    break;
                case R_FAIL:
                    TestResult(R_FAIL);
                    break;
              
                case SEND_REND_WRITE_SD:
                
                   testSdcard();
                    break;
                    
            }
        };
    };
    
    public static String getSdCardState() {
        try {
            IMountService mMntSvc = null;
            if (mMntSvc == null) {
                mMntSvc = IMountService.Stub.asInterface(ServiceManager
                                                         .getService("mount"));
            }
            return mMntSvc.getVolumeState(USB_PATH);
        } catch (Exception rex) {
            return Environment.MEDIA_REMOVED;
        }

    }
    //获取外置sd卡路径
    private static String getStoragePath(Context mContext, boolean is_removale) {  

          mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
            Class<?> storageVolumeClazz = null;
            try {
                storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
                Method getPath = storageVolumeClazz.getMethod("getPath");
                Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
                Object result = getVolumeList.invoke(mStorageManager);
                final int length = Array.getLength(result);
                for (int i = 0; i < length; i++) {
                    Object storageVolumeElement = Array.get(result, i);
                    String path = (String) getPath.invoke(storageVolumeElement);
                    boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                    if (is_removale == removable) {
                        return path;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
    }
      public void init_StoragePath(Context context) {
        // flash dir

           mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        flash_dir = Environment.getExternalStorageDirectory().getPath();
        final List<VolumeInfo> volumes = mStorageManager.getVolumes();
        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
        for (VolumeInfo vol : volumes) {
            if (vol.getType() == VolumeInfo.TYPE_PUBLIC) {
                Log.d(TAG, "Volume path:" + vol.getPath());
                DiskInfo disk = vol.getDisk();
                if (disk != null) {
                    if (disk.isUsb()) {
                        // sdcard dir
                     StorageVolume sv = vol.buildStorageVolume(context,
                                context.getUserId(), false);
                        usb_dir = sv.getPath();                 
                    } else if (disk.isSd()) {
                      
                    }
                }
            }
        }

        USB_PATH = usb_dir;
        Log.d(TAG, "usb_dir: " + usb_dir);
    }
//判断sd卡是否挂载
private boolean isSDMounted() {  
      boolean isMounted = false;  
      StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);  
  
      try {  
          Method getVolumList = StorageManager.class.getMethod("getVolumeList", null);  
          getVolumList.setAccessible(true);  
          Object[] results = (Object[])getVolumList.invoke(sm, null);  
          if (results != null) {  
              for (Object result : results) {  
                  Method mRemoveable = result.getClass().getMethod("isRemovable", null);  
                  Boolean isRemovable = (Boolean) mRemoveable.invoke(result, null);  
                  if (isRemovable) {  
                      Method getPath = result.getClass().getMethod("getPath", null);  
                      String path = (String) mRemoveable.invoke(result, null);  
                      Method getState = sm.getClass().getMethod("getVolumeState", String.class);  
                      String state = (String)getState.invoke(sm, path);  
                      if (state.equals(Environment.MEDIA_MOUNTED)) {  
                          isMounted = true;  
                          break;  
                      }  
                  }  
              }  
          }  
      } catch (NoSuchMethodException e){  
          e.printStackTrace();  
      } catch (IllegalAccessException e){  
          e.printStackTrace();  
      } catch (InvocationTargetException e) {  
          e.printStackTrace();  
      }  
  
      return isMounted;  
  }  


}
