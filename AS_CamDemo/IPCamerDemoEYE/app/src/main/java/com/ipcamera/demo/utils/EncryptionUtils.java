package com.ipcamera.demo.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;


/**
 * 加密工具类
 */
public final class EncryptionUtils {
	
	/**
     * Don't let anyone instantiate this class.
     */
    private EncryptionUtils() {
        throw new Error("Do not need instantiate!");
    }
    
	// == ----------------------------------------- ==
	private static final String TAG = "EncryptionUtils";
	// 如需要小写则把ABCDEF改成小写,或结果直接转换
	private static final char HEX_DIGITS[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	

	/**
	 * 加密内容 - 32位大小MD5
	 * @param s 加密内容
	 * @return
	 */
	public final static String MD5(String s) {
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            return  toHexString(md).toLowerCase();
        } catch (Exception e) {
			e.printStackTrace();
        }
        return null;
    }
	
	/**
	 * 进行转换
	 */
	public static String toHexString(byte[] bData) {
		StringBuilder sb = new StringBuilder(bData.length * 2);
		for (int i = 0; i < bData.length; i++) {
			sb.append(HEX_DIGITS[(bData[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[bData[i] & 0x0f]);
		}
		return sb.toString();
	}
	
	/**
	 * 获取文件MD5值
	 * @param fileName 文件地址
	 * @return
	 */
	public static String getHash(String fileName){
		try {
			InputStream fis = new FileInputStream(fileName);
			byte[] buffer = new byte[1024];
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			return toHexString(md5.digest());
		} catch (Exception e) {
		}
		return null;
	}
	

	/**
	 * 获取请求加密字符串    test+当前时间+随机数
	 * @param rNumber 随机数
	 * @return time 当前时间
	 */
	public static String getRequestEncryp(String clientCode,String time, String rNumber){
		return MD5(clientCode +time+ rNumber).toLowerCase();
	}

}
