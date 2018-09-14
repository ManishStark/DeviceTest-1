package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.FileDescriptor;
import java.io.IOException;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SpeakerTestActivity extends Activity {
	private AudioManager mAudioManager;
	private MediaPlayer mPlayer;
	private int mOldVolume;
	private boolean mSpeakerOn;

	private Button leftButton;
	private Button rightButton;

	private boolean leftEnable = true;
	private boolean rightEnable = true;

	
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.speakertest);
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		TextView txtContent = (TextView) findViewById(R.id.txtContent);
		txtTitle.setText(R.string.SpeakerTitle);
		txtContent.setText(getString(R.string.SpeakerTip));

		mAudioManager = (AudioManager) getSystemService("audio");
		mPlayer = new MediaPlayer();
		try {
			// mPlayer.setDataSource("/system/media/audio/ringtones/CrazyDream.ogg");
			AssetFileDescriptor fd = getAssets().openFd("test_music.mp3");
			mPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
					fd.getDeclaredLength());

			mPlayer.prepare();
			mPlayer.setLooping(true);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		leftButton = (Button) findViewById(R.id.spk_btn_left);
		leftButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				leftEnable = !leftEnable;
				updateButtons();
			}
		});

		rightButton = (Button) findViewById(R.id.spk_btn_right);
		rightButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				rightEnable = !rightEnable;
				updateButtons();
			}
		});
		updateButtons();

		ControlButtonUtil.initControlButtonView(this);
		
	}

	protected void updateButtons() {
		leftButton.setText("left " + (leftEnable ? "enabled" : "disabled"));
		rightButton.setText("right " + (rightEnable ? "enabled" : "disabled"));

		mPlayer.setVolume(leftEnable ? 1 : 0, rightEnable ? 1 : 0);
	}

	protected void onDestroy() {
		super.onDestroy();
		mPlayer.stop();
		if (this.mPlayer == null) {
			return;
		}
		this.mPlayer.release();
		this.mPlayer = null;
	}

	protected void onPause() {
		super.onPause();
		this.mAudioManager.setStreamSolo(AudioManager.STREAM_MUSIC, false);
		this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				this.mOldVolume, 0);
		if (this.mSpeakerOn)
			return;
		this.mAudioManager.setSpeakerphoneOn(false);

	}

	protected void onResume() {
		super.onResume();
		stopMediaPlayBack();
		this.mAudioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
		int i = this.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mOldVolume = i;
		int j = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, j, 0);
		this.mSpeakerOn = this.mAudioManager.isSpeakerphoneOn();
		if (!this.mSpeakerOn) {
			this.mAudioManager.setSpeakerphoneOn(true);
		}
		this.mPlayer.start();

	}

	private void stopMediaPlayBack() {
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		sendBroadcast(i);

	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}
