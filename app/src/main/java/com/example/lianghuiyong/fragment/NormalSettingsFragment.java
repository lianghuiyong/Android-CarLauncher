package com.example.lianghuiyong.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lianghuiyong.R;
import com.example.lianghuiyong.data.MainApplication;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.kyleduo.switchbutton.SwitchButton;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

/**
 * 设置->常规设置fragment
 */
public class NormalSettingsFragment extends Fragment implements View.OnClickListener, DiscreteSeekBar.OnProgressChangeListener {

    private final String TAG = "NormalSettingsFragment";
    private Context context = null;

    //view
    private TextView text_satellite_num = null;
    private TextView text_satellite_strength = null;
    private TextView text_altitude = null;
    private TextView text_location_lat = null;
    private TextView text_location_long = null;

    private SwitchButton swh_screen_control = null;
    private SwitchButton swh_security_control = null;

    private DiscreteSeekBar seekBar_volume_control = null;
    private DiscreteSeekBar seekBar_collision_grade = null;

    //GPS
    private LocationManager locationManager = null;
    private Location location = null;
    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
    private View normalView = null;
    private BarChart gpsBarChart = null;

    private ExecutorService gpsInfoPool = null;
    private volatile boolean gpsPoolFlag = false;                //Pool thread flag

    //audio
    private AudioManager audiomanage = null;
    private SystemVolumeReceiver systemVolumeReceiver = null;
    private IntentFilter systemVolumeIF = null;


    public NormalSettingsFragment(Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        normalView = inflater.inflate(R.layout.fragment_normal_settings, container, false);
        return normalView;
    }

