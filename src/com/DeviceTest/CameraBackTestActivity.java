package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import com.DeviceTest.helper.ControlButtonUtil;

import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager; 
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;
import android.widget.ImageView;

public class CameraBackTestActivity extends Activity{
	
		private static final String TAG="CameraWithFlashTestActivity";

		private static final int MSG_TAKE_OVER = 1;
		private static final int MSG_TAKE_ERROR = 2;
		private TextView tv_prompt;
		private ImageView imageView;
		private boolean isTakeStat=false; //FOR ESC key 
		private boolean isCanTake=true; //if in taking process,click preview screen should not call take photo again!
//		private int  CameraCount=0;
//		private int  testCount=0;
		private Bitmap bitmap;
		private  int testCameraID=0;
		public void onCreate(Bundle bundle) {
		        super.onCreate(bundle);
		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		        requestWindowFeature(Window.FEATURE_NO_TITLE);
		      //  setContentView(new CameraView(this)); 
		      setContentView(R.layout.camerawithflashtest);
		      tv_prompt=(TextView)findViewById(R.id.CameratextMsg);
			//  tv_prompt.setText("请点击屏幕打开后置摄像头\n然后点击预览屏幕拍照");
			  tv_prompt.setText(getString(R.string.CameraFlashtextBack));
		      ControlButtonUtil.initControlButtonView(this);
//			CameraCount=getCameraCount();
	    }

		protected void onResume() {
			super.onResume();
//			Log.d(TAG,"onResume... CameraCount="+CameraCount);
			 findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);	
		//	 findViewById(R.id.btn_Pass).setEnabled(false);
			 isTakeStat=false;
			 isCanTake=true;
//			 testCount=0;
			 
		}

		public boolean onTouchEvent(MotionEvent paramMotionEvent) {
			if(isCanTake){
		        if (paramMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					setContentView(new CameraView(this,testCameraID)); 
					testCameraID=0;
					isTakeStat=true;
		           }
				}  
	            	return super.onTouchEvent(paramMotionEvent);      
	        }

		public boolean dispatchKeyEvent(KeyEvent event) {
			//open camera maybe error,allow user back.
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				if(isTakeStat)
				{
					myHandler.sendEmptyMessage(MSG_TAKE_OVER);
				}
				else
					return false;
			}
			return super.dispatchKeyEvent(event);
		}

//		 private int getCameraCount()
//	   	{  
//		        int cameraCount = 0;  
//		        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
//		        cameraCount = Camera.getNumberOfCameras(); // get cameras number    c
//		        return cameraCount;  
//	   	 } 
		   
		Handler myHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case MSG_TAKE_OVER:
						setContentView(R.layout.cameratestv);
						ControlButtonUtil.initControlButtonView(CameraBackTestActivity.this);
						imageView=(ImageView) findViewById(R.id.imgV);
						imageView.setImageBitmap(bitmap);
						//Log.i("lzx", "-------------bitmap----"+bitmap.toString());
						findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
					//	findViewById(R.id.btn_Pass).setEnabled(true);
						break;
					case MSG_TAKE_ERROR:
							isTakeStat=false;
							isCanTake=true;
//							testCount=0;
//							setContentView(R.layout.camerawithflashtest);
							
							 ControlButtonUtil.initControlButtonView(CameraBackTestActivity.this);
							  findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
							//findViewById(R.id.btn_Pass).setEnabled(false);
						break;
						
					}
					super.handleMessage(msg);
				}
			};

		public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {
			private SurfaceHolder holder;
			private Camera camera;
			private boolean af;
			private int cameraId;
			
			public CameraView(Context context) {
				super(context);
				Log.d(TAG,"constructed...");
				holder = getHolder();
				holder.addCallback(this);
		 
				//holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}

			public CameraView(Context context,int camera_Id) {
				super(context);
				Log.d(TAG,"constructed... 2");
				holder = getHolder();
				holder.addCallback(this);
				cameraId=camera_Id;
		 
				//holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//
			}
		 
			public void surfaceCreated(SurfaceHolder holder) {//
				try {
					
					camera = Camera.open(cameraId);
					Log.d(TAG,"surfaceCreated:camera!=null is:"+(camera!=null)+",cameraId="+cameraId);
					camera.setPreviewDisplay(holder);
					if(cameraId==0)
					{
						Camera.Parameters parameters = camera.getParameters();
						//parameters.setPreviewSize(width, height);
						parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);   
						camera.setParameters(parameters);
					}
					setRightCameraOrientation(cameraId, camera);
					camera.startPreview();
				} catch (Exception e) {
						Log.w(TAG,"surfaceCreated e:"+e.getMessage());
				}
			}
		 
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				
			}
		 
			public void surfaceDestroyed(SurfaceHolder holder) {
				if(null != camera)
				{
					camera.setPreviewCallback(null);
					camera.stopPreview();
					camera.release();
					camera = null;
				}
				Log.d(TAG,"surfaceDestroyed");
			}
		 
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.d(TAG,"MotionEvent.ACTION_DOWN");
					if(null != camera)
					{
						camera.autoFocus(null);
						af = true;
						Log.i("lzx", "-------------------------touch event---------------------");
					}
					else
					{
						
						myHandler.sendEmptyMessage(MSG_TAKE_ERROR);
					}
				}
				if (event.getAction() == MotionEvent.ACTION_UP){
					Log.d(TAG,"event.getAction() == MotionEvent.ACTION_UP");
					if( af == true && isCanTake) {
						camera.takePicture(null, null, this);
						isCanTake=false;
						af = false;
					
					}
				}
				return true;
			}
		 
			public void onPictureTaken(byte[] data, Camera camera) {
				try {
				isTakeStat=false;
				isCanTake=false;
				bitmap= BitmapFactory.decodeByteArray(data, 0, data.length);
			} catch (Exception e) {
			}
		//	camera.startPreview();

			myHandler.sendEmptyMessage(MSG_TAKE_OVER);
			Log.i("lzx", "-------------picture-bitmap---"+bitmap.toString());
			}
		 
			private void data2file(byte[] w, String fileName) throws Exception {
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(fileName);
					out.write(w);
					out.close();
				} catch (Exception e) {
					if (out != null)
						out.close();
					throw e;
				}
			}
		 
		}
		private void setRightCameraOrientation(int cameraId, Camera mCamera) {
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(cameraId, info);
			int rotation = this.getWindowManager().getDefaultDisplay()
					.getRotation();
			int degrees = 0;
			switch (rotation) {
				case Surface.ROTATION_0:
					degrees = 0;
					break;
				case Surface.ROTATION_90:
					degrees = 90;
					break;
				case Surface.ROTATION_180:
					degrees = 180;
					break;
				case Surface.ROTATION_270:
					degrees = 270;
					break;
			}
			int result;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
			mCamera.setDisplayOrientation(result);
		}
	} 


