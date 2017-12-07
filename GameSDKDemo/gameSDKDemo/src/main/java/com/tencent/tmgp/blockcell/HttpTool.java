package com.tencent.tmgp.blockcell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class HttpTool {
	
    public static String post(String uri,List<NameValuePair> params){  
        BufferedReader reader = null;    
        StringBuffer sb = null;  
        String result = "";  
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        HttpPost request = new HttpPost(uri);  
        try {  
            //设置字符集  
            HttpEntity entity = new UrlEncodedFormEntity(params,"utf-8");  
            //请求对象  
            request.setEntity(entity);  
            //发送请求  
            HttpResponse response = client.execute(request);  
              
            //请求成功  
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));  
                sb = new StringBuffer();  
                String line = "";  
                while((line = reader.readLine()) != null){  
                    sb.append(line);  
                }  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        finally{  
            try {  
                //关闭流  
                if (null != reader){  
                    reader.close();  
                    reader = null;  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        if (null != sb){  
            result =  sb.toString();  
        }  
        return result;  
    }
    

}
