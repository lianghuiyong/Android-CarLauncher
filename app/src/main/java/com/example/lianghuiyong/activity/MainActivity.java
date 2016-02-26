package com.example.lianghuiyong.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lianghuiyong.data.DBHelper;
import com.example.lianghuiyong.data.DBManager;
import com.example.lianghuiyong.R;
import com.example.lianghuiyong.adapter.LauncherViewPagerAdapter;
import com.example.lianghuiyong.data.MainApplication;
import com.example.lianghuiyong.dialog.NaviDialog;
import com.example.lianghuiyong.utils.NetUtils;
import com.example.lianghuiyong.utils.TimeUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.txznet.sdk.TXZNavManager;
import com.txznet.sdk.TXZTtsManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.relex.circleindicator.CircleIndicator;

import static com.txznet.sdk.TXZNavManager.NavToolType.NAV_TOOL_BAIDU_NAV_HD;
import static com.txznet.sdk.TXZNavManager.NavToolType.NAV_TOOL_GAODE_MAP;

public class MainActivity extends Activity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    private View launcher_pager1 = null;
    private View launcher_pager2 = null;

    //panel button
    private ImageButton panel_btn_hide = null;
    private ImageButton btn_showpanel = null;


    //weather
    private static ImageView weather_icon = null;
    private static TextView text_weather_temp_low = null;
    private static TextView text_weather_temp_higt = null;
    private static TextView text_weather_cond = null;
    private static TextView text_weather_area_wind = null;
    private static String weather_cond = null;
    private static String weather_temp = null;


    //time(handler更新的UI设置为static变量)
    private static TextView text_data = null;
    private static TextView text_time = null;

    private ViewPager launcher_viewPager = null;
    private CircleIndicator launcher_indicator = null;
    private ArrayList pagerlist = null;
    private LayoutInflater lf = null;
    private RelativeLayout layout_Panel = null;
    private RelativeLayout layout_blank_Left = null;
    private RelativeLayout layout_blank_Right = null;

    //Intent
    private Intent edogIntent = null;
    private Intent mediaIntent = null;
    private Intent recordIntent = null;
    private Intent kumusicIntent = null;
    private Intent dialerIntent = null;
    private Intent fileIntent = null;
    private Intent mmsIntent = null;
    private Intent fmIntent = null;
    private Intent weixinIntent = null;
    private Intent settingsIntent = null;
    private Intent autoNaviIntent = null;

    //thread flag
    private volatile boolean timeFlag = false;
    private volatile boolean weatherFlag = false;

    //百度定位
    public LocationClient mLocationClient = null;
    public BDLocationListener mbdListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init_view();
        init_data();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init_weather();
        init_time();
        initLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //线程Flag置false
        timeFlag = false;
        weatherFlag = false;

        //百度地图定位注销
        if (mLocationClient.isStarted()) {
            mLocationClient.unRegisterLocationListener(mbdListener);
            mLocationClient.stop();
            Log.d(TAG, "百度定位 Stop");
        }
    }

    private void init_view() {
        layout_Panel = (RelativeLayout) findViewById(R.id.layout_panel);
        layout_blank_Left = (RelativeLayout) findViewById(R.id.layout_blank_left);
        layout_blank_Right = (RelativeLayout) findViewById(R.id.layout_blank_right);

        panel_btn_hide = (ImageButton) findViewById(R.id.panel_btn_hide);
        btn_showpanel = (ImageButton) findViewById(R.id.bl_btn_showpanel);
        panel_btn_hide.setOnClickListener(this);
        btn_showpanel.setOnClickListener(this);

        launcher_viewPager = (ViewPager) findViewById(R.id.launcher_viewpager);
        //launcher_indicator = (CircleIndicator)findViewById(R.id.launcher_indicator);
        //launcher_indicator.setViewPager(launcher_viewPager);

        lf = LayoutInflater.from(this);
        launcher_pager1 = lf.inflate(R.layout.launcher_pager1, null);
        //launcher_pager2 = lf.inflate(R.layout.launcher_pager2,null);

        //launcher_pager1
        launcher_pager1.findViewById(R.id.pager1_btn_navi).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_file).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_edog).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_fm).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_media).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_record).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_music).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_weixin).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_settings).setOnClickListener(this);
        launcher_pager1.findViewById(R.id.pager1_btn_autoNavi).setOnClickListener(this);


        //weather
        weather_icon = (ImageView) findViewById(R.id.img_weather);
        text_weather_temp_low = (TextView) findViewById(R.id.text_weather_temp_low);
        text_weather_temp_higt = (TextView) findViewById(R.id.text_weather_temp_high);
        text_weather_cond = (TextView) findViewById(R.id.text_weather_cond);
        text_weather_area_wind = (TextView) findViewById(R.id.text_area_wind);

        //time
        text_time = (TextView) findViewById(R.id.text_time);
        text_data = (TextView) findViewById(R.id.text_data);
    }

    private void init_data() {
        //launcher viewPager
        pagerlist = new ArrayList<>();
        pagerlist.add(launcher_pager1);
        //pagerlist.add(launcher_pager2);
        launcher_viewPager.setAdapter(new LauncherViewPagerAdapter(pagerlist, this));

        //线程Flag
        timeFlag = true;
        weatherFlag = true;


        //初始化导航
        //获取用户设置默认导航
        String naviSP = MainApplication.customSpf.getString("voiceDefaltNavi", "BaiduMap");
        if (naviSP.equals("BaiduMap")) {
            TXZNavManager.getInstance().setNavTool(NAV_TOOL_BAIDU_NAV_HD);
            Log.d(TAG, "导航初始化为：百度地图");
        } else if (naviSP.equals("GaodeMap")) {
            TXZNavManager.getInstance().setNavTool(NAV_TOOL_GAODE_MAP);
            Log.d(TAG, "导航初始化为：高德地图");
        }
        //导航
        autoNaviIntent = new Intent();
        //intent.setClassName("com.baidu.navi.hd", "com.baidu.navi.NaviActivity");
        autoNaviIntent.setClassName("com.autonavi.minimap", "com.autonavi.map.activity.SplashActivity");
        autoNaviIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //电子狗
        edogIntent = new Intent();
        edogIntent.setClassName("com.chetuobang.android.edog", "com.chetuobang.android.edog.SplashActivity");
        //intent.setClassName("com.nengzhong.app.activity", "com.nengzhong.app.activity.DogActivity");
        edogIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //视频播放
        mediaIntent = new Intent();
        //intent.setClassName("com.mediatek.videoplayer", "com.mediatek.videoplayer.MovieListActivity");
        mediaIntent.setClassName("com.android.gallery3d", "com.android.gallery3d.app.GalleryActivity");

        //行车记录
        recordIntent = new Intent();
        //recordIntent.setClassName("com.dvr.android.dvr", "com.dvr.android.dvr.RecorderActivity");  //安培
        //recordIntent.setClassName("com.dvr.android.dvr", "com.dvr.android.dvr.DVRActivity");
        recordIntent.setClassName("com.mediatek.carcorderdemo", "com.mediatek.carcorderdemo.CarcorderDemoActivity");   //mtk
        recordIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //音乐
        kumusicIntent = new Intent();
        //intent.setClassName("com.android.music", "com.android.music.MusicBrowserActivity");
        kumusicIntent.setClassName("com.glsx.ddmusic", "com.glsx.ddmusic.ui.launcher.MusicActivity"); //滴滴音乐
        //kumusicIntent.setClassName("cn.kuwo.kwmusiccar","cn.kuwo.kwmusiccar.WelcomeActivity");//酷我音乐
        kumusicIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //联系人
        dialerIntent = new Intent();
        dialerIntent.setClassName("com.android.dialer", "com.android.dialer.DialtactsActivity");

        //一键导航
        autoNaviIntent = new Intent();
        autoNaviIntent.setClassName("com.glsx.autonavi", "com.glsx.autonavi.ui.MainActivity");
        autoNaviIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);


        //文件管理
        fileIntent = new Intent();
        fileIntent.setClassName("com.mediatek.filemanager", "com.mediatek.filemanager.FileManagerOperationActivity");
        fileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //信息
        mmsIntent = new Intent();
        mmsIntent.setClassName("com.android.mms", "com.android.mms.ui.BootActivity");
        mmsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //FM发射
        fmIntent = new Intent();
        fmIntent.setClassName("com.mediatek.FMTransmitter", "com.mediatek.FMTransmitter.FMTransmitterActivity");
        fmIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //微信助手
        weixinIntent = new Intent();
        weixinIntent.setClassName("com.txznet.webchat", "com.txznet.webchat.ui.AppStartActivity");

        //系统设置
        settingsIntent = new Intent();
        settingsIntent.setClass(MainActivity.this, SettingsActivity.class);
        //settingsIntent = new Intent();
        //settingsIntent.setClassName("com.android.settings", "com.android.settings.Settings");
        //settingsIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    /**
     * 百度定位结果回调
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getCity() == null) {
                Log.e("TAG", "---->>location.getCity() == null");
            } else {
                if (location.getLocType() == BDLocation.TypeGpsLocation) { // GPS定位结果
                    Log.d(TAG, "gps定位成功:" + location.getCity());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    Log.d(TAG, "网络定位成功：" + location.getCity());
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    Log.d(TAG, "离线定位成功，离线定位结果也是有效的：" + location.getCity());
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Log.d(TAG, "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Log.d(TAG, "网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Log.d(TAG, "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                } else {
                    Log.d(TAG, "定位方式不明");
                }
                String[] str = location.getCity().split("市");
                MainApplication.baiduGPSCity = str[0];
                Log.e("TAG","---->>定位城市："+MainApplication.baiduGPSCity);
            }
        }
    }

    /*
    * 初始化百度定位
    * */
    private void initLocation(){
        mLocationClient = new LocationClient(this);
        mbdListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mbdListener);// 注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(30 * 1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    //天气模块初始化
    private void init_weather() {
        Log.d(TAG, "locationService start!!!");
        ExecutorService weatherPool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        weatherPool.execute(new Runnable() {
            @Override
            public void run() {
                int thread_i = 0;
                NetUtils netUtils = new NetUtils();
                while (weatherFlag) {
                    if (MainApplication.baiduGPSCity != null) {
                        try {
                            //数据库文件 citychina.db 得到城市代码
                            DBHelper helper = new DBHelper(getApplicationContext());
                            DBManager manager = new DBManager(getApplicationContext());
                            manager.copyDatabase();
                            String cityCode = null;
                            String sql = "select * from city_table where CITY =" + "'" + MainApplication.baiduGPSCity + "'" + ";";
                            Cursor cursor = helper.getReadableDatabase().rawQuery(sql, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                //获取WEATHER_ID
                                cityCode = cursor.getString(cursor.getColumnIndex("WEATHER_ID"));
                            }
                            if (cursor != null) {
                                cursor.close();
                            }
                            helper.close();
                            //数据库 end

                            //获取风力
                            String getWeatherWindUrl = "http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode=" + cityCode + "&weatherType=1";
                            netUtils.xUtilsHttpHandlerWindJson(getWeatherWindUrl);


                            //获取天气网JSON数据,并在回调处理方式上实现handler
                            String weatherUrl = "http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode=" + cityCode + "&weatherType=0";
                            netUtils.xUtilsHttpHandler(weatherUrl);

                            if(MainApplication.isFistRun){
                            Thread.sleep(3000);
                            }else {
                                Thread.sleep(30*60000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    //日期 时间模块初始化
    private void init_time() {
        ExecutorService timePool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        timePool.execute(new Runnable() {
            @Override
            public void run() {
                NetUtils netUtils = new NetUtils();
                while (timeFlag) {
                    try {
                        //延时一秒作用
                        Message msgtimedata = new Message();
                        msgtimedata.what = MainApplication.msgKey_time_data;
                        MainApplication.mainHandler.sendMessage(msgtimedata);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /*
    * 监听菜单按钮
    * */
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                //侧边显示栏控制
                case R.id.panel_btn_hide:
                    layout_Panel.setVisibility(View.GONE);
                    layout_blank_Left.setVisibility(View.VISIBLE);
                    layout_blank_Right.setVisibility(View.VISIBLE);
                    break;
                case R.id.bl_btn_showpanel:
                    layout_Panel.setVisibility(View.VISIBLE);
                    layout_blank_Left.setVisibility(View.GONE);
                    layout_blank_Right.setVisibility(View.GONE);
                    break;
                //pager 1
                case R.id.pager1_btn_navi:
                    //导航提示框
                    NaviDialog naviDialog = new NaviDialog(this);
                    naviDialog.show();
                    break;
                case R.id.pager1_btn_autoNavi:  startActivity(autoNaviIntent); break;
                case R.id.pager1_btn_edog:      startActivity(edogIntent);      break;
                case R.id.pager1_btn_media:     startActivity(mediaIntent);     break;
                case R.id.pager1_btn_record:    startActivity(recordIntent);    break;
                case R.id.pager1_btn_music:     startActivity(kumusicIntent);   break;
                case R.id.pager1_btn_file:      startActivity(fileIntent);      break;
                case R.id.pager1_btn_fm:        startActivity(fmIntent);        break;
                case R.id.pager1_btn_weixin:    startActivity(weixinIntent);    break;
                case R.id.pager1_btn_settings:  startActivity(settingsIntent);  break;
                default:
                    break;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static class MainHandler extends android.os.Handler {

        int weather_back_icon[] = {
                R.drawable.weather_0, R.drawable.weather_1, R.drawable.weather_2, R.drawable.weather_3, R.drawable.weather_4,
                R.drawable.weather_5, R.drawable.weather_6, R.drawable.weather_7, R.drawable.weather_8, R.drawable.weather_9,
                R.drawable.weather_10, R.drawable.weather_11, R.drawable.weather_12, R.drawable.weather_13, R.drawable.weather_14,
                R.drawable.weather_15, R.drawable.weather_16, R.drawable.weather_17, R.drawable.weather_18, R.drawable.weather_19,
                R.drawable.weather_20, R.drawable.weather_21, R.drawable.weather_22, R.drawable.weather_23, R.drawable.weather_24,
                R.drawable.weather_25, R.drawable.weather_26, R.drawable.weather_27, R.drawable.weather_28, R.drawable.weather_29,
                R.drawable.weather_30, R.drawable.weather_31, R.drawable.weather_32

        };

        //NormalSettingsFragment UI updata view
        private TextView text_satellite_strength = null;
        private TextView text_altitude = null;
        private TextView text_location_lat = null;
        private TextView text_location_long = null;

        @Override
        public void handleMessage(Message msg) {
            TimeUtils timeUtils = new TimeUtils();
            Bundle bundle = msg.getData();
            switch (msg.what) {

                case MainApplication.msgKey_time_data:
                    text_data.setText(timeUtils.getDayOfWeek());
                    text_time.setText(timeUtils.getHour_Min12());
                    break;
                case MainApplication.msgKey_weather:
                    text_weather_temp_low.setText(bundle.getString("temp_low"));
                    text_weather_temp_higt.setText(bundle.getString("temp_high"));
                    text_weather_cond.setText(bundle.getString("content"));
                    weather_cond = bundle.getString("content");
                    weather_temp = bundle.getString("temp_low") + "~" + bundle.getString("temp_high");

                    /*
                    * 判断是否第一次运行，是否接收到风力强度
                    * */
                    if (MainApplication.isFistRun  &&  (MainApplication.weatherWind != null) ) {
                        MainApplication.isFistRun = false;
                        //第一次运行语音播报天气
                        TXZTtsManager.getInstance().speakText(MainApplication.baiduGPSCity + "今天天气:" + weather_cond + weather_temp+"," + MainApplication.weatherWind);
                    }

                    int index = bundle.getInt("weather_img_icon");
                    if (index == 53)
                        index = 32;//将网络上53的图标对应为本地32号的图标
                    if (0 <= index && index <= 32)
                        weather_icon.setBackgroundResource(weather_back_icon[index]);
                    else
                        weather_icon.setBackgroundResource(weather_back_icon[2]);
                    break;

                case MainApplication.msgKey_weather_wind:
                    text_weather_area_wind.setText(bundle.getString("area_wind"));
                    break;

                case MainApplication.msgKey_gpsinfo:
                    View normalView = (View) msg.obj;
                    text_satellite_strength = (TextView) normalView.findViewById(R.id.satellite_strength);
                    text_altitude = (TextView) normalView.findViewById(R.id.altitude);
                    text_location_lat = (TextView) normalView.findViewById(R.id.location_lat);
                    text_location_long = (TextView) normalView.findViewById(R.id.location_long);

                    text_satellite_strength.setText(bundle.getString("satellite_strength"));
                    text_altitude.setText(bundle.getString("altitude"));
                    text_location_lat.setText(bundle.getString("location_lat"));
                    text_location_long.setText(bundle.getString("location_long"));
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    //取消该activity 返回按钮的实现
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
