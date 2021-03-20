package com.xxun.xunlauncher.receiver;

import java.util.List;
import android.util.Log;
import android.os.Bundle;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.BatteryManager;
import java.util.concurrent.Executors;
import android.content.BroadcastReceiver;
import java.util.concurrent.ExecutorService;
import com.xxun.xunlauncher.utils.UploadStatusUtils;
import com.xxun.xunlauncher.utils.LowBatteryWarningUpload;

/**
 * @author lihaizhou
 * @createtime 2017.10.20
 * @class describe 接收电池状态变化的广播类
 */
public class BatteryReceiver extends BroadcastReceiver {

    private int currentPowerValue;
    private int lastReportPowerValue;
    private boolean isChargingNow;
    private long lastReportToServerTime;
    private Object lock = new Object();
    private PowerManager.WakeLock wakeLock;
    private PowerManager powerManager = null;
    private LowBatteryWarningUpload lowBatteryWaringUpload;
    private ExecutorService singleThreadExecutor;
    protected List<BatteryReceiverListener> batteryListeners = new ArrayList<BatteryReceiverListener>();
    protected static List<ChargeStateListener> chargeListeners = new ArrayList<ChargeStateListener>();

    private long lastTime;
    private static final String TAG = "BatteryReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isChargingNow = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            currentPowerValue = intent.getExtras().getInt("level") * 100 / intent.getExtras().getInt("scale");

	    Log.d("stevenli","current charging status = :"+status+", currentPowerValue = "+currentPowerValue);
	    //when charge full,light screen by lihaizhou begin 20190301
            if(status== BatteryManager.BATTERY_STATUS_FULL && (System.currentTimeMillis() - lastTime) > 1000*60*30){
	       if (powerManager == null) {
            	   powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
               }
	       if(!powerManager.isScreenOn() && currentPowerValue==100){
	           wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "filtercamera");
                   wakeLock.acquire(500);
		   lastTime = System.currentTimeMillis();
	        }
	    }
	    //when charge full,light screen end 20190301
	    if(singleThreadExecutor==null){
	       singleThreadExecutor = Executors.newSingleThreadExecutor();
	    }
	       singleThreadExecutor.execute(new Runnable() {
               @Override
               public void run() {
                  if (isChargingNow) {
                      UploadStatusUtils.getUploadStatusUtilsInstance().setChargeStatus("1");
                  } else {
                      UploadStatusUtils.getUploadStatusUtilsInstance().setChargeStatus("0");

		      //低电量上报 pzh add start
		      if(currentPowerValue<25)
                      {
			 Log.d(TAG,"electricity is low 25%!!!! currentPowerValue="+currentPowerValue);
			 if(lowBatteryWaringUpload==null){
			    lowBatteryWaringUpload = new LowBatteryWarningUpload(context);
			 }
			 lowBatteryWaringUpload.xunHandleLowBatteryEvent(currentPowerValue,isChargingNow);	
	              }
		      //pzh add end
                  }

                 //Add by xuzhonghong for upload watch status at 20171206 start
                 if (currentPowerValue!=lastReportPowerValue) {
		     if(currentPowerValue%5==0){
			UploadStatusUtils.getUploadStatusUtilsInstance().setBatteryLevel(Integer.toString(currentPowerValue));			    
                        if(isChargingNow){
                           UploadStatusUtils.getUploadStatusUtilsInstance().uploadStatus(context);
                        }else{
                           UploadStatusUtils.getUploadStatusUtilsInstance().uploadStatusForpadding(context); 
                        }
		        lastReportPowerValue = currentPowerValue;
		     }  
                  }
                 //Add by xuzhonghong for upload watch status at 20171206 end
                 }
              });
	 //通知所有注册监听者状态变化
            notifyPowerStateToAll();
	    notifyChargeStateToAll();
       }    
    }

    public void addPowerListener(BatteryReceiverListener batteryReceiverListener) {
        synchronized (lock) {
            batteryListeners.add(batteryReceiverListener);
        }
    }

    public void addChargeListener(ChargeStateListener chargeStateListener) {
        synchronized (lock) {
            chargeListeners.add(chargeStateListener);
        }
    }

    public void removePowerListeners() {
        synchronized (lock) {
            batteryListeners.clear();
        }
    }
    
    public void removeChargeListeners() {
        synchronized (lock) {
            chargeListeners.clear();
        }
    }

    private void notifyPowerStateToAll() {
        synchronized (lock) {
            for (BatteryReceiverListener listener : batteryListeners) {
                notifyPowerState(listener);
            }
        }
    }

    private void notifyPowerState(BatteryReceiverListener listener) {
        listener.currentPower(currentPowerValue);
    }
     
    private void notifyChargeStateToAll() {
        synchronized (lock) {
            for (ChargeStateListener listener : chargeListeners) {
                notifyChargeState(listener);
            }
        }
    }

    private void notifyChargeState(ChargeStateListener listener) {
        listener.isChargeNow(isChargingNow);
    }
    
     /* 
     *注册Charge状态监听器
     * */
    public static void registerChargeListener(ChargeStateListener listener) {
        chargeListeners.add(listener);
    }

    /*
    * 解除注册Charge监听器
    * */
    public static void unRegisterChargeListener() {
        chargeListeners.clear();
    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.30
     * @describe 接收当前电量值变化
     */
    public interface BatteryReceiverListener {
        void currentPower(int power);
    }
    
    /**
     * @author lihaizhou
     * @createtime 2017.12.26
     * @describe Charge状态变化接口
     */
    public interface ChargeStateListener {
        void isChargeNow(boolean isChargingNow);
    }

}
