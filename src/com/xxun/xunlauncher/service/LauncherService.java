package com.xxun.xunlauncher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemProperties;
//import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.app.Notification;
import android.app.PendingIntent;

//import com.android.internal.tedongle.PhoneConstants;
//import com.mediatek.telephony.TelephonyManagerEx;
import com.xxun.xunlauncher.activity.MainActivity;
//[add by jxring for Dialer 2017.11.04 start]
import android.os.Handler;
import android.content.IntentFilter;
import android.provider.Settings;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.database.ContentObserver;

import android.content.ContentUris;
import android.content.ContentValues;

import net.minidev.json.JSONObject;

import com.google.gson.Gson;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.app.AlarmManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Date;
import java.text.DateFormat;

import android.os.Message;

import java.text.SimpleDateFormat;

import com.xiaoxun.jason.Contacts.Contacts;
import com.xiaoxun.jason.Contacts.SyncArrayBean;
import com.xiaoxun.jason.every.contact.DeviceInfo;
import com.xiaoxun.jason.WatchMode.WatchMode;
import com.xiaoxun.jason.WatchMode.PLBean;
import com.xiaoxun.jason.preventaddition.PreventObj;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.statistics.XiaoXunStatisticsManager;//del by lihaizhou for the time being avoid comile error
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;

import android.provider.ContactsContract;
import android.provider.CallLog;
//import android.telecom.TelecomManager;
import android.os.Environment;
import android.os.RemoteException;
import android.content.OperationApplicationException;
import android.content.ContentProviderOperation;
import android.os.StatFs;

import java.lang.InterruptedException;
//[add by jxring for Dialer 2017.11.04 end!]

import java.lang.reflect.Method;

import android.app.ActivityManager;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import net.minidev.json.JSONObject;

import android.os.SystemClock;

import java.text.ParseException;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.xxun.xunlauncher.Constants;
import com.xiaoxun.jason.constant.Constant;
import com.xxun.xunlauncher.R;

import android.os.Looper;
import android.content.res.Resources;

import com.xxun.xunlauncher.qrcode.GenerateQrcode;
import com.xxun.xunlauncher.utils.FreeMemoryUtils;
import com.xxun.xunlauncher.utils.NetworkUtils;
import com.xxun.xunlauncher.utils.TempControlUtils;
import com.xxun.xunlauncher.systemui.WeatherInfo;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xxun.xunlauncher.utils.UploadStatusUtils;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.os.CountDownTimer;
import android.app.AlertDialog;
import android.view.Window;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.ImageButton;

import java.util.Timer;
import java.util.TimerTask;

import android.os.SystemProperties;
import android.os.BatteryManager;

import com.xxun.xunlauncher.nvram.NvRAMAgent;

import android.os.ServiceManager;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import android.os.PowerManager;
import android.widget.TextView;
import android.provider.Settings.SettingNotFoundException;

import com.xxun.xunlauncher.utils.PhaseCheckParse;
import com.xxun.xunlauncher.receiver.AudioCommandsReceiver;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.lang.InterruptedException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xiaoxun.jason.Video.InCallBean;
import com.xiaoxun.jason.Video.ReceivETECall;
import com.xiaoxun.jason.Video.ReceiveETEBean;
import com.xiaoxun.jason.Video.SendETEInCall;
import com.xiaoxun.jason.Video.SendETECallToEnd;
import com.xiaoxun.jason.Video.SendEndCallBean;

import java.net.URL;

import android.os.AsyncTask;
import android.util.XiaoXunUtil;

import com.xiaoxun.appstore.DealInstallSubAciton;
import com.xiaoxun.xiaoxuninstallapk.XunConstant;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//add by zhanghaijun@longcheer.com for contacts interface 20190611 start
import com.xxun.xunlauncher.utils.ContactsInterface;
//add by zhanghaijun@longcheer.com for contacts interface 20190611 end
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import com.xxun.xunlauncher.xunaidl.IxunLauncher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import com.xiaoxun.appstore.PLBeanX;

import com.xxun.xunlauncher.IntergralBean;

import java.util.HashMap;
import java.util.Map;
import com.xxun.xunlauncher.dialog.TaskFinshhintDialog;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe XunLauncher服务,主要做一些监测数据库,同步Server等操作
 */

public class LauncherService extends Service {

    private TelephonyManager telecomManager;
    protected static List<FunctionListChangeListener> functionlistListeners = new ArrayList<FunctionListChangeListener>();

    //[add by jxring for Dialer 2017.11.04 start]
    private IntentFilter mFilter;

    private SharedPreferences msp = null;
    private XiaoXunNetworkManager mxiaoXunNetworkManager = null;
    private XiaoXunStatisticsManager mxiaoxunstatisticsManager = null;//del by lihaizhou for the time being avoid comile error

    private Contacts mUpdateContacts = null;
    private DeviceInfo mDeviceInfo = null;
    private String[] project = new String[]{"mimetype", "data1", "data2", "data3", "data4", "data5", "data6", "data7"
            , "data8", "data9", "data10", "data11", "data12"
            , "data13", "data14", "data15"};
    private boolean isDebug = true;
    private boolean isCharging = false;
    private static final String Tag_CONTACT = "jxring";
    private static final long AJUST_TIME = 1546300800000L; //mayanjun change to 20190101;
    //[add by jxring for Dialer 2017.11.04 end!]

    private LoginStateRecevive loginstaterecevive;
    private IntentFilter intentFilter;
    private String qrcode_url = "";
    //    private String serverTime;
    private MediaPlayer mMediaPlayer;
    private Notification notification;
    private static final int MSG_SYNC_SERVER_TIME = 1;
    private String watch_eid;
    private String watch_gid;
    private ExecutorService fixedThreadExecutor;

    //*/ xiaoxun.zhangweinan
    AlertDialog temperWarnDialog = null;
    AlertDialog stopStoryDialog = null;
    private final int HIGHTEMPER_COUNTDOWNTIMER_DURATION = 10000;
    private final int STOPSTORY_COUNTDOWNTIMER_DURATION = 5000;
    private final int HIGHTEMPER_WARNING_ALARM_DURATION = 5 * 60 * 1000;
    private final int HIGHTEMPER_WARNING_TIMER_DURATION = 60 * 1000;
    private final int HIGHTEMPER_WARNING_MAX_VALUE = 49000;
    private final int HIGHTEMPER_WARNING_MIN_VALUE = 46000;
    private PendingIntent mHighTemperIntent = null;
    private static AlarmManager mWarnAlarmManager = null;
    private Timer readTemperTimer = new Timer();
    private int mHealth;
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 09:44:53 +0800
 */
    private int mTemperature;
// End of longcheer:lishuangwei
    private int extra_level = -1;
    PowerManager mPowerManager;
    private static File FACTORYRESET_FLAG_FILE = new File("/productinfo/factoryreset_flag");
    private static File SN_FLAG_FILE = new File("/productinfo/sn_flag");
    private static File BOOT_COMPLETE_FLAG_FILE = new File("/productinfo/boot_completed_flag");
    //*/
    private int isLocalprop;

    private String mLastUploadTime = "";
    private static final String AUTHORITY = "com.xiaoxun.video.provider";
    private static final Uri XUN_VIDEO_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/xxvideolog");

    public static int defalutClockStyleIndex;

    public static final String TAG = "LauncherService";
    private CheckQrcodeData mcheckQrcodeData = null;
    private QrcodeResquestDealRecevive qrcodeResquestDealRecevive;

    //add by zhanghaijun@longcheer.com for contacts interface 20190611 start
    private ContactsInterface myInterface;
    //add by zhanghaijun@longcheer.com for contacts interface 20190611 end

    private AudioCommandsReceiver audioCommandsReceiver;

    private static HashMap<Integer, Integer> resultMap;
    private IntergralBean igbean;
    private IntergralBean intergralBean;
    private ContentResolver resolver;
    private int totalexExp;
    private int getTotal;
    private static HashMap<Integer, String> rulenameMap;

    @Override
    public void onCreate() {
        super.onCreate();
        //markSystemReady();//delete it,because of we recover fallbackhome
        //add by mayanjun for sync time to 20190101;
        if (System.currentTimeMillis() < AJUST_TIME) {
            Log.w(TAG, "System clock is before 20190101; setting to 20190101.");
            SystemClock.setCurrentTimeMillis(AJUST_TIME);
        }

        initServerService();
        initWeatherData();    //pengzonghong 
        openUserdebugMode();  //pengzonghong 
        //[add by jxring for Dialer 2017.11.04 start]
        RegisterCallLogObserve();
        RegisterObserve();
        RegisterIntentFilter();
        RegisterQrcodeResquest();  //by pzh 20190109
        //*/ xiaoxun.zhangweinan, 20190906. receive audio commands
        RegisterAudioCommands();
        //*/
        if (telecomManager == null)
            telecomManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //[add by jxring for Dialer 2017.11.04 end!]
        RegisterLoginState();
        //*/ xiaoxun.zhangweinan, 20180313. for Read SN from NV & save value
        readSn();
        //*/
        //*/ xiaoxun.zhangweinan, 20180207. for read AP temperature
        mWarnAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //*/
        fixedThreadExecutor = Executors.newFixedThreadPool(3);
        //add by zhanghaijun@longcheer.com for contacts interface 2019611 start
        myInterface = new ContactsInterface(getApplicationContext());
        //add by zhanghaijun@longcheer.com for contacts interface 2019611 end

    }

    /**
     * @author lihaizhou
     * @createtime 2018.02.09
     * @describe due to remove fallbackhome,we need add a persistent setting to allow other apps to know the device has been provisioned
     */
    private void markSystemReady() {
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), "user_setup_complete", 1);
    }

    //pengzonghong add start

    private class CheckQrcodeData extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            pullQrcodeFromServer();
            return null;
        }

    }

    private void RegisterQrcodeResquest() {
        IntentFilter mintentFilter = new IntentFilter();
        mintentFilter.addAction("brocast_qrcode_resqust");
        qrcodeResquestDealRecevive = new QrcodeResquestDealRecevive();
        registerReceiver(qrcodeResquestDealRecevive, mintentFilter);
    }

    class QrcodeResquestDealRecevive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "[QRCODE]---brocast_qrcode_resqust----");
            if (mxiaoXunNetworkManager == null) {
                mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
            }
            if (mxiaoXunNetworkManager.isLoginOK()) {
                if (mcheckQrcodeData == null) {
                    Log.d(TAG, "[QRCODE]-----mcheckQrcodeData==null");
                    mcheckQrcodeData = new CheckQrcodeData();
                    mcheckQrcodeData.execute();
                } else {
                    mcheckQrcodeData.cancel(true);
                    Log.d(TAG, "[QRCODE]-----mcheckQrcodeData:" + mcheckQrcodeData);
                    mcheckQrcodeData = new CheckQrcodeData();
                    mcheckQrcodeData.execute();
                }
            }
        }
    }

    private void openUserdebugMode() {
        if (SystemProperties.get("ro.build.type").equals("user")) {
            File file = new File("/data/local.prop");

            if (file.exists()) {
                Log.d("USBDEBUG", "local.prop file is exists,open adb debug!!!");
                //*/ xiaoxun.zhangweinan, 20180411. for restore factoryrest flag in NV
                restoreFacRestFlag((byte) '0');
                //*/
                //Settings.Global.putInt(getContentResolver(),Settings.Global.ADB_ENABLED, 1);
                Settings.System.putInt(getContentResolver(), "is_localprop_exist", 1);
                //*/ xiaoxun.zhangweinan, 20190111. replace key_paths for adb security
                SystemProperties.set("persist.sys.factory.reset", "0");
                //*/
            } else {
                Log.d("USBDEBUG", "local.prop file is not exists!!!");
                Settings.System.putInt(getContentResolver(), "is_localprop_exist", 0);
                //*/ xiaoxun.zhangweinan, 20190111. replace key_paths for adb security
                SystemProperties.set("persist.sys.factory.reset", "1");
                //*/
            }
        } else {
            Log.d("USBDEBUG", "not user version!!!");
        }
    }

    private void initWeatherData() {
        Log.d("weather", "[weather]initWeatherdata!!!");
        long lastTime = Settings.System.getLong(getContentResolver(), "last pull weather time", 0);
        if(lastTime != 0 && (System.currentTimeMillis() - lastTime) > 10800000) {
            Settings.System.putInt(getContentResolver(), "first pull weather msg", 0);
            Settings.System.putString(getContentResolver(), "today_weather", "empty");
            Settings.System.putString(getContentResolver(), "tomorrow_weather", "empty");
            Settings.System.putString(getContentResolver(), "third_weather", "empty");
            Settings.System.putLong(getContentResolver(), "last pull weather time", 0);
            Settings.System.putLong(getContentResolver(), "login in time", 0);
        }
    }
    //pengzonghong add end


    /**
     * @author lihaizhou
     * @createtime 2018.01.02
     * @describe init XiaoXun service
     */
    public void initServerService() {
        setIsCameraUsing("false");
        SystemProperties.set("persist.sys.xxun.ota_status", "0");
        SystemProperties.set("persist.sys.isconfirmstate", "false");
        SystemProperties.set("persist.sys.temper.warning", Boolean.toString(true));
        Settings.Global.putInt(getContentResolver(), "story_downloading_flag", 0);
        mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        mxiaoxunstatisticsManager = (XiaoXunStatisticsManager) getSystemService("xun.statistics.service");//del by lihaizhou for the time being avoid comile error
    }

    /**
     * @author lihaizhou
     * @createtime 2017.12.06
     * @describe 保证XunLauncher服务始终前台运行
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	Log.d("yuanshuai", "onStartCommand");
        notification = new Notification(R.drawable.ic_launcher, getText(R.string.app_name),
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //del by lihaizhou due to compile error on 4.4
        //startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    //*/ xiaoxun.zhangweinan, 20190906. receive audio commands
    private void RegisterAudioCommands() {
        audioCommandsReceiver = new AudioCommandsReceiver();
        IntentFilter audioCommandsFilter = new IntentFilter();
        audioCommandsFilter.addAction("android.action.xun.audio.play");
        audioCommandsFilter.addAction("android.action.xun.audio.record");
        audioCommandsFilter.addAction("android.action.xun.audio.playrecord");
        registerReceiver(audioCommandsReceiver, audioCommandsFilter);
    }
    //*/

    private void RegisterLoginState() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.xiaoxun.sdk.action.LOGIN_OK");
        loginstaterecevive = new LoginStateRecevive();
        registerReceiver(loginstaterecevive, intentFilter);
    }

    class LoginStateRecevive extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            boolean isBinded = SystemProperties.getBoolean("persist.sys.isbinded", false);
            Log.d(TAG, "Launcher receive login broadcast 手表是否绑定" + isBinded);
            // xxun liuluyang start
            if (com.xiaoxun.sdk.utils.Constant.PROJECT_NAME.equals("SW706") && isBinded && NetworkUtils.getNetWorkUtilsInstance().isNeedCarrier(context, Constants.CHINA_TELECOM)) {
                NetworkUtils.getNetWorkUtilsInstance().modifyVolteMsgStatus(context);
            }
            // xxun liuluyang end
            //serverTime = intent.getStringExtra("servertime");
            //Add by lihaizhou,while network is ok, play alert sound
            Log.d(TAG, "is first boot:" + Settings.System.getString(getContentResolver(), "xunlauncher_first_boot"));
            if ("true".equals(Settings.System.getString(getContentResolver(), "xunlauncher_first_boot")) && !isBinded) {
                mMediaPlayer = MediaPlayer.create(context, R.raw.network_success);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                        Settings.System.putString(getContentResolver(), "xunlauncher_first_boot", "false");
                    }
                });

