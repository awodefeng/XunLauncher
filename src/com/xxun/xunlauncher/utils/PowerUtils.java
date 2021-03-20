package com.xxun.xunlauncher.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import com.xxun.xunlauncher.receiver.BatteryReceiver;

/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe 电量相关的工具类
 */
public class PowerUtils {

    private static BatteryReceiver batteryReceiver;

    /**
     * @param activity 注册该listener的Activity
     * @param listener 电量&充电状态接口
     * @author lihaizhou
     * @createtime 2017.11.20
     * @describe 注册电量值&充电状态监听
     */
    public static void registerPowerListener(Activity activity, BatteryReceiver.BatteryReceiverListener listener) {
        batteryReceiver = new BatteryReceiver();
        batteryReceiver.addPowerListener(listener);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        activity.registerReceiver(batteryReceiver, ifilter);
    }

    /**
     * @param activity 注册该listener的Activity
     * @author lihaizhou
     * @createtime 2017.11.20
     * @describe 解除注册电量值&充电状态监听
     */
    public static void unRegisterPowerListener(Activity activity) {
        if (batteryReceiver != null) {
	    try{
	       activity.unregisterReceiver(batteryReceiver);
	    }catch (IllegalArgumentException e) {  
               e.printStackTrace();  
            }
            batteryReceiver.removePowerListeners();
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2017.09.20
     * @describe 获取当前手表的电量值
     */
    public static int getCurrentPowerLevel(int currentPower) {
        int powerLevel = 3;
	if(currentPower == 0){
	    powerLevel = 0;
	} else if (currentPower >= 0 && currentPower < 10) {
            powerLevel = 1;
        } else if (currentPower >= 10 && currentPower <=20) {
            powerLevel = 2;
        } else if (currentPower > 20 && currentPower < 30) {
            powerLevel = 3;
        } else if (currentPower >= 30 && currentPower < 40) {
            powerLevel = 3;
        } else if (currentPower >= 40 && currentPower < 50) {
            powerLevel = 4;
        } else if (currentPower >= 50 && currentPower < 60) {
            powerLevel = 5;
        } else if (currentPower >= 60 && currentPower < 70) {
            powerLevel = 6;
        } else if (currentPower >= 70 && currentPower < 80) {
            powerLevel = 7;
        } else if (currentPower >= 80 && currentPower < 90) {
            powerLevel = 8;
        } else if (currentPower >= 90 && currentPower < 100) {
            powerLevel = 9;
        } else if (currentPower == 100) {
            powerLevel = 10;
        }
        return powerLevel;
    }

}
