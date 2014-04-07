package net.nexustools.extrafeaturesprovider.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

public class ContentGrabber {
	/**
	 * Allowing all certificates is potentially insecure and not recommended for releases.
	 */
	public static final int ALLOW_ALL_CERTIFICATES = -2;
	public static final int DEFAULT_BUFFER_SIZE = 512;
	private String userAgent;
	private int bufferSize;
	
	private SSLContext sslContext;
	
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
	 * Creates a {@link org.apache.http.client.HttpClient HttpClient} based off a {@link org.apache.http.impl.client.DefaultHttpClient DefaultHttpClient}.
	 * 
	 * @return A rather 'normal' instance of a {@link org.apache.http.impl.client.DefaultHttpClient DefaultHttpClient}.
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
	 * Creates a insecure version of the {@link org.apache.http.client.HttpClient HttpClient} which will ignore certificates.
	 * 
	 * @return An insecure version of a {@link org.apache.http.impl.client.DefaultHttpClient DefaultHttpClient}.
	 */
	public HttpClient createInsecureHttpClient() {
		try {
			return createHttpClient(null, ALLOW_ALL_CERTIFICATES, null);
		} catch(KeyStoreException e) {
			e.printStackTrace();
		}
		return null; // -Shouldn't- happen.
	}
	
	/**
	 * Creates a {@link org.apache.http.client.HttpClient HttpClient} based off a DefaultHttpClient with the ability to load keystores/certificates for Secure HTTP, preventing errors like having "No peer certificate".
	 * 
	 * @param context
	 *            The context required that contains the resource, or null if no context.
	 * @param keystoreResourceId
	 *            the resource id of the generated keystore exported using <a href="http://www.bouncycastle.org/">Bouncy Castle Crypto</a>. Ex: <code>R.raw.myKeystore</code>, or -1 if no keystore. <br />
	 *            If the resource id is <code>ALLOW_ALL_CERTIFICATES</code>, it'll provide a fake keystore allowing all certificates.
	 * @param keystorePassword
	 *            The password used for the exported keystore aforementioned, or null if no keystore.
	 * @return A rather 'normal' instance of a {@link org.apache.http.impl.client.DefaultHttpClient DefaultHttpClient}.
	 * @throws KeyStoreException
	 *             If there's an issue with the keystore.
	 * @see java.security.KeyStore
	 */
	public HttpClient createHttpClient(Context context, int keystoreResourceId, String keystorePassword) throws KeyStoreException {
		HttpParams httpParams = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		
		SSLSocketFactory socketFactory = null;
		if(context != null && keystoreResourceId != -1 && keystoreResourceId != ALLOW_ALL_CERTIFICATES) {
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
		} else if(keystoreResourceId == ALLOW_ALL_CERTIFICATES) {
			if(sslContext == null) {
				try {
					sslContext = SSLContext.getInstance("TLS");
					sslContext.init(null, new TrustManager[] {new X509TrustManager() {
						public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
						
						public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
						
						public X509Certificate[] getAcceptedIssuers() {
							return null;
						}
					}}, null);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			try {
				socketFactory = new SSLSocketFactory(null) {
					@Override
					public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
						return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
					}
					
					@Override
					public Socket createSocket() throws IOException {
						return sslContext.getSocketFactory().createSocket();
					}
				};
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else
			socketFactory = SSLSocketFactory.getSocketFactory();
		
		socketFactory.setHostnameVerifier(keystoreResourceId == ALLOW_ALL_CERTIFICATES ? SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER : SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		schemeRegistry.register(new Scheme("https", socketFactory, 443));
		
		ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		return new DefaultHttpClient(connectionManager, httpParams);
	}
	
	/**
	 * Obtain a {@link java.io.InputStream InputStream} from a URL.
	 * 
	 * @param request
	 *            The URL to request data from.
	 * @return The {@link java.io.InputStream InputStream} constructed from the <code>request</code>
	 * @throws IOException
	 *             If there was an error connecting, or other I/O problems.
	 * @throws URISyntaxException
	 *             If there was an issue with encoding the {@code request} URL with the {@link #DEFAULT_CHARSET_NAME} charset.
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
	 * Obtain a {@link java.io.InputStream InputStream} from a URL.
	 * 
	 * @param context
	 *            The context required that contains the resource.
	 * @param keystoreResourceId
	 *            the resource id of the generated keystore exported using <a href="http://www.bouncycastle.org/">Bouncy Castle Crypto</a>. Ex: R.raw.myKeystore
	 * @param keystorePassword
	 *            The password used for the exported keystore aforementioned.
	 * @param request
	 *            The URL to request data from.
	 * @return The {@link java.io.InputStream InputStream} constructed from the {@code request}
	 * @throws IOException
	 *             If there was an error connecting, or other I/O problems.
	 * @throws KeyStoreException
	 *             If there's an issue with the keystore.
	 * @see java.security.KeyStore
	 */
	public InputStream getInputStream(Context context, int keystoreResourceId, String keystorePassword, String request) throws IOException, KeyStoreException {
		HttpGet httpGet = new HttpGet(request.replaceAll(" ", "%20"));
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
	 * @throws URISyntaxException
	 *             If there was an issue with encoding the {@code request} URL with the {@link #DEFAULT_CHARSET_NAME} charset.
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
	 * @see java.security.KeyStore
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
