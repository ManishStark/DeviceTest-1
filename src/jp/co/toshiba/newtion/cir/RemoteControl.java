package jp.co.toshiba.newtion.cir;

import java.io.IOException;

import jp.co.ntt.east.hardware.IrRemoteController;
import jp.co.ntt.east.hardware.IrRemoteController.Data;
import android.util.Log;

public class RemoteControl {
	private static final String TAG = "NewtonTest";

	private static final int SEND_TIMEOUT = 1;

	private static final int CARRIER_HIGH_TIME = 130;

	private static final int CARRIER_LOW_TIME = 130;

	private static final int PULSE_0_HIGH_TIME = 560;

	private static final int PULSE_0_LOW_TIME = 560;

	private static final int PULSE_1_HIGH_TIME = 1690;

	private static final int PULSE_1_LOW_TIME = 560;

	private static final int START_HIGH_TIME = 9000;

	private static final int START_LOW_TIME = 4500;

	private static final int STOP_HIGH_TIME = 560;

	private static final int REPEAT_HIGH_TIME = 9000;

	private static final int REPEAT_LOW_TIME = 2250;

	private static final int DURATION_TIME = 1080;

	private static final int REPEAT_COUNT = 1;

	private static final byte CUSTOM_CODE = (byte) 0x40;

	public static final byte COMMAND_POWER = (byte) 0x12;

	public static final byte COMMAND_CHANNEL_BASE = (byte) 0x01;

	public static final byte COMMAND_CHANNEL_UP = (byte) 0x1b;

	public static final byte COMMAND_CHANNEL_DOWN = (byte) 0x1f;

	public static final byte COMMAND_VOLUME_UP = (byte) 0x1a;

	public static final byte COMMAND_VOLUME_DOWN = (byte) 0x1e;

	public static final byte COMMAND_MUTE = (byte) 0x10;

	public static final byte COMMAND_INPUT_SWITCH = (byte) 0x0f;

	public static final int CHANNEL_MIN = 1;

	public static final int CHANNEL_MAX = 12;

	public static boolean sendCommand(byte command) {
		boolean returnValue = false;
		byte sendData[] = new byte[4];

		sendData[0] = (byte) (CUSTOM_CODE);
		sendData[1] = (byte) (~CUSTOM_CODE);

		sendData[2] = (byte) command;
		sendData[3] = (byte) (~command);

		IrRemoteController.Data irData1 = new IrRemoteController.Data();
		irData1.setCarrier(CARRIER_HIGH_TIME, CARRIER_LOW_TIME);
		irData1.setPulse(IrRemoteController.Data.HIGH_LOW, PULSE_0_HIGH_TIME,
				PULSE_0_LOW_TIME, IrRemoteController.Data.HIGH_LOW,
				PULSE_1_HIGH_TIME, PULSE_1_LOW_TIME);
		irData1.setParameter(START_HIGH_TIME, START_LOW_TIME, STOP_HIGH_TIME);
		irData1.setData(sendData, sendData.length * 8);
		irData1.setDuration(DURATION_TIME);
		irData1.setRepeatCount(REPEAT_COUNT);

		IrRemoteController irController = IrRemoteController.getInstance();
		IrRemoteController.Data irDatas[] = { irData1 };

		returnValue = true;
		try {
			irController.send(irDatas, SEND_TIMEOUT);
		} catch (IllegalStateException e) {
		}

		catch (IOException ex) {
			returnValue = false;
			Log.e(TAG, ex.getMessage());
		}

		return returnValue;
	}

	public static boolean sendChannelCommand(int channel) {
		boolean returnValue = false;

		if (CHANNEL_MIN <= channel && channel <= CHANNEL_MAX) {
			byte commandValue = (byte) (channel - CHANNEL_MIN + COMMAND_CHANNEL_BASE);
			boolean bResult = sendCommand(commandValue);
			if (bResult) {
				returnValue = true;
			}
		}

		return returnValue;
	}
}
