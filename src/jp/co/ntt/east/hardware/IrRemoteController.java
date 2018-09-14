
package jp.co.ntt.east.hardware;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.view.KeyEvent;

public class IrRemoteController {
	
	static {
		System.loadLibrary("rk29_cir");
	}
	
	private static final int SEND_WAIT_TIME = 250; 
	private static IrRemoteController instance;
	private volatile boolean isRunning;
	private IrRemoteController.Data[] data;
	private int timeout;
	private int count;
	private Timer timer;
	
	public static synchronized IrRemoteController getInstance() {
		if (instance == null) {
			instance = new IrRemoteController();
		}
		return instance;
	}
	
	public void send(IrRemoteController.Data[] data, int timeout) throws IOException {
		send(data, timeout, -1);
	}
	
	public void send(IrRemoteController.Data[] data, int timeout, int count) throws IOException {
		if (isRunning) {
			throw new IllegalStateException("IrRemote is running.");
		}
		this.data = data;
		this.timeout = timeout;
		this.count = count;
		
		isRunning = true;
		new SendThread().start();
		timer.schedule(new TimerThread(), (long)timeout * 1000L);
	}
	
	public void stop() throws IOException {
		isRunning = false;
		native_hal_stop();
		timer.cancel();
	}
	
	private IrRemoteController() {
		timer = new Timer();
	}
	
	private static native int native_hal_init();
	private static native int native_hal_deinit();
	private static native int native_hal_send();
	private static native int native_hal_stop();
	private static native int native_hal_set_formate(int high, int low, byte[] data,
			int length, int duration, int startHigh, int startLow, int stopHigh,
			int data0Pattern, int data0High, int data0Low, int data1Pattern, 
			int data1High, int data1Low, int count);
	
	public static class Data {
		public static final int HIGH_LOW = 1;
		public static final int INFINITE = 0;
		public static final int LOW_HIGH = 2;
		
		int carry_high;
		int carry_low;
		int data0Pattern;
		int data0High;
		int data0Low;
		int data1Pattern;
		int data1High;
		int data1Low;
		int startHigh;
		int startLow;
		int stopHigh;
		byte[] data;
		int length;
		int count;
		int duration;
		
		public void setCarrier(int high, int low) {
			this.carry_high = (int)(high / 10.0);
			this.carry_low = (int)(low / 10.0);
		}
		
		public void setPulse(int data0Pattern, int data0High, int data0Low,
				int data1Pattern, int data1High, int data1Low) {
			this.data0Pattern = (data0Pattern == HIGH_LOW) ? 0 : 1;
			this.data0High = data0High;
			this.data0Low = data0Low;
			this.data1Pattern = (data1Pattern  == HIGH_LOW) ? 0 : 1;
			this.data1High = data1High;
			this.data1Low = data1Low;			
		}
		
		public void setParameter(int startHigh, int startLow, int stopHigh) {
			this.startHigh = startHigh;
			this.startLow = startLow;
			this.stopHigh = stopHigh;
		}
		
		public void setData(byte[] data, int length) {
			this.data = data;
			this.length = length;
		}
		
		public void setRepeatCount(int count) {
			this.count = (count == INFINITE) ? -1 : count;
		}
		
		public void setDuration(int duration) {
			this.duration = duration;
		}
	}
	
	private class SendThread extends Thread {
		
		public void run() {
			native_hal_init();
			for (int i = 0; i < data.length && isRunning; i++) {
				Data d = data[i];
				native_hal_set_formate(d.carry_high, d.carry_low, d.data, d.length, d.duration,
						d.startHigh, d.startLow, d.stopHigh, d.data0Pattern, d.data0High, 
						d.data0Low, d.data1Pattern, d.data1High, d.data1Low, d.count);
				native_hal_send();
			}
			
			try {
				Thread.sleep(SEND_WAIT_TIME);
			}
			catch (InterruptedException e) {
				;
			}
			isRunning = false;
			timer.cancel();
			native_hal_deinit();
		}
	}
	
	private class TimerThread extends TimerTask {
		
		public void run() {
			if (isRunning) {
				isRunning = false;
				native_hal_stop();
			}
		}
	}
}