//		AlarmManager mAlarmManager = (AlarmManager) LauncherApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
//                mAlarmManager.setTimeZone("Asia/Shanghai");//GMT+08
            }
            fixedThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    pullQrcodeFromServer();
                    xunPullWeatherInfo(context);
                    //add by liaoyi 19/1/25
                    oldLoginHandle(context);
                    //end
                    //syncServerTime();
                    Looper.loop();
                }
            });
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2017.12.11
     * @describe Sync server Time
     */
//    private void syncServerTime(){
//	SimpleDateFormat serverTimeFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//	SimpleDateFormat systemTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//	try {
//	    if(serverTime == null || serverTime.length() <= 0){
//            serverTime = System.currentTimeMillis()+"";
//	    }else{
//		Log.d(TAG,"sync server time is : "+serverTime);
//		serverTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//                Date resultDate = serverTimeFormat.parse(serverTime);
//        	serverTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        	serverTime = serverTimeFormat.format(resultDate.getTime());
//		Log.d(TAG,"convert server time to local time zone is : "+serverTime);
//	    }
//	  //set serverTime as local time
//            long time = systemTimeFormat.parse(serverTime.substring(0, 13)).getTime();
//	    SystemClock.setCurrentTimeMillis(time);
//            Log.d(TAG, "servertime change to currentTimeMillis formate:" + time);
//        } catch (ParseException e) {
//	    Log.d(TAG, "ParseException:" + e);
//        }
//    }
    private void xunPullWeatherInfo(Context context) {
        Log.d("TAG", "[Weather] login  broadcast,xunPullWeatherInfo");
        WeatherInfo weatherinfo = new WeatherInfo(context);
        weatherinfo.pullweatherinfostart();
    }

    private void pullQrcodeFromServer() {
        watch_eid = mxiaoXunNetworkManager.getWatchEid();
        watch_gid = mxiaoXunNetworkManager.getWatchGid();
        Log.d(TAG, "[QRCODE]:xunnetworkmanger.eid:" + watch_eid + "gid:" + watch_gid);
        mxiaoXunNetworkManager.getGroupInfoByGid(watch_eid, watch_gid, new xungetgroupInfoByGidcallback());
    }

    private class xungetgroupInfoByGidcallback extends IResponseDataCallBack.Stub {

        public void onSuccess(ResponseData response) {
            int i = 0;
            JSONObject object_pl;
            String service_eid;
            JSONObject content = (JSONObject) JSONValue.parse(response.getResponseData());
            JSONArray array_pl = (JSONArray) content.get("PL");
            Log.d(TAG, "[QRCODE]respon onSuccess: " + "[QRCODE]content:" + content.toString() + ",[QRCODE]array_pl:" + array_pl.toString() + ",[QRCODE]array_pl.size():" + array_pl.size());
            if (array_pl.size() > 0) {
                for (i = 0; i < array_pl.size(); i++) {
                    object_pl = (JSONObject) array_pl.get(i);
                    service_eid = (String) object_pl.get("EID");
                    Log.d(TAG, "[QRCODE]service_eid:" + service_eid + "watch_eid:" + watch_eid);
                    if (service_eid.equals(watch_eid)) {
                        qrcode_url = (String) object_pl.get("qrStr");
                    }

                    //获取小孩的体重和升高，pzh add 0613
                    String childweight = String.valueOf(object_pl.get("Weight"));
                    if ((null != childweight) && (!"".equals(childweight)) && (!"0.0".equals(childweight))) {
                        Settings.System.putString(getContentResolver(), "childweight", childweight);
                    }
                    String childHeight = String.valueOf(object_pl.get("Height"));
                    if ((null != childHeight) && (!"".equals(childHeight)) && (!"0.0".equals(childHeight))) {
                        Settings.System.putString(getContentResolver(), "childHeight", childHeight);
                    }
                }
            }
            Log.d(TAG, "[QRCODE]qrcode_url:" + qrcode_url);
            if (qrcode_url != null) {
                GenerateQrcode gen_picture = new GenerateQrcode(getApplicationContext());
                Resources res = getResources();
                gen_picture.xunCreatQRcodeBitmap(res, qrcode_url);
            }
        }

        public void onError(int i, String s) {
            Log.d(TAG, "[QRCODE]response fail errorMessage:" + s);
        }
    }

    //Add by lihaizhou
    private void notifyFunctionlistChangeToAll() {
        for (FunctionListChangeListener listener : functionlistListeners) {
            notifyfunctionState(listener);
        }
    }

    //Add by lihaizhou
    private void notifyfunctionState(FunctionListChangeListener listener) {
        listener.updateFunctionlist();
    }

    /**
     * @author lihaizhou
     * @createtime 2017.11.20
     * @describe APP端功能列表变化接口
     */
    public interface FunctionListChangeListener {
        void updateFunctionlist();
    }

    //Add by lihaizhou 注册functionlist监听器
    public static void registerFunctionListener(FunctionListChangeListener listener) {
        functionlistListeners.add(listener);
    }

    /*
     * Add by lihaizhou 解除注册functionlist监听器
     * */
    public static void unRegisterFunctionListener() {
        functionlistListeners.clear();
    }


    final private IxunLauncher.Stub mstub = new IxunLauncher.Stub() {
        @Override
        public String getEid() {
            if (mxiaoXunNetworkManager == null)
                mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
            return mxiaoXunNetworkManager.getWatchEid();
        }

        @Override
        public String getSid() {
            if (mxiaoXunNetworkManager == null)
                mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
            return mxiaoXunNetworkManager.getSID();
        }

        @Override
        public String getTransMingWen(String MingWen) {
            if (mxiaoXunNetworkManager == null)
                mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
            //Deal Message,And Return;
            String aes_key = mxiaoXunNetworkManager.getAESKey();
            byte[] plainText = MingWen.getBytes();
            if (aes_key != null) {
                try {
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    byte[] raw = aes_key.getBytes();
                    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                    IvParameterSpec iv = new IvParameterSpec(aes_key.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                    byte[] cipherText = cipher.doFinal(plainText);
                    //return cipherText;
                    return Base64.encodeToString(cipherText, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            return null;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mstub;
    }

    //[add by jxring for Dialer 2017.11.04 start]
    private void RegisterCallLogObserve() {
        this.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mCallLogObserver);
        this.getContentResolver().registerContentObserver(XUN_VIDEO_CONTENT_URI, true, mVideoLogObserver);
        this.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("Login_sync_contacts"), true, mLogin_contacts);
    }


    final private ContentObserver mVideoLogObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            UploadStatisticsVideoCallLog(GetLastVideoCallLog());
            DelVideoCallLog();
               /* String callLog = GetLatestVideoCallLog();
                if (!callLog.equals("")) {
                        UploadStatisticsVideoCallLog(callLog);
                        mhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UpLoadVideoCallLog(callLog);
                            }
                        }, 100);
             }*/
        }
    };

    final private ContentObserver mLogin_contacts = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (Settings.System.getInt(getContentResolver(), "Login_sync_contacts", 0) == 1) {
                ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
                scheduledThreadPool.schedule(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("zhj sylar: mLogin_SessionPing here ...");
                        //do sync
                        getContactsFromServer();
                        getDeviceInfo();

                        if (XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT)
                            TellAppStoreUploadSystemApkInfo();
                        Settings.System.putInt(LauncherService.this.getContentResolver(), "Login_sync_contacts", 0);
                    }
                }, 5, TimeUnit.SECONDS);
            }
        }
    };

    private String GetLastVideoCallLog() {
        JSONArray mjson_array = new JSONArray();
        Cursor cursor = getContentResolver().query(XUN_VIDEO_CONTENT_URI,
                new String[]{"type"
                        , "duration"
                        , "number"
                        , "name"
                        , "time"}
                , null, null, null);
        if (cursor != null) {
            if (cursor.moveToLast()) {
                JSONObject Json = new JSONObject();
                Json.put("type", cursor.getInt(cursor.getColumnIndex("type")));
                Json.put("duration", cursor.getInt(cursor.getColumnIndex("duration")));
                Json.put("local_num", "");
                Json.put("name", cursor.getString(cursor.getColumnIndex("name")));
                Json.put("number", cursor.getString(cursor.getColumnIndex("number")));
                Json.put("time", cursor.getString(cursor.getColumnIndex("time")));
                mjson_array.add(Json);

            }
            cursor.close();
        }
        if (mjson_array.size() <= 0) return "";

        return mjson_array.toJSONString();
    }


    private void UploadStatisticsVideoCallLog(String jasonArray) {
        Object obj = JSONValue.parse(jasonArray);
        JSONArray marray = (JSONArray) obj;
        if (mxiaoxunstatisticsManager == null)
            mxiaoxunstatisticsManager = (XiaoXunStatisticsManager) getSystemService("xun.statistics.service");
        for (int i = 0; i < marray.size(); i++) {
            JSONObject mjason = (JSONObject) marray.get(i);
            Integer count_second = (Integer) mjason.get("duration");
            Integer type = (Integer) mjason.get("type");
            switch (type) {
                //呼出
                case 5:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_OUT);
                    if (count_second > 0) {
                        mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_OUT_SUCCESS);
                    } else {
                        mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_OUT_FAILED);
                    }
                    break;
                //呼入未接
                case 6:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_IN);
                    break;
                //呼入接听
                case 7:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_IN);
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_IN_SUCCESS);
                    break;
                //呼入据接
                case 8:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_IN);
                    break;
                default:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_IN);
                    break;
            }
            if (count_second > 0) {
                mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_VIDEO_CALL_TIME, count_second);
            }
        }
    }

    private void DelVideoCallLog() {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newDelete(XUN_VIDEO_CONTENT_URI)
                    .build());
            this.getContentResolver().applyBatch(AUTHORITY, ops);
        } catch (RemoteException reception) {

        } catch (OperationApplicationException oaeception) {

        }

    }


    final private ContentObserver mCallLogObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            uploadStatistics();
            final String callLog = GetLatestCallLog();
            if (isDebug) System.out.println(Tag_CONTACT + ", callLog happened json is :" + callLog);
            if (!callLog.equals("")) {
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UpLoadCallLog(callLog);
                    }
                }, 2000);
            }
        }
    };


	/**
	jifen
	*/
	final private ContentObserver  sCoresystemcontentResolver = new ContentObserver(new Handler()) {
	 ArrayList<IntergralBean> igbeanList= new ArrayList<IntergralBean>();	
            @Override
            public void onChange(boolean selfChange) {
		getTotal =Settings.System.getInt(getContentResolver(), "save_Total_integral", 0);
                Log.i("yuanshuai", "the sms table has onChange"+"getTotal="+getTotal);
		getScoreRule();
                Uri uri_user = Uri.parse("content://" + Constants.AUTOHORITY + "/intergral");
                // 通过ContentResolver 向ContentProvider中查询数据
                Cursor cursor = getContentResolver().query(uri_user, new String[]{"_id", "moduleid", "type", "getgold", "timestamp", "flag"}, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String string = cursor.getString(0);//自增字段
                        int moduleid = cursor.getInt(1);
                        //int actions = cursor.getInt(2);
                        int type = cursor.getInt(2);
                        int getgold = cursor.getInt(3);
                        String finishdata = cursor.getString(4);
                        int flag = cursor.getInt(5);
			Log.i("yuanshuai", "moduleid:"+moduleid + "type:"+type +"getgold:"+getgold +"finishdata:"+finishdata+"flag:"+flag);
                        igbean = new IntergralBean(moduleid, type, getgold, finishdata, flag);
                        igbeanList.add(igbean);
                    }
                    cursor.close();
                }
		if(igbeanList.size()>0){
		Log.i("yuanshuai", "igbeanList.size()>0");
                    initgetgold(igbeanList);
		}else{
		Log.i("yuanshuai", "igbeanList.size()=0");	
		}
            }
        }; 
	//最终的参数封装
     public static Map<Integer,Integer> getScoreRule() {
	resultMap = new HashMap<Integer, Integer>();
	//resultMap.put(Constants.OPENBOX,5);
        resultMap.put(Constants.SUNSTEP, 5);
        resultMap.put(Constants.RUNKILOMETRE, 5);
        resultMap.put(Constants.BUSCARD, 10);
        resultMap.put(Constants.MAKEFRIENDS, 10);
        resultMap.put(Constants.PHOTOLEARNREAD, 5);
        resultMap.put(Constants.PHOTOLEARNPICTURE, 5);
        resultMap.put(Constants.USELITTLELOVE, 5);
        resultMap.put(Constants.RELEASEWECHAT, 5);
        resultMap.put(Constants.LIKESWECHAT, 5);
        resultMap.put(Constants.COMMENTWECHAT, 5);
        resultMap.put(Constants.ENGLISHSTUDY, 5);
        resultMap.put(Constants.ENGLISHPASS, 5);
        resultMap.put(Constants.OTAUPDATE, 5); 
        return resultMap;
    }

    private void initgetgold(List<IntergralBean> igbeanList) {
	int exp=0; 
	Log.i("yuanshuai", "initgetgold");
        for (int i = 0; i < igbeanList.size(); i++) {
                intergralBean = igbeanList.get(i);
		int moduledid = intergralBean.getModuleid();
		Log.i("yuanshuai", "moduledid:"+moduledid);
            if (intergralBean.getModuleid() != 0 && intergralBean.getModuleid() != 14) {
                updateGetgold(intergralBean.getModuleid());
		exp = resultMap.get(intergralBean.getModuleid());
		 Log.i("yuanshuai", "!=1and14 exp="+exp);
            }else{
		exp=intergralBean.getGetgold();
            } 
	    int getgold = intergralBean.getGetgold();
	    Map<Integer, String> integerStringMap = getruleNametoId();
	    //index popup
	     String result = Settings.System.getString(getContentResolver(), "SilenceList_result");
             boolean SilenceList_result = (result == null ? false : Boolean.parseBoolean(result));
	    Log.i("yuanshuai", "SilenceList_result:"+SilenceList_result);
		if(!SilenceList_result){
		setPopDialog(integerStringMap, intergralBean, getgold);
		}
	    
        }
	getTotal+=exp;
	Log.i("yuanshuai", "totalexExp="+getTotal);
        setTotalExp(getTotal);
     }

     private void updateGetgold(int type) {
        Uri uri_user = Uri.parse("content://" + Constants.AUTOHORITY + "/intergral");
        ContentValues values = new ContentValues();
	Log.i("yuanshuai", "updateGetgold:"+resultMap.get(type) + "Moduleid: " + intergralBean.getModuleid());
        values.put("getgold", resultMap.get(type));
        getContentResolver().update(uri_user, values, "moduleid = ?", new String[]{String.valueOf(intergralBean.getModuleid())});
    }

    public void setTotalExp(int exp) {
        //SPUtil.put(this, "oneshaName", "save_Total_integral", totalexExp);
       Settings.System.putInt(LauncherService.this.getContentResolver(), "save_Total_integral", exp);
	Log.i("yuanshuai", "setTotalEx()");
    }



    private void setPopDialog(Map<Integer, String> integerStringMap, IntergralBean intergralBean, int getgold) {
       if(intergralBean.getModuleid()==0){
	    return;
           // judgePop(integerStringMap, intergralBean, getgold, Constants.isSUNSTEP_China);
        }
        if(intergralBean.getModuleid()==1){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isSUNSTEP_China);
        }
        if(intergralBean.getModuleid()==2){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isRUNKILOMETRE_China);
        }
        if(intergralBean.getModuleid()==3){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isBUSCARD);
        }
        if(intergralBean.getModuleid()==4){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isMAKEFRIENDS);
        }
        if(intergralBean.getModuleid()==5){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isPHOTOLEARNREAD);
        }
        if(intergralBean.getModuleid()==6){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isPHOTOLEARNPICTURE);
        }
        if(intergralBean.getModuleid()==7){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isUSELITTLELOVE);
        }
        if(intergralBean.getModuleid()==8){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isRELEASEWECHAT);
        }
        if(intergralBean.getModuleid()==9){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isLIKESWECHAT);
        }
        if(intergralBean.getModuleid()==10){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isCOMMENTWECHAT);
        }
        if(intergralBean.getModuleid()==11){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isENGLISHSTUDY);
        }
        if(intergralBean.getModuleid()==12){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isENGLISHPASS);
        }
        if(intergralBean.getModuleid()==13){
            judgePop(integerStringMap, intergralBean, getgold, Constants.isOTAUPDATE);
        }

    }

 private void judgePop(Map<Integer, String> integerStringMap, IntergralBean intergralBean, int getgold, String isSUNSTEP_china) {
	Log.i("yuanshuai", "isSUNSTEP_china=="+Settings.System.getInt(getContentResolver(), isSUNSTEP_china, 0));
        if (Settings.System.getInt(getContentResolver(), isSUNSTEP_china, 0) == 0) {
            //SPUtil.put(this, Canstance.SharedPreferencesName, isSUNSTEP_china, 1);
		Settings.System.putInt(LauncherService.this.getContentResolver(), isSUNSTEP_china, 1);
            final TaskFinshhintDialog taskFinshhintDialog = new TaskFinshhintDialog(LauncherService.this, integerStringMap.get(intergralBean.getModuleid()), getgold + "");
            if(taskFinshhintDialog!=null){
		Log.i("yuanshuai", "taskFinshhintDialog!=null");
		taskFinshhintDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
                taskFinshhintDialog.show();
            }
            final Timer t = new Timer();
            new Timer().schedule(new TimerTask() {
                public void run() {
                  taskFinshhintDialog.dismiss();
                    t.cancel();
                }
            }, 5000);

        }
    }




 //最终的参数封装
    public  Map<Integer, String> getruleNametoId() {
        rulenameMap = new HashMap<Integer,String>();
        rulenameMap.put(0,Constants.OPENBOX_China);
        rulenameMap.put(1,Constants.SUNSTEP_China);
        rulenameMap.put(2,Constants.RUNKILOMETRE_China);
        rulenameMap.put(3,Constants.BUSCARD_China);
        rulenameMap.put(4,Constants.MAKEFRIENDS_China);
        rulenameMap.put(5,Constants.PHOTOLEARNREAD_China);
        rulenameMap.put(6,Constants.PHOTOLEARNPICTURE_China);
        rulenameMap.put(7,Constants.USELITTLELOVE_China);
        rulenameMap.put(8,Constants.RELEASEWECHAT_China);
        rulenameMap.put(9,Constants.LIKESWECHAT_China);
        rulenameMap.put(10,Constants.COMMENTWECHAT_China);
        rulenameMap.put(11,Constants.ENGLISHSTUDY_China);
        rulenameMap.put(12,Constants.ENGLISHPASS_China);
        rulenameMap.put(13,Constants.OTAUPDATE_China);
        rulenameMap.put(14,Constants.LASTBOX_China);
        return rulenameMap;
    }



    private void uploadStatistics() {
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.NUMBER
                        , CallLog.Calls.DATE
                        , CallLog.Calls.DURATION
                        , CallLog.Calls.TYPE}
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor == null) return;
        if (cursor.moveToFirst()) {
            if (mxiaoxunstatisticsManager == null)
                mxiaoxunstatisticsManager = (XiaoXunStatisticsManager) getSystemService("xun.statistics.service");
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            if (mLastUploadTime.equals(getUTCTimeStr(dateLong))) return;
            mLastUploadTime = getUTCTimeStr(dateLong);
            switch (type) {
                case 1:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_IN);
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_IN_SUCCESS);
                    break;
                case 2:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_OUT);
                    if (duration > 0) {
                        mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_OUT_SUCCESS);
                    } else {
                        mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_OUT_FAILED);
                    }
                    break;
                case 3:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_IN);
                    break;
                case 5:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_IN);
                    break;
                default:
                    mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_IN);
                    break;
            }
            if (duration > 0) {
                mxiaoxunstatisticsManager.stats(XiaoXunStatisticsManager.STATS_CALL_TIME, duration);
            }
        }
        cursor.close();
    }

    private String GetLatestCallLog() {
        JSONArray mjson_array = new JSONArray();
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.NUMBER
                        , CallLog.Calls.DATE
                        , CallLog.Calls.DURATION
                        , CallLog.Calls.TYPE}
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor == null) return "";
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            mjson_array.add(InsertCallLogData(number, dateLong, duration, type));

        }
        cursor.close();
        if (mjson_array.size() <= 0) return "";

        return mjson_array.toJSONString();
    }


    private void DelLatestCallLog() {
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"_id"}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                getContentResolver().delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id + ""});
            }
            cursor.close();
        }
    }

    private JSONObject InsertCallLogData(String number, long date, int duration, int type) {
        JSONObject Json = new JSONObject();
        switch (type) {
            case 1:
                type = 3;//call in and answer
                break;
            case 2:
                type = 1;//call out
                break;
            case 3:
                type = 2;//call in and not answer
                break;
            case 5:
                type = 4;//call in and reject
                break;
            default:
                type = 2;
                break;
        }
        Json.put("type", type);
        Json.put("duration", duration);
        Json.put("local_num", "");
        Json.put("name", "");
        Json.put("number", number);
        Json.put("time", getUTCTimeStr(date));
        return Json;
    }


    private String getUTCTimeStr(long dial_date) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
        TimeZone pst = TimeZone.getTimeZone("GMT+8");
        Date curDate = new Date(dial_date);
        dateFormatter.setTimeZone(pst);
        String str = dateFormatter.format(curDate);
        char[] result = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char getParameters : result) {
            sb.append(String.valueOf(9 - Integer.valueOf(String.valueOf(getParameters))));
        }

        return sb.toString();
    }


    private void UpLoadCallLog(String jasonArray) {
        if (mxiaoXunNetworkManager == null)
            mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        mxiaoXunNetworkManager.paddingUploadCallLog(mxiaoXunNetworkManager.getWatchEid(), mxiaoXunNetworkManager.getWatchGid(), jasonArray, new mCallLog() {
            @Override
            public void onSuccess(ResponseData responseData) {
                if (isDebug) System.out.println(Tag_CONTACT + " , upload Success .....");
                DelLatestCallLog();
            }

            @Override
            public void onError(int i, String s) {
                if (isDebug) System.out.println(Tag_CONTACT + " , upload error .....");
            }
        });

    }


    private void RegisterObserve() {
	Log.i("yuanshuai", "RegisterObserve");
        this.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("dialer_subaction"), true, mHeadsUpObserver);
        this.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("receive_call_subaction"), true, mVideoCallObserver);
        this.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("watch_subaction"), true, mDialObserver);
        this.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("SilenceList"), true, mSilenceObserver);
        this.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("prevent_addication"), true, mPreventAdditionObserver);
        this.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("receive_app_install"), true, mAppStoreObserver);
        this.getContentResolver().registerContentObserver(Settings.System.getUriFor(Constants.FUNCTIONLIST), true, functionListObserver);//Add by lihaizhou
        this.getContentResolver().registerContentObserver(Settings.System.getUriFor(Constants.NOTIFY_LAUNCHER_CHANGE), true, goBackToHomeObserver);//Add by lihaizhou
	this.getContentResolver().registerContentObserver(Uri.parse("content://" + Constants.AUTOHORITY),true, sCoresystemcontentResolver);
	
    }

    final private ContentObserver mPreventAdditionObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            initPreventData();
        }
    };

    private void initPreventData() {
        String value = android.provider.Settings.System.getString(getContentResolver(), "prevent_addication");
        boolean noValue = false;
        if (value == null || value.equals("")) {
            noValue = true;
        }
        PreventObj mprevent = PreventObj.objectFromData(value);
        if (!noValue) {
            if ((mprevent.totalDur < mprevent.allowDur) || mprevent.totalDur >= 7200 || mprevent.allowDur >= 7200 || mprevent.preventDur >= 7200) {
                noValue = true;
                android.provider.Settings.System.putString(getContentResolver(), "prevent_addication", "");
            }
        }

        android.provider.Settings.System.putInt(getContentResolver(), "prevent_on_off", noValue ? 1 : mprevent.onOff);
        android.provider.Settings.System.putInt(getContentResolver(), "prevent_totalDur", noValue ? 1800 : mprevent.totalDur);
        android.provider.Settings.System.putInt(getContentResolver(), "prevent_allowDur", noValue ? 900 : mprevent.allowDur);
        android.provider.Settings.System.putInt(getContentResolver(), "prevent_preventDur", noValue ? 900 : mprevent.preventDur);
        android.provider.Settings.System.putInt(getContentResolver(), "vcall_limit", noValue ? 600 : (mprevent.vcallLimit <= 0) ? 600 : mprevent.vcallLimit);
        String mStr = "";
        if (!noValue) {
            for (int i = 0; i < mprevent.appList.size(); i++) {
                mStr = mStr + mprevent.appList.get(i) + "#";
            }
        }
        android.provider.Settings.System.putString(getContentResolver(),
                "prevent_appList", noValue ? "com.xxun.xunimgrec#com.xxun.watch.xunbrain.x2#com.xxun.duer.dcs#com.xxun.watch.xunbrain.c3#com.xxun.xunwordsrec" : mStr);
    }


    final private ContentObserver mSilenceObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            Intent silence = new Intent("com.xunlauncher.silence");
            sendBroadcast(silence);
        }
    };

    /**
     * @author lihaizhou
     * @createtime 2017.11.29
     * @describe 监测xiaoxun APP FunctionList的变化
     */
    private ContentObserver functionListObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "zhj APP's funcationlist change! notify Launcher to update it!");
            notifyFunctionlistChangeToAll();
        }
    };

    /**
     * @author lihaizhou
     * @createtime 2017.12.06
     * @describe 监测PowerKey变化
     */
    private ContentObserver goBackToHomeObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "goback to homescreen and clear all background apps");
            boolean isUsbConfigured = SystemProperties.getBoolean("persist.sys.isUsbConfigured", false);
            try {
                isLocalprop = Settings.System.getInt(getContentResolver(), "is_localprop_exist");
            } catch (SettingNotFoundException e) {
                e.printStackTrace();
            }
            String isMidtest = Settings.System.getString(getContentResolver(), "isMidtest");
			String RuninTest = Settings.System.getString(getContentResolver(), "rt_runing");
            String buildType = SystemProperties.get("ro.build.type");
            if (!(isUsbConfigured && "user".equals(buildType) && isLocalprop == 0 && !"true".equals(isMidtest) &&  !"true".equals(RuninTest))) {
                Intent homeIntent = new Intent(LauncherService.this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FreeMemoryUtils.getFreeMemoryUtilsInstance().removebackgroundTaks();
                    }
                }, 5000);
            }

        }
    };


    private void sendEndStoryBroad() {
        Intent sendStory = new Intent();
        sendStory.setAction("com.xiaoxun.xxun.story.finish");
        sendBroadcast(sendStory);
    }

    Handler mhandler = new Handler();
    WatchMode mWatchdatas = null;
    long Watchmode_split_time = 0;
    final private ContentObserver mDialObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            //*/ xiaoxun.zhangweinan, 20180320. when high temper reject watch monitor
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 10:00:23 +0800
            if ((SystemProperties.getInt("persist.sys.xxun.aptemper", 2) == Constants.XUN_TEMPER_HEAT)
                    || (SystemProperties.getInt("persist.sys.xxun.aptemper", 2) == Constants.XUN_TEMPER_OVERHEAT)
 */
            if ((SystemProperties.getInt("persist.sys.xxun.aptemper", -1) >= Constants.XUN_TEMPER_HEAT)
                    || (SystemProperties.getInt("persist.sys.xxun.aptemper", -1) >= Constants.XUN_TEMPER_OVERHEAT)
// End of longcheer:lishuangwei
            ) {
                return;
            }
            String isIncall = android.provider.Settings.System.getString(getContentResolver(), "xun_video");
            if (isIncall != null) {
                if (isIncall.equals("true")) {
                    return;
                }
            }
            //*/
            String value = android.provider.Settings.System.getString(getContentResolver(), "watch_subaction");
            System.out.println("zhj" + " watch_subaction >>>  Mode = " + value);
            if (value != null) {
                long splitTime = System.currentTimeMillis() - Watchmode_split_time;
                if (!"WatchMode".equals(value) && !"init".equals(value) && !"vibrate".equals(value) && !isTopActivity("com.xxun.watch.xunbrain.x2") && !isTopActivity("com.xxun.watch.xunbrain.c3")
                        && (splitTime > 60 * 1000)) {
                    Watchmode_split_time = System.currentTimeMillis();
                    Gson mgson = new Gson();
                    if (telecomManager == null)
                        telecomManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (telecomManager.getCallState() == android.telephony.TelephonyManager.CALL_STATE_IDLE) {
                        sendEndStoryBroad();
                        mWatchdatas = WatchMode.objectFromData(value);
                        WatchMode mSendData = new WatchMode();
                        mSendData.CID = 30011;
                        mSendData.SN = mWatchdatas.SN;
                        mSendData.Version = CloudBridgeUtil.PROTOCOL_VERSION;
                        String[] mTEID = new String[]{mWatchdatas.SEID};
                        mSendData.TEID = mTEID;
                        PLBean msendPlbean = new PLBean();
                        msendPlbean.EID = mWatchdatas.PL.EID;
                        msendPlbean.phoneNum = mWatchdatas.PL.phoneNum;
                        msendPlbean.timestamp = mWatchdatas.PL.timestamp;
                        msendPlbean.type = mWatchdatas.PL.type;
                        msendPlbean.subAction = mWatchdatas.PL.subAction;
                        if (mxiaoXunNetworkManager == null)
                            mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
                        if (isContainNumbers(mWatchdatas.PL.phoneNum)) {
                            msendPlbean.RC = 1;
                            mSendData.SID = mxiaoXunNetworkManager.getSID();
                            mSendData.PL = msendPlbean;
                            mxiaoXunNetworkManager.sendJsonMessage(mgson.toJson(mSendData, WatchMode.class), new mWatchMode() {
                                @Override
                                public void onSuccess(ResponseData responseData) {
                                    //if (isDebug)
                                    //System.out.println(Tag_CONTACT + " ,has number onSuccess Mode ....."+mgson.toJson(mSendData, WatchMode.class));
                                }

                                @Override
                                public void onError(int i, String s) {
                                    if (isDebug)
                                        System.out.println(Tag_CONTACT + " ,has number  onError Mode .....");
                                }
                            });

                            // adding wakelock to prevent mhandler delay more than absolute setting time long. mayanjun 20190801;
                            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "set_preferred_secs");
                            wakeLock.acquire(3000);

                            android.provider.Settings.System.putLong(getContentResolver(), "start_backcall", System.currentTimeMillis());

                            mhandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //add by mayanjun for backgroud call tips; 20190903;
                                    if("false".equals(Settings.System.getString(getContentResolver(), "on_xunlauncher_homescreen"))
                                            && com.xiaoxun.sdk.utils.Constant.PROJECT_NAME.equals("SW760"))
                                    {
                                        System.out.println(Tag_CONTACT + " backcall ,return to idle;");
                                        if ("true".equals(Settings.System.getString(getContentResolver(), "notify_launcher_change"))) {
                                            Settings.System.putString(getContentResolver(), "notify_launcher_change", "false");
                                        } else {
                                            Settings.System.putString(getContentResolver(), "notify_launcher_change", "true");
                                        }
                                    }

                                    android.provider.Settings.System.putString(getContentResolver(), "watch_subaction", "WatchMode");
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    Uri data = Uri.parse("tel:" + mWatchdatas.PL.phoneNum);
                                    intent.setData(data);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 2000);

                        } else {
                            if (isDebug)
                                System.out.println(Tag_CONTACT + ",  not Contains number ...");
                            msendPlbean.RC = 0;
                            mSendData.PL = msendPlbean;
                            mxiaoXunNetworkManager.sendJsonMessage(mgson.toJson(mSendData, WatchMode.class), new mWatchMode() {
                                @Override
                                public void onSuccess(ResponseData responseData) {
                                }

                                @Override
                                public void onError(int i, String s) {
                                }
                            });
                        }

                    }
                }
            }
        }
    };


    boolean isSyncContact = false;
    final private ContentObserver mHeadsUpObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isCanLoadDataFromServer()) return;
            String value = android.provider.Settings.System.getString(getContentResolver(), "dialer_subaction");
            System.out.println(Tag_CONTACT + "server notify ,opt data: here ...........");
            SyncArrayBean mcontact = SyncArrayBean.objectFromData(value);
            GetMessageById(mcontact);
        }
    };

    //[Appstore ---------------------------------------------------------start-----------]
    private boolean isHasData(String app_id) {
        if (app_id == null) return false;
        if (app_id.equals("")) return false;
        boolean isContains = false;
        Cursor mcursor = LauncherService.this.getContentResolver().query(XunConstant.CONTENT_URI, new String[]{
                XunConstant.APP_ID}, XunConstant.APP_ID + "=?", new String[]{app_id}, null);
        if (mcursor != null) {
            if ((mcursor.getCount()) > 0) {
                isContains = true;
            }
            mcursor.close();
        }
        return isContains;
    }


    private void AppstoreInsert(DealInstallSubAciton deal) {
        if (isHasData(deal.PL.appId)) {
            AppstoreUpdate(deal);
            return;
        }
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newInsert(XunConstant.CONTENT_URI)
                    .withValue(XunConstant.NAME, deal.PL.name)
                    .withValue(XunConstant.TYPE, deal.PL.type)
                    .withValue(XunConstant.APP_ID, deal.PL.appId)
                    .withValue(XunConstant.EID, deal.PL.EID)
                    .withValue(XunConstant.GID, deal.PL.GID)
                    .withValue(XunConstant.OPTYPE, deal.PL.optype)
                    .withValue(XunConstant.ICON, deal.PL.icon)
                    .withValue(XunConstant.STATUS, deal.PL.status)
                    .withValue(XunConstant.HIDDEN, deal.PL.hidden)
                    .withValue(XunConstant.VERSION, deal.PL.version)
                    .withValue(XunConstant.VERSION_CODE, deal.PL.versionCode)
                    .withValue(XunConstant.DOWNLOAD_URL, deal.PL.downloadUrl)
                    .withValue(XunConstant.WIFI, deal.PL.wifi)
                    .withValue(XunConstant.SIZE, deal.PL.size)
                    .withValue(XunConstant.MD5, deal.PL.md5)
                    .withValue(XunConstant.UPDATES, deal.PL.updateTS)
                    .withYieldAllowed(true)
                    .build());
            LauncherService.this.getContentResolver().applyBatch(XunConstant.AUTHORITY, ops);
        } catch (RemoteException reception) {

        } catch (OperationApplicationException oaeception) {

        }
    }

    private void AppstoreUpdate(DealInstallSubAciton deal) {
        if (!isHasData(deal.PL.appId)) {
            AppstoreInsert(deal);
            return;
        }
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newUpdate(XunConstant.CONTENT_URI)
                    .withSelection(XunConstant.APP_ID + " =?", new String[]{deal.PL.appId + ""})
                    .withValue(XunConstant.NAME, deal.PL.name)
                    .withValue(XunConstant.TYPE, deal.PL.type)
                    .withValue(XunConstant.APP_ID, deal.PL.appId)
                    .withValue(XunConstant.EID, deal.PL.EID)
                    .withValue(XunConstant.GID, deal.PL.GID)
                    .withValue(XunConstant.OPTYPE, deal.PL.optype)
                    .withValue(XunConstant.ICON, deal.PL.icon)
                    .withValue(XunConstant.STATUS, deal.PL.status)
                    .withValue(XunConstant.HIDDEN, deal.PL.hidden)
                    .withValue(XunConstant.VERSION, deal.PL.version)
                    .withValue(XunConstant.VERSION_CODE, deal.PL.versionCode)
                    .withValue(XunConstant.DOWNLOAD_URL, deal.PL.downloadUrl)
                    .withValue(XunConstant.WIFI, deal.PL.wifi)
                    .withValue(XunConstant.SIZE, deal.PL.size)
                    .withValue(XunConstant.MD5, deal.PL.md5)
                    .withValue(XunConstant.UPDATES, deal.PL.updateTS)
                    .withYieldAllowed(true)
                    .build());
            LauncherService.this.getContentResolver().applyBatch(XunConstant.AUTHORITY, ops);
        } catch (RemoteException reception) {

        } catch (OperationApplicationException oaeception) {

        }
    }

    private void toTellBegainTask() {
        Intent sendcommand = new Intent();
        sendcommand.setAction("com.xiaoxun.appstore.task.begain");
        sendcommand.setPackage("com.xxun.xunlauncher");
        sendBroadcast(sendcommand);
    }

    private void toTellLauncherHidden() {
        Intent sendcommand = new Intent();
        sendcommand.setAction("com.xiaoxun.appstore.task.hiddenapp");
        sendcommand.setPackage("com.xxun.xunlauncher");
        sendBroadcast(sendcommand);
    }


    final private ContentObserver mAppStoreObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isCanLoadDataFromServer()) return;
            String value = android.provider.Settings.System.getString(getContentResolver(), "receive_app_install");
            Log.d("zhj", "mAppStoreObserver value =" + value);
            DealInstallSubAciton deal = DealInstallSubAciton.objectFromData(value);
            System.out.println("zhj" + "======  mAppStoreObserver   =====  PL.type =" + deal.PL.type + ",    PL.hidden = " + deal.PL.hidden);

            if (deal.PL.optype == 0) {//add
                AppstoreInsert(deal);
            } else if (deal.PL.optype == 1) {//update
                AppstoreUpdate(deal);
                //处理卸载apk任务
                if (deal.PL.status == 3) {
                    Intent mintent = new Intent();
                    mintent.setComponent(new ComponentName("ado.install.xiaoxun.com.xiaoxuninstallapk", "com.xiaoxun.service.DealSubActionService"));
                    mintent.setAction("com.xiaoxun.uninstall.app");
                    mintent.putExtra("appid", deal.PL.appId);
                    LauncherService.this.startService(mintent);
                } else if (deal.PL.type == 2) {
                    toTellLauncherHidden();
                } else if (deal.PL.hidden == 1) {
                    //隐藏或者显示app应用
                    if (deal.PL.appId != null)
                        TempControlUtils.getTempControlUtilsInstance().forceStopPackage(deal.PL.appId, getApplicationContext());
                }
            }
            toTellBegainTask();
        }
    };


    //[Appstore----------------------------------------------------------end---------------]
    private boolean isNeedChangeTo4GMode() {
        if (mxiaoXunNetworkManager == null) {
            mxiaoXunNetworkManager =
                    (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        }
        boolean result = mxiaoXunNetworkManager.requireLTEMode("com.xiaoxun.dialer");
        mxiaoXunNetworkManager.releaseLTEMode("com.xiaoxun.dialer");
        return result;
    }


    private boolean isWifiContected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }


    private SyncArrayBean getParamsByComingUI(String seid) {
        SyncArrayBean arrayBean = new SyncArrayBean();
        Cursor mcursor = null;
        if (seid != null) {
            mcursor = this.getContentResolver().query(Constant.CONTENT_URI, new String[]{
                    Constant.ID
                    , Constant.NAME
                    , Constant.NUMBER
                    , Constant.SUB_NUMBER
                    , Constant.OPTYPE
                    , Constant.CONTACTSTYPE
                    , Constant.OLDUPDATETS
                    , Constant.ATTRI
                    , Constant.CONTACT_WEIGHT
                    , Constant.USER_GID
                    , Constant.UPDATETS
                    , Constant.USER_EID
                    , Constant.AVATAR
                    , Constant.BLANK_ONE
                    , Constant.BLANK_TWO
                    , Constant.BLANK_THREE
                    , Constant.BLANK_FOUR
            }, Constant.USER_EID + "=?", new String[]{seid}, null);
        }

        if (mcursor != null) {
            if (mcursor.moveToNext()) {
                arrayBean.name = mcursor.getString(mcursor.getColumnIndex(Constant.NAME));
                arrayBean.number = mcursor.getString(mcursor.getColumnIndex(Constant.NUMBER));
                arrayBean.avatar = mcursor.getString(mcursor.getColumnIndex(Constant.AVATAR));
                arrayBean.attri = mcursor.getInt(mcursor.getColumnIndex(Constant.ATTRI));
                arrayBean.contactWeight = mcursor.getInt(mcursor.getColumnIndex(Constant.CONTACT_WEIGHT));
                arrayBean.contactsType = mcursor.getInt(mcursor.getColumnIndex(Constant.CONTACTSTYPE));
                arrayBean.avatar = mcursor.getString(mcursor.getColumnIndex(Constant.AVATAR));
                arrayBean.id = mcursor.getString(mcursor.getColumnIndex(Constant.ID));
                arrayBean.userEid = mcursor.getString(mcursor.getColumnIndex(Constant.USER_EID));
            }
        }
        mcursor.close();
        return arrayBean;
    }

    private int LastSn = -1;
    private TelephonyManager tm;

    final private ContentObserver mVideoCallObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            String isIncall = android.provider.Settings.System.getString(getContentResolver(), "xun_video");
            String isInNormalCall = android.provider.Settings.System.getString(getContentResolver(), "isIncall");
            String value = android.provider.Settings.System.getString(getContentResolver(), "receive_call_subaction");
            String result = android.provider.Settings.System.getString(getContentResolver(), "SilenceList_result");
            boolean SilenceList_result = (result == null ? false : Boolean.parseBoolean(result));
            if (value.equals("")) return;
            if (value != null) {
                if (value.contains("RC")) {
                    Intent rcIntent = new Intent("com.xiaoxun.receiver.rc");
                    rcIntent.setPackage("com.android.dialer");
                    LauncherApplication.getInstance().sendBroadcast(rcIntent);
                }
            }
            final ReceivETECall mdata = ReceivETECall.objectFromData(value);
            if (LastSn == mdata.PL.SN) {
                return;
            }
            LastSn = mdata.PL.SN;
            //To end Dialer
            if ((extra_level != -1) && (extra_level <= 20)) {
                sendE2EAtCall(new String[]{mdata.PL.SEID}, mdata.SN, 7);
                return;
            }
            if (isCharging) {
                sendE2EAtCall(new String[]{mdata.PL.SEID}, mdata.SN, 8);
                return;
            }

            if (!isCanLoadDataFromServer()) {
                sendE2EAtCall(new String[]{mdata.PL.SEID}, mdata.SN, 6);
                return;
            }
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 10:01:11 +0800
            if ((SystemProperties.getInt("persist.sys.xxun.aptemper", 2) == Constants.XUN_TEMPER_HEAT)
                    || (SystemProperties.getInt("persist.sys.xxun.aptemper", 2) == Constants.XUN_TEMPER_OVERHEAT)
 */
            if ((SystemProperties.getInt("persist.sys.xxun.aptemper", -1) >= Constants.XUN_TEMPER_HEAT)
                    || (SystemProperties.getInt("persist.sys.xxun.aptemper", -1) >= Constants.XUN_TEMPER_OVERHEAT)
// End of longcheer:lishuangwei
            ) {
                sendE2EAtCall(new String[]{mdata.PL.SEID}, mdata.SN, 5);
                return;
            }
            if (SilenceList_result) {
                sendE2EAtCall(new String[]{mdata.PL.SEID}, mdata.SN, 4);
                return;
            }
            if(tm == null){
                tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            }

            if(!(tm.getCallState() == TelephonyManager.CALL_STATE_IDLE)){
                sendE2EAtCall(new String[]{mdata.PL.SEID},mdata.SN,1);
                return;
            }
            //智能省电开关开启，并且当前为2G,并且没有连接wifi,我们先break 视频通话，并且切换到4G;
            if (isNeedChangeTo4GMode() && SystemProperties.getBoolean("persist.sys.lteswitchon", false) && !isWifiContected()) {
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendE2EAtCall(new String[]{mdata.PL.SEID}, mdata.SN, 3);
                    }
                }, 5000);
                return;
            }
            if (isIncall != null) {
                if (isIncall.equals("true")) {
                    sendE2EAtCall(new String[]{mdata.PL.SEID}, mdata.SN, 2);
                    return;
                }
            }
            SyncArrayBean arrayBean = getParamsByComingUI(mdata.PL.SEID);
            if (arrayBean != null) {
                //内存太低，来电就去清理内存;
                final Intent intent_out = new Intent(Intent.ACTION_MAIN, null);
                intent_out.putExtra("number", arrayBean.number);
                intent_out.putExtra("attri", arrayBean.attri);
                intent_out.putExtra("name", arrayBean.name);
                intent_out.putExtra("url", arrayBean.avatar);
                intent_out.putExtra("seid", arrayBean.userEid);
                intent_out.putExtra("appId", mdata.PL.appId);
                intent_out.putExtra("uidOther", mdata.PL.uidOther);
                intent_out.putExtra("tokenOther", mdata.PL.tokenOther);
                intent_out.putExtra("channelName", mdata.PL.channelName);
                intent_out.putExtra("sn", mdata.PL.SN);
                if (mdata.PL.tutkType != null) {
                    if (mdata.PL.tutkType.equals("2")) {
                        intent_out.setComponent(new ComponentName("com.xiaoxun.dialer", "com.xiaoxun.dialer.video.VideoChatViewActivity_incoming_other"));
                    } else {
                        intent_out.setComponent(new ComponentName("com.xiaoxun.dialer", "com.xiaoxun.dialer.video.VideoChatViewActivity_incoming"));
                    }

                } else {
                    intent_out.setComponent(new ComponentName("com.xiaoxun.dialer", "com.xiaoxun.dialer.video.VideoChatViewActivity_incoming"));
                }
                intent_out.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_out);
            }

        }
    };

    /**
     * author:guohongcheng
     * 有联系人变更下发，广播到其他应用
     */
    private void sendNewBroad() {
        Log.d(TAG, "sendNewBroad >> ..");
        Intent newfriends = new Intent();
        newfriends.setAction("com.xunaddnew.new");
        sendBroadcast(newfriends);
    }

    private void sendGroupBroad() {
        Log.d(TAG, "sendGroupBroad >> ..");
        Intent sendGroup = new Intent();
        sendGroup.setAction("com.xungroup.new");
        sendBroadcast(sendGroup);
    }

    /*
      add by guohongcheng_20180329
      开机时，设置Camera标志位false
     */
    private void setIsCameraUsing(String isCameraUsing) {
        Log.d(TAG, "setIsCameraUsing: " + isCameraUsing);
        try {
            android.provider.Settings.System.putString(
                    getContentResolver()
                    , "camera_isRecording"
                    , isCameraUsing);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }


    private void RegisterIntentFilter() {
        mFilter = new IntentFilter();
        mFilter.addAction(Constants.ACTION_LOGIN_SUCC);
        mFilter.addAction(Constants.ACTION_SESSION_PING_OK);
        mFilter.addAction(Constants.ACTION_SIM_STATE_CHANGED);
        mFilter.addAction(Constants.ACTION_XIAOXUN_SEND_SIM_CHANGE);
        mFilter.addAction(Constants.ACTION_XIAOXUN_AIRPLAN_OFF);
        //*/ xiaoxun.zhangweinan, 20180111. for read AP temperature
        mFilter.addAction(Intent.ACTION_TIME_TICK);
        mFilter.addAction(Constants.ACTION_BROAST_MEDIA_SONG_STATUS_REQ);
        mFilter.addAction(Constants.XUN_PRODIC_READ_AP_TEMPER);
        mFilter.addAction(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mFilter.addAction(Constants.XIAOXUN_PREVENT_ADDITION);
        mFilter.addAction(Constants.XIAOXUN_CLEAR_RAM_MEMORY);
        //add by jxring for reboot dialog 2018.4.20
        mFilter.addAction("com.xiaoxun.reboot");
        mFilter.addAction("com.xiaoxun.video.endcall");
        mFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        //add by zhanghaijun@longcheer.com for contacts interface 20190611 start
        mFilter.addAction("com.xiaoxun.getcontact");
        //add by zhanghaijun@longcheer.com for contacts interface 20190611 end

        //*/
        this.registerReceiver(mReceiver, mFilter);
    }


    public boolean isContainNumbers(String number) {
        if (number == null) return false;
        if (number.equals("")) return false;
        boolean isContains = false;
        Cursor mcursor = LauncherService.this.getContentResolver().query(Constant.CONTENT_URI, new String[]{
                Constant.ID
                , Constant.NAME
                , Constant.NUMBER
                , Constant.SUB_NUMBER
                , Constant.OPTYPE
                , Constant.CONTACTSTYPE
                , Constant.OLDUPDATETS
                , Constant.ATTRI
                , Constant.CONTACT_WEIGHT
                , Constant.USER_GID
                , Constant.UPDATETS
                , Constant.USER_EID
                , Constant.AVATAR
                , Constant.BLANK_ONE
                , Constant.BLANK_TWO
                , Constant.BLANK_THREE
                , Constant.BLANK_FOUR
        }, Constant.NUMBER + "=?" + " or " + Constant.SUB_NUMBER + "=?", new String[]{number, number}, null, null);
        if (mcursor != null) {
            if ((mcursor.getCount()) > 0) {
                isContains = true;
            }
            mcursor.close();
        }
        return isContains;
    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.ACTION_LOGIN_SUCC.equals(action)) {
                if (isDebug)
                    System.out.println(Tag_CONTACT + " ,getBroacast here loginSuceess......");
                fixedThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "zhj loginSuceess do background work");
                        getContactsFromServer();
                        getDeviceInfo();
                        if (XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT)
                            TellAppStoreUploadSystemApkInfo();
                        //Add by xuzhonghong for upload watch status at 20171215 start
                        UploadStatusUtils.getUploadStatusUtilsInstance().uploadStatus(context);
                        //Add by xuzhonghong for upload watch status at 20171215 end
                        //update by liaoyi 17/12/15
                        UploadStatusUtils.getUploadStatusUtilsInstance().uploadVersion(context);
                        //end
                    }
                });

            } else if (Constants.ACTION_SESSION_PING_OK.equals(action)) {
                if (isDebug)
                    System.out.println(Tag_CONTACT + " ,getBroacast here sessionping ok......");
                fixedThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "zhj session ok, do background work");
                        getContactsFromServer();

                        if (XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT)
                            TellAppStoreUploadSystemApkInfo();
                        //Add by xuzhonghong for upload watch status at 20171215 start
                        UploadStatusUtils.getUploadStatusUtilsInstance().uploadStatus(context);
                        //Add by xuzhonghong for upload watch status at 20171215 end
                        //update by liaoyi 17/12/15
                        UploadStatusUtils.getUploadStatusUtilsInstance().uploadVersion(context);
                        //end
                    }
                });

                //[add by jxring for sim card change we getSimNo 2018.1.2 start]
            } else if (Constants.ACTION_SIM_STATE_CHANGED.equals(action)) {
                if (msp != null) {
                    if (telecomManager != null) {
                        String nativePhoneNumber = telecomManager.getLine1Number();
                        if (nativePhoneNumber != null) {
                            if (!msp.getString("watch_self_number", "").equals("") && !nativePhoneNumber.equals("")) {
                                if (!nativePhoneNumber.equals(msp.getString("watch_self_number", ""))) {
                                    JSONObject Json = new JSONObject();
                                    if (!msp.getString("kid_name", "").equals(""))
                                        Json.put("kid_name", msp.getString("kid_name", ""));
                                    if (!msp.getString("encrypted_qr", "").equals(""))
                                        Json.put("encrypted_qr", msp.getString("encrypted_qr", ""));
                                    SharedPreferences.Editor medit = msp.edit();
                                    Json.put("watch_self_number", nativePhoneNumber);
                                    medit.putString("watch_self_number", nativePhoneNumber);
                                    medit.commit();
                                    android.provider.Settings.Global.putString(getContentResolver(), "kid_info", Json.toJSONString());
                                }

                            }
                        }
                    }
                }
            } else if (Constants.ACTION_XIAOXUN_SEND_SIM_CHANGE.equals(action)) {
                String mSimNo = intent.getStringExtra("simno");
                if (mSimNo != null) {
                    if (!mSimNo.equals("")) {
                        if (msp == null)
                            msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
                        JSONObject Json = new JSONObject();
                        if (!msp.getString("kid_name", "").equals(""))
                            Json.put("kid_name", msp.getString("kid_name", ""));
                        if (!msp.getString("encrypted_qr", "").equals(""))
                            Json.put("encrypted_qr", msp.getString("encrypted_qr", ""));
                        SharedPreferences.Editor medit = msp.edit();
                        Json.put("watch_self_number", mSimNo);
                        medit.putString("watch_self_number", mSimNo);
                        medit.commit();
                        android.provider.Settings.Global.putString(getContentResolver(), "kid_info", Json.toJSONString());
                    }
                }

            } else if (Constants.ACTION_XIAOXUN_AIRPLAN_OFF.equals(action)) {
                Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
                Intent offair = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                offair.putExtra("state", false);
                context.sendBroadcast(offair);

            }
            //add by zhanghaijun@longcheer.com for contacts interface 20190611 start
            else if ("com.xiaoxun.getcontact".equals(action)) {
                myInterface.setContactInfo();
            }
            //add by zhanghaijun@longcheer.com for contacts interface 20190611 end

            else if ("com.xiaoxun.reboot".equals(action)) {
                Intent intent_reboot = new Intent("android.intent.action.REBOOT");
                intent_reboot.putExtra("nowait", 1);
                intent_reboot.putExtra("interval", 1);
                intent_reboot.putExtra("window", 0);
                sendBroadcast(intent_reboot);
            } else if ("com.xiaoxun.video.endcall".equals(action)) {
                int mVideoSn = intent.getIntExtra("sn", -1);
                String seid = intent.getStringExtra("seid");
                sendETEcallToEndMethod(mVideoSn, seid);
            } else if (Constants.XIAOXUN_PREVENT_ADDITION.equals(action)) {
                warnPreventEnterDialog();
                System.currentTimeMillis();
                if (msp == null) msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
                //SharedPreferences.Editor medit = msp.edit();
                //medit.putLong("marktime", System.currentTimeMillis());
                //medit.putBoolean("isCurrentPrevent", true);
                //medit.commit();
                Log.d("zhj", "=== XIAOXUN_PREVENT_ADDITION ===");
                android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), "isCurrentPrevent", 1);// 1 is on, 0 is off
                android.provider.Settings.System.putLong(getApplicationContext().getContentResolver(), "marktime", System.currentTimeMillis());
                TempControlUtils.getTempControlUtilsInstance().forceStopPackage(getApplicationContext());
            } else if (Constants.XIAOXUN_CLEAR_RAM_MEMORY.equals(action)) {
                TempControlUtils.getTempControlUtilsInstance().forceStopPackage(getApplicationContext());
            }
            //[add by jxring for sim card change we getSimNo 2018.1.2 end!] 
            //*/ xiaoxun.zhangweinan, 20180111. for read AP temperature    
            else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                if (SystemProperties.getBoolean("persist.sys.temper.warning", false)) {
                    mHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 2);
                    extra_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 09:59:23 +0800
                    SystemProperties.set("persist.sys.xxun.aptemper", mHealth + "");
 */
