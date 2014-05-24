package net.nexustools.extrafeaturesprovider.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
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
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

public class ContentGrabber {
	/**
	 * Allowing all certificates is potentially insecure and not recommended for releases.
	 */
	public static final int NO_TIMEOUT_SET = -1;
	public static final int ALLOW_ALL_CERTIFICATES = -2;
	public static final int DEFAULT_BUFFER_SIZE = 512;
	private String userAgent;
	private int bufferSize;
	private int connectionTimeout = NO_TIMEOUT_SET;
	private int socketTimeout = NO_TIMEOUT_SET;
	private CookieJar cookieJar;
	private String encodedCredentials;
	
	private SSLContext sslContext;
	
	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param bufferSize
	 *            The optional buffer size to use while grabbing any content.
	 * @param userAgent
	 *            The optional user-agent to provide while grabbing any content.
	 * @param cookieJar
	 *            The optional cookie jar instance used to send/receive cookies.
	 * @see #ContentGrabber()
	 * @see #ContentGrabber(int)
	 * @see #ContentGrabber(String)
	 * @see #ContentGrabber(int, CookieJar)
	 * @see #ContentGrabber(String, CookieJar)
	 * @see #ContentGrabber(String, int)
	 */
	public ContentGrabber(String userAgent, int bufferSize, CookieJar cookieJar) {
		this.userAgent = userAgent;
		this.bufferSize = bufferSize;
		this.cookieJar = cookieJar;
	}

	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param bufferSize
	 *            The buffer size to use while grabbing any content.
	 * @param userAgent
	 *            The user-agent to provide while grabbing any content.
	 * @see #ContentGrabber()
	 * @see #ContentGrabber(int)
	 * @see #ContentGrabber(String)
	 * @see #ContentGrabber(int, CookieJar)
	 * @see #ContentGrabber(String, CookieJar)
	 * @see #ContentGrabber(String, int, CookieJar)
	 */
	public ContentGrabber(String userAgent, int bufferSize) {
		this(userAgent, bufferSize, null);
	}
	
	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param userAgent
	 *            The user-agent to provide while grabbing any content.
	 * @see #ContentGrabber()
	 * @see #ContentGrabber(int)
	 * @see #ContentGrabber(int, CookieJar)
	 * @see #ContentGrabber(String, CookieJar)
	 * @see #ContentGrabber(String, int)
	 * @see #ContentGrabber(String, int, CookieJar)
	 */
	public ContentGrabber(String userAgent) {
		this(userAgent, DEFAULT_BUFFER_SIZE, null);
	}

	
	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param userAgent
	 *            The user-agent to provide while grabbing any content.
	 * @param cookieJar
	 *            The cookie jar instance used to send/receive cookies.
	 * @see #ContentGrabber()
	 * @see #ContentGrabber(int)
	 * @see #ContentGrabber(String)
	 * @see #ContentGrabber(int, CookieJar)
	 * @see #ContentGrabber(String, int)
	 * @see #ContentGrabber(String, int, CookieJar)
	 */
	public ContentGrabber(String userAgent, CookieJar cookieJar) {
		this(userAgent, DEFAULT_BUFFER_SIZE, cookieJar);
	}
	
	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param bufferSize
	 *            The buffer size to use while grabbing any content.
	 * @see #ContentGrabber()
	 * @see #ContentGrabber(String)
	 * @see #ContentGrabber(int, CookieJar)
	 * @see #ContentGrabber(String, CookieJar)
	 * @see #ContentGrabber(String, int)
	 * @see #ContentGrabber(String, int, CookieJar)
	 */
	public ContentGrabber(int bufferSize) {
		this(null, bufferSize, null);
	}
	
	/**
	 * Constructs a ContentGrabber.
	 * 
	 * @param bufferSize
	 *            The buffer size to use while grabbing any content.
	 * @param cookieJar
	 *            The cookie jar instance used to send/receive cookies.
	 * @see #ContentGrabber()
	 * @see #ContentGrabber(int)
	 * @see #ContentGrabber(String)
	 * @see #ContentGrabber(String, CookieJar)
	 * @see #ContentGrabber(String, int)
	 * @see #ContentGrabber(String, int, CookieJar)
	 */
	public ContentGrabber(int bufferSize, CookieJar cookieJar) {
		this(null, bufferSize, cookieJar);
	}
	
	/**
	 * Constructs a default ContentGrabber.
	 * @see #ContentGrabber(int)
	 * @see #ContentGrabber(String)
	 * @see #ContentGrabber(int, CookieJar)
	 * @see #ContentGrabber(String, CookieJar)
	 * @see #ContentGrabber(String, int)
	 * @see #ContentGrabber(String, int, CookieJar)
	 */
	public ContentGrabber() {
		this(null, DEFAULT_BUFFER_SIZE, null);
	}
	
	/**
	 * Creates a {@link org.apache.http.client.HttpClient HttpClient} based off a {@link org.apache.http.impl.client.DefaultHttpClient DefaultHttpClient}.
	 * 
	 * @return A rather 'normal' instance of a {@link org.apache.http.impl.client.DefaultHttpClient DefaultHttpClient}.
	 */
	public HttpCookieClient createHttpClient() {
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
	public HttpCookieClient createInsecureHttpClient() {
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
	public HttpCookieClient createHttpClient(Context context, int keystoreResourceId, String keystorePassword) throws KeyStoreException {
		HttpParams httpParams = new BasicHttpParams();
		if(connectionTimeout != NO_TIMEOUT_SET)
			HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
		if(socketTimeout != NO_TIMEOUT_SET)
			HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
		
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
						@Override
						public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
						
						@Override
						public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
						
						@Override
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
		System.out.println("Cookie Jar: " + cookieJar);
		HttpCookieClient httpClient = new HttpCookieClient(connectionManager, httpParams, cookieJar);
		httpClient.setUserAgent(userAgent);
		httpClient.setEncodedCredentials(encodedCredentials);
		httpClient.getClient().setRedirectHandler(new DefaultRedirectHandler() {
			@Override
			public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
				response.setHeader("Location", response.getFirstHeader("Location").getValue().replace(" ", "%20"));
				return super.getLocationURI(response, context);
			}
		});
		return httpClient;
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
		HttpGet httpGet = new HttpGet(request.replace(" ", "%20"));
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
	 * Reads and returns all data from the stream. Closes <code>stream</code> when complete.
	 * @param stream The stream to read and return data from.
	 * @return The data from the stream.
	 * @throws IOException If there was an error connecting, or other I/O problems.
	 */
	public String read(InputStream stream) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader reader = new InputStreamReader(stream);
		char[] data = new char[bufferSize];
		int read = -1;
		while((read = reader.read(data)) != -1)
			sb.append(data, 0, read);
		reader.close();
		stream.close();
		return sb.toString();
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
		return read(getInputStream(context, keystoreResourceId, keystorePassword, request));
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public String getEncodedCredentials() {
	    return encodedCredentials;
    }

	public void setEncodedCredentials(String encodedCredentials) {
	    this.encodedCredentials = encodedCredentials;
    }
}
