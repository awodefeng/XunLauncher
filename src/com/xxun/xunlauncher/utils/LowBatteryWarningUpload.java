package com.xxun.xunlauncher.utils;

import android.app.Service;
import android.content.Context;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

import net.minidev.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.provider.Settings;

/**
 * Created by pengzonghong on 2018/3/15.
 */

public class LowBatteryWarningUpload {

    private static final String TAG = "LowBatteryWarningUpload";

    private static final int LOW_BAT_P25_LEVEL =25;
    private static final int LOW_BAT_P20_LEVEL =20;
    private static final int LOW_BAT_P15_LEVEL =15;
    private static final int LOW_BAT_P10_LEVEL =10;
    private static final int LOW_BAT_P05_LEVEL =5;


    private static final int  LOW_BAT_STATUS_NO_WARNING = 0;
    private static final int  LOW_BAT_STATUS_P25_NO_REPORTED =1;
    private static final int  LOW_BAT_STATUS_P25_REPORTED =2;
    private static final int  LOW_BAT_STATUS_P20_NO_REPORTED =3;
    private static final int  LOW_BAT_STATUS_P20_REPORTED =4;
    private static final int  LOW_BAT_STATUS_P15_NO_REPORTED =5;
    private static final int  LOW_BAT_STATUS_P15_REPORTED =6;
    private static final int  LOW_BAT_STATUS_P10_NO_REPORTED =7;
    private static final int  LOW_BAT_STATUS_P10_REPORTED =8;
    private static final int  LOW_BAT_STATUS_P05_NO_REPORTED =9;
    private static final int  LOW_BAT_STATUS_P05_REPORTED =10;
    private static final int  LOW_BAT_STATUS_MAX =11;


    private int battery_level=0;
    private int lowBatteryState=0;

    private String gid;
    private String eid;
    private static final String type = "battery";
    private String batteryContent;

    private XiaoXunNetworkManager mXiaoXunNetworkManager;
    private Context mcontext;

    public LowBatteryWarningUpload(Context context) {
        mcontext = context;
    }


    public synchronized void xunHandleLowBatteryEvent(int electricity,boolean isChargingNow)
    {
        lowBatteryState = Settings.System.getInt(mcontext.getContentResolver(),"lowbatterystate",0); 

        Log.d(TAG,"read form system ,lowBatteryState="+lowBatteryState +"  electricity=" +electricity +"isChargingNow="+isChargingNow);
        
        if((electricity > LOW_BAT_P20_LEVEL) || isChargingNow){
            lowBatteryState = LOW_BAT_STATUS_NO_WARNING;
            Settings.System.putInt(mcontext.getContentResolver(),"lowbatterystate",lowBatteryState); 
            return;
        }else if(electricity > LOW_BAT_P15_LEVEL){
            if(lowBatteryState == LOW_BAT_STATUS_P20_REPORTED)
                return;
            else
            {    
		lowBatteryState = LOW_BAT_STATUS_P20_NO_REPORTED;
                electricity = LOW_BAT_P20_LEVEL;
            }
        }else if(electricity > LOW_BAT_P10_LEVEL){
            if(lowBatteryState == LOW_BAT_STATUS_P15_REPORTED)
                return;
            else
            {   
		lowBatteryState = LOW_BAT_STATUS_P15_NO_REPORTED;
                electricity = LOW_BAT_P15_LEVEL;
	    }
        }else if(electricity > LOW_BAT_P05_LEVEL){
            if(lowBatteryState == LOW_BAT_STATUS_P10_REPORTED)
                return;
            else
            {
		lowBatteryState = LOW_BAT_STATUS_P10_NO_REPORTED;
		electricity = LOW_BAT_P10_LEVEL;
            }
        }else if(electricity > 0){
            if(lowBatteryState == LOW_BAT_STATUS_P05_REPORTED)
                return;
            else
             {
		  lowBatteryState = LOW_BAT_STATUS_P05_NO_REPORTED; 
		  electricity = LOW_BAT_P05_LEVEL;
             }
        }else{

        }


        Log.d(TAG,"need upload lowBatteryState=" +lowBatteryState);

        //if the lowbatteryState is odd number,the state need to be send.
        if((lowBatteryState & 0x01)!=0){
            //get next state.
            lowBatteryState += 1;
            Settings.System.putInt(mcontext.getContentResolver(),"lowbatterystate",lowBatteryState); 
            xunReportLowBatteryWarning(electricity);
        }
    }

    private void xunReportLowBatteryWarning(int electricity){

		if (mXiaoXunNetworkManager == null) {
		    mXiaoXunNetworkManager = (XiaoXunNetworkManager) mcontext.getSystemService("xun.network.Service");
		}
		
		if (mXiaoXunNetworkManager.isLoginOK() && mXiaoXunNetworkManager.isBinded()) {
		    gid = mXiaoXunNetworkManager.getWatchGid();
		    eid = mXiaoXunNetworkManager.getWatchEid();
		    batteryContent = Integer.toString(electricity);

                    Log.d(TAG,"eid= " +eid +"  gid= "+gid + "  type="+type+" batteryContent="+batteryContent);

		    mXiaoXunNetworkManager.uploadNotice(eid, gid, type, batteryContent, new lowerBatterySendMessageCallback());
		}
      			
    }

     private class lowerBatterySendMessageCallback extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
            Log.d(TAG, "upload low battery state success!!!" + responseData);
   
        }

        @Override
        public void onError(int i, String s) {
            Log.d(TAG, "upload low battery state fail !!! i = " + i + ",s = :" + s);

             //get next state.  上传失败后，下次可在传一次
             lowBatteryState -= 1;
             Settings.System.putInt(mcontext.getContentResolver(),"lowbatterystate",lowBatteryState); 
        }
    }
}
