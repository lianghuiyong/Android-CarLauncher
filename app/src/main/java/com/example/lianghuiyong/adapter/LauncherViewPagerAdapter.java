package com.example.lianghuiyong.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by LiangHuiyong on 2016/1/5.
 */
public class LauncherViewPagerAdapter extends PagerAdapter{

    private Context context = null;
    private ArrayList<View> pagerlist = null;

    public LauncherViewPagerAdapter(ArrayList<View> pagerlist, Context context) {
        this.pagerlist = pagerlist;
        this.context = context;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pagerlist.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pagerlist.get(position));
        return position;
    }

    @Override
    public int getCount() {
        return pagerlist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        //return view == object;
        //根据传来的key，找到view,判断与传来的参数View arg0是不是同一个视图
        //return arg0 == viewList.get((int)Integer.parseInt(arg1.toString()));

        return view == pagerlist.get((int)Integer.parseInt(object.toString()));
    }
}
