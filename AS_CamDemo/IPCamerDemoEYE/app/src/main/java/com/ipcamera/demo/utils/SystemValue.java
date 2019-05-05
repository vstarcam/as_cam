package com.ipcamera.demo.utils;

public class SystemValue {
	public static String deviceName = null;
	public static String devicePass = null;
	public static String deviceId = null;

	public static boolean supportLightAndSirenO13AndO10(String ver) {
		String[] temp = ver.replace("\"","").split("\\.");
		if (temp.length == 4) {
			int second = Integer.parseInt(temp[1]);
			if ((second==53 && Integer.parseInt(temp[2])==10 && Integer.parseInt(temp[0])==220) ||    //010 固件
							(second==52 && Integer.parseInt(temp[2])==10 && Integer.parseInt(temp[0])==20)  ||    //013 摄像灯固件
							(second==53 && Integer.parseInt(temp[2])==10 && Integer.parseInt(temp[0])==20)        //013 摄像灯新固件
							|| (second==53 && Integer.parseInt(temp[2])==210 && Integer.parseInt(temp[0])==20))
				return true;
			else
				return false;
		}
		return false;
	}
}
