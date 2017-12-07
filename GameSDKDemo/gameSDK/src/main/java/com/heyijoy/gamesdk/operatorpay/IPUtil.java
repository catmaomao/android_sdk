package com.heyijoy.gamesdk.operatorpay;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class IPUtil {

	public static long ip2Long(String strip) {
		// 将127.0.0.1 形式的ip地址转换成10进制整数，这里没有进行任何错误处理
		long[] ip = new long[4];
		int position1 = strip.indexOf(".");
		int position2 = strip.indexOf(".", position1 + 1);
		int position3 = strip.indexOf(".", position2 + 1);
		ip[0] = Long.parseLong(strip.substring(0, position1));
		ip[1] = Long.parseLong(strip.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strip.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strip.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3]; // ip1*256*256*256+ip2*256*256+ip3*256+ip4
	}

	public static String long2IP(long longip) {
		// 将10进制整数形式转换成127.0.0.1形式的ip地址，在命令提示符下输入ping 3396362403l
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf(longip >>> 24));// 直接右移24位
		sb.append(".");
		sb.append(String.valueOf((longip & 0x00ffffff) >>> 16)); // 将高8位置0，然后右移16位
		sb.append(".");
		sb.append(String.valueOf((longip & 0x0000ffff) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longip & 0x000000ff));
		//sb.append(".");
		return sb.toString();
	}
 

	public static String callCmd(String cmd,String filter) {    
	     String result = "";    
	     String line = "";    
	     try { 
	         Process proc = Runtime.getRuntime().exec(cmd); 
	         InputStreamReader is = new InputStreamReader(proc.getInputStream());    
	         BufferedReader br = new BufferedReader (is);    
	           
	         //执行命令cmd，只取结果中含有filter的这一行 
	         while ((line = br.readLine ()) != null && line.contains(filter)== false) {    
	             //result += line; 
	             //Log.i("test","line: "+line); 
	         } 
	           
	         result = line; 
	         //Log.i("test","result: "+result); 
	     }    
	     catch(Exception e) {    
	         e.printStackTrace();    
	     }    
	     return result;    
	 }
	 
	
	/**
     * 获取手机mac地址<br/>
     * 错误返回12个0
     */
    public static String getMacAddress(Context context) {
        // 获取mac地址：
        String macAddress = "000000000000";
        try {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr
                    .getConnectionInfo());
            if (null != info) {
            	if (info.getMacAddress()!=null)
            		macAddress = info.getMacAddress().replace(":", "");
                else
                    return macAddress;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return macAddress;
        }
        return macAddress;
    }
	
	
	public static void main(String[] args){
		long ipnum=ip2Long("114.80.235.146");
	}
 
}
