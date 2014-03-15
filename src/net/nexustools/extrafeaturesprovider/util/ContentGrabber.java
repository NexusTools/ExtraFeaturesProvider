package net.nexustools.extrafeaturesprovider.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ContentGrabber {
	public static final int DEFAULT_BUFFER_SIZE = 512;
	private String userAgent;
	private int bufferSize;
	
	public ContentGrabber(String userAgent, int bufferSize) {
		this.userAgent = userAgent;
		this.bufferSize = bufferSize;
	}
	
	public ContentGrabber(String userAgent) {
		this(userAgent, DEFAULT_BUFFER_SIZE);
	}
	
	public ContentGrabber(int bufferSize) {

		this(null, bufferSize);
	}
	
	public ContentGrabber() {
		this(null, DEFAULT_BUFFER_SIZE);
	}
	
	public InputStream getInputStream(String request) throws IOException {
		HttpGet httpGet = new HttpGet(request);
		if(userAgent != null)
			httpGet.setHeader("User-Agent", userAgent);
		HttpEntity ent = new DefaultHttpClient().execute(httpGet).getEntity();
		return ent.getContent();
	}
	
	public String fetch(String request) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader reader = new InputStreamReader(getInputStream(request));
		char[] data = new char[bufferSize];
		int read = -1;
		while((read = reader.read(data)) != -1)
			sb.append(data, 0, read);
		reader.close();
		return sb.toString();
	}
}
