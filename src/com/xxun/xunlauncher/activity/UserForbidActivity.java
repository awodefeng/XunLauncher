package com.xxun.xunlauncher.activity;

import android.app.Activity;
import com.xxun.xunlauncher.R;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.util.Calendar;
import java.util.TimeZone;
import android.util.Log;
import android.graphics.Color;
import android.widget.TextView;
import java.util.GregorianCalendar;
import com.xxun.xunlauncher.Constants;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import com.xxun.xunlauncher.utils.PowerUtils;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.os.SystemProperties;
import android.util.XiaoXunUtil;
import com.xxun.xunlauncher.receiver.BatteryReceiver;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xxun.xunlauncher.utils.NetworkUtils;

import com.xiaoxun.xiaoxuninstallapk.IAppStoreService;
import com.xiaoxun.xiaoxuninstallapk.IProgressCallBack;
import com.xiaoxun.sdk.utils.Constant;


/**
 *  @author lihaizhou
 *  @time 2018.06.08
 *  @describe 插入充电器后显示的全局提示窗口
 *            拔掉充电器后窗口消失且窗口不可滑动
 */
	 
public class UserForbidActivity extends Activity{

    private TextView currentTimeTv,powerTv,app_mention,install_mention,chargehint;
    private static final int UPDATE_TIME = 1;
    private int[] timedigits;
    private ImageView mImgH_1;
    private ImageView mImgH_2;
    private ImageView mImgM_1;
    private ImageView mImgM_2;
    private ImageView powerImg;
    private ImageView settingImg;
    private int[] powerImgLists;
    private BatteryValueReceiver batteryValueReceiver;
    private BatteryBroadcastReceiver batteryBroadcastReceiver;
    private static final String TAG = "UserForbidActivity";
    private String CurrentAppName  = "";
    private String CurrentPckageName  ="";
    private int state = 1;
    private NetworkUtils mNetUtil;
    private static final int IDLE  = 1;//状态空闲，可下载，可卸载，可安装
    private static final int DOWNLOADING  = 2;//正在下载
    private static final  int KEEP_DOWNLOAD  = 3;//断点继续下载
    private static final  int INSTALL_NEED  = 4;//apk下载完成，待安装
    private static final  int INSTALLING  = 5;//正在安装
    private static final  int INSTALLING_COMPLETE_SUCCESS  = 6;//安装成功
    private static final  int INSTALLING_COMPLETE_FAIL  = 7;//安装失败
    private static final  int UNINSTALLING  = 8;//正在卸载
    private static final  int UNINSTALLING_COMPLETE_SUCCESS  = 9;//卸载成功
    private static final  int UNINSTALLING_COMPLETE_FAIL  = 10;//卸载失败
    private static final  int NOTIFY_BEGAIN_TASK  = 11;//通知开始任务
    private static final  int NOTIFY_NO_TASK  = 12;//通知开始任务
    private boolean HAS_FIRST_DOWNLOAD = false;
    private static final String APP_INSTALL = "com.xiaoxun.app.install";
    private static final String APP_UNINSTALL = "com.xiaoxun.app.uninstall";
    private static final String APPSTORE_TASK_BEGAIN = "com.xiaoxun.appstore.task.begain";
    private static final String APPSTORE_TALL_NOT_RAM = "com.xiaoxun.appstore.tall.not_ram";
    private int lastPorgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_forbid);
        Log.d(TAG,"UserForbidActivity oncreate");
        initData();
        //currentTimeTv = (TextView) findViewById(R.id.time_tv);
        powerTv = (TextView) findViewById(R.id.power_tv);
        app_mention = (TextView) findViewById(R.id.app_mention);
        install_mention = (TextView) findViewById(R.id.install_mention);
        chargehint = (TextView) findViewById(R.id.charging_hint);
        LauncherApplication.addIntoWaitDestoryList(this, "UserForbidActivity");
        new TimeThread().start();
        //registerPowerListener(); 
	batteryBroadcastReceiver = new BatteryBroadcastReceiver();
	IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        batteryFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        batteryFilter.addAction(APPSTORE_TASK_BEGAIN);
		//add zhj
		batteryFilter.addAction(APPSTORE_TALL_NOT_RAM);
	registerReceiver(batteryBroadcastReceiver, batteryFilter); 

	batteryValueReceiver = new BatteryValueReceiver();
        IntentFilter powerValueFilter = new IntentFilter();
        powerValueFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryValueReceiver, powerValueFilter);
        if(XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT){
             state = Settings.System.getInt(getContentResolver(),"xunapp_state",1);
			 Log.d("zhj","state =" + state);
             mHandler.sendEmptyMessageDelayed(0x1001,2000);
             mNetUtil= NetworkUtils.getNetWorkUtilsInstance();
          } 
	Settings.System.putString(getContentResolver(), "on_xunlauncher_homescreen", "true");
    }
    
    //[add by jxring for appstore 2019.1.10 start..................................]
    private void updateAppStoreUI(int progress,String appName,int state){
          chargehint.setText(getResources().getString(R.string.appstore_app_show,appName));
          chargehint.setTextColor(Color.WHITE);
          String mention = "";
          install_mention.setText(mention);
          switch(state){
              case DOWNLOADING:
                mention = getResources().getString(R.string.appstore_app_download,progress+"%");
              break;
              case INSTALLING:
                mention = getResources().getString(R.string.appstore_app_install);
              break;
              case INSTALLING_COMPLETE_SUCCESS:
                mention = getResources().getString(R.string.appstore_app_install_complete);
              break;
              case INSTALLING_COMPLETE_FAIL:
                mention = getResources().getString(R.string.appstore_app_install_fail);
              break;
              case NOTIFY_NO_TASK:
                mention = getResources().getString(R.string.appstore_app_settings);
                chargehint.setText(getResources().getString(R.string.charging));
                chargehint.setTextColor(Color.parseColor("#FF605F"));
                if(settingImg != null) settingImg.setEnabled(true);
              break;
          }
          install_mention.setText(mention);
    }

    IAppStoreService mAppstoreService;

    IProgressCallBack mprogress = new IProgressCallBack.Stub(){
        @Override
        public void getProgress(int progress,String name,int state) throws RemoteException {
                        Message message = new Message();
                        Bundle mbundle = new Bundle();
                        mbundle.putString("appName",name);
                        mbundle.putInt("progress",progress);
                        mbundle.putInt("state",state);
                        message.what = 0x1002;
                        message.setData(mbundle);
                        mHandler.sendMessage(message);
                
        }
    };
    
    @Override
    public void onPause() {
        super.onPause();
        //[add by jxring for appstore 2019.3.12 start]
        if(XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT){
                if(mHandler.hasMessages(0x1003)){
                        mHandler.removeMessages(0x1003);
                }
        }
        lastPorgress = 0;
        //[add by jxring for appstore 2019.3.12 end!]
    }
    


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAppstoreService = IAppStoreService.Stub.asInterface(service);
            try {
                mAppstoreService.registercallback(mprogress);
            }catch (RemoteException re){

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                  mAppstoreService.unregistercallback(mprogress);
            }catch (RemoteException re){

            }

        }
    };
    
   //[add by jxring for appstore 2019.1.10 end..................................]
    private void initData(){
        timedigits = new int[]{
                R.drawable.digit0,R.drawable.digit1,R.drawable.digit2,R.drawable.digit3,R.drawable.digit4,R.drawable.digit5,R.drawable.digit6,
                R.drawable.digit7,R.drawable.digit8,R.drawable.digit9
            };
        mImgH_1 = (ImageView)findViewById(R.id.h_1);
        mImgH_2 = (ImageView)findViewById(R.id.h_2);
        mImgM_1 = (ImageView)findViewById(R.id.m_1);
        mImgM_2 = (ImageView)findViewById(R.id.m_2);

        powerImg = (ImageView)findViewById(R.id.power_img);
        powerImgLists = new int[]{
                    R.drawable.power10, R.drawable.power20, R.drawable.power30, R.drawable.power40, R.drawable.power50, 
                    R.drawable.power60, R.drawable.power70,R.drawable.power80,R.drawable.power90,R.drawable.power100,R.drawable.powerfull
            };
        settingImg = (ImageView)findViewById(R.id.setting_img);

        settingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    startActivity(getPackageManager().getLaunchIntentForPackage("com.xxun.watch.xunsettings"));
                } catch (NullPointerException ex) {
                    Toast.makeText(UserForbidActivity.this, "not found setting?", Toast.LENGTH_SHORT).show();
                } 
            }
        });

    }


    /**
     * @author lihaizhou
     * @createtime 2018.06.08
     * @describe 每隔一分钟刷新下界面时间
     */
    class TimeThread extends Thread {
        @Override
        public void run (){
            try {
                Thread.sleep(1000);
                Message msg = new Message();
                msg.what = UPDATE_TIME;
                mHandler.sendMessage(msg);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:
		    if(isCharging()){
		       refreshTime();
		    }else{
		       finish();
		    }
                    break;
                 case 0x1001:
				 		Log.d("zhj","==== bindService  ===");
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("ado.install.xiaoxun.com.xiaoxuninstallapk", "com.xiaoxun.service.AppStoreService"));
                        boolean isSuccess = bindService(intent,mConnection,BIND_AUTO_CREATE);
                     break;
                 case 0x1002:
                        int progress  = msg.getData().getInt("progress");
                        String appName  = msg.getData().getString("appName");
                        int state  = msg.getData().getInt("state");
                        if(progress > lastPorgress){
                            updateAppStoreUI(progress,appName,state);
                        }
                        lastPorgress = progress;
                        if(lastPorgress == 100){
                            lastPorgress = 0;
                        }
                        break;
                  case 0x1003:
				       Log.d("zhj","==== start downloadapk===");
				       if(mAppstoreService != null && mNetUtil.isNetworkAvailable()){
                        try{
                                //HAS_FIRST_DOWNLOAD = true;
								Log.d("zhj","==== downloadapk begin ");
                                if(settingImg != null) settingImg.setEnabled(false);
                                mAppstoreService.updatetask();
                                mAppstoreService.downloadapk();
                        }catch (RemoteException re){

                        }
                     }
                     break;
                default:
                    break;
            }
        }
    };

    /**
     * @author lihaizhou
     * @createtime 2018.06.21
     * @describe 刷新当前时间显示
     */
    public void refreshTime() {
        int mHour;
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        String formate = Settings.System.getString(LauncherApplication.getInstance().getContentResolver(), Constants.CLOCK_FORMATE_NAME);
        if ("".equals(formate) || formate == null) {
            mHour = calendar.get(Calendar.HOUR);
            Settings.System.putString(LauncherApplication.getInstance().getContentResolver(), "clock_formate", "12");
        } else {
            if (Constants.CLOCK_TWELVE_FORMATE.equals(formate)) {
                mHour = calendar.get(Calendar.HOUR);
            } else {
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
            }
        }
        int mMinute = calendar.get(12);
        //显示小时
        int h_1 = mHour / 10;
        int h_2 = mHour % 10;
        if (h_1 == 0 && h_2 == 0) {
            mImgH_1.setImageResource(timedigits[1]);
            mImgH_2.setImageResource(timedigits[2]);
        }else {
            mImgH_1.setImageResource(timedigits[h_1]);
            mImgH_2.setImageResource(timedigits[h_2]);
        }
        //显示分钟
        int m_2 = mMinute % 10;
        mImgM_1.setImageResource(timedigits[mMinute / 10]);
        mImgM_2.setImageResource(timedigits[m_2]);

    }

     /**
     * @author lihaizhou
     * @createtime 2018.06.08
     * @describe 监听power值变化
     */
    /*private void registerPowerListener() {
        PowerUtils.registerPowerListener(this, new BatteryReceiver.BatteryReceiverListener() {
            @Override
            public void currentPower(int power) {
                powerTv.setText("当前电量: "+power + "%");
		if(power == 100 && isChargingFull()){
		   powerImg.setImageResource(powerImgLists[10]);
		}else{
		   int currentPowerLevel = getCurrentPowerLevel(power);
                   powerImg.setImageResource(powerImgLists[currentPowerLevel]);
		}
            }
        });
    }*/

    /**
     * @author lihaizhou
     * @createtime 2018.11.26
     * @describe 监听power值变化
     */
    class BatteryValueReceiver extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context context, Intent intent) {
           if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
	       int currentPowerValue = intent.getExtras().getInt("level") * 100 / intent.getExtras().getInt("scale");
	       powerTv.setText("当前电量: "+currentPowerValue + "%");
		if(currentPowerValue == 100 && isChargingFull()){
		   powerImg.setImageResource(powerImgLists[10]);
		}else{
		   int currentPowerLevel = getCurrentPowerLevel(currentPowerValue);
                   powerImg.setImageResource(powerImgLists[currentPowerLevel]);
		}
	   }
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.06.21
     * @describe 获取当前手表的电量level
     */
    private int getCurrentPowerLevel(int currentPower) {
        int powerLevel = 3;
        if (currentPower >= 0 && currentPower <=10) {
            powerLevel = 0;
        } else if (currentPower > 10 && currentPower < 20) {
            powerLevel = 1;
        } else if (currentPower >= 20 && currentPower < 30) {
            powerLevel = 2;
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
        } else if (currentPower >= 90 && currentPower <= 100) {
            powerLevel = 9;
        }
        return powerLevel;
    }
    
   /**
     * @author lihaizhou
     * @createtime 2018.06.25
     * @describe check whether charging full
     */
   public boolean isChargingFull() {
           IntentFilter batteryChargeFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
           Intent batteryStatusIntent = registerReceiver(null, batteryChargeFilter);
	   if(batteryStatusIntent!=null){
	      int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	      if(status == BatteryManager.BATTERY_STATUS_FULL){
	         return true;
	      }
	   }
	   return false;
    }
    
    public boolean isCharging() {
           boolean isUsbConfigured = SystemProperties.getBoolean("persist.sys.isUsbConfigured", false);
	   return isUsbConfigured;
    }

    class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
			if(Intent.ACTION_POWER_CONNECTED.equals(action)){
				return;
			}else if(Intent.ACTION_POWER_CONNECTED.equals(action)){
				finish();
				return;
			}else if(APPSTORE_TASK_BEGAIN.equals(action)){
				if(mAppstoreService != null && mNetUtil.isNetworkAvailable() && Settings.System.getInt(getContentResolver(),"xunapp_state",IDLE) == IDLE){

					try{
							mAppstoreService.updatetask();
							mAppstoreService.downloadapk();
					}catch (RemoteException re){
				
					}
				 }

			}else if(APPSTORE_TALL_NOT_RAM.equals(action)){
				//add zhj
				String text = install_mention.getText().toString();
				if(getResources().getString(R.string.appstore_app_install_complete).equals(text)){
				    updateAppStoreUI(0,"",NOTIFY_NO_TASK);
				}
				if(settingImg != null) settingImg.setEnabled(true);
			}
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "UserForbidActivity onNewIntent");
    }

    @Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isChargingFull()) {  
            powerImg.setImageResource(powerImgLists[10]);
        }
    } 

    /**
     * @author lihaizhou
     * @createtime 2018.06.08
     * @describe 系统回调方法, 因该窗口需要屏蔽掉右滑退出功能，故重写back键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTime();
        //[add by jxring for appstore 2019.3.12 start]
        if(XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT){
                if(mHandler.hasMessages(0x1003)){
                        mHandler.removeMessages(0x1003);
						Log.d("zhj","==== removeMessages downloadapk===");
                }
                mHandler.sendEmptyMessageDelayed(0x1003,3000);
        }
        //[add by jxring for appstore 2019.3.12 end!]
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT){
           //HAS_FIRST_DOWNLOAD = false;
           try {
                if(mAppstoreService != null){ 
                        mAppstoreService.pausedownload();
                        unbindService(mConnection);
                }
            }catch (RemoteException re){

            }
        }
    lastPorgress = 0;
	unregisterReceiver(batteryBroadcastReceiver);
	//PowerUtils.unRegisterPowerListener(this);
	unregisterReceiver(batteryValueReceiver);
        mHandler.removeCallbacksAndMessages(null);
    }

}
