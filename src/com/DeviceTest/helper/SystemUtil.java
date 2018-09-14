package com.DeviceTest.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.rockchip.newton.UserModeManager;

import android.content.pm.PackageManager;
import android.graphics.Picture;
import android.util.Log;

public class SystemUtil {
	private static final String TAG = "SystemUtil";
	public static int execShellCmdForStatue(String command) {
		int status = -1;
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = "";
			while((s = bufferedReader.readLine()) != null){
				Log.d(TAG, " >>>> " + s);
			}
			status = p.waitFor();
			Log.d(TAG, " ________________----------- command: " + command + "    status = " + status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}

	public static String execShellCmd(String command) {
		String result = "";
		Log.i("execShellCmd", command);
		try {
			Process process = Runtime.getRuntime().exec(command + "\n");
			DataOutputStream stdin = new DataOutputStream(
					process.getOutputStream());
			DataInputStream stdout = new DataInputStream(
					process.getInputStream());
			DataInputStream stderr = new DataInputStream(
					process.getErrorStream());
			String line;
			while ((line = stdout.readLine()) != null) {
				result += line + "\n";
			}
			if (result.length() > 0) {
				result = result.substring(0, result.length() - 1);
			}
			while ((line = stderr.readLine()) != null) {
				Log.e("EXEC", line);
			}
			process.waitFor();
		} catch (Exception e) {
			e.getMessage();
		}
		return result;
	}

	public static String execRootCmd(String command) {
		int userMode = UserModeManager.getCurrentUserMode();
		UserModeManager.switchToUserMode(UserModeManager.SUPER_USER_MODE);

		String result = execShellCmd("su root " + command + "\n");

		UserModeManager.switchToUserMode(userMode);

		return result;
	}

	public static String execScriptCmd(String command, String path, boolean root) {
		int userMode = UserModeManager.getCurrentUserMode();
		UserModeManager.switchToUserMode(UserModeManager.SUPER_USER_MODE);
		File tempFile = null;
		String result = "";
		Log.i("execScriptCmd", command);
		try {
			tempFile = new File(path);
			tempFile.deleteOnExit();
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(tempFile)));
			br.write("#!/system/bin/sh\n");
			br.write(command);
			br.close();
			SystemUtil.execShellCmd("su root chmod 777 "
					+ tempFile.getAbsolutePath());
			result = SystemUtil.execShellCmd((root ? "su root " : "")
					+ tempFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}
		}
		UserModeManager.switchToUserMode(userMode);
		return result;
	}

	public static boolean killProcessByPath(String exePath) {
		File dir = new File("/proc/");
		String[] files = dir.list();
		int pid = -1;
		for (String path : files) {
			File file = new File("/proc/" + path + "/cmdline");
			if (file.exists()) {
				String cmdline = execShellCmd("cat " + file.getAbsolutePath());
				if (cmdline.startsWith(exePath)) {
					try {
						pid = Integer.parseInt(path);
						break;
					} catch (Exception e) {
						break;
					}
				}
			}
		}

		if (pid >= 0) {
			int userMode = UserModeManager.getCurrentUserMode();
			UserModeManager.switchToUserMode(UserModeManager.SUPER_USER_MODE);
			execShellCmd("su root kill " + pid);
			UserModeManager.switchToUserMode(userMode);
			return true;
		}
		return false;
	}

}
