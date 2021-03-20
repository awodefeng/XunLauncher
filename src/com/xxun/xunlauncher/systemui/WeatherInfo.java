package com.xxun.xunlauncher.systemui;

import android.os.Handler;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

import java.util.List;

import com.xxun.xunlauncher.utils.NetworkUtils;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IMessageReceiveListener;
import com.xiaoxun.sdk.IResponseDataCallBack;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import android.provider.Settings;


import java.text.SimpleDateFormat;
import java.util.Locale;

import java.lang.Object;

import android.os.CountDownTimer;

public class WeatherInfo {

    private String today_weather;
    private String tomorrow_weather;
    private String third_weather;
    private String weather_cell_msg;
    private TelephonyManager tm;
    private int pull_weather = 0;   //0,第一次开机拉取
    private Context mcontext;
    private static final String TAG = "WeatherInfo";
    private boolean get_bts_once = false;
    private XiaoXunNetworkManager weathernetworkmanger;


    public WeatherInfo(Context context) {
        mcontext = context;
    }


    public void pullweatherinfostart() {

        Log.d(TAG, " [weather] pullweatherinfostart");
        Log.d(TAG, " [weather] network state: " + NetworkUtils.getNetWorkUtilsInstance().isNetworkAvailable());
        Log.d(TAG, " [weather] SIM state: " + NetworkUtils.getNetWorkUtilsInstance().isSimCardOn());

        weathernetworkmanger = (XiaoXunNetworkManager) mcontext.getSystemService("xun.network.Service");


        Log.d(TAG, "[weather]:weathernetworkmanger.isBinded():" + weathernetworkmanger.isBinded());


        if (NetworkUtils.getNetWorkUtilsInstance().isNetworkAvailable()) {
            Log.d(TAG, " [weather] isBinded success,xunstartgetcellmsg  ");
            get_bts_once = false;    //因为通过获得所有基站信息拿到的bts,bts会获得多次，只需拿到一次上传服务器就可以了
            xunstartgetcellmsg();

        } else {
                Log.d(TAG, " [weather] network service !!!");
        }
  

    }

    private void xunstartgetcellmsg() {
        String mcc = "";
        String mnc = "";
        String lac = "";
        String cid = "";
        String dbm = "";
        String bts = "";


        Log.d(TAG, " [weather] xunstartgetcellmsg function enter ");
        //注册电话监听
        tm = (TelephonyManager) mcontext.getSystemService(mcontext.TELEPHONY_SERVICE);
        // 返回值MCC + MNC
        String operator = tm.getNetworkOperator();   //后续
        int type = tm.getNetworkType();

        Log.d(TAG, " [weather] operator: " + operator + " type: " + type);

        List<CellInfo> cellInfos = tm.getAllCellInfo();
        if (cellInfos == null) {
            Log.d(TAG, " [weather] cellInfos == null,wait miniter pull again!!! ");
            return;
        }

        for (CellInfo cellInfo : cellInfos) {
            Log.d(TAG, "onCreate: " + cellInfo.toString());
            if (cellInfo instanceof CellInfoGsm) {

                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                mcc = Integer.toString(cellInfoGsm.getCellIdentity().getMcc());
                mnc = Integer.toString(cellInfoGsm.getCellIdentity().getMnc());
                lac = Integer.toString(cellInfoGsm.getCellIdentity().getLac());
                cid = Integer.toString(cellInfoGsm.getCellIdentity().getCid());
                dbm = Integer.toString(cellInfoGsm.getCellSignalStrength().getDbm());

                if ((mcc.length() == 3) && (mnc.length() < 3)) {
                    bts = mcc + "," + mnc + "," + lac + "," + cid + "," + dbm;
                    Log.d(TAG, "GSM:bts: " + bts);

                    if (get_bts_once == false) {
                        get_bts_once = true;

                        xunStartWeatherInforequest(bts);
                    }

                } else {

                    Log.d(TAG, "GSM bts invaild:mcc=" + mcc + " mnc" + mnc + " lac" + lac + " cid" + cid + " dbm" + dbm);

                }


            } else if (cellInfo instanceof CellInfoCdma) {


            } else if (cellInfo instanceof CellInfoWcdma) {
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                mcc = Integer.toString(cellInfoWcdma.getCellIdentity().getMcc());
                mnc = Integer.toString(cellInfoWcdma.getCellIdentity().getMnc());
                lac = Integer.toString(cellInfoWcdma.getCellIdentity().getLac());
                cid = Integer.toString(cellInfoWcdma.getCellIdentity().getCid());
                dbm = Integer.toString(cellInfoWcdma.getCellSignalStrength().getDbm());

                if ((mcc.length() == 3) && (mnc.length() < 3)) {
                    bts = mcc + "," + mnc + "," + lac + "," + cid + "," + dbm;
                    Log.d(TAG, "WCDMA:bts: " + bts);
                    if (get_bts_once == false) {
                        get_bts_once = true;
                        xunStartWeatherInforequest(bts);
                    }
                } else {
                    Log.d(TAG, "WCDMA bts invaild:mcc=" + mcc + " mnc" + mnc + " lac" + lac + " cid" + cid + " dbm" + dbm);
                }


            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                mcc = Integer.toString(cellInfoLte.getCellIdentity().getMcc());
                mnc = Integer.toString(cellInfoLte.getCellIdentity().getMnc());
                lac = Integer.toString(cellInfoLte.getCellIdentity().getTac());
                cid = Integer.toString(cellInfoLte.getCellIdentity().getCi());
                dbm = Integer.toString(cellInfoLte.getCellSignalStrength().getDbm());

                if ((mcc.length() == 3) && (mnc.length() < 3)) {
                    bts = mcc + "," + mnc + "," + lac + "," + cid + "," + dbm;
                    Log.d(TAG, "LTE,bts: " + bts);
                    if (get_bts_once == false) {

                        get_bts_once = true;
 
                        xunStartWeatherInforequest(bts);
                    }
                } else {
                    Log.d(TAG, "LTE bts invaild:mcc=" + mcc + " mnc" + mnc + " lac" + lac + " cid" + cid + " dbm" + dbm);
                }


            }
        }
    }

