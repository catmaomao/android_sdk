package com.heyijoy.gamesdk.operatorpay;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

public class APNManager {
	// CHINA MOBILE
	private static final String CMNET = "cmnet";
	private static final String CMWAP = "cmwap";
	
	//CHINA UNICOM 3G
	private static final String G_3WAP = "3gwap";
	private static final String G_3NET = "3gnet";
	
	// CHINA UNICOM
	private static final String UNIWAP = "uniwap";
	private static final String UNINET = "uninet";
	
    private static String TAG = "APNManager";
    private static final Uri APN_TABLE_URI = Uri.
    	parse("content://telephony/carriers");// 所有的APN配配置信息位置
    private static final Uri PREFERRED_APN_URI = Uri.
    	parse("content://telephony/carriers/preferapn");// 当前APN
    private static String[] projection = { "_id", "apn", "type", 
    	"current", "proxy", "port" };
    private static String APN_NET_ID = null;
    private static String APN_WAP_ID = null;
    
    
    //	返回当前的APN name
	public static String checkApn(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
		boolean isConnect = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting(); 
		String currentAPN = info.getExtraInfo(); 
		if(currentAPN != null){
			if(currentAPN.equals(CMNET)){
				return CMNET;
			}else if(currentAPN.equals(CMWAP)){
				return CMWAP;
			}else if(currentAPN.equals(G_3WAP)){
				return G_3WAP;
			}else if(currentAPN.equals(G_3NET)){
				return G_3NET;
			}else if(currentAPN.equals(UNIWAP)){
				return UNIWAP;
			}else if(currentAPN.equals(UNINET)){
				return UNINET;
			}
		}
		

		return null;
		
	}

    // net-->wap
    private static boolean changeNet2Wap(Context context) {
        final String wapId = getWapApnId(context);
        final String netId = getNetApnId(context);
        String apnId = getCurApnId(context);
        // 若当前apn为net，则切换至wap
        if (netId.equals(apnId)) {
            APN_WAP_ID = getWapApnId(context);
            setApn(context, APN_WAP_ID);
            // 切换apn需要一定时间，先等待几秒，与机器性能有关
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            Log.e("Net-->wap", "setApn");
            return true;
        }
        return true;
    }
    
    // wap-->net
    public static boolean changeWap2Net(Context context){
    	
        final String wapId = getWapApnId(context);
        final String netId = getNetApnId(context);
        String apnId = getCurApnId(context);
        // 若当前apn是wap，则切换至net
        if (wapId.equals(apnId)) {
            APN_NET_ID = getNetApnId(context);
            setApn(context, APN_NET_ID);
            // 切换apn需要一定时间，先让等待几秒，与机子性能有关
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("xml", "setApn");
            return true;
        }
        return true;
    
    }
    
   

    //获取当前APNID
    public static String getCurApnId(Context context) {
        ContentResolver resoler = context.getContentResolver();
        // String[] projection = new String[] { "_id" };
        Cursor cur = resoler.query(PREFERRED_APN_URI, projection, null, null,
                null);
        String apnId = null;
        if (cur != null && cur.moveToFirst()) {
            apnId = cur.getString(cur.getColumnIndex("_id"));
        }
        Log.i("xml","getCurApnId:"+apnId);
        return apnId;
    }

