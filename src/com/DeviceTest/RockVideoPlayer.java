/*
 * Copyright (C) 2009 The Rockchip Android MID Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.DeviceTest;


import java.io.File;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.VideoListAdapter;
import com.DeviceTest.helper.TestCase.RESULT;

public class RockVideoPlayer extends ListActivity
 	implements View.OnCreateContextMenuListener{
	 private static final String TAG = "RockVideoPlayer";	
	 private static final boolean DEBUG = true;
	 public static void LOG(String msg)
	 {
		 if(DEBUG)
		 {
			 Log.d(TAG,msg);
		 }
	 }
	 //private ArrayList<VideoItem> VideoSet;
	 private String[] mCols = new String[] {
			 MediaStore.Video.Media._ID,
			 MediaStore.Video.Media.TITLE,
			 MediaStore.Video.Media.DISPLAY_NAME,
			 MediaStore.Video.Media.DURATION,
			 MediaStore.Video.Media.MIME_TYPE,
			 MediaStore.Video.Media.SIZE,
			 MediaStore.Video.Media.BOOKMARK,
			 MediaStore.Video.Media.DATA
	 };
	 /*���Ե�cursor,�������������� */
	public Cursor mVideoCursor;
	private String mSortOrder;
	 private VideoListAdapter mVideoListAdapter;
     private Uri mCurrentVideoUri;
     private String mCurrentVideoFilename;
     private View MainView;
     private View sNoFileView;
     static final int DIALOG_DELETE_CHOICE = 1;
     static final int DIALOG_DELETE_CONFIRM = 2;
	private static final int PlayDone = 1;
     private int mLastPosition = 0;
     int timeoutmode;
     int screenOn;
     int mOldBrightness;
     boolean mForbidenClick = false;
     private Dialog mMediaScanningDialog;
     private ProgressDialog pd;
     private BroadcastReceiver mReceiver;
     private ListView mVideoList;
     private Uri mUri;
	 public RockVideoPlayer(){
	 }
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);	 
		
	     setContentView(R.layout.main_display_land);
	     MainView = findViewById(R.id.main_layout);	 
	     sNoFileView = findViewById(R.id.novideofile);	 
	     ControlButtonUtil.initControlButtonView(this);
	  }
	 public void VideoDisplayVisible()
	 {
		 LOG("Begin to setListAdapter");
		 VideoDisplay();		  
	     mForbidenClick = false;
	     //RockVideoPlayer.this.getListView().setSelection(mLastPosition);
	 }
	 public void VideoDisplayVInVisible()
	 {		 
		 sNoFileView.setVisibility(View.VISIBLE);
	 }	  
	 	
	 @Override
	 public void onResume() {
		 LOG("Enter onResume()");	
		 super.onResume();		  
		 String Dilog_tile = getResources().getString(R.string.load_title);
		 String Dilog_wait = getResources().getString(R.string.wait);
		 pd = ProgressDialog.show(this, Dilog_tile,Dilog_wait, true, false);
		 IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
	        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
	        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
	        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
	        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);		       
	        intentFilter.addDataScheme("file");	 
	        mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					
					 String action = intent.getAction();
					 LOG("action = " + action);
		                if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {		                	
		                } else if (action.equals(Intent.ACTION_MEDIA_EJECT)
		                        ||action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){	                			                          		                	
		                	finish();
		                	return;
		                } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)
		                		||action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
		                	mReScanHandler.sendEmptyMessage(0);     
		                } 
				}
	  };
	        registerReceiver(mReceiver, new IntentFilter(intentFilter));
	        VideoDisplayVisible();
	 }
	 public void updataAdapter()
	 {
		 LOG("Enter updataAdapter()");
		 LOG("mVideoListAdapter = " + mVideoListAdapter);		 
		 setListAdapter(mVideoListAdapter);			 
		 setVideoDisplayViewBackground();
		 
	 }
	 public void setVideoDisplayViewBackground()
	 {
		 LOG("Enter setVideoDisplayViewBackground() ");
		 
		 if (mVideoListAdapter == null)
			 //MainView.setBackgroundResource(R.drawable.novideofile);
			 sNoFileView.setVisibility(View.VISIBLE);
		 else{
			 if(mVideoCursor == null){
				 //MainView.setBackgroundResource(R.drawable.novideofile);
				 sNoFileView.setVisibility(View.VISIBLE);
			 }else if(mVideoCursor.moveToFirst() == false){
				 //MainView.setBackgroundResource(R.drawable.novideofile);
				 sNoFileView.setVisibility(View.VISIBLE);
			 }
			 else if( mVideoCursor.moveToFirst() != false && mVideoCursor.getCount()!= 0){
			  //MainView.setBackgroundColor(R.color.black);
				 {
					 sNoFileView.setVisibility(View.GONE);
					// RockVideoPlayer.this.getListView().setSelection(mLastPosition);	     	
				 }
			 }else{
				 sNoFileView.setVisibility(View.VISIBLE);
			 }
		 }
	 }
	 @Override
	 public void onRestart()
	 {
		 LOG("Enter onRestart()");
		 super.onRestart();		
		 //RockVideoPlayer.this.getListView().setSelection(mLastPosition);
	 }
	 @Override
	    public void onPause() {
		 LOG("Enter onPause()"); 
		 super.onPause();		 
		 Uri uri = mUri;
		 mReScanHandler.removeCallbacksAndMessages(null);     
		 unregisterReceiverSafe(mReceiver);      
	        System.gc();
	    }
	 @Override
	    public void onDestroy() {
		 LOG("Enter onDestroy()");
		 unregisterReceiverSafe(mReceiver);
		 super.onDestroy();
	 }
	  private void unregisterReceiverSafe(BroadcastReceiver receiver) {
	        try {
	            unregisterReceiver(receiver);
	        } catch (IllegalArgumentException e) {
	            // ignore
	        }
	    }
	 public void VideoDisplay()
	 {		 
		 LOG("Enter VideoDisplay()");
	       mVideoList = getListView();	       
	       if (mVideoListAdapter == null){
	    	   mVideoListAdapter =  new VideoListAdapter(
	                        RockVideoPlayer.this, // need to use application context to avoid leaks
	                        this,
	                        R.layout.video_item_land,  
	                        null, // cursor
	                        new String[] {},
	                        new int[] {}
	                        );
	                LOG("mVideoListAdapter = " + mVideoListAdapter);
	                mVideoListAdapter.setActivity(this);
	                setListAdapter(mVideoListAdapter);	              
	                getVideoCursor(mVideoListAdapter.getQueryHandler());
	                
	       }else{
	    	   mVideoListAdapter.setActivity(this);	    	    	   
	    	   mVideoCursor = mVideoListAdapter.getCursor();	          
	            LOG("mVideoCursor = " + mVideoCursor);
	            if (mVideoCursor != null) {
	            	initVideoCursor(mVideoCursor);
	            } else {
	            	getVideoCursor(mVideoListAdapter.getQueryHandler());
	            }
               LOG("mVideoCursor2 = " + mVideoCursor);
	           setListAdapter(mVideoListAdapter);
	           setVideoDisplayViewBackground();
	       }		
		 }
	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id)
	 {		
		 LOG("onListItemClick:mForbidenClick = " + mForbidenClick);
		
			 mLastPosition = position;	
			 String videoid = Long.valueOf(id).toString();
			 Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
					 videoid);
			 if(uri == null){
				 return;
			 }
			 Cursor cur = getCurrentCursor(this,uri);
			 if(cur == null)
				 return;
			String tepMimetype = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
			 if(checkVideoAvailable(this, cur)){				
				/* Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				 intent.putExtra(name, value);
				 intent.setClass(this, VideoPlayActivity.class);
				 intent.putExtra("mediaTypes", tepMimetype);
				 startActivityForResult(intent,PlayDone);*/
			//     Intent intent = new Intent("com.rk.app.mediafloat.CUSTOM_ACTION");
			      Intent intent = new Intent(Intent.ACTION_VIEW);
			       intent.setDataAndType(uri, tepMimetype);

			       // intent.putExtra("URI", uri.toString());
			        //intent.putExtra("POSITION", 0);
			        startActivity(intent);
			       // ControlButtonUtil. setIntent("com.rk.app.mediafloat.CUSTOM_ACTION");
				 mForbidenClick = true;
			 }
			
	 }
	 public  Cursor getCurrentCursor(Context context,Uri currenturi)
	    {
	        Uri uri = MediaStore.Video.Media.getContentUri("external");
	        Cursor cur = getContentResolver().query(uri, mCols, null, null, null);
	        if(cur != null)
	        {
	            if(cur.moveToFirst())
	            {
	                while(!cur.isAfterLast())
	                {
	                    if(currenturi.equals(ContentUris.withAppendedId(uri,cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Video.Media._ID)))))
	                    return cur;
	                    else 
	                        cur.moveToNext();
	                }
	            }
	        }
	        return null;        
	    }
	  public static boolean checkVideoAvailable(Context context,Cursor cur){
	        String videofile = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
	        File file = new File(videofile);
	        if (file.exists()){
	            return true;
	        }else
	        return false;
	    }
	 Handler mHandler = new Handler();
	 public  Cursor query(Uri uri, String[] projection,
             String selection, String[] selectionArgs, String sortOrder) {
         LOG("Enter query()");
         try {
             ContentResolver resolver = getContentResolver();
             if (resolver == null) {
                 return null;
             }
             return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
          } catch (UnsupportedOperationException ex) {
             return null;
         }
     }
     public Cursor getVideoCursor(AsyncQueryHandler async){
    	 LOG("Enter getVideoCursor()");
    	 Cursor ret = null;
    	 mSortOrder = MediaStore.Video.Media._ID;
	        StringBuilder where = new StringBuilder();
	        where.append(MediaStore.Video.Media._ID + " != ''");
    	 if (async != null) {
    		 LOG("getVideoCursor:startQuery()");
             async.startQuery(0, null,
            		 MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            		 mCols,
            		 where.toString(), null, mSortOrder);           
         } else {
             ret = query(
            		 MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mCols,
            		 where.toString(), null,mSortOrder);
         }
    	 LOG("ret/async = " + ret + "/" + async);
    	 if (ret != null && async != null) {
    		 	LOG("getVideoCursor:initVideoCursor()");
	        	initVideoCursor(ret);
	        }	        
    	 return ret;
 	}
     public void initVideoCursor(Cursor newCursor){
    	 LOG("Enter initVideoCursor() and newCursor = " + newCursor);
		 mVideoListAdapter.changeCursor(newCursor);
		 LOG("mVideoCursor = " + mVideoCursor + "newCursor = " + newCursor);
			if (mVideoCursor == null){ 			               
				pd.dismiss();
				VideoDisplayVInVisible();
				mReScanHandler.sendEmptyMessageDelayed(0, 1000);//1000);
				return;
			}
			 LOG("mVideoCursor.moveToFirst() = and mVideoCursor.getCount() = "
					 + mVideoCursor.moveToFirst() + mVideoCursor.getCount());
			 if(mVideoCursor.moveToFirst() == false || mVideoCursor.getCount() == 0){	          
	        	if(pd != null)
	        		pd.dismiss();
	        	VideoDisplayVInVisible();
				HintNoVideo();	        
			 }else if (mVideoCursor.getCount() != 0){
				 if(pd != null)
		        		pd.dismiss();
				 //MainView.setBackgroundResource(R.drawable.textlistbak);
				  sNoFileView.setVisibility(View.GONE);
			 	  //RockVideoPlayer.this.getListView().setSelection(mLastPosition);	     	
			 }
		pd.dismiss(); 
 	}
     public void HintNoVideo()
 	{
 		Toast.makeText(this, R.string.no_mediafiles, 1500).show();
 	}
     private Handler mReScanHandler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
         	LOG("mReScanHandler  mTrackCursor / mVideoListAdapter = " + mVideoCursor + " / " + mVideoListAdapter);
         	 getVideoCursor(mVideoListAdapter.getQueryHandler());       	
         }
     };

     public boolean dispatchKeyEvent(KeyEvent event) {
         if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
             return false;
         }
         return super.dispatchKeyEvent(event);
     }
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	 if ((resultCode == RESULT_OK)
                 && (data != null)) {
    		 mUri = data.getData();
    		 LOG("onActivityResult:mUri = " + mUri);
    	 }
    	  /*int result = DBUtils.FindPosition(this,mUri);
    	  if(result >= 0)
    	  mLastPosition = result;*/
     }
}