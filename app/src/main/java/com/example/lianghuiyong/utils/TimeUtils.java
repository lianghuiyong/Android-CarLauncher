package com.example.lianghuiyong.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/1/15.
 */
public class TimeUtils {
    Calendar calendar;

    public TimeUtils(){
        this.calendar =Calendar.getInstance();
        this.calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    }

    //获取星期几方法
    public String getDayOfWeek(){
        String mWay = null;
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case 1:
                mWay ="天";
                break;
            case 2:
                mWay ="一";
                break;
            case 3:
                mWay ="二";
                break;
            case 4:
                mWay ="三";
                break;
            case 5:
                mWay ="四";
                break;
            case 6:
                mWay ="五";
                break;
            case 7:
                mWay ="六";
                break;
        }
        return "星期"+mWay;
    }

    //获取十二进制 A/PM 时:分
    public String getHour_Min12(){

        String hour;
        String min ;
        String dayFlag;
        if (calendar.get(Calendar.HOUR_OF_DAY)>13) {
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY) - 12);
            dayFlag = "PM ";
        }
        else {
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            dayFlag = "AM ";
        }

        if (calendar.get(Calendar.MINUTE) < 10)
            min = "0"+String.valueOf(calendar.get(Calendar.MINUTE));
        else
            min = String.valueOf(calendar.get(Calendar.MINUTE));

        String hour_Min12 = dayFlag + hour + ":" + min;
        //e.g: AM 11:12
        return hour_Min12;
    }

}
