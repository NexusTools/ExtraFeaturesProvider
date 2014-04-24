package net.nexustools.extrafeaturesprovider.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class HttpCookieClient {
	private static final Pattern COOKIE_PAIR_PATTERN = Pattern.compile("^([^=]+)=([^;]+);");
	
	private CookieJar cookieJar;
	private DefaultHttpClient httpClient;
	
	public HttpCookieClient(ClientConnectionManager httpManager, HttpParams httpParams, CookieJar jar) {
		httpClient = new DefaultHttpClient(httpManager, httpParams);
		setCookieJar(jar);
	}
	
	public void setCookieJar(CookieJar jar) {
		cookieJar = jar;
	}
	
	public CookieJar getCookieJar() {
		return cookieJar;
	}
	
	protected HttpRequest preProcessRequest(HttpRequest request) {
		if(cookieJar != null) {
			String cookie = cookieJar.getCookieString();
			System.out.println("Cookie " + cookie);
			if(cookie != null)
				request.setHeader("Cookie", cookie);
		}
		return request;
	}
	
	protected <T> T postProcessResponse(T t) {
		if(cookieJar != null) {
			HttpResponse response = (HttpResponse) t;
			Header[] cookieHeaders = response.getHeaders("Set-Cookie");
			for(Header cookieHeader : cookieHeaders) {
				System.out.println("cookieHeader " + cookieHeader.getValue());
				Matcher cookiePairMatcher = COOKIE_PAIR_PATTERN.matcher(cookieHeader.getValue());
				if(cookiePairMatcher.find()) {
					String cookieValue = cookiePairMatcher.group(2);
					System.out.println("Adding cookie id: " + cookiePairMatcher.group(1) + " to: " + cookieValue);
					if(cookieValue.equals("deleted"))
						cookieJar.remove(cookieValue);
					else
						cookieJar.set(cookiePairMatcher.group(1), cookieValue);
				} else {
					System.err.println("No cookie found!");
				}
			}
			
			if(cookieHeaders.length > 0)
				cookieJar.saveToCookiePreferences();
		}
		return (T) t;
	}
	
	public DefaultHttpClient getClient() {
		return httpClient;
	}
	
	public HttpResponse execute(HttpUriRequest request) throws ClientProtocolException, IOException {
		return postProcessResponse(httpClient.execute((HttpUriRequest) preProcessRequest(request)));
	}
	
	public HttpResponse execute(HttpHost target, HttpUriRequest request) throws ClientProtocolException, IOException {
		return postProcessResponse(httpClient.execute(target, (HttpUriRequest) preProcessRequest(request)));
	}
	
	public HttpResponse execute(HttpHost target, HttpUriRequest request, HttpContext context) throws ClientProtocolException, IOException {
		return postProcessResponse(httpClient.execute(target, (HttpUriRequest) preProcessRequest(request), context));
	}
}
