package com.heyijoy.sdk.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.heyijoy.sdk.utils.Base64;
import com.heyijoy.sdk.utils.GUtils;
import com.heyijoy.sdk.CallBack;
import com.heyijoy.sdk.HeyiJoySDK;

import android.os.AsyncTask;
import android.util.Log;

public class HYHttpApi {

	public static final int TIMEOUT = 3 * 1000;
	private static HYHttpApi instance;
	public final static String NETWORK_DESC = null;

	private HYHttpApi() {

	}

	public static HYHttpApi getInstance() {
		if (instance == null) {
			instance = new HYHttpApi();
		}
		return instance;
	}

	/**
	 * Https GET 请求
	 * 
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public void httpsGet(final String urlStr, final CallBack callBack) {
		AsyncTask<Void, Integer, String> task = new AsyncTask<Void, Integer, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return doGet(urlStr);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callBack.callBack(result);
			}

		};
		task.execute();
	}

	public String doGet(String urlStr) {

		try {
			HttpURLConnection http = null;
			URL url = new URL(urlStr);
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			http = https;

			http.setDoInput(true);
			http.setDoOutput(true);
			http.setUseCaches(false);
			http.setConnectTimeout(TIMEOUT);// 设置超时时间
			http.setReadTimeout(TIMEOUT);
			http.setRequestMethod("GET");// 设置请求类型为get
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Language", "zh-cn");
			http.setRequestProperty("Connection", "keep-alive");
			http.setRequestProperty("Cache-Control", "no-cache");
			http.setRequestProperty("Charset", "UTF-8");
			int resCode;
			resCode = http.getResponseCode();
			Log.i("HeyiJoySDK", "get_resCode=" + resCode);
			InputStream input = resCode == 200 || resCode == 201 ? http.getInputStream() : http.getErrorStream();
			BufferedReader data = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String strLine = "";
			StringBuffer sbResult = null;
			while ((strLine = data.readLine()) != null) {
				if (null == sbResult) {
					sbResult = new StringBuffer();
				}
				sbResult.append(strLine);
			}

			input.close();
			http.disconnect();

			if (sbResult == null) {
				return null;
			}
			return sbResult.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Https POST 请求
	 * 
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public void httpsPost(final String urlStr, final String mparams, final CallBack callBack) {
		AsyncTask<Void, Integer, String> task = new AsyncTask<Void, Integer, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return doPosts(urlStr, mparams);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callBack.callBack(result);
			}
		};
		task.execute();
	}

	public String doPosts(String urlStr, String params) {
		try {
			String paramsEncoded = "";
			if (params != null) {
				paramsEncoded = Base64.encode(params.getBytes());
				paramsEncoded = URLEncoder.encode(paramsEncoded, "UTF-8");
			}
			trustAllHosts();
			HttpURLConnection http = null;
			URL url = new URL(urlStr);
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			https.setHostnameVerifier(DO_NOT_VERIFY);
			http = https;
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setUseCaches(false);
			http.setConnectTimeout(TIMEOUT);// 设置超时时间
			http.setReadTimeout(TIMEOUT);
			http.setRequestMethod("POST");// 设置请求类型为post
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Language", "zh-cn");
			http.setRequestProperty("Connection", "keep-alive");
			http.setRequestProperty("Cache-Control", "no-cache");
			http.setRequestProperty("Charset", "UTF-8");

			String toBeSent = "&data=" + paramsEncoded;//+ "&debug=1"; //
			DataOutputStream out = new DataOutputStream(http.getOutputStream());
			out.write(toBeSent.getBytes("UTF-8"));
			out.flush();
			out.close();

			int resCode = 0;
			resCode = http.getResponseCode();
			InputStream input = resCode == 200 || resCode == 201 ? http.getInputStream() : http.getErrorStream();
			BufferedReader data = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String strLine = "";
			StringBuffer sbResult = null;
			while ((strLine = data.readLine()) != null) {
				if (null == sbResult) {
					sbResult = new StringBuffer();
				}
				sbResult.append(strLine);
			}
			input.close();
			http.disconnect();

			if (sbResult == null) {
				return null;
			}
			return sbResult.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("HeyiJoySDK", "exception message = " + ex.getMessage());
		}
		return null;
	}

	/**
	 * https请求中，信任所有主机-对于任何证书都不做检查
	 * 
	 * @Title: trustAllHosts
	 * @return void
	 * @date 2012-3-6 下午7:47:41
	 */
	public static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		// Android 采用X509的证书信息机制
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {

			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {

			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
}