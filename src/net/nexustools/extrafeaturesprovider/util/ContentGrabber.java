package net.nexustools.extrafeaturesprovider.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyStoreException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import android.content.Context;

public class ContentGrabber {
	public static final int DEFAULT_BUFFER_SIZE = 512;
	private String userAgent;
	private int bufferSize;
	
	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param bufferSize
	 *            The buffer size to use while grabbing any content.
	 * @param userAgent
	 *            The user-agent to provide while grabbing any content.
	 */
	public ContentGrabber(String userAgent, int bufferSize) {
		this.userAgent = userAgent;
		this.bufferSize = bufferSize;
	}
	
	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param userAgent
	 *            The user-agent to provide while grabbing any content.
	 */
	public ContentGrabber(String userAgent) {
		this(userAgent, DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param bufferSize
	 *            The buffer size to use while grabbing any content.
	 */
	public ContentGrabber(int bufferSize) {
		this(null, bufferSize);
	}
	
	/**
	 * Constructs a default ContentGrabber.
	 */
	public ContentGrabber() {
		this(null, DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * Creates a HttpClient based off a DefaultHttpClient.
	 * 
	 * @return A rather 'normal' instance of a DefaultHttpClient.
	 */
	public HttpClient createHttpClient() {
		try {
			return createHttpClient(null, -1, null);
		} catch(KeyStoreException e) {
			e.printStackTrace();
		}
		return null; // -Shouldn't- happen.
	}
	
	/**
	 * Creates a HttpClient based off a DefaultHttpClient with the ability to load keystores/certificates for Secure HTTP, preventing errors like having "No peer certificate".
	 * 
	 * @param context
	 *            The context required that contains the resource.
	 * @param keystoreResourceId
	 *            the resource id of the generated keystore exported using <a href="http://www.bouncycastle.org/">Bouncy Castle Crypto</a>. Ex: R.raw.myKeystore
	 * @param keystorePassword
	 *            The password used for the exported keystore aforementioned.
	 * @return A rather 'normal' instance of a DefaultHttpClient.
	 * @throws KeyStoreException
	 *             If there's an issue with the keystore.
	 */
	public HttpClient createHttpClient(Context context, int keystoreResourceId, String keystorePassword) throws KeyStoreException {
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		
		SSLSocketFactory socketFactory = null;
		if(context != null && keystoreResourceId != -1) {
			KeyStore trustedKeyStoreEntries = KeyStore.getInstance("BKS");
			try {
				InputStream keyStream = context.getResources().openRawResource(keystoreResourceId);
				try {
					trustedKeyStoreEntries.load(keyStream, keystorePassword.toCharArray());
				} finally {
					keyStream.close();
				}
				socketFactory = new SSLSocketFactory(trustedKeyStoreEntries);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else
			socketFactory = SSLSocketFactory.getSocketFactory();
		
		socketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		schemeRegistry.register(new Scheme("https", socketFactory, 443));
		
		ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(defaultHttpClient.getParams(), schemeRegistry);
		return new DefaultHttpClient(connectionManager, defaultHttpClient.getParams());
	}
	
	/**
	 * Obtain a <code>InputStream</code> from a URL.
	 * 
	 * @param request
	 *            The URL to request data from.
	 * @return The InputStream constructed from the <code>request</code>
	 * @throws IOException
	 *             If there was an error connecting, or other I/O problems.
	 */
	public InputStream getInputStream(String request) throws IOException {
		try {
			return getInputStream(null, -1, null, request);
		} catch(KeyStoreException e) {
			e.printStackTrace();
		}
		return null; // -Shouldn't- happen.
	}
	
	/**
	 * Obtain a <code>InputStream</code> from a URL.
	 * 
	 * @param context
	 *            The context required that contains the resource.
	 * @param keystoreResourceId
	 *            the resource id of the generated keystore exported using <a href="http://www.bouncycastle.org/">Bouncy Castle Crypto</a>. Ex: R.raw.myKeystore
	 * @param keystorePassword
	 *            The password used for the exported keystore aforementioned.
	 * @param request
	 *            The URL to request data from.
	 * @return The InputStream constructed from the <code>request</code>
	 * @throws IOException
	 *             If there was an error connecting, or other I/O problems.
	 * @throws KeyStoreException
	 *             If there's an issue with the keystore.
	 */
	public InputStream getInputStream(Context context, int keystoreResourceId, String keystorePassword, String request) throws IOException, KeyStoreException {
		HttpGet httpGet = new HttpGet(request);
		if(userAgent != null)
			httpGet.setHeader("User-Agent", userAgent);
		HttpEntity ent = createHttpClient(context, keystoreResourceId, keystorePassword).execute(httpGet).getEntity();
		return ent.getContent();
	}
	
	/**
	 * Obtain all data from a URL.
	 * 
	 * @param request
	 *            The URL to request data from.
	 * @return A string with the content fetched.
	 * @throws IOException
	 *             If there was an error connecting, or other I/O problems.
	 */
	public String fetch(String request) throws IOException {
		try {
			return fetch(null, -1, null, request);
		} catch(KeyStoreException e) {
			e.printStackTrace();
		}
		return null; // -Shouldn't- happen.
	}
	
	/**
	 * Obtain all data from a URL.
	 * 
	 * @param context
	 *            The context required that contains the resource.
	 * @param keystoreResourceId
	 *            the resource id of the generated keystore exported using <a href="http://www.bouncycastle.org/">Bouncy Castle Crypto</a>. Ex: R.raw.myKeystore
	 * @param keystorePassword
	 *            The password used for the exported keystore aforementioned.
	 * @param request
	 *            The URL to request data from.
	 * @return A string with the content fetched.
	 * @throws IOException
	 *             If there was an error connecting, or other I/O problems.
	 * @throws KeyStoreException
	 *             If there's an issue with the keystore.
	 */
	public String fetch(Context context, int keystoreResourceId, String keystorePassword, String request) throws IOException, KeyStoreException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader reader = new InputStreamReader(getInputStream(context, keystoreResourceId, keystorePassword, request));
		char[] data = new char[bufferSize];
		int read = -1;
		while((read = reader.read(data)) != -1)
			sb.append(data, 0, read);
		reader.close();
		return sb.toString();
	}
}
