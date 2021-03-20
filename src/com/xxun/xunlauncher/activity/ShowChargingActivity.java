package com.xxun.xunlauncher.activity;

import com.xxun.xunlauncher.R;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import com.xxun.xunlauncher.gif.GifTempletView;
//import android.support.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * @author lihaizhou
 * @time 2017.12.28
 * @class describe 亮屏后显示正在充电动画以及当前电量值
 */

public class ShowChargingActivity extends Activity {
    private TextView powerTv;
    private boolean isChargingFull;
    private static final String TAG = "ShowChargingActivity";	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charging);
        String power = getIntent().getStringExtra("power");
        powerTv = (TextView) findViewById(R.id.current_power_value);
	GifTempletView gifTempletView = (GifTempletView)findViewById(R.id.charging);
	
	if("100".equals(power)){
	   IntentFilter batteryChargeFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
           Intent batteryStatusIntent = registerReceiver(null, batteryChargeFilter);
	   if(batteryStatusIntent!=null){
	      int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	      if(status == BatteryManager.BATTERY_STATUS_CHARGING){
	         Log.d(TAG,"charging going on");
	         isChargingFull = false;
	      }else if(status == BatteryManager.BATTERY_STATUS_FULL){
	         isChargingFull = true;
	         Log.d(TAG,"charging full!");
	      }
	   }
	}else{
	   isChargingFull = false;
	}

	if(isChargingFull){
	powerTv.setText("充电完成");
	gifTempletView.setMovieResource(R.raw.charging_full);
	}else{
	powerTv.setText("当前电量 "+power+"%");
	gifTempletView.setMovieResource(R.raw.charging);
	}

	ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
			finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

}
