package com.example.lianghuiyong.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 类名：NetWorkUtil
 * 功能描述：网络状态检测工具类
 * @author android_ls
 */
public class NetWorkUtil {
	/**
	 * 检测当的网络（WLAN、3G/2G）状态
	 * @param context Context
	 * @return true 表示网络可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity 
		                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) 
			{
				// 当前网络是连接的
				if (info.getState() == NetworkInfo.State.CONNECTED) 
				{
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}
}