// End of longcheer:lishuangwei
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 09:48:29 +0800
                    if (mHealth == Constants.XUN_TEMPER_OVERHEAT && !isTopActivity("com.xxun.xunlauncher") && !isTopActivity("com.xxun.watch.xunsettings")) {
 */
                    mTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                    SystemProperties.set("persist.sys.xxun.aptemper", mTemperature + "");
                    Log.i(TAG, "mHealth = " + mHealth+",mTemperature="+mTemperature);
                    if (mTemperature >= Constants.XUN_TEMPER_OVERHEAT && !isTopActivity("com.xxun.xunlauncher") && !isTopActivity("com.xxun.watch.xunsettings")) {
// End of longcheer:lishuangwei
                        Log.i(TAG, "It's over heat");
                        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        if (mPowerManager.isScreenOn()) {
                            warnHighTemperDialog();
                        } else {
                            Log.i(TAG, "is not screen on");
                            TempControlUtils.getTempControlUtilsInstance().removeAllTasks();
                            Intent stopStoryIntent = new Intent(Constants.ACTION_BROAST_STORY_FINISH);
                            stopStoryIntent.putExtra("SourceType", "highTemperature");
                            sendBroadcast(stopStoryIntent);
                            if (telecomManager == null) {
                                telecomManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            }
                            if (telecomManager.getCallState() == android.telephony.TelephonyManager.CALL_STATE_OFFHOOK) {
                                Log.i(TAG, "mHighTemperTimer_CALL_STATE_OFFHOOK");
                                endCall();
                            }
                        }
                    }
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 09:53:14 +0800
                    if (mHealth == Constants.XUN_TEMPER_HEAT) {
 */
                    if (mTemperature >= Constants.XUN_TEMPER_HEAT) {
// End of longcheer:lishuangwei
                        Intent queryStoryStatusIntent = new Intent(Constants.ACTION_BROAST_MEDIA_SONG_STATUS);
                        sendBroadcast(queryStoryStatusIntent);
                    }
                }
            } else if (Constants.ACTION_BROAST_MEDIA_SONG_STATUS_REQ.equals(action)) {
                if (SystemProperties.getBoolean("persist.sys.temper.warning", false)) {
                    String playStatue = intent.getStringExtra("is_play");
                    Log.i(TAG, "playStatue = " + playStatue);
                    if (playStatue != null) {
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 09:55:17 +0800
                        if (playStatue.equals("1") && mHealth == Constants.XUN_TEMPER_HEAT) {
 */
                        if (playStatue.equals("1") && mTemperature >= Constants.XUN_TEMPER_HEAT) {
// End of longcheer:lishuangwei
                            warnStopStoryDialog();
                        }
                    }
                }
            } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                isCharging = true;

            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                isCharging = false;

            }
            //*/
        }
    };

    //*/ xiaoxun.zhangweinan, 20180411. for restore factoryrest flag in NV
    private void restoreFacRestFlag(byte flag) {
        try {
            FileOutputStream fileOutstream = new FileOutputStream(FACTORYRESET_FLAG_FILE);
            fileOutstream.write(flag);
            fileOutstream.write("\n".getBytes());
            SystemProperties.set("persist.sys.nvram.reset", "");
            SystemProperties.set("persist.sys.pdtinfo.reset", "0");
        } catch (Exception e) {
            Log.i(TAG, "write reset flag error");
        }
    }
    //*/

    //手表做一次上传服务器系统apk信息
    private void TellAppStoreUploadSystemApkInfo() {
        if (msp == null) msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
        boolean hasInsert = msp.getBoolean("hasInsert", false);//代表已经生成系统apk信息数据库，我们可以启动应用商店service上传了
        String upload_value = Settings.System.getString(getContentResolver(), "hasupload"); //true:代表上传过;
        boolean hasUpload = (upload_value == null ? false : (upload_value.equals("true") ? true : false));
        Log.d("zhj", "hasInsert =" + hasInsert + ", hasUpload = " + hasUpload);
        //add zhj
        if (hasInsert && !hasUpload) {
            Intent mintent = new Intent();
            mintent.setComponent(new ComponentName("ado.install.xiaoxun.com.xiaoxuninstallapk", "com.xiaoxun.service.DealSubActionService"));
            mintent.setAction("com.xiaoxun.upload.system.toserver");
            startService(mintent);
        } else {
            hasUpload = false;
            Intent mintent = new Intent();
            mintent.setComponent(new ComponentName("ado.install.xiaoxun.com.xiaoxuninstallapk", "com.xiaoxun.service.DealSubActionService"));
            mintent.setAction("com.xiaoxun.update.local.database");
            startService(mintent);
        }

        if (com.xiaoxun.sdk.utils.Constant.PROJECT_NAME.equals("SW703") || com.xiaoxun.sdk.utils.Constant.PROJECT_NAME.equals("SW706")) {
            updateServerAPPName();
        }
    }

    //通过appId 获取整个PLBeanX
    public PLBeanX getPLBeanX(String app_id, int optype) {
        PLBeanX mPl = null;
        Cursor mcursor = getContentResolver().query(XunConstant.CONTENT_URI, new String[]{
                XunConstant.EID
                , XunConstant.WIFI
                , XunConstant.GID
                , XunConstant.TYPE
                , XunConstant.HIDDEN
                , XunConstant.ICON
                , XunConstant.VERSION_CODE
                , XunConstant.VERSION
                , XunConstant.OPTYPE
                , XunConstant.SIZE
                , XunConstant.DOWNLOAD_URL
                , XunConstant.NAME
                , XunConstant.APP_ID
                , XunConstant.STATUS
                , XunConstant.MD5
                , XunConstant.ATTR
        }, XunConstant.APP_ID + " =?", new String[]{app_id + ""}, null);
        if (mcursor != null) {
            while (mcursor.moveToNext()) {
                mPl = new PLBeanX();
                mPl.EID = mcursor.getString(mcursor.getColumnIndex(XunConstant.EID));
                mPl.wifi = mcursor.getInt(mcursor.getColumnIndex(XunConstant.WIFI));
                mPl.GID = mcursor.getString(mcursor.getColumnIndex(XunConstant.GID));
                mPl.type = mcursor.getInt(mcursor.getColumnIndex(XunConstant.TYPE));
                mPl.hidden = mcursor.getInt(mcursor.getColumnIndex(XunConstant.HIDDEN));
                mPl.icon = mcursor.getString(mcursor.getColumnIndex(XunConstant.ICON));
                mPl.versionCode = mcursor.getInt(mcursor.getColumnIndex(XunConstant.VERSION_CODE));
                mPl.version = mcursor.getString(mcursor.getColumnIndex(XunConstant.VERSION));
                mPl.optype = optype;//mcursor.getString(mcursor.getColumnIndex(XunConstant.OPTYPE));//0:add 1:modify 2:delete
                mPl.size = mcursor.getInt(mcursor.getColumnIndex(XunConstant.SIZE));
                mPl.downloadUrl = mcursor.getString(mcursor.getColumnIndex(XunConstant.DOWNLOAD_URL));
                mPl.name = mcursor.getString(mcursor.getColumnIndex(XunConstant.NAME));
                mPl.appId = mcursor.getString(mcursor.getColumnIndex(XunConstant.APP_ID));
                mPl.status = mcursor.getInt(mcursor.getColumnIndex(XunConstant.STATUS));
                mPl.md5 = mcursor.getString(mcursor.getColumnIndex(XunConstant.MD5));
                mPl.attr = mcursor.getString(mcursor.getColumnIndex(XunConstant.ATTR));
            }
            mcursor.close();
        }
        return mPl;
    }


    //更新数据库，系统应用，更新服务器上应用名字
    private void updateDatabase_ApkName(String app_id, String name) {
        Log.d("zhj", "UpdateDatabase_ApkName =" + name);
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newUpdate(XunConstant.CONTENT_URI)
                    .withSelection(XunConstant.APP_ID + " =?", new String[]{app_id + ""})
                    .withValue(XunConstant.NAME, name)
                    .withYieldAllowed(true)
                    .build());
            getContentResolver().applyBatch(XunConstant.AUTHORITY, ops);
        } catch (RemoteException reception) {
        } catch (OperationApplicationException oaeception) {
        }
    }

    private void updateServerAPPName() {
        String[] packageNameList = getResources().getStringArray(R.array.applists_name);
        String[] packageId = getResources().getStringArray(R.array.applists);
        boolean isNeedNotify = false;
        String saveAppId = android.provider.Settings.System.getString(getContentResolver(), "system_appname");
        System.out.println("zhj Launcher updateServerAPPName  saveAppId =" + saveAppId);
        if (packageId == null) return;
        for (int i = 0; i < packageId.length; i++) {
            String localName = packageNameList[i];
            String localAppId = packageId[i];
            PLBeanX pl = getPLBeanX(localAppId, 1);
            if (pl != null && !localAppId.equals("ado.install.xiaoxun.com.xiaoxuninstallapk")) {
                System.out.println("zhj updateServerAPPName  localName = " + localName + ",   serverName =" + pl.name + ",   appid = " + localAppId);
                if (!localName.equals(pl.name)) {
                    isNeedNotify = true;
                    updateDatabase_ApkName(localAppId, localName);
                    if (saveAppId != null && (saveAppId.contains("#system"))) {
                        android.provider.Settings.System.putString(getContentResolver(), "system_appname", saveAppId + "#system_" + localAppId);
                    } else {
                        android.provider.Settings.System.putString(getContentResolver(), "system_appname", "#system_" + localAppId);
                    }
                }
            }
        }

        if (!isNeedNotify) return;

        Intent mintent = new Intent();
        mintent.setComponent(new ComponentName("ado.install.xiaoxun.com.xiaoxuninstallapk", "com.xiaoxun.service.DealSubActionService"));
        mintent.setAction("com.xiaoxun.update.system_appname");
        startService(mintent);
    }


    /**
     * 老的重复注册广播 登录代码
     *
     * @author liaoyi 19/1/25
     */
    private void oldLoginHandle(Context context) {
        if (isDebug)
            System.out.println(Tag_CONTACT + " ,getBroacast here loginSuceess......");
        Log.d(TAG, "oldLoginHandle  zhj loginSuceess do background work");
        //getContactsFromServer();
        //getDeviceInfo();

        //add by zhanghaijun@longcheer.com for contacts interface 20190611 start
        myInterface.setContactInfo();
        //add by zhanghaijun@longcheer.com for contacts interface 20190611 end

        //if(XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT) TellAppStoreUploadSystemApkInfo();
        //Add by xuzhonghong for upload watch status at 20171215 start
        UploadStatusUtils.getUploadStatusUtilsInstance().uploadStatus(context);
        //Add by xuzhonghong for upload watch status at 20171215 end
        //update by liaoyi 17/12/15
        UploadStatusUtils.getUploadStatusUtilsInstance().uploadVersion(context);
        //end
    }

    //*/ xiaoxun.zhangweinan, 20180313. for Read SN from NV & save value
    private void readSn() {
        byte[] mSNBuff = new byte[15];
        byte[] mBootBuff = new byte[1];
        String mAPSN = "";
        char bootFlag = '0';
        /*
        byte[] mSNBuff = new byte[15];
        byte[] mBootBuff = new byte[1];        
        String mAPSN = "";
        char bootFlag = '0';
        try {
            FileInputStream fileInstream = new FileInputStream(SN_FLAG_FILE);
            fileInstream.read(mSNBuff);
        } catch (Exception e) {
            Log.i(TAG, "read sn flag error = " + e);
        }
        for(int i=0; i<15; i++){
            char mValue = (char)(new Byte(mSNBuff[i])).intValue();
            mAPSN += String.valueOf(mValue).trim();
        }
        */
        PhaseCheckParse parse = new PhaseCheckParse();
        if (parse != null) {
            mAPSN = parse.getSn2().trim();
        }
        Log.i(TAG, "SN =" + mAPSN);
        SystemProperties.set("persist.sys.xxun.sn", mAPSN);

        //Add by lihaizhou for defaultclockstyle begin 20190130 #sync code from 705
//        if (mAPSN != null && mAPSN.length() > 6) {
//            if ("SWX003".equals(mAPSN.substring(0, 6)) || "SWX004".equals(mAPSN.substring(0, 6))) {
//                defalutClockStyleIndex = 1;
//            } else if ("SWF005".equals(mAPSN.substring(0, 6)) || "SWF006".equals(mAPSN.substring(0, 6))) {
//                defalutClockStyleIndex = 2;
//            }
//        }
        //Add by lihaizhou for defaultclockstyle end 20190130 #sync code from 705
        try {
            FileInputStream fileInstream = new FileInputStream(BOOT_COMPLETE_FLAG_FILE);
            fileInstream.read(mBootBuff);
        } catch (Exception e) {
            Log.i(TAG, "read boot flag error = " + e);
        }
        bootFlag = (char) (new Byte(mBootBuff[0]).intValue());
        Log.i(TAG, "bootFlag = " + bootFlag);
        if (SystemProperties.getInt("sys.boot_completed", 0) == 1 && bootFlag != 'P') {
            try {
                FileOutputStream fileOutstream = new FileOutputStream(BOOT_COMPLETE_FLAG_FILE);
                fileOutstream.write((byte) 'P');
                fileOutstream.write("\n".getBytes());
                SystemProperties.set("persist.sys.nvram.reset", "");
            } catch (Exception e) {
                Log.i(TAG, "write boot flag error = " + e);
            }
        }

    }
    //*/

    //*/ xiaoxun.zhangweinan, 20180111. for read AP temperature 
    TimerTask readTempertask = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (SystemProperties.getBoolean("persist.sys.temper.warning", false)) {
                        Log.i(TAG, "I am a timer");
                        TempControlUtils.readCurrentAPtemp();
                        if (SystemProperties.getInt("persist.sys.xxun.aptemper", 0) >= HIGHTEMPER_WARNING_MAX_VALUE && !isTopActivity("com.xxun.xunlauncher")) {
                            Log.i(TAG, "mTempCurrentValue is" + SystemProperties.get("persist.sys.xxun.aptemper"));
                            warnHighTemperDialog();
                        }
                        if (SystemProperties.getInt("persist.sys.xxun.aptemper", 0) >= HIGHTEMPER_WARNING_MIN_VALUE && SystemProperties.getInt("persist.sys.xxun.aptemper", 0) < HIGHTEMPER_WARNING_MAX_VALUE) {
                            Intent queryStoryStatusIntent = new Intent(Constants.ACTION_BROAST_MEDIA_SONG_STATUS);
                            sendBroadcast(queryStoryStatusIntent);
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void initReadTemperTimer() {
        readTemperTimer.schedule(readTempertask, 0, HIGHTEMPER_WARNING_TIMER_DURATION);
    }

    private void cancelReadTemperTimer() {
        if (readTemperTimer != null && readTempertask != null) {
            readTemperTimer.cancel();
            readTemperTimer = null;
            readTempertask.cancel();
            readTempertask = null;
        }
    }

    private void StartHighTemperWarningAlarm(long timeout) {
        Log.i(TAG, "StartHighTemperWarningAlarm");
        Intent intent = new Intent(Constants.XUN_PRODIC_READ_AP_TEMPER);
        mHighTemperIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mWarnAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeout, mHighTemperIntent);
    }

    private void StopAlarm() {
        try {
            Log.i(TAG, "StopAlarm");
            mWarnAlarmManager.cancel(mHighTemperIntent);
        } catch (Exception e) {
            Log.i(TAG, "StopAlarm: " + e.toString());
        }
    }

    private void SendToVideoKeepData() {
        Intent keepdata = new Intent(Constants.TELL_VIDEO_TO_KEEP_DATA);
        keepdata.setPackage("com.xiaoxun.dialer");
        sendBroadcast(keepdata);
    }

    public void warnHighTemperDialog() {
        SendToVideoKeepData();
        View view = View.inflate(this, R.layout.high_temper_warning, null);
        if (temperWarnDialog == null) {
            temperWarnDialog = new WarningDialog(this);
            temperWarnDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            temperWarnDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            temperWarnDialog.show();
            setDialogStyleTemp(view, temperWarnDialog);
        } else {
            temperWarnDialog.show();
        }
        mHighTemperTimer.start();
    }

    public void warnStopStoryDialog() {
        View view = View.inflate(this, R.layout.stop_story_warning, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CommentStyle);
        if (stopStoryDialog == null) {
            stopStoryDialog = builder.create();
            stopStoryDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            stopStoryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            stopStoryDialog.show();
            Window window = stopStoryDialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.CENTER);
            window.setContentView(view);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            stopStoryDialog.show();
        }
        ImageButton mCancel = (ImageButton) view.findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stopStoryDialog != null) {
                    stopStoryDialog.dismiss();
                    stopStoryDialog = null;
                }
            }
        });
        mStopStoryTimer.start();
    }

    private void setDialogStyleTemp(View view, AlertDialog dialog) {
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(view);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private class WarningDialog extends AlertDialog {
        public WarningDialog(Context context) {
            super(context, R.style.CommentStyle);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return true;
        }

    }

    private boolean isTopActivity(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getPackageName().equals(packageName);
    }

    private CountDownTimer mHighTemperTimer = new CountDownTimer(HIGHTEMPER_COUNTDOWNTIMER_DURATION, 100000) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mHighTemperTimer.cancel();
            if (temperWarnDialog != null) {
                temperWarnDialog.dismiss();
            }
            Log.i(TAG, "mHighTemperTimer_onFinish");
            TempControlUtils.getTempControlUtilsInstance().highTemperForceStopPackage(getApplicationContext());
            Intent stopStoryIntent = new Intent(Constants.ACTION_BROAST_STORY_FINISH);
            stopStoryIntent.putExtra("SourceType", "highTemperature");
            sendBroadcast(stopStoryIntent);
            if (telecomManager == null) {
                telecomManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            }
            if (telecomManager.getCallState() == android.telephony.TelephonyManager.CALL_STATE_OFFHOOK) {
                Log.i(TAG, "mHighTemperTimer_CALL_STATE_OFFHOOK");
                endCall();
            }
            //*/ xiaoxun.zhangweinan, 20190420. restore isMidtest&isIncall
            Settings.System.putString(getContentResolver(), "isMidtest", "false");
            Settings.System.putString(getContentResolver(), "isIncall", "false");
            //*/
        }
    };

    private CountDownTimer mStopStoryTimer = new CountDownTimer(STOPSTORY_COUNTDOWNTIMER_DURATION, 100000) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mStopStoryTimer.cancel();
            if (stopStoryDialog != null) {
                stopStoryDialog.dismiss();
            }
            Intent stopStoryIntent = new Intent(Constants.ACTION_BROAST_STORY_FINISH);
            stopStoryIntent.putExtra("SourceType", "highTemperature");
            sendBroadcast(stopStoryIntent);
        }
    };

    public void endCall() {
        Intent stopCallIntent = new Intent(Constants.XIAOXUN_ACTION_END_CALL);
        stopCallIntent.setPackage("com.xiaoxun.dialer");
        sendBroadcast(stopCallIntent);   
      /*  try {  
            Class<?> clazz = Class.forName("android.os.ServiceManager");  
            Method method = clazz.getDeclaredMethod("getService", String.class);  
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);  
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);  
            iTelephony.endCall();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  */
    }

    private void getDeviceInfo() {
        if (mDeviceInfo != null || !isCanLoadDataFromServer()) return;
        if (telecomManager == null)
            telecomManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (msp == null) msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
        if (mxiaoXunNetworkManager == null)
            mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        mxiaoXunNetworkManager.getDeviceInfo(mxiaoXunNetworkManager.getWatchEid(), new mDeviceInfo() {
            @Override
            public void onSuccess(ResponseData responseData) {
                if (isDebug) System.out.println(Tag_CONTACT + ", Success deviceInfo");
                mDeviceInfo = DeviceInfo.objectFromData(responseData.getResponseData());
                if (mDeviceInfo.qqlicense != null) {
                    android.provider.Settings.Global.putString(getContentResolver(),
                            "qq_license", mDeviceInfo.qqlicense);
                }

                JSONObject Json = new JSONObject();
                SharedPreferences.Editor medit = msp.edit();
                if (mDeviceInfo.NickName != null) {
                    if (!"".equals(mDeviceInfo.NickName)) {
                        Json.put("kid_name", mDeviceInfo.NickName);
                        medit.putString("kid_name", mDeviceInfo.NickName);
                    } else {
                        Json.put("kid_name", getResources().getString(R.string.default_nickname));
                        medit.putString("kid_name", getResources().getString(R.string.default_nickname));
                    }
                } else {
                    Json.put("kid_name", getResources().getString(R.string.default_nickname));
                    medit.putString("kid_name", getResources().getString(R.string.default_nickname));
                }
                if (mDeviceInfo.guid != null) {
                    Json.put("encrypted_qr", mDeviceInfo.guid);
                    medit.putString("encrypted_qr", mDeviceInfo.guid);
                }

                if (telecomManager != null) {
                    String nativePhoneNumber = telecomManager.getLine1Number();
                    if (nativePhoneNumber != null) {
                        if (!nativePhoneNumber.equals("")) {
                            Json.put("watch_self_number", nativePhoneNumber);
                            medit.putString("watch_self_number", nativePhoneNumber);
                        } else {
                            if (mDeviceInfo.SimNo != null) {
                                Json.put("watch_self_number", mDeviceInfo.SimNo);
                                medit.putString("watch_self_number", mDeviceInfo.SimNo);
                            }
                        }
                    } else {
                        if (mDeviceInfo.SimNo != null) {
                            Json.put("watch_self_number", mDeviceInfo.SimNo);
                            medit.putString("watch_self_number", mDeviceInfo.SimNo);
                        }
                    }
                } else {
                    if (mDeviceInfo.SimNo != null) {
                        Json.put("watch_self_number", mDeviceInfo.SimNo);
                        medit.putString("watch_self_number", mDeviceInfo.SimNo);
                    }
                }
                medit.commit();
                android.provider.Settings.Global.putString(getContentResolver(), "kid_info", Json.toJSONString());
            }

            @Override
            public void onError(int i, String s) {
            }
        });
    }

    private void getContactsFromServer() {
        if (!isCanLoadDataFromServer()) return;
        if (mxiaoXunNetworkManager == null)
            mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
        if (mxiaoXunNetworkManager != null) {
            if (msp.getBoolean("FIRST", true) || getData_every().toJSONString().equals("[]")) {
                mxiaoXunNetworkManager.getContact(mxiaoXunNetworkManager.getWatchEid(), new mContactsData() {
                    @Override
                    public void onSuccess(ResponseData responseData) {
                        if (isDebug)
                            System.out.println(Tag_CONTACT + " first insert success:" + responseData.getResponseData());
                        mUpdateContacts = Contacts.objectFromData(responseData.getResponseData());
                        SharedPreferences.Editor medit = msp.edit();
                        medit.putBoolean("FIRST", false);
                        System.out.println("jxring: mUpdateContacts.lastTS is  :" + mUpdateContacts.lastTS);
                        if (null != mUpdateContacts.lastTS)
                            medit.putString("lastTS", mUpdateContacts.lastTS);
                        medit.commit();
                        batchAddContact(mUpdateContacts.syncArray);
                        System.out.println("jxring: mUpdateContacts.syncArray  is  null " + (mUpdateContacts.syncArray == null));
                    }

                    @Override
                    public void onError(int i, String s) {
                        if (isDebug) System.out.println(Tag_CONTACT + ",first insert error:");
                    }
                });
            } else {
                if (isDebug)
                    System.out.println(Tag_CONTACT + "updateContacts local data is :" + getData_every().toJSONString());
                mxiaoXunNetworkManager.getContacts(mxiaoXunNetworkManager.getWatchEid(), getData_every().toJSONString(), msp.getString("lastTS", ""), new mContactsData() {
                    @Override
                    public void onSuccess(ResponseData responseData) {
                        if (isDebug)
                            System.out.println(Tag_CONTACT + ":updateContacts success:" + responseData.getResponseData());
                        SharedPreferences.Editor medit = msp.edit();
                        mUpdateContacts = Contacts.objectFromData(responseData.getResponseData());
                        if (null != mUpdateContacts)
                            medit.putString("lastTS", mUpdateContacts.lastTS);
                        medit.commit();
                        for (SyncArrayBean mcontact : mUpdateContacts.syncArray) {
                            GetMessageById(mcontact);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        if (isDebug)
                            System.out.println(Tag_CONTACT + " ,updateContacts error: s is :" + s);
                    }
                });

            }

        }
    }


    private synchronized void GetMessageById(SyncArrayBean mcontact) {
        if (mcontact.optype == 0 && mcontact.contactsType != 2) {
            batchAddContact(mcontact);
        } else {
            if (mcontact.optype == 2) {
                batchDeleteContact(mcontact.id);
            } else {
                if (mcontact.optype == 1) {
                    batchUpdateContact(mcontact);
                } else {
                    batchAddContact(mcontact);
                }
            }
        }
    }


    private JSONArray getData_every() {
        JSONArray mjson_array = new JSONArray();
        Cursor mcursor = this.getContentResolver().query(Constant.CONTENT_URI, new String[]{
                Constant.ID
                , Constant.UPDATETS
        }, null, null, null);
        if (mcursor != null) {
            StringBuffer Message = new StringBuffer();
            while (mcursor.moveToNext()) {
                JSONObject Json = new JSONObject();
                Json.put("updateTS", mcursor.getString(mcursor.getColumnIndex(Constant.UPDATETS)));
                Json.put("id", mcursor.getString(mcursor.getColumnIndex(Constant.ID)));
                if (!Json.isEmpty()) mjson_array.add(Json);
            }
            mcursor.close();
        }
        return mjson_array;
    }

    private void sendE2EAtCall(String[] teid, int sn, int result) {
        if (mxiaoXunNetworkManager == null) {
            mxiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        }
        InCallBean send = new InCallBean();
        send.result = result;
        send.subAction = 116;
        send.SEID = mxiaoXunNetworkManager.getWatchEid();

        SendETEInCall all = new SendETEInCall();
        all.CID = 30011;
        all.Version = CloudBridgeUtil.PROTOCOL_VERSION;
        all.SN = sn;
        all.SID = mxiaoXunNetworkManager.getSID();
        all.PL = send;
        all.TEID = teid;
        all.RC = 1;
        mxiaoXunNetworkManager.sendJsonMessage(new Gson().toJson(all, SendETEInCall.class), new mVideoCallAt());
    }


    public class mVideoCallAt extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
        }

        @Override
        public void onError(int i, String s) {
        }
    }


    public class mContactsData extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
        }

        @Override
        public void onError(int i, String s) {
        }
    }

    public class mCallLog extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
        }

        @Override
        public void onError(int i, String s) {
        }
    }

    public class mDeviceInfo extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
        }

        @Override
        public void onError(int i, String s) {
        }
    }

    public class mWatchMode extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
        }

        @Override
        public void onError(int i, String s) {
        }
    }

    public boolean CheckUrlExist(final String url) {
        File sdCard = android.os.Environment.getExternalStorageDirectory();
        File mCacheDir = new File(sdCard, "xiaoxun_cache");
        File[] files = (new File(mCacheDir, "photo_img")).listFiles();
        if (files == null) return false;
        String filename = "";
        try {
            filename = URLEncoder.encode(url.contains("https") ? url : url.replace("http", "https"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (File f : files) {
            if (filename.equals(f.getName())) {
                return true;
            }

        }
        return false;
    }


    public void WriteToFileOfPicture(final String url) {
        String filename = "";
        try {
            filename = URLEncoder.encode(url.contains("https") ? url : url.replace("http", "https"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                OutputStream os = null;
                try {
                    URL imageUrl = new URL(url.contains("https") ? url : url.replace("http", "https"));
                    conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    conn.setInstanceFollowRedirects(true);
                    is = conn.getInputStream();
                    os = new FileOutputStream(new File("/storage/emulated/0/xiaoxun_cache/photo_img/", URLEncoder.encode(url.contains("https") ? url : url.replace("http", "https"), "utf-8")));

                    final int buffer_size = 1024;
                    byte[] bytes = new byte[buffer_size];
                    for (; ; ) {
                        int count = is.read(bytes, 0, buffer_size);
                        if (count == -1)
                            break;
                        os.write(bytes, 0, count);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (os != null) os.close();
                        if (is != null) is.close();
                        if (conn != null) conn.disconnect();
                    } catch (IOException e) {
                    }
                }

            }
        }).start();
    }

    public void batchDeleteContact(final String contactId) {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean canSendToMMs = false;
                if (contactId != null) {
                    if (msp == null)
                        msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor medit = msp.edit();
                    String MyGroupContact = msp.getString("groupcontact", "");
                    if (MyGroupContact.contains(contactId + "#")) {
                        canSendToMMs = true;
                        MyGroupContact = MyGroupContact.replace(contactId + "#", "");
                    }
                    if (isDebug)
                        System.out.println(Tag_CONTACT + " MyGroupContact is : ... " + MyGroupContact);
                    medit.putString("groupcontact", MyGroupContact);
                    medit.commit();
                }
                try {
                    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                    ops.add(ContentProviderOperation.newDelete(Constant.CONTENT_URI)
                            .withSelection(Constant.ID + " =?", new String[]{contactId + ""})
                            .build());
                    LauncherService.this.getContentResolver().applyBatch(Constant.AUTHORITY, ops);
                    sendNewBroad();
                    if (canSendToMMs) {
                        sendGroupBroad();
                    }
                } catch (RemoteException reception) {

                } catch (OperationApplicationException oaeception) {

                }
            }
        }, 100);
    }

    public void batchUpdateContact(final SyncArrayBean bean) {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    if (bean.avatar != null) {
                        if (!CheckUrlExist(bean.avatar)) {
                            WriteToFileOfPicture(bean.avatar);
                        }
                    }
                    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                    ops.add(ContentProviderOperation.newUpdate(Constant.CONTENT_URI)
                            .withSelection(Constant.ID + " =?", new String[]{bean.id + ""})
                            .withValue(Constant.NAME, bean.name)
                            .withValue(Constant.NUMBER, bean.number)
                            .withValue(Constant.SUB_NUMBER, bean.subNumber)
                            .withValue(Constant.OPTYPE, bean.optype)
                            .withValue(Constant.CONTACTSTYPE, bean.contactsType)
                            .withValue(Constant.OLDUPDATETS, bean.oldupdateTS)
                            .withValue(Constant.ATTRI, (bean.contactsType == 2) ? 27 : bean.attri)
                            .withValue(Constant.CONTACT_WEIGHT, bean.contactWeight)
                            .withValue(Constant.USER_GID, bean.userGid)
                            .withValue(Constant.UPDATETS, bean.updateTS)
                            .withValue(Constant.USER_EID, bean.userEid)
                            .withValue(Constant.AVATAR, bean.avatar)
                            .withValue(Constant.BLANK_ONE, "")
                            .withValue(Constant.BLANK_TWO, "")
                            .withValue(Constant.BLANK_THREE, "")
                            .withValue(Constant.BLANK_FOUR, "")
                            .withYieldAllowed(true)
                            .build());
                    LauncherService.this.getContentResolver().applyBatch(Constant.AUTHORITY, ops);
                    sendNewBroad();
                    sendGroupBroad();
                } catch (RemoteException reception) {

                } catch (OperationApplicationException oaeception) {

                }
            }
        }, 0);

    }

    public void batchAddContact(final List<SyncArrayBean> mlist) {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                    for (SyncArrayBean bean : mlist) {
                        if (checkContactBeanIsExit(bean.id)) continue;//如果数据库存在当前ID的联系人，for循环跳过当前
                        if (bean.contactsType != 1) {
                            if (msp == null)
                                msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor medit = msp.edit();
                            String MyGroupContact = msp.getString("groupcontact", "");
                            MyGroupContact = MyGroupContact + bean.id + "#";
                            if (isDebug)
                                System.out.println(Tag_CONTACT + " MyGroupContact is : ... " + MyGroupContact);
                            medit.putString("groupcontact", MyGroupContact);
                            medit.commit();
                        }

                        ops.add(ContentProviderOperation.newInsert(Constant.CONTENT_URI)
                                .withValue(Constant.ID, bean.id)
                                .withValue(Constant.NAME, bean.name)
                                .withValue(Constant.NUMBER, bean.number)
                                .withValue(Constant.SUB_NUMBER, bean.subNumber)
                                .withValue(Constant.OPTYPE, bean.optype)
                                .withValue(Constant.CONTACTSTYPE, bean.contactsType)
                                .withValue(Constant.OLDUPDATETS, bean.oldupdateTS)
                                .withValue(Constant.ATTRI, (bean.contactsType == 2) ? 27 : bean.attri)
                                .withValue(Constant.CONTACT_WEIGHT, bean.contactWeight)
                                .withValue(Constant.USER_GID, bean.userGid)
                                .withValue(Constant.UPDATETS, bean.updateTS)
                                .withValue(Constant.USER_EID, bean.userEid)
                                .withValue(Constant.AVATAR, bean.avatar)
                                .withValue(Constant.BLANK_ONE, "one")
                                .withValue(Constant.BLANK_TWO, "two")
                                .withValue(Constant.BLANK_THREE, "three")
                                .withValue(Constant.BLANK_FOUR, "four")
                                .withYieldAllowed(true)
                                .build());
                    }
                    LauncherService.this.getContentResolver().applyBatch(Constant.AUTHORITY, ops);
                    sendNewBroad();
                } catch (RemoteException reception) {

                } catch (OperationApplicationException oaeception) {

                }
            }
        }, 0);

    }

    public boolean checkContactBeanIsExit(String id) {
        boolean isExit = false;
        Cursor mcursor = this.getContentResolver().query(Constant.CONTENT_URI, new String[]{
                Constant.ID
        }, Constant.ID + "=?", new String[]{id}, null);
        if (mcursor != null) {
            if (mcursor.getCount() != 0) isExit = true;
        }
        mcursor.close();
        return isExit;
    }

    public void batchAddContact(final SyncArrayBean mcontact) {
        if (checkContactBeanIsExit(mcontact.id)) return;//如果数据库存在当前ID的联系人，我们不能在插入，纠错处理.
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mcontact.contactsType != 1) {
                    if (msp == null)
                        msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor medit = msp.edit();
                    String MyGroupContact = msp.getString("groupcontact", "");
                    MyGroupContact = MyGroupContact + mcontact.id + "#";
                    if (isDebug)
                        System.out.println(Tag_CONTACT + " MyGroupContact is : ... " + MyGroupContact);
                    medit.putString("groupcontact", MyGroupContact);
                    medit.commit();
                }

                if (mcontact.avatar != null) {
                    WriteToFileOfPicture(mcontact.avatar);
                }

                try {
                    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                    ops.add(ContentProviderOperation.newInsert(Constant.CONTENT_URI)
                            .withValue(Constant.ID, mcontact.id)
                            .withValue(Constant.NAME, mcontact.name)
                            .withValue(Constant.NUMBER, mcontact.number)
                            .withValue(Constant.SUB_NUMBER, mcontact.subNumber)
                            // .withValue(Constant.OPTYPE, bean.optype)
                            .withValue(Constant.CONTACTSTYPE, mcontact.contactsType)
                            .withValue(Constant.OLDUPDATETS, mcontact.oldupdateTS)
                            .withValue(Constant.ATTRI, (mcontact.contactsType == 2) ? 27 : mcontact.attri)
                            .withValue(Constant.CONTACT_WEIGHT, mcontact.contactWeight)
                            .withValue(Constant.USER_GID, mcontact.userGid)
                            .withValue(Constant.UPDATETS, mcontact.updateTS)
                            .withValue(Constant.USER_EID, mcontact.userEid)
                            .withValue(Constant.AVATAR, mcontact.avatar)
                            .withValue(Constant.BLANK_ONE, "one")
                            .withValue(Constant.BLANK_TWO, "two")
                            .withValue(Constant.BLANK_THREE, "three")
                            .withValue(Constant.BLANK_FOUR, "four")
                            .withYieldAllowed(true)
                            .build());
                    LauncherService.this.getContentResolver().applyBatch(Constant.AUTHORITY, ops);
                    sendNewBroad();
                    if ((mcontact.contactsType) != 1 && mcontact.optype != 2) {
                        sendGroupBroad();
                    }
                } catch (RemoteException reception) {

                } catch (OperationApplicationException oaeception) {

                }
            }
        }, 0);

    }

    //检查当前内存是否大于10M
    public boolean isCanLoadDataFromServer() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (double) (availableBlocks * blockSize) / 1024 / 1024 > 10;
    }

    private void sendETEcallToEndMethod(int CurrentSN, String seid) {
        if (mxiaoXunNetworkManager == null) {
            mxiaoXunNetworkManager = (XiaoXunNetworkManager) getApplicationContext().getSystemService("xun.network.Service");
        }
        SendEndCallBean send = new SendEndCallBean();
        send.SN = CurrentSN;
        send.subAction = 117;
        send.SEID = mxiaoXunNetworkManager.getWatchEid();

        SendETECallToEnd all = new SendETECallToEnd();
        all.CID = 30011;
        all.Version = CloudBridgeUtil.PROTOCOL_VERSION;
        all.SN = mxiaoXunNetworkManager.getMsgSN();
        all.SID = mxiaoXunNetworkManager.getSID();
        all.PL = send;
        all.TEID = new String[]{seid};
        mxiaoXunNetworkManager.sendJsonMessage(new Gson().toJson(all, SendETECallToEnd.class), new sendE2ECallBack());

    }

    private class sendE2ECallBack extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
        }

        @Override
        public void onError(int i, String s) {
        }
    }


    //[add by jxring for pervent_addition dialog 2018.6.8 start]
    AlertDialog mPrevent_dialog = null;


    public void warnPreventEnterDialog() {
        View view = View.inflate(this, R.layout.xiaoxun_prevent, null);
        TextView mtextview = (TextView) view.findViewById(R.id.prevent);
        ImageButton callcancel = (ImageButton) view.findViewById(R.id.cancel);
        mtextview.setText(getResources().getString(R.string.prevent_dialog_show_one));
        if (mPrevent_dialog == null) {
            mPrevent_dialog = new WarningDialog(this);
            mPrevent_dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mPrevent_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mPrevent_dialog.show();
            setDialogStyleTemp(view, mPrevent_dialog);
        } else {
            mPrevent_dialog.show();
        }
        callcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPrevent_dialog != null) {
                    mPrevent_dialog.dismiss();
                    mPrevent_dialog = null;
                }
            }
        });
    }
    //[add by jxring for pervent_addition dialog 2018.6.8 end!]
    //[add by jxring for Dialer SQL modify 2017.11.03 end!]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        //del by lihaizhou due to compile error on 4.4
        //startForeground(1, notification);//service销毁时重新起来前台
        unregisterReceiver(loginstaterecevive);
        unregisterReceiver(mReceiver);
        //*/ xiaoxun.zhangweinan, 20190906. receive audio commands
        unregisterReceiver(audioCommandsReceiver);
        //*/
        this.getContentResolver().unregisterContentObserver(mHeadsUpObserver);
        this.getContentResolver().unregisterContentObserver(mDialObserver);
        this.getContentResolver().unregisterContentObserver(mSilenceObserver);
        this.getContentResolver().unregisterContentObserver(mCallLogObserver);
        this.getContentResolver().unregisterContentObserver(functionListObserver);
        this.getContentResolver().unregisterContentObserver(goBackToHomeObserver);
        this.getContentResolver().unregisterContentObserver(mLogin_contacts);
 		this.getContentResolver().unregisterContentObserver(sCoresystemcontentResolver);
    }
}
