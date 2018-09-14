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

package com.DeviceTest.helper;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.DeviceTest.R;
import com.DeviceTest.RockVideoPlayer;


class VideoItem {
    Drawable bitmap;
    Uri uri;
    int Totaltime;
    int Lasttime;
    int Currenttime;
	String videoname;
	String mimetype;
	String size;
	String videopath;
}
/*
class Item implements Comparable<Item>{
	private static final String TAG = "Item";	
	 //private static final boolean DEBUG = true;
	 private static final boolean DEBUG = false;
	 public void LOG(String msg)
	 {
		 if(DEBUG)
		 {
			 Log.d(TAG,msg);
		 }
	 }

	public int compareTo(Item another) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
*/
public class VideoListAdapter extends SimpleCursorAdapter 
implements SectionIndexer {
	int resource;
	//private final LayoutInflater mInflater;	
	private static final String TAG = "VideoListAdapter";	
	 private static final boolean DEBUG = true;
	 //private static final boolean DEBUG = false;
	 private RockVideoPlayer mActivity = null;
	    /** @see AlphabetIndexer. */
	    private AlphabetIndexer mIndexer;
		/** ����Э��ʵ�ֶ� ContentResolver �첽�� query ������ helper ���ʵ��. */
	    private AsyncQueryHandler mQueryHandler;
	    int nameIdx;
	    Uri    uriIdx;
	    int    idIdx;
	    int mimetypeIdx;
	    int    bookmarkIdx;
	    int    durationIdx;
	    int sizeIdx;
	    int pahtIdx;
	    private String mConstraint = null;
	    private boolean mConstraintIsValid = false;
	 public void LOG(String msg)
	 {
		 if(DEBUG)
		 {
			 Log.d(TAG,msg);
		 }
	 }
	 
	public VideoListAdapter(Context context, 
            RockVideoPlayer currentactivity,
            int layout,
            Cursor cursor,
            String[] from,
            int[] to) {
		super(context, layout, cursor, from, to);
		mActivity = currentactivity;	
		getColumnIndices(cursor);  
        mQueryHandler = new QueryHandler(context.getContentResolver());     /* ����ת��. */
        LOG("Built mQueryHandler = " + mQueryHandler);
	}
	
	public void setActivity(RockVideoPlayer newactivity) {
  	  mActivity = newactivity;
    }
	class ViewHolder {
		// ��u21069  list item view �� ��u26469 ��u31034 ��u24212  data item(��u-28212 ��ĳu20010 ��u23450 ��u27468 ��u30340 ��u24687 ) ��column "TITLE" ��value ��TextView ��u24341 �� 
		ImageView video_icon;
		TextView video_name;       
		TextView video_time;
		TextView video_type;
		TextView video_size;
		TextView video_path;
		CharArrayBuffer buffer1;
	   // ��u26469 ��u23384  "ARTIST" ��u20018 . 
	   char [] buffer2;
	 }
	/** 
	 * ���Ƶ� AsyncQueryHandler ����. 
	 * @see AsyncQueryHandler.
	 * */
	class QueryHandler extends AsyncQueryHandler {
		  /** Ctor. */
	    QueryHandler(ContentResolver res) {
	        super(res);
	    }
		  @Override
	    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			//LOG("@@@ query complete: " + cursor.getCount() + "   ");
	        /* ��ʼ�� ��ǰ activity. */
			  mActivity.initVideoCursor(cursor);
	    }           
	}
    public AsyncQueryHandler getQueryHandler() {
  	  return mQueryHandler;
    }
	/**
     * ��ȡָͬ�� Cursor ʵ����ص� �ض��� column �� index, �������� "this"(��ǰ����ʵ��) ��. 
     * �������� �����и�.
     */
    private void getColumnIndices(Cursor cur) {
    	
    	LOG("Enter getColumnIndices() and cur = " + cur);
    	Uri uri = MediaStore.Video.Media.getContentUri("external");
    	if (cur!= null) {
    		cur.moveToFirst();
    		nameIdx = cur.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
    		LOG("nameIdx= " + nameIdx);
    	    uriIdx = ContentUris.withAppendedId(uri,cur.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
    	    LOG("uriIdx= " + uriIdx);
    	    idIdx = cur.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
    		mimetypeIdx = cur.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
    		LOG("mimetypeIdx= " + mimetypeIdx);
    		bookmarkIdx = cur.getColumnIndexOrThrow(MediaStore.Video.Media.BOOKMARK);
    		LOG("bookmarkIdx= " + bookmarkIdx);
    		durationIdx = cur.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
    		LOG("durationIdx= " + durationIdx);
    		sizeIdx = cur.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
    		LOG("sizeIdx= " + sizeIdx);
    		pahtIdx = cur.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);   	    
    		LOG("pahtIdx= " + pahtIdx);   		
    	}
    }
    @Override       
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
          View v = super.newView(context, cursor, parent);
          
          ViewHolder vh = new ViewHolder();
          vh.video_icon = (ImageView)v.findViewById(R.id.video_image);
 		  vh.video_name = (TextView)v.findViewById(R.id.video_name);
 		  vh.video_time = (TextView)v.findViewById(R.id.time_info);
 		  vh.video_type = (TextView)v.findViewById(R.id.type_info);
 		  vh.video_size = (TextView)v.findViewById(R.id.size_info);
 		  //vh.video_path = (TextView)v.findViewById(R.id.path_info);
 		  vh.buffer1 = new CharArrayBuffer(100);      
          vh.buffer2 = new char[200];
          v.setTag(vh);
          return v;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	ViewHolder vh = (ViewHolder) view.getTag(); /* ����ض��� list item view, �������� newView() ֮�󱻵���. ����� ����ת�͵���ȷ��, "����" �ɳ���Ա��֤. */
        cursor.copyStringToBuffer(nameIdx, vh.buffer1);
        vh.video_name.setText(vh.buffer1.data, 0, vh.buffer1.sizeCopied);
        mFormatBuilder = new StringBuilder();
	     mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        vh.video_time.setText(makeTimeString(cursor.getInt(bookmarkIdx),cursor.getInt(durationIdx)));
        vh.video_type.setText(cursor.getString(mimetypeIdx));
        vh.video_size.setText(makeSizeString(cursor.getInt(sizeIdx)));
        //vh.video_path.setText(cursor.getString(pahtIdx));
        ImageView iv = vh.video_icon;
        iv.setImageResource(R.drawable.video_icon);
        //iv.setImageBitmap(getVideoCover(cursor.getString(pahtIdx)));
    }
    @Override
    public void changeCursor(Cursor cursor) {
  	 //LOG("Enter changeCursor() get "+ cursor.getCount() + "and cursor = " + cursor);
  	 
  	 if(cursor != null) 
  		cursor.moveToFirst();
  	  if (cursor != mActivity.mVideoCursor) { 
  		   super.changeCursor(cursor);
            mActivity.mVideoCursor = cursor;           
            getColumnIndices(cursor);       /* ��Ϊ "cursor" ʵ��仯, �������е� column ID Ҳ���ܱ仯. �ʸ���֮. */
        }
    }
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
  	  String s = constraint.toString();
        if (mConstraintIsValid 
            && ( (s == null && mConstraint == null) 
                || (s != null && s.equals(mConstraint)))) {
            return getCursor();
        }
        Cursor c = mActivity.getVideoCursor(null);
        mConstraint = s;
        mConstraintIsValid = true;
        return c;
    }
    
    StringBuilder               mFormatBuilder;
	 Formatter                   mFormatter;
	  
	 public String stringForTime(int timeMs) 
	    {
	    	  int totalSeconds = timeMs / 1000;

	          int seconds = totalSeconds % 60;
	          int minutes = (totalSeconds / 60) % 60;
	          int hours   = totalSeconds / 3600;

	          mFormatBuilder.setLength(0);
	        
	          if(hours > 0)
	        	  return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
	          else 
	        	  return mFormatter.format("%02d:%02d", minutes, seconds).toString();
	    }
	 
	public String makeTimeString(int time1,int time2)
	{
		StringBuilder time = new StringBuilder();
		time.append(stringForTime(time1)+'/'+stringForTime(time2));
		return time.toString();
	}
	
	public String makeSizeString(int size){		
		StringBuilder sizeBuilder = new StringBuilder();
		if (size <= 0){
			sizeBuilder.append("0 K");
			return sizeBuilder.toString();
		}
		int sizeK = size/1000;
		int sizeM = sizeK/1000;	
		if(0 < sizeK && sizeK < 1024){			
			sizeBuilder.append(sizeK);
			sizeBuilder.append(" K");
			return sizeBuilder.toString();
		}else{
			sizeBuilder.append(sizeM);
			sizeBuilder.append(" M");	
			return sizeBuilder.toString();
		}			
	}
	 public int getSectionForPosition(int position) {
         return 0;
     }   

	 public int getPositionForSection(int section) {
   	  int pos = mIndexer.getPositionForSection(section);
         return pos;
     }

	public Object[] getSections() {
		if (mIndexer != null) { 
            return mIndexer.getSections();
        } else {
            return null;
        }
	}
	

}

