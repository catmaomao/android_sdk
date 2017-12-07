package com.heyijoy.gamesdk.http;import android.os.AsyncTask;import com.heyijoy.gamesdk.act.GameSDKApplication;import com.heyijoy.gamesdk.constants.HYConstant;import com.heyijoy.gamesdk.util.EncryptUtils;import com.heyijoy.gamesdk.util.Logger;import com.heyijoy.gamesdk.util.Util;import org.apache.http.HttpEntity;import org.apache.http.HttpResponse;import org.apache.http.HttpStatus;import org.apache.http.NameValuePair;import org.apache.http.client.ClientProtocolException;import org.apache.http.client.HttpClient;import org.apache.http.client.entity.UrlEncodedFormEntity;import org.apache.http.client.methods.HttpPost;import org.apache.http.impl.client.DefaultHttpClient;import java.io.BufferedReader;import java.io.DataOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.net.HttpURLConnection;import java.net.URL;import java.net.URLDecoder;import java.net.URLEncoder;import java.util.Iterator;import java.util.List;import java.util.TreeMap;import javax.net.ssl.HostnameVerifier;import javax.net.ssl.HttpsURLConnection;import javax.net.ssl.SSLContext;import javax.net.ssl.SSLSession;import javax.net.ssl.TrustManager;import javax.net.ssl.X509TrustManager;/** * * HTTP请求类 使用方法示例： Initial initial = new Initial(); IHttpRequest httpRequest = * YoukuService.getService(IHttpRequest.class, true); HttpIntent httpIntent = * new HttpIntent(URLContainer.getInitURL()); *  * httpRequest.request(httpIntent, new IHttpRequestCallBack() { *  * @Override public void onSuccess(HttpRequestManager httpRequestManager) { *           initial = httpRequestManager.parse(initial); } * @Override public void onFailed(String failReason) { *  *           } }); *  * @author msh Add： if (TextUtils.isEmpty(cookie)) { throw new *         NullPointerException("the cookie is not setted!"); } else { *         conn.setRequestProperty("Cookie", cookie); } Cookie 使用示例： *         HttpRequestManager httpRequestTask = new HttpRequestManager(); *         httpRequestTask.setCookie(cookie); HttpIntent httpIntent = new *         HttpIntent(URLContainer.getPlayHistoryInCloud(), true); */public class HttpRequestManager implements IHttpRequest {	public static final int SUCCESS = 0x1;	public static final int FAIL = 0x2;	public static final String HTTP = "HTTP";	public static final String HTTPS = "HTTPS";	public static final String COOKIE_OVERDUE = "login first";	/**	 * 执行状态	 */	private int state = FAIL;	public static final int TIMEOUT = 5 * 1000;	public static final String METHOD_GET = "GET";	public static final String METHOD_POST = "POST";	public static final String METHOD_HOTWIND_POST = "HOTWIND_POST";	private String cookie;	// private HYAsyncTask<Object, Integer, Object> task;	private AsyncTask<Object, Integer, Object> task;	/**	 * 默认连接超时, 默认读取超时	 */	private int connect_timeout_millis, read_timout_millis;	/**	 * 无网络连接错误提示	 */	public static final String STATE_ERROR_WITHOUT_NETWORK = "无网络连接，请检查后重试";	public static final String STATE_ERROR_NETWORK_NOTWELL = "连接超时，请稍后再试";	/**	 * 失败原因	 */	public String fail_reason = "";	private String taskFlag = "";	/**	 * 传递过来需要解析的数据对象	 */	// private Object dataObject;	/**	 * 从网络拿到的数据	 */	private String dataString;	/**	 * 请求的url地址	 */	public String uri;	public HttpRequestManager() {	}	/**	 * 下载给出Uri的数据	 * 	 * @return	 * @throws NullPointerException	 */	private String downloadUri(HttpIntent httpIntent) throws NullPointerException {		if (Util.hasInternet(GameSDKApplication.getInstance().getContext())) {			// 所有请求公共的参数			httpIntent.putParams("appkey", GameSDKApplication.getInstance().getAppkey());			if (!"".equals(GameSDKApplication.getInstance().getImei())) {				httpIntent.putParams("imei", GameSDKApplication.getInstance().getImei());			}			httpIntent.putParams("sdk_ver", HYConstant.getSDKVersion());			httpIntent.putParams("sdk_lang", HYConstant.LANGUAGE);			httpIntent.putParams("sdk_platform", HYConstant.ANDROID);			httpIntent.putParams("sdk_api_ver", HYConstant.SDK_API_VER);			httpIntent.putParams("rkey", System.currentTimeMillis() + "");			if (HttpRequestManager.METHOD_HOTWIND_POST.equals(httpIntent.getStringExtra(HttpIntent.METHOD))) {// 热云统计				dataString = hotWindPost(httpIntent.getStringExtra(HttpIntent.URI), httpIntent.getStringExtra("data"));			} else if (HttpRequestManager.METHOD_GET.equals(httpIntent.getStringExtra(HttpIntent.METHOD))) {				dataString = get(httpIntent.getStringExtra(HttpIntent.URI), httpIntent.getParams());			} else {				dataString = post(httpIntent.getStringExtra(HttpIntent.URI), httpIntent.getParams(), null);			}			if (dataString == null) {				state = FAIL;				fail_reason = STATE_ERROR_NETWORK_NOTWELL;			}			return dataString;		} else {			state = FAIL;			fail_reason = STATE_ERROR_WITHOUT_NETWORK;			if (GameSDKApplication.getInstance().getFlagForException()) {				GameSDKApplication.getInstance().setSend(true);			}			// Toast.makeText(GameSDKApplication.getInstance().getContext(),			// fail_reason, Toast.LENGTH_SHORT).show();			return dataString;		}	}	@Override	public void request(final HttpIntent httpIntent, final IHttpRequestCallBack callBack) {		// task = new HYAsyncTask<Object, Integer, Object>() {		task = new AsyncTask<Object, Integer, Object>() {			@Override			protected Object doInBackground(Object... params) {				return downloadUri(httpIntent);			}			@Override			protected void onPostExecute(Object result) {				super.onPostExecute(result);				switch (state) {				case SUCCESS:					if (callBack != null)						callBack.onSuccess(HttpRequestManager.this);					break;				case FAIL:					if (callBack != null) {						Logger.d("HttpRequestManager.request(...).new HYAsyncTask() {...}#onPostExecute()",								fail_reason);						callBack.onFailed(HYConstant.EXCEPTION_CODE, fail_reason);					}					break;				}			}		};		task.execute();	}	/**	 * 获取微信相关信息	 * 	 * @param url	 * @param callBack	 */	public void requestWX(final String url, final IHttpRequestCallBack callBack) {		task = new AsyncTask<Object, Integer, Object>() {			@Override			protected Object doInBackground(Object... params) {				try {					return downloadUriWX(url);				} catch (Exception e) {					e.printStackTrace();				}				return "";			}			@Override			protected void onPostExecute(Object result) {				super.onPostExecute(result);				switch (state) {				case SUCCESS:					if (callBack != null)						callBack.onSuccess(HttpRequestManager.this);					break;				case FAIL:					if (callBack != null) {						Logger.d("HttpRequestManager.request(...).new HYAsyncTask() {...}#onPostExecute()",								fail_reason);						callBack.onFailed(HYConstant.EXCEPTION_CODE, fail_reason);					}					break;				}			}		};		task.execute();	}	private String downloadUriWX(String url) throws Exception {		if (Util.hasInternet(GameSDKApplication.getInstance().getContext())) {			dataString = getWX(url);			if (dataString == null) {				state = FAIL;				fail_reason = STATE_ERROR_NETWORK_NOTWELL;			}			return dataString;		} else {			state = FAIL;			fail_reason = STATE_ERROR_WITHOUT_NETWORK;			return dataString;		}	}	public void request(final HttpIntent httpIntent, final IHttpRequestCallBack callBack, String taskFlag) {		if (taskFlag == null) {			taskFlag = "";		}		this.taskFlag = taskFlag;		request(httpIntent, callBack);	}	@Override	public void cancel() {		if (null != task && !task.isCancelled()) {			task.cancel(true);		}	}	@Override	public String getDataString() {		return dataString;	}	@Override	public void setCookie(String cookie) {		this.cookie = cookie;	}	/*	 * private static String get(String uri,List<NameValuePair> params){	 * BufferedReader reader = null; StringBuffer sb = null; String result = "";	 * HttpClient client = new DefaultHttpClient(); String paramStr = "";	 * for(int i = 0;i<params.size();i++){ String name =	 * params.get(i).getName(); String value = params.get(i).getValue();	 * paramStr += paramStr = "&" + name + "=" + value; }	 * 	 * if (!paramStr.equals("")) { paramStr = paramStr.replaceFirst("&", "?");	 * uri += paramStr; } HttpGet request = new HttpGet(uri); try { //发送请求，得到响应	 * HttpResponse response = client.execute(request); //请求成功 if	 * (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){ reader =	 * new BufferedReader(new	 * InputStreamReader(response.getEntity().getContent())); sb = new	 * StringBuffer(); String line = ""; while((line = reader.readLine()) !=	 * null){ sb.append(line); } } } catch (ClientProtocolException e) {	 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }	 * finally{ try { if (null != reader){ reader.close(); reader = null; } }	 * catch (IOException e) { e.printStackTrace(); } } if (null != sb){ result	 * = sb.toString(); } return result; }	 *//**		 * 以post方式发送请求，访问web		 * 		 * @param uri		 *            web地址		 * @return 响应数据		 */	private static String post(String uri, List<NameValuePair> params) {		BufferedReader reader = null;		StringBuffer sb = null;		String result = "";		HttpClient client = new DefaultHttpClient();		HttpPost request = new HttpPost(uri);		try {			// 设置字符集			HttpEntity entity = new UrlEncodedFormEntity(params, "utf-8");			// 请求对象			request.setEntity(entity);			// 发送请求			HttpResponse response = client.execute(request);			// 请求成功			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));				sb = new StringBuffer();				String line = "";				while ((line = reader.readLine()) != null) {					sb.append(line);				}			}		} catch (ClientProtocolException e) {			e.printStackTrace();		} catch (IOException e) {			e.printStackTrace();		} finally {			try {				// 关闭流				if (null != reader) {					reader.close();					reader = null;				}			} catch (IOException e) {				e.printStackTrace();			}		}		if (null != sb) {			result = sb.toString();		}		return result;	}	/**	 * http/https post请求，参数在body中	 * 	 * @Title: post	 * @param strUrl	 * @return String	 * @date 2012-3-6 下午7:50:50	 */	public String post(String strUrl, List<NameValuePair> list, String hasCookie) {		try {			TreeMap<String, String> treeMap = new TreeMap<String, String>();			HttpURLConnection http = null;			URL url = new URL(strUrl);			String paras = "";			String signStr = "";			for (int i = 0; i < list.size(); i++) {				String name = list.get(i).getName();				String value = list.get(i).getValue();//				Log.e(HYConstant.TAG, "value1="+value);				if ("username".equals(name)) {					value = URLEncoder.encode(value, "utf-8");//					Log.e(HYConstant.TAG, "value2="+value);				}				treeMap.put(name, value);// 重组排序			}			Iterator<String> iterator = treeMap.keySet().iterator();			while (iterator.hasNext()) {				String name = iterator.next();				String value = treeMap.get(name);				paras += paras = "&" + name + "=" + value;				signStr += signStr = "\002" + name + "\001" + value;			}			signStr = signStr + "\003" + GameSDKApplication.getInstance().getAppPrivateKey();			// 去掉最前面多余的&			if (!paras.equals("")) {				paras = paras.replaceFirst("&", "");				signStr = signStr.replaceFirst("\002", "");			}			paras = paras.replace(" ", "_");			signStr = signStr.replace(" ", "_");			// if (url.getProtocol().toLowerCase().equals("https")) {			trustAllHosts();			String sign = EncryptUtils.md5(EncryptUtils.getEncryption(HYConstant.HEYIJOY_KEY)					+ EncryptUtils.md5(URLDecoder.decode(signStr)).toLowerCase()).toLowerCase();			paras += "&sign=" + sign;			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();			https.setHostnameVerifier(DO_NOT_VERIFY);			http = https;			// }			Logger.v(strUrl + "-----Post请求参数---" + paras);			http.setDoInput(true);			http.setDoOutput(true);			http.setUseCaches(false);			http.setConnectTimeout(TIMEOUT);// 设置超时时间			http.setReadTimeout(TIMEOUT);			http.setRequestMethod("POST");// 设置请求类型为post			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");			http.setRequestProperty("Content-Language", "zh-cn");			http.setRequestProperty("Connection", "keep-alive");			http.setRequestProperty("Cache-Control", "no-cache");			// http.setRequestProperty("Accept", "*");// 设置接收类型			String UA = "GameSDK" + ";" + HYConstant.getSDKVersion() + ";" + "Android" + ";"					+ android.os.Build.VERSION.RELEASE + ";" + android.os.Build.MODEL;			http.setRequestProperty("User-Agent", UA);// 设置UserAgent			http.setRequestProperty("Charset", "UTF-8");//			http.setRequestProperty("Cookie", GameSDKApplication.getInstance().getCookie());			DataOutputStream out = new DataOutputStream(http.getOutputStream());			out.writeBytes(paras);			// if (datas != null) {			// out.write(datas);			// }			out.flush();			out.close();			int resCode;			try {				if (GameSDKApplication.getInstance().getFlagForException()) {					GameSDKApplication.getInstance().setSend(true);				}				resCode = http.getResponseCode();				Logger.v(strUrl + "-----返回码---" + resCode);			} catch (IOException e) {// 修正java bug - Received authentication										// challenge is										// null，这时access_token已过期，清除access_token，续传时重新获取				e.printStackTrace();				if (e.getMessage() != null && (e.getMessage().equals("Received authentication challenge is null")						|| e.getMessage().equals("No authentication challenges found"))) {					resCode = 401;					fail_reason = COOKIE_OVERDUE;					return "{\"status\":\"failed\",\"code\":401,\"desc\":\"login first\"}";// 模拟cookie过期的返回值					// UploadConfig.saveUploadAccessToken("");				}				return null;			}			if (resCode == 200) {				String cookieVal = null;				String cookie = "";				String key = null;				for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {					if (key.equalsIgnoreCase("Set-Cookie")) {						cookieVal = http.getHeaderField(i);						cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));						cookie = cookie + cookieVal + ";";					}				}				Logger.d("ykyt", "COOKIE---get=:" + cookie);				if (cookie != null && !"".equals(cookie)) {					GameSDKApplication.getInstance().saveCookie(cookie);				}			} else {				state = FAIL;			}			/*			 * if (resCode == 302) { post(http.getHeaderField("location"),			 * paras, datas); }			 */			InputStream input = resCode == 200 || resCode == 201 ? http.getInputStream() : http.getErrorStream();			BufferedReader data = new BufferedReader(new InputStreamReader(input, "UTF-8"));			String strLine = "";			StringBuffer sbResult = null;			while ((strLine = data.readLine()) != null) {				if (null == sbResult) {					sbResult = new StringBuffer();				}				sbResult.append(strLine);			}			Logger.v(strUrl + "-----返回值---" + sbResult);			input.close();			http.disconnect();			if (sbResult == null) {				return null;			}			/*			 * JSONObject json_result = new JSONObject(sbResult.toString());			 * return json_result;			 */			state = SUCCESS;			return sbResult.toString();		} catch (Exception ex) {			state = FAIL;			ex.printStackTrace();		}		return null;	}	public String hotWindPost(String strUrl, String paras) {		try {			HttpURLConnection http = null;			URL url = new URL(strUrl);			http = (HttpURLConnection) url.openConnection();			Logger.v(strUrl + "-----Post请求参数---" + paras);			http.setDoInput(true);			http.setDoOutput(true);			http.setUseCaches(false);			http.setConnectTimeout(TIMEOUT);// 设置超时时间			http.setReadTimeout(TIMEOUT);			http.setRequestMethod("POST");// 设置请求类型为post			http.setRequestProperty("Content-Type", "application/json");			http.setRequestProperty("Content-Language", "zh-cn");			http.setRequestProperty("Connection", "keep-alive");			http.setRequestProperty("Cache-Control", "no-cache");			// http.setRequestProperty("Accept", "*");// 设置接收类型			String UA = "GameSDK" + ";" + HYConstant.getSDKVersion() + ";" + "Android" + ";"					+ android.os.Build.VERSION.RELEASE + ";" + android.os.Build.MODEL;			http.setRequestProperty("User-Agent", UA);// 设置UserAgent			http.setRequestProperty("Charset", "UTF-8");//			http.setRequestProperty("Cookie", GameSDKApplication.getInstance().getCookie());			DataOutputStream out = new DataOutputStream(http.getOutputStream());			out.writeBytes(paras);			// if (datas != null) {			// out.write(datas);			// }			out.flush();			out.close();			int resCode;			try {				if (GameSDKApplication.getInstance().getFlagForException()) {					GameSDKApplication.getInstance().setSend(true);				}				resCode = http.getResponseCode();				Logger.v(strUrl + "-----返回码---" + resCode);			} catch (IOException e) {// 修正java bug - Received authentication				// challenge is				// null，这时access_token已过期，清除access_token，续传时重新获取				e.printStackTrace();				if (e.getMessage() != null && (e.getMessage().equals("Received authentication challenge is null")						|| e.getMessage().equals("No authentication challenges found"))) {					resCode = 401;					fail_reason = COOKIE_OVERDUE;					return "{\"status\":\"failed\",\"code\":401,\"desc\":\"login first\"}";// 模拟cookie过期的返回值					// UploadConfig.saveUploadAccessToken("");				}				return null;			}			if (resCode == 200) {			} else {				state = FAIL;			}			InputStream input = resCode == 200 || resCode == 201 ? http.getInputStream() : http.getErrorStream();			BufferedReader data = new BufferedReader(new InputStreamReader(input, "UTF-8"));			String strLine = "";			StringBuffer sbResult = null;			while ((strLine = data.readLine()) != null) {				if (null == sbResult) {					sbResult = new StringBuffer();				}				sbResult.append(strLine);			}			Logger.v(strUrl + "-----返回值---" + sbResult);			input.close();			http.disconnect();			if (sbResult == null) {				return null;			}			state = SUCCESS;			return sbResult.toString();		} catch (Exception ex) {			state = FAIL;			ex.printStackTrace();		}		return null;	}	/**	 * http/https get方法	 * 	 * @Title: get @param strUrl @param paras @return String 返回流 @date 2012-3-6	 *         下午7:48:52 @throws	 */	public String get(String strUrl, List<NameValuePair> paras) {		try {			TreeMap<String, String> treeMap = new TreeMap<String, String>();			HttpURLConnection http = null;			String paramStr = "";			String signStr = "";			for (int i = 0; i < paras.size(); i++) {				String name = paras.get(i).getName();				String value = paras.get(i).getValue();				// \002=="&",\001=="="				treeMap.put(name, value);// 重组排序			}			Iterator<String> iterator = treeMap.keySet().iterator();			while (iterator.hasNext()) {				String name = iterator.next();				String value = treeMap.get(name);				paramStr += paramStr = "&" + name + "=" + value;				signStr += signStr = "\002" + name + "\001" + value;// 参数按照一定规则签名			}			signStr = signStr + "\003" + GameSDKApplication.getInstance().getAppPrivateKey();			if (!paramStr.equals("")) {				paramStr = paramStr.replaceFirst("&", "?");				signStr = signStr.replaceFirst("\002", "");				// URLEncoder.encode(paramStr,"UTF-8");				// strUrl += paramStr;			}			// paramStr = paramStr.replace(" ", "_");			// signStr = signStr.replace(" ", "_");			// 判断是http请求还是https请求			String sign = EncryptUtils.md5(EncryptUtils.getEncryption(HYConstant.HEYIJOY_KEY)					+ EncryptUtils.md5(URLDecoder.decode(signStr)).toLowerCase()).toLowerCase();			// String sign =			// EncryptUtils.md5(URLDecoder.decode(signStr)).toLowerCase();			paramStr += "&sign=" + sign;			strUrl += paramStr;			URL url = new URL(strUrl);			Logger.v(strUrl);			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();			http = https;			http.setDoInput(true);			http.setUseCaches(false);			http.setConnectTimeout(TIMEOUT);// 设置超时时间			http.setReadTimeout(TIMEOUT);			http.setRequestMethod("GET");// 设置请求类型为get			http.setRequestProperty("Accept", "*/*");// 设置接收类型			String UA = "GameSDK" + ";" + HYConstant.getSDKVersion() + ";" + "Android" + ";"					+ android.os.Build.VERSION.RELEASE + ";" + android.os.Build.MODEL;			http.setRequestProperty("User-Agent", UA);// 设置UserAgent			http.setRequestProperty("Charset", "UTF-8");			// http.setRequestProperty("Connection", "close");			int resCode;			try {				resCode = http.getResponseCode();				Logger.d("pack&ver", "返回=" + resCode);				if (resCode == 204) {					state = SUCCESS;					Logger.d("pack&ver", "返回204");					return "";				}			} catch (IOException e) {// 修正java bug - Received authentication										// challenge is										// null，这时access_token已过期，清除access_token，续传时重新获取				e.printStackTrace();				if (e.getMessage() != null && (e.getMessage().equals("Received authentication challenge is null")						|| e.getMessage().equals("No authentication challenges found"))) {					resCode = 401;					// UploadConfig.saveUploadAccessToken("");				}				return null;			}			/*			 * if (resCode == 302) { get(http.getHeaderField("location"),			 * paras); }			 */			InputStream input = resCode == 200 || resCode == 201 ? http.getInputStream() : http.getErrorStream();			BufferedReader data = new BufferedReader(new InputStreamReader(input, "UTF-8"));			String strLine = "";			StringBuffer sbResult = null;			while ((strLine = data.readLine()) != null) {				if (null == sbResult) {					sbResult = new StringBuffer();				}				sbResult.append(strLine);			}			input.close();			http.disconnect();			if (sbResult == null)				return null;			/*			 * JSONObject json_result = new JSONObject(sbResult.toString());			 * return json_result;			 */			Logger.v(sbResult.toString());			state = SUCCESS;			return sbResult.toString();		} catch (Exception ex) {			ex.printStackTrace();			state = FAIL;		}		return null;	}	/**	 * 获取微信access_token等请求	 */	public String getWX(String strUrl) {		try {			URL url = new URL(strUrl);			Logger.v(strUrl);			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();			https.setDoInput(true);			https.setUseCaches(false);			https.setConnectTimeout(TIMEOUT);// 设置超时时间			https.setReadTimeout(TIMEOUT);			https.setRequestMethod("GET");// 设置请求类型为get			https.setRequestProperty("Accept", "*/*");// 设置接收类型			https.setRequestProperty("Charset", "UTF-8");			int resCode;			try {				resCode = https.getResponseCode();				Logger.d("pack&ver", "返回=" + resCode);				if (resCode == 204) {					state = SUCCESS;					Logger.d("pack&ver", "返回204");					return "";				}			} catch (IOException e) {// 修正java bug - Received authentication										// challenge is										// null，这时access_token已过期，清除access_token，续传时重新获取				e.printStackTrace();				if (e.getMessage() != null && (e.getMessage().equals("Received authentication challenge is null")						|| e.getMessage().equals("No authentication challenges found"))) {					resCode = 401;					// UploadConfig.saveUploadAccessToken("");				}				return null;			}			InputStream input = resCode == 200 || resCode == 201 ? https.getInputStream() : https.getErrorStream();			BufferedReader data = new BufferedReader(new InputStreamReader(input, "UTF-8"));			String strLine = "";			StringBuffer sbResult = null;			while ((strLine = data.readLine()) != null) {				if (null == sbResult) {					sbResult = new StringBuffer();				}				sbResult.append(strLine);			}			input.close();			https.disconnect();			if (sbResult == null)				return null;			Logger.v(sbResult.toString());			state = SUCCESS;			return sbResult.toString();		} catch (Exception ex) {			ex.printStackTrace();			state = FAIL;		}		return null;	}	/**	 * https请求中，信任所有主机-对于任何证书都不做检查	 * 	 * @Title: trustAllHosts	 * @return void	 * @date 2012-3-6 下午7:47:41	 */	public void trustAllHosts() {		// Create a trust manager that does not validate certificate chains		// Android 采用X509的证书信息机制		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {			public java.security.cert.X509Certificate[] getAcceptedIssuers() {				return new java.security.cert.X509Certificate[] {};			}			@Override			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)					throws java.security.cert.CertificateException {			}			@Override			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)					throws java.security.cert.CertificateException {			}		} };		// Install the all-trusting trust manager		try {			SSLContext sc = SSLContext.getInstance("TLS");			sc.init(null, trustAllCerts, new java.security.SecureRandom());			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());		} catch (Exception e) {			e.printStackTrace();		}	}	private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {		@Override		public boolean verify(String hostname, SSLSession session) {			return true;		}	};}