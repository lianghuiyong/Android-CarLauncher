package com.example.lianghuiyong.data;


import android.app.Application;
import android.content.SharedPreferences;

import com.example.lianghuiyong.activity.MainActivity;
import com.example.lianghuiyong.tool.DidihuMusicTool;
import com.example.lianghuiyong.tool.TXZVoiceInit;
import com.txznet.sdk.TXZMusicManager;


/**
 * 主Application
 */
public  class MainApplication extends Application {
    public static final String TAG = "MainApplication";
	//handler key
    public static final int msgKey_time_data = 1;
    public static final int msgKey_weather = 2;
    public static final int msgKey_weather_wind = 3;
    public static final int msgKey_gpsinfo = 4;

    //共享handle变量
    public static MainActivity.MainHandler mainHandler;

    public volatile static Boolean isSpeakWeather = false;
    public static String weatherWind = null;         //天气情况，风力
    public volatile static boolean isFistRun = true;         //第一次运行
    public static String  baiduGPSCity = null;       //百度定位城市
    public static SharedPreferences customSpf = null;  //用户操作习惯信息存储

    @Override
    public void onCreate() {
        super.onCreate();

        /*初始化Handle*/
        mainHandler = new MainActivity.MainHandler();

        /*初始化SP文件*/
        customSpf = getSharedPreferences("user_custom",MODE_PRIVATE);

        /*同行者语音初始化*/
        new TXZVoiceInit(this);

        //初始化第三方音乐播放器（滴滴虎播放器）
        TXZMusicManager.getInstance().setMusicTool(new DidihuMusicTool(this));
    }
}
