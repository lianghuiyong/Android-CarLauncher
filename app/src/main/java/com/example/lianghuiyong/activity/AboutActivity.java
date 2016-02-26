package com.example.lianghuiyong.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.example.lianghuiyong.R;
import com.example.lianghuiyong.fragment.SystemUpdataFragment;
import com.example.lianghuiyong.utils.NetUtils;

public class AboutActivity extends Activity implements View.OnClickListener{

    private Button btn_deviceinfo;
    private Button btn_systemupdata;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private DevicePrefFragment deviceprefFragment;
    private SystemUpdataFragment systemUpdataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init_view();
        init_data();
        //transaction.add(R.id.about_info_view,systemUpdataFragment);
        transaction = manager.beginTransaction();
        transaction.add(R.id.about_info_view, deviceprefFragment, "fragment_device");
        transaction.commit();
    }

    private void init_data(){
        systemUpdataFragment = new SystemUpdataFragment();
        deviceprefFragment = new DevicePrefFragment();

        manager = getFragmentManager();

        btn_deviceinfo.setOnClickListener(this);
        btn_systemupdata.setOnClickListener(this);
    }
    private void init_view(){
        btn_deviceinfo = (Button)findViewById(R.id.about_btn_deviceinfo);
        btn_systemupdata = (Button)findViewById(R.id.about_btn_system_updata);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_btn_deviceinfo:
                btn_deviceinfo.setBackgroundColor(Color.parseColor("#FF0000"));
                btn_systemupdata.setBackgroundColor(Color.parseColor("#2B2B2E"));
                manager.beginTransaction().replace(R.id.about_info_view, deviceprefFragment, "fragment_device").commit();
                break;
            case R.id.about_btn_system_updata:
                btn_deviceinfo.setBackgroundColor(Color.parseColor("#2B2B2E"));
                btn_systemupdata.setBackgroundColor(Color.parseColor("#ff0000"));
                manager.beginTransaction().replace(R.id.about_info_view, systemUpdataFragment, "systemUpdataFragment").commit();
                break;
            default:
                break;
        }
    }

    public static class DevicePrefFragment extends PreferenceFragment {

        private Preference preference_version = null;
        private Preference preference_serial_number = null;
        private Preference preference_imei_number = null;
        private Preference preference_ip_address = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //加载preference_device文件
            addPreferencesFromResource(R.xml.preference_device);

            //初始化 preference 控件
            init_preference();
        }

        @Override
        public void onStart() {
            super.onStart();
            preference_version.setSummary("Android" + android.os.Build.VERSION.RELEASE);
            preference_serial_number.setSummary(android.os.Build.SERIAL);
            /*preference_imei_number.setSummary(((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
            preference_ip_address.setSummary(new NetUtils().getDefaultIpAddresses(AboutActivity.this));*/
        }
        private void init_preference(){
            preference_version = findPreference("System_version");
            preference_serial_number = findPreference("serial_number");
            preference_imei_number = findPreference("IMEI_number");
            preference_ip_address = findPreference("IP_address");
        }
    }
}