    //
    public static APN getCurApnInfo(final Context context) {
        ContentResolver resoler = context.getContentResolver();
        // String[] projection = new String[] { "_id" };
        Cursor cur = resoler.query(PREFERRED_APN_URI, projection, null, null,
                null);
        APN apn = new APN();
        if (cur != null && cur.moveToFirst()) {
            apn.id = cur.getString(cur.getColumnIndex("_id"));
            apn.apn = cur.getString(cur.getColumnIndex("apn"));
            apn.type = cur.getString(cur.getColumnIndex("type"));
            
        }
        return apn;
    }

    
    public static void setApn(Context context, String id) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("apn_id", id);
        resolver.update(PREFERRED_APN_URI, values, null, null);
        Log.d("xml", "setApn");
    }

    //获取WAP APN
    public static String getWapApnId(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        // 查询cmwapAPN
        Cursor cur = contentResolver.query(APN_TABLE_URI, projection,
                "apn = \'cmwap\' and current = 1", null, null);
        // wap APN 端口不为空
        if (cur != null && cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex("_id"));
                String proxy = cur.getString(cur.getColumnIndex("proxy"));
                if (!TextUtils.isEmpty(proxy)) {
                	Log.i("xml","getWapApnId"+id);
                    return id;
                }
            } while (cur.moveToNext());
        }
        return null;
    }
    
    

    public static String getNetApnId(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cur = contentResolver.query(APN_TABLE_URI, projection,
                "apn = \'cmnet\' and current = 1", null, null);
        if (cur != null && cur.moveToFirst()) {
            return cur.getString(cur.getColumnIndex("_id"));
        }
        return null;
    }

    //获取所有APN
       public static ArrayList<APN> getAPNList(final Context context) {

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cr = contentResolver.query(APN_TABLE_URI, projection, null,
                null, null);

        ArrayList<APN> apnList = new ArrayList<APN>();

        if (cr != null && cr.moveToFirst()) {
            do{
                Log.d(TAG,
                        cr.getString(cr.getColumnIndex("_id")) + ";"
                                + cr.getString(cr.getColumnIndex("apn")) + ";"
                                + cr.getString(cr.getColumnIndex("type")) + ";"
                                + cr.getString(cr.getColumnIndex("current"))+ ";"
                                + cr.getString(cr.getColumnIndex("proxy")));
                APN apn = new APN();
                apn.id = cr.getString(cr.getColumnIndex("_id"));
                apn.apn = cr.getString(cr.getColumnIndex("apn"));
                apn.type = cr.getString(cr.getColumnIndex("type"));
                apnList.add(apn);
            }while(cr.moveToNext());
           
            cr.close();
        }
        return apnList;
    }

    //获取可用的APN
      public static ArrayList<APN> getAvailableAPNList(final Context context) {
        // current不为空表示可以使用的APN
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cr = contentResolver.query(APN_TABLE_URI, projection,
                "current is not null" , null, null);
        ArrayList<APN> apnList = new ArrayList<APN>();
        if (cr != null && cr.moveToFirst()) {
            do{
                Log.d(TAG,
                        cr.getString(cr.getColumnIndex("_id")) + ";"
                                + cr.getString(cr.getColumnIndex("apn")) + ";"
                                + cr.getString(cr.getColumnIndex("type")) + ";"
                                + cr.getString(cr.getColumnIndex("current"))+ ";"
                                + cr.getString(cr.getColumnIndex("proxy")));
                APN apn = new APN();
                apn.id = cr.getString(cr.getColumnIndex("_id"));
                apn.apn = cr.getString(cr.getColumnIndex("apn"));
                apn.type = cr.getString(cr.getColumnIndex("type"));
                apnList.add(apn);
            }while (cr.moveToNext());
           
            cr.close();
        }
        return apnList;

    }
    // 判断是否有Wifi存在
   	public static boolean isWiFi(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = cm.getActiveNetworkInfo();  
		if(networkinfo != null && networkinfo.getType() == ConnectivityManager.TYPE_WIFI){
			return true;
		}
		return false;
	} 
   	
   	// 打开 or 关闭Wifi
   	public static void ChangeWifiState(Context c){
		WifiManager wifiManager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
		if(wifiManager.isWifiEnabled()){ // if Wifi is Active
			wifiManager.setWifiEnabled(false);  
		}else{
			wifiManager.setWifiEnabled(true);  
		}
	}
      
      
   //自定义APN包装类
    static class APN {

        String id;

        String apn;

        String type;

        public String toString() {
            return "id=" + id + ",apn=" + apn + ";type=" + type;
        }
    }
}
