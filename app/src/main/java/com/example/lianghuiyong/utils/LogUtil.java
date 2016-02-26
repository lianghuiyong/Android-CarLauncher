package com.example.lianghuiyong.utils;

import android.util.Log;

/**
 * 类名：LogUtil
 * 功能描述：LOG打印工具类
 * @author android_ls 
 */
public class LogUtil {
	private static final String TAG = "CarLauncherMini";
	
	public static void v(String msg) {
			Log.v(TAG, msg);
	}

	public static void d( String msg) {
			Log.d(TAG, msg);
	}

	public static void i( String msg) {
			Log.i(TAG, msg);
	}

	public static void w( String msg) {
			Log.w(TAG, msg);
	}

	public static void e( String msg) {
			Log.e(TAG, msg);
	}

}
