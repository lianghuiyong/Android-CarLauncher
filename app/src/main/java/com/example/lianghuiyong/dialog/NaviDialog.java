package com.example.lianghuiyong.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.example.lianghuiyong.R;
import com.example.lianghuiyong.data.MainApplication;
import com.txznet.sdk.TXZNavManager;

import static com.txznet.sdk.TXZNavManager.NavToolType.NAV_TOOL_BAIDU_NAV_HD;
import static com.txznet.sdk.TXZNavManager.NavToolType.NAV_TOOL_GAODE_MAP;

/**
 * Created by lianghuiyong on 2016/1/27.
 */
public class NaviDialog extends Dialog implements View.OnClickListener ,CompoundButton.OnCheckedChangeListener{

    private CheckBox baiduCheckBox = null;
    private CheckBox gaodeCheckBox = null;
    private Intent baiduNaviIntent = null;
    private Intent gaodeNaviIntent = null;
    private Context context;
    private SharedPreferences.Editor voiceNaviEditor = null;
    private String naviSP = null;

    public NaviDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.navi_choice_layout);

        //初始化Bulider处理事件
        builder_init();

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        lp.width = (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.5); // 宽度
        lp.height =(int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.6); // 高度
        dialogWindow.setAttributes(lp);
    }
    private void builder_init() {
        //init_view
        RelativeLayout layout_kailide = (RelativeLayout)this.findViewById(R.id.navi_baidu);
        RelativeLayout layout_gaode = (RelativeLayout)this.findViewById(R.id.navi_gaode);
        baiduCheckBox = (CheckBox)this.findViewById(R.id.navi_choice_baidu);
        gaodeCheckBox = (CheckBox)this.findViewById(R.id.navi_choice_gaode);

        //init data
        layout_kailide.setOnClickListener(this);
        layout_gaode.setOnClickListener(this);
        baiduCheckBox.setOnCheckedChangeListener(this);
        gaodeCheckBox.setOnCheckedChangeListener(this);


        //初始化checkbox选中状态
        naviSP = MainApplication.customSpf.getString("voiceDefaltNavi", "BaiduMap");
        if(naviSP.equals("BaiduMap")){
            baiduCheckBox.setChecked(true);
        }else if (naviSP.equals("GaodeMap")){
            gaodeCheckBox.setChecked(true);
        }


        //百度地图导航
        baiduNaviIntent=new Intent();
        baiduNaviIntent.setClassName("com.baidu.navi.hd", "com.baidu.navi.NaviActivity");
        baiduNaviIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        //高德地图导航
        gaodeNaviIntent = new Intent();
        //gaodeNaviIntent.setClassName("com.autonavi.xmgd.navigator", "com.autonavi.xmgd.navigator.Warn");
        gaodeNaviIntent.setClassName("com.autonavi.minimap", "com.autonavi.map.activity.SplashActivity");
        gaodeNaviIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navi_baidu:
                try {
                    context.startActivity(baiduNaviIntent);
                    this.dismiss();  //关闭显示的activity上的Dialog
                }
                catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.navi_gaode:
                try {
                    context.startActivity(gaodeNaviIntent);
                    this.dismiss();  //关闭显示的activity上的Dialog
                }
                catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        voiceNaviEditor = MainApplication.customSpf.edit();  //获取SharedPreferences
        switch (buttonView.getId()){
            case R.id.navi_choice_baidu:
                if(isChecked){
                    buttonView.setEnabled(false);
                    gaodeCheckBox.setChecked(false);          //高德取消选中
                    baiduCheckBox.setEnabled(false);           //禁止点击
                    gaodeCheckBox.setEnabled(true);

                    //保存用户操作
                    voiceNaviEditor.putString("voiceDefaltNavi","BaiduMap");
                    voiceNaviEditor.commit();

                    //修改同行者 默认导航
                    TXZNavManager.getInstance().setNavTool(NAV_TOOL_BAIDU_NAV_HD);
                }
                break;

            case R.id.navi_choice_gaode:
                if(isChecked){
                    baiduCheckBox.setChecked(false);           //百度取消选中
                    gaodeCheckBox.setEnabled(false);           //禁止点击
                    baiduCheckBox.setEnabled(true);

                    //保存用户操作
                    voiceNaviEditor.putString("voiceDefaltNavi", "GaodeMap");
                    voiceNaviEditor.commit();

                    //修改同行者 默认导航
                    TXZNavManager.getInstance().setNavTool(NAV_TOOL_GAODE_MAP);
                }
                break;
        }
    }
}