    public void xunStartWeatherInforequest(String cell_bts) {

        Log.d(TAG, "[weather]second xunStartWeatherInforequest:send cell info to service!");

        String eid = weathernetworkmanger.getWatchEid();
        String gid = weathernetworkmanger.getWatchGid();

        Log.d(TAG, "[weather]:second bts:" + cell_bts + "eid:" + eid);
    
 	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());    //james.test
	Log.d(TAG, "[weather][update time]"+format.format(System.currentTimeMillis()) + "currentTimeMillis: " +System.currentTimeMillis());
        
      
	weathernetworkmanger.getWeatherInfo(eid,cell_bts,new xungetWeatherInfocallback());
	
       	//weathernetworkmanger.paddingGetWeatherInfo(eid,cell_bts,new xungetWeatherInfocallback());
        
       

    }



    private class xungetWeatherInfocallback extends IResponseDataCallBack.Stub{
	    
            public void onSuccess(ResponseData response){
                Log.d(TAG,"[weather] response success");

		JSONObject json_weather_info = (JSONObject)JSONValue.parse(response.getResponseData());
		
                Log.d(TAG, "[weather]json_weather_info.toString:" + json_weather_info.toString());
                
	        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());    //james.test
	        Log.d(TAG, "[weather][update time response]"+format.format(System.currentTimeMillis()) + "currentTimeMillis: " +System.currentTimeMillis());

                if(json_weather_info != null)
                {
			//today
		        String weather = (String) json_weather_info.get("weather");

	                if(weather.indexOf("云")!=-1){
                                weather="多云";
			}
			else if(weather.indexOf("晴")!=-1)
			{
				weather="晴";
			}
			else if(weather.indexOf("阴")!=-1)
			{
				weather="阴";
			}
			else if(weather.indexOf("雨")!=-1)
			{
                                if(weather.indexOf("大雨")!=-1)
                                {
				    weather="大雨";
                                }else if(weather.indexOf("中雨")!=-1){
                                    weather="中雨";
				}else if(weather.indexOf("小雨")!=-1){
				     weather="小雨";
				}else{
				     weather="雨";
                                }
			}
			else if(weather.indexOf("雪")!=-1)
			{
					
 				if(weather.indexOf("大雪")!=-1)
                                {
				      weather="大雪";
                                }else if(weather.indexOf("中雪")!=-1){
                                      weather="中雪";
				}else if(weather.indexOf("小雪")!=-1){
				      weather="小雪";
				}else{
				      weather="雪";
                                }
			}
			else if(weather.indexOf("雾")!=-1)
			{
				weather="雾";
			}
			else if(weather.indexOf("霾")!=-1)
			{
				weather="霾";
			}


		        String temp_range = (String) json_weather_info.get("temp_range");
		        String today_weather_msg="今天" + " " + weather + " " + temp_range;
		        Log.d(TAG,"[weather] today_weather:"+today_weather_msg);
			Settings.System.putString(mcontext.getContentResolver(),"today_weather",today_weather_msg); 
		        JSONObject forecast = (JSONObject) json_weather_info.get("forecast");
		        Log.d(TAG, "[weather] weatherJsonArray.toString:" + forecast.toString());

	 		//tomorrow weather
		        String weather1 = (String) forecast.get("weather2");

	                if(weather1.indexOf("云")!=-1){
                                weather1="多云";
			}
			else if(weather1.indexOf("晴")!=-1)
			{
				weather1="晴";
			}
			else if(weather1.indexOf("阴")!=-1)
			{
				weather1="阴";
			}
			else if(weather1.indexOf("雨")!=-1)
			{
                                if(weather1.indexOf("大雨")!=-1)
                                {
				    weather1="大雨";
                                }else if(weather1.indexOf("中雨")!=-1){
                                    weather1="中雨";
				}else if(weather1.indexOf("小雨")!=-1){
				     weather1="小雨";
				}else{
				     weather1="雨";
                                }
			}
			else if(weather1.indexOf("雪")!=-1)
			{
					
 				if(weather1.indexOf("大雪")!=-1)
                                {
				      weather1="大雪";
                                }else if(weather1.indexOf("中雪")!=-1){
                                      weather1="中雪";
				}else if(weather1.indexOf("小雪")!=-1){
				      weather1="小雪";
				}else{
				      weather1="雪";
                                }
			}
			else if(weather1.indexOf("雾")!=-1)
			{
				weather1="雾";
			}
			else if(weather1.indexOf("霾")!=-1)
			{
				weather1="霾";
			}

		        String temp_range1 = (String) forecast.get("temp2");
			String tomorrow_icon = (String) forecast.get("img_title2");
		        String tomorrow_weather_msg = "明天" + " " + weather1 + " " +temp_range1;
		        Log.d(TAG, "[weather] tomorrow_weather:" + tomorrow_weather_msg);

		      
                        Settings.System.putString(mcontext.getContentResolver(),"tomorrow_weather",tomorrow_weather_msg); 
		         //third_weather
		        String weather2 = (String) forecast.get("weather3");

	                if(weather2.indexOf("云")!=-1){
                                weather2="多云";
			}
			else if(weather2.indexOf("晴")!=-1)
			{
				weather2="晴";
			}
			else if(weather2.indexOf("阴")!=-1)
			{
				weather2="阴";
			}
			else if(weather2.indexOf("雨")!=-1)
			{
                                if(weather2.indexOf("大雨")!=-1)
                                {
				    weather2="大雨";
                                }else if(weather2.indexOf("中雨")!=-1){
                                    weather2="中雨";
				}else if(weather2.indexOf("小雨")!=-1){
				     weather2="小雨";
				}else{
				     weather2="雨";
                                }
			}
			else if(weather2.indexOf("雪")!=-1)
			{
					
 				if(weather2.indexOf("大雪")!=-1)
                                {
				      weather2="大雪";
                                }else if(weather2.indexOf("中雪")!=-1){
                                      weather2="中雪";
				}else if(weather2.indexOf("小雪")!=-1){
				      weather2="小雪";
				}else{
				      weather2="雪";
                                }
			}
			else if(weather2.indexOf("雾")!=-1)
			{
				weather2="雾";
			}
			else if(weather2.indexOf("霾")!=-1)
			{
				weather2="霾";
			}

		        String temp_range2 = (String) forecast.get("temp3");
			String third_icon = (String) forecast.get("img_title3");
		        String third_weather_msg = "后天" + " " + weather2 + " " +temp_range2;
		        Log.d(TAG, "[weather] third_weather:" + third_weather_msg);
		       
			Settings.System.putString(mcontext.getContentResolver(),"third_weather",third_weather_msg); 

                        long currentTime = System.currentTimeMillis();
                        Log.d(TAG, " [weather] response success currentTime: " +currentTime);
			Settings.System.putLong(mcontext.getContentResolver(),"last pull weather time", currentTime);
	
                }
                else
                {
		    	Log.d(TAG, "[weather] service response success but content is null!!!");
	
		}
		
            }

            public void onError(int i, String s){
                Log.d(TAG,"[weather] response fail errorMessage:"+s);
  
            }
    }



}
