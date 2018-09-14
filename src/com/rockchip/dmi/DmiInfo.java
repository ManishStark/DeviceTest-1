package com.rockchip.dmi;

public class DmiInfo {
	private String manufacture;
	private String productName;
	private String version;
	private String serialNumber;
	private byte[] UUID;
	private String OEM_Strings;
	private String terminalID;

	private static DmiInfo instance;

	private DmiInfo(Object[] dmiInfo) {

		manufacture = (String) dmiInfo[0];
		productName = (String) dmiInfo[1];
		version = (String) dmiInfo[2];
		serialNumber = (String) dmiInfo[3];

		UUID = (byte[]) dmiInfo[4];

		OEM_Strings = (String) dmiInfo[5];
		terminalID = (String) dmiInfo[6];
	}

	public String toString() {
		String str = "";
		str += "Manufacture:" + manufacture + "\n";
		str += "ProductName:" + productName + "\n";
		str += "Version:" + version + "\n";
		str += "SerialNumber:" + serialNumber + "\n";

		str += "UUID:";
		for (int i = 0; i < UUID.length; i++) {
			str += String.format("%02X", UUID[i]);
		}
		str += "\n";

		str += "OEM_Strings:" + OEM_Strings + "\n";
		str += "TerminalID:" + terminalID + "\n";
		return str;
	}

	protected static DmiInfo getInstance() {
		return instance;
	}

	protected static DmiInfo getInstance(Object[] dmiInfo) {
		if (dmiInfo == null) {
			instance = null;
		} else {
			try {
				instance = new DmiInfo(dmiInfo);
			} catch (Exception e) {
				instance = null;
			}
		}
		return instance;
	}

	public String getManufacture() {
		return manufacture;
	}

	public String getProductName() {
		return productName;
	}

	public String getVersion() {
		return version;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public byte[] getUUID() {
		return UUID;
	}

	public String getOEM_Strings() {
		return OEM_Strings;
	}

	public String getTerminalID() {
		return terminalID;
	}
}
