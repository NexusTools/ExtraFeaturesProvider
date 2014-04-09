package net.nexustools.extrafeaturesprovider.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.SharedPreferences;


public class CookieJar {
	private SharedPreferences storagePreferences;
	private String cookieString;
	private HashMap<String, String> cookies;
	
	public CookieJar(SharedPreferences preferences) {
		cookies = new HashMap<String, String>();
		setCookiePreferences(preferences);
	}
	
	public void setCookiePreferences(SharedPreferences preferences) {
		setCookiePreferences(preferences, true);
	}
	
	public void setCookiePreferences(SharedPreferences preferences, boolean destroyOld) {
		synchronized(cookies) {
			if(destroyOld)
				destroyCookiePreferences();
			else
				saveToCookiePreferences();
			
			storagePreferences = preferences;
			
			loadFromCookiePreferences();
		}
	}
	
	public void set(String key, String value) {
		synchronized(cookies) {
			cookies.put(key, value);
			cookieString = null;
		}
	}
	
	public String getCookieString() {
		synchronized(cookies) {
			if(cookieString == null) {
				StringBuffer cookieStringBuffer = null;
				Iterator<Entry<String, String>> it = cookies.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> pairs = it.next();
					
					if(cookieStringBuffer != null) {
						cookieStringBuffer.append("; ");
					} else {
						cookieStringBuffer = new StringBuffer();
					}
					
					cookieStringBuffer.append(pairs.getKey()).append("=").append(pairs.getValue());
				}
				cookieString = cookieStringBuffer == null ? null : cookieStringBuffer.toString();
			}
			
			return cookieString;
		}
		
	}
	
	public String get(String key) {
		return cookies.get(key);
	}
	
	protected void destroyCookiePreferences() {
		synchronized(cookies) {
			if(storagePreferences == null)
				return;
			cookies.clear();
			storagePreferences.edit().clear().commit();
		}
	}
	
	public void saveToCookiePreferences() {
		synchronized(cookies) {
			if(storagePreferences == null)
				return;
			SharedPreferences.Editor cookieEditor = storagePreferences.edit();
			cookieEditor.clear();
			int pairsNum = cookies.size();
			cookieEditor.putInt("pairsNum", pairsNum);
			int index = 0;
			Iterator<Entry<String, String>> it = cookies.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> pairs = it.next();

				cookieEditor.putString("Key" + index, pairs.getKey());
				cookieEditor.putString("Value" + index, pairs.getValue());
				index++;
			}
			cookieEditor.commit();
		}
	}
	
	protected void loadFromCookiePreferences() {
		synchronized(cookies) {
			if(storagePreferences == null)
				return;
			int pairsNum = storagePreferences.getInt("pairsNum", 0);
			for(int i = 0; i < pairsNum; i++) {
				set(storagePreferences.getString("Key" + i, null), storagePreferences.getString("Value" + i, null));
			}
		}
	}
	
}