package net.nexustools.extrafeaturesprovider.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoHelper {
	
	// Originally from http://fancifulandroid.blogspot.ca/2014/01/android-convert-string-to-md5-properly.html
	public static String MD5(String message) {
		try {
			MessageDigest messageDigester = MessageDigest.getInstance("MD5");
			byte[] digested = messageDigester.digest(String.valueOf(message).getBytes());
			
			StringBuffer stringBuffer = new StringBuffer();
			for(int i = 0; i < digested.length; i++)
				stringBuffer.append(Integer.toHexString((digested[i] & 0xFF) | 0x100).substring(1, 3));
			
			return stringBuffer.toString();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
