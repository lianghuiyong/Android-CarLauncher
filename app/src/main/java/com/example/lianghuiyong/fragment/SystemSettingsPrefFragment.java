package com.example.lianghuiyong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import com.example.lianghuiyong.R;

/**
 * Created by lianghuiyong on 2016/2/16.
 */
public class SystemSettingsPrefFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private Preference preference_wifi;
    private Preference preference_network;
    private Preference preference_sdcard;
    private Preference preference_app_running;
    private Preference preference_data_time;

    private Intent intent_settings_wifi;
    private Intent intent_settings_network;
    private Intent intent_settings_sdcard;
    private Intent intent_settings_app_running;
    private Intent intent_settings_data_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载preference_device文件
        addPreferencesFromResource(R.xml.preference_settings);
    }

    @Override
    public void onStart() {
        //初始化 preference 控件
        init_preference();
        super.onStart();
    }

    private void init_preference() {
        preference_wifi = findPreference("setting wifi");
        preference_network = findPreference("setting network");
        preference_sdcard = findPreference("setting sdcard");
        preference_app_running = findPreference("setting app running");
        preference_data_time = findPreference("setting time data");

        preference_wifi.setOnPreferenceClickListener(this);
        preference_network.setOnPreferenceClickListener(this);
        preference_sdcard.setOnPreferenceClickListener(this);
        preference_app_running.setOnPreferenceClickListener(this);
        preference_data_time.setOnPreferenceClickListener(this);

        intent_settings_wifi = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent_settings_network = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        intent_settings_sdcard = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
        intent_settings_app_running = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        intent_settings_data_time = new Intent(Settings.ACTION_DATE_SETTINGS);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == preference_wifi) {
            startActivity(intent_settings_wifi);
        } else if (preference == preference_network) {
            startActivity(intent_settings_network);
        } else if (preference == preference_sdcard) {
            startActivity(intent_settings_sdcard);
        } else if (preference == preference_app_running) {
            startActivity(intent_settings_app_running);
        } else if (preference == preference_data_time) {
            startActivity(intent_settings_data_time);
        }
        return false;
    }
}