    @Override
    public void onStart() {
        super.onStart();
        init_view();
        init_data();

        //init GPS
        if (isOpenGPS()) {
            //卫星数量监听
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.addGpsStatusListener(statusListener);
            init_gps_info();
        } else {
            text_satellite_num.setText("卫星数：0");
            text_satellite_strength.setText("GPS精度：NULL");
            text_altitude.setText("海拔：NULL");
            text_location_lat.setText("经度：NULL");
            text_location_long.setText("纬度：NULL");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //响应该界面物理音量按钮，seekbar也实时显示
        systemVolumeIF = new IntentFilter();
        systemVolumeIF.addAction("android.media.VOLUME_CHANGED_ACTION") ;
        systemVolumeReceiver = new SystemVolumeReceiver();
        getActivity().registerReceiver(systemVolumeReceiver, systemVolumeIF);
    }

    private void init_view(){
        //init view
        swh_screen_control = (SwitchButton)normalView.findViewById(R.id.screen_control);
        swh_security_control = (SwitchButton)normalView.findViewById(R.id.security_control);
        seekBar_volume_control = (DiscreteSeekBar)normalView.findViewById(R.id.volume_control);
        seekBar_collision_grade = (DiscreteSeekBar)normalView.findViewById(R.id.collision_grade);

        text_satellite_num = (TextView) normalView.findViewById(R.id.satellite_num);
        text_satellite_strength = (TextView) normalView.findViewById(R.id.satellite_strength);
        text_altitude = (TextView) normalView.findViewById(R.id.altitude);
        text_location_lat = (TextView)normalView.findViewById(R.id.location_lat);
        text_location_long = (TextView)normalView.findViewById(R.id.location_long);
    }

    private void init_data(){
        gpsPoolFlag = true;
        audiomanage = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        seekBar_volume_control.setMin(0);
        seekBar_volume_control.setMax(audiomanage.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        seekBar_volume_control.setProgress(audiomanage.getStreamVolume(AudioManager.STREAM_SYSTEM));

        //初始化screen_off_timeout
        try {
            if(Settings.System.getInt(getActivity().getContentResolver(), SCREEN_OFF_TIMEOUT)<0){
                swh_screen_control.setChecked(false);
            }else {
                swh_screen_control.setChecked(true);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        //OnProgressChangeListener
        seekBar_volume_control.setOnProgressChangeListener(this);
        seekBar_collision_grade.setOnProgressChangeListener(this);

        //OnClickListener
        swh_screen_control.setOnClickListener(this);
        swh_security_control.setOnClickListener(this);

        gpsBarChart = (BarChart)getActivity().findViewById(R.id.gps_barchart);

        gpsBarChart.setDescription("GPS 信号强度");
        gpsBarChart.setNoDataTextDescription("Get GPS Info...");//没数据显示
        gpsBarChart.setDrawBorders(false);
    }

    private void init_gps_info() {
        gpsInfoPool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        gpsInfoPool.execute(new Runnable() {
            @Override
            public void run() {
                while (gpsPoolFlag){
                    try {
                        //studio处理getLastKnownLocation有提醒可忽略！
                         location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //更新UI
                        updataGpsInfo(location);
                        Thread.sleep(10*1000);//十分钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updataGpsInfo(Location location) {
        //handler更新UI
        Handler mainHandler = MainApplication.mainHandler;
        Message gpsInfoMsg = new Message();
        Bundle gpsInfoBundle = new Bundle();
        gpsInfoMsg.what = MainApplication.msgKey_gpsinfo;
        gpsInfoMsg.obj = normalView;
        if (location != null){
            gpsInfoBundle.putString("satellite_strength", "GPS精度：" + location.getAccuracy());
            gpsInfoBundle.putString("altitude", "海拔：" + location.getAltitude() + "m");
            gpsInfoBundle.putString("location_lat", "经度：" +  new DecimalFormat(".00").format(location.getLongitude()));
            gpsInfoBundle.putString("location_long", "纬度：" + new DecimalFormat(".00").format(location.getLatitude()));
            Log.d(TAG, "GPS精度：" + location.getAccuracy());
            Log.d(TAG, "海拔：" + location.getAltitude());
            Log.d(TAG, "经度：" + new DecimalFormat(".00").format(location.getLatitude()) + " 纬度：" + new DecimalFormat(".00").format(location.getLongitude()));
        }else {
            gpsInfoBundle.putString("satellite_strength", "GPS精度：NULL");
            gpsInfoBundle.putString("altitude", "海拔：NULL");
            gpsInfoBundle.putString("location_lat", "经度：NULL");
            gpsInfoBundle.putString("location_long", "纬度：NULL" );
            Log.d(TAG, "GPS精度：NULL");
            Log.d(TAG, "海拔：");
            Log.d(TAG, "经度：NULL  纬度：NULL");
        }
        gpsInfoMsg.setData(gpsInfoBundle);
        mainHandler.sendMessage(gpsInfoMsg);
    }

    private boolean isOpenGPS(){
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 卫星状态监听器,获取卫星数量
     */
    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
            GpsStatus status = locationManager.getGpsStatus(null); //取当前状态
            Log.d(TAG,"卫星数："+updateGpsStatus(event, status));
            text_satellite_num.setText("卫星数：" + updateGpsStatus(event, status));
        }
    };

    //更新收到的卫星数量
    private int updateGpsStatus(int event, GpsStatus status) {

        ArrayList<BarEntry> yValsGpsSnrInfo =  new ArrayList<BarEntry>();
        ArrayList<String> xValsGpsSnrInfo = new ArrayList<String>();
        if (status == null) {
            return 0;
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();
            gpsBarChart.clear();
            int count = 0;
            while (it.hasNext() && count <= status.getMaxSatellites()) {
                GpsSatellite s = it.next();
                numSatelliteList.add(s);
                xValsGpsSnrInfo.add(Integer.toString(s.getPrn())); //获取卫星伪噪声码（卫星编号）给X轴
                yValsGpsSnrInfo.add(new BarEntry(s.getSnr(),count)); //获取卫星信号强度给Y轴

                Log.d(TAG,"卫星信噪比："+s.getSnr());
                count++;
            }


        }
        BarDataSet set1 = new BarDataSet(yValsGpsSnrInfo, "GPS");
        set1.setBarSpacePercent(10f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        BarData data = new BarData(xValsGpsSnrInfo, dataSets);
        data.setValueTextSize(10f);
        data.setDrawValues(false);

        gpsBarChart.setData(data);
        return numSatelliteList.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //熄屏
            case R.id.screen_control:
                if (swh_screen_control.isChecked()){
                    //30秒休眠
                    Settings.System.putInt(getActivity().getContentResolver(), SCREEN_OFF_TIMEOUT, 30000);
                }else {
                    //永不休眠
                    Settings.System.putInt(getActivity().getContentResolver(), SCREEN_OFF_TIMEOUT, -1);
                }

                break;
            //防盗设置
            case R.id.security_control:
                if (swh_security_control.isChecked()){

                }else {

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //gpsFlag置否，结束线程
        gpsPoolFlag = false;
        locationManager.removeGpsStatusListener(statusListener);
        //广播注销
        getActivity().unregisterReceiver(systemVolumeReceiver);
    }
    //
    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        switch (seekBar.getId()){
            //音量调节
            case R.id.volume_control:
                audiomanage.setStreamVolume(AudioManager.STREAM_SYSTEM,seekBar.getProgress(),AudioManager.FLAG_PLAY_SOUND);
                break;
            //碰撞等级
            case R.id.collision_grade:
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }
    //接收系统音量改变时改变 音量调节的刻度
    private class SystemVolumeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                seekBar_volume_control.setProgress(audiomanage.getStreamVolume(AudioManager.STREAM_SYSTEM));
            }
        }
    }
}
