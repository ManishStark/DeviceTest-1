package com.rockchip.irda;

public class IrdaTestUtil {
	static { 
		System.loadLibrary("IrdaTest"); 
	} 
	private static native boolean native_testSirReceive(); 
	private static native boolean native_testSirSend(); 
	private static native boolean native_testFirReceive(); 
	private static native boolean native_testFirSend(); 

	public static boolean testSirReceive() {
		return native_testSirReceive();
	}
	public static boolean testSirSend() {
		return native_testSirSend();
	}
	public static boolean testFirReceive() {
		return native_testFirReceive();
	}
	public static boolean testFirSend() {
		return native_testFirSend();
	}
}
