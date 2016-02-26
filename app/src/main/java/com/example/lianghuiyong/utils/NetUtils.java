package com.example.lianghuiyong.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lianghuiyong.data.MainApplication;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Administrator on 2016/1/12.
 * 网络常用工具类：IP地址
 */
public class NetUtils {
    private final String TAG = "NetUtils";
    public static JSONObject httpJsonResult;
    public static JSONObject httpJsonWindJson;
   Handler mainHandler = MainApplication.mainHandler;

    /*获取IP地址
    * */
    public String getDefaultIpAddresses(Context context) {

        //获取wifi服务
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //如果wifi开启
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            //ip值转换
            String ip = (ipAddress & 0xFF ) + "." +
                        ((ipAddress >> 8 ) & 0xFF) + "." +
                        ((ipAddress >> 16 ) & 0xFF) + "." +
                        ( ipAddress >> 24 & 0xFF) ;
            return ip;
        }else {
            //以下为非wifi网络时的IP
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();

            if(info !=null && info.getType() ==  ConnectivityManager.TYPE_MOBILE){
                try {
                    for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                // if (!inetAddress.isLoopbackAddress() && inetAddress
                                // instanceof Inet6Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //xUtils Http Get方法
    /*
    * 处理weatherUrl请求并实现请求成功时的handler操作
    * */
    public JSONObject  xUtilsHttpHandler(String url){
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    httpJsonResult  = new JSONObject(responseInfo.result).getJSONObject("weatherinfo");
                    Log.d(TAG,"JSONObject = "+httpJsonResult);

                    String weather_content;
                    String weather_temp;
                    String weather_temp_low;
                    String weather_temp_high;
                    String weather_img;
                    httpJsonResult  = new JSONObject(responseInfo.result).getJSONObject("weatherinfo");

                    weather_content = httpJsonResult.getString("weather1");
                    weather_temp = httpJsonResult.getString("temp1");
                    weather_img =httpJsonResult.getString("img1");

                    String[] str = weather_temp.split("~");
                    weather_temp_low = str[0];
                    weather_temp_high = str[1];


                    //handler更新UI
                    Message msgweather = new Message();
                    msgweather.what = MainApplication.msgKey_weather;
                    Bundle weatherbundle = new Bundle();
                    weatherbundle.putString("content",weather_content);
                    weatherbundle.putString("temp_low", weather_temp_low);
                    weatherbundle.putString("temp_high", weather_temp_high);
                    weatherbundle.putInt("weather_img_icon", Integer.parseInt(weather_img));
                    msgweather.setData(weatherbundle);
                    mainHandler.sendMessage(msgweather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //result = null;
                Log.d(TAG,"jsonObject = NULL");
            }
        });
        return httpJsonResult;
    }

    //xUtils Http handler 风力方法
    public JSONObject  xUtilsHttpHandlerWindJson(String url){
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    Message msgweather_wind = new Message();
                    httpJsonWindJson  = new JSONObject(responseInfo.result).getJSONObject("weatherinfo");
                    MainApplication.weatherWind = httpJsonWindJson.getString("WD");

                    //handler更新UI
                    msgweather_wind.what = MainApplication.msgKey_weather_wind;
                    Bundle bundle = new Bundle();
                    bundle.putString("area_wind", MainApplication.baiduGPSCity + " " + MainApplication.weatherWind);
                    msgweather_wind.setData(bundle);
                    mainHandler.sendMessage(msgweather_wind);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //result = null;
                Log.d(TAG,"jsonObject = NULL");
            }
        });
        return httpJsonResult;
    }

}
