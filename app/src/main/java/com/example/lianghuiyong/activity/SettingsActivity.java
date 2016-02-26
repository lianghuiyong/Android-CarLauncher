package com.example.lianghuiyong.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.lianghuiyong.R;
import com.example.lianghuiyong.fragment.NormalSettingsFragment;
import com.example.lianghuiyong.fragment.SystemSettingsPrefFragment;

public class SettingsActivity extends FragmentActivity {

    private RadioGroup mGroup;
    private RadioButton radioButton;
    private FragmentManager manager;
    private SystemSettingsPrefFragment systemSettingsPrefFragment;
    private NormalSettingsFragment normalSettingsFragment;

    public SettingsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init_view();
        init_data();
    }

    private void init_data(){
        systemSettingsPrefFragment = new SystemSettingsPrefFragment();
        normalSettingsFragment = new NormalSettingsFragment(this);
        manager = getFragmentManager();
    }
    private void init_view(){
        mGroup = (RadioGroup)findViewById(R.id.about_group_btn);
        mGroup.setOnCheckedChangeListener(new myCheckChangeListener());

        radioButton = (RadioButton)findViewById(R.id.setting_btn_normalsetteings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        radioButton.setChecked(true);
        manager.beginTransaction().replace(R.id.setting_info_view, normalSettingsFragment, "normalSettingsFragment").commit();
    }

    /**
     *RadioButton切换Fragment
     */
    private class myCheckChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.setting_btn_normalsetteings:
                    manager.beginTransaction().replace(R.id.setting_info_view, normalSettingsFragment, "normalSettingsFragment").commit();
                    break;
                case R.id.setting_btn_systemsetteings:
                    manager.beginTransaction().replace(R.id.setting_info_view, systemSettingsPrefFragment, "systemSettingsPrefFragment").commit();
                    break;
            }
        }
    }
}
