package com.xxun.xunlauncher.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

import com.android.ims.ImsManager;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xxun.xunlauncher.Constants;
import com.xxun.xunlauncher.R;
import com.xxun.xunlauncher.dialog.QrcodeDialogFragment;
import com.xxun.xunlauncher.dialog.LowMemDialogFragment;
import com.xxun.xunlauncher.systemui.WeatherFragAdapter;
import com.xxun.xunlauncher.dialog.ClockStyleDialogFragment;
import com.xxun.xunlauncher.systemui.weatherfragment.WeatherFirstFragment;
import com.xxun.xunlauncher.systemui.weatherfragment.WeatherSecondFragment;
import com.xxun.xunlauncher.systemui.weatherfragment.WeatherThirdFragment;
import com.xxun.xunlauncher.receiver.PowerBtnClickReceiver;
import com.xxun.xunlauncher.receiver.BatteryReceiver;
import com.xxun.xunlauncher.receiver.LauncherAlarmReceiver;
import com.xxun.xunlauncher.receiver.SimCardStateChangeReceiver;
import com.xxun.xunlauncher.service.LauncherService;
import com.xxun.xunlauncher.utils.NetworkUtils;
import com.xxun.xunlauncher.utils.ShutDownUtils;
import com.xxun.xunlauncher.utils.JsonUtils;
import com.xxun.xunlauncher.utils.MemoryUtils;

import android.app.AlertDialog;
import android.view.Window;
import android.view.Gravity;
import android.view.WindowManager;
import android.net.wifi.WifiInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.CountDownTimer;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.content.res.TypedArray;

import org.json.JSONException;

import com.xxun.xunlauncher.toast.StyleableToast;
import com.xxun.xunlauncher.utils.PowerUtils;
import com.xxun.xunlauncher.systemui.SystemUiPanel;
import com.xxun.xunlauncher.launcherpager.LauncherViewPager;
import com.xxun.xunlauncher.launcherpager.LauncherPagerAdapter;
import com.xxun.xunlauncher.systemui.WeatherInfo;
import com.xxun.xunlauncher.utils.UploadStatusUtils;

import android.widget.Toast;
import android.database.ContentObserver;
import android.os.BatteryManager;
import android.support.v4.view.ViewPager;
import android.provider.Settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xxun.xunlauncher.gif.GifTempletView;

import android.app.AlarmManager;
import android.app.PendingIntent;

import java.util.Calendar;

import android.widget.Toast;
import android.widget.ImageButton;
import android.util.XiaoXunUtil;

import com.xiaoxun.sdk.utils.Constant;

import android.media.AudioManager;
import android.os.SystemClock;
import android.provider.Settings.SettingNotFoundException;

import com.xxun.xunlauncher.MsgManager;
import com.xxun.xunlauncher.utils.NetworkUtils;

import android.net.Uri;

import java.io.File;

import com.xiaoxun.xiaoxuninstallapk.XunConstant;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.XiaoXunUtil;

import java.io.BufferedReader;
import java.io.IOException;

import android.database.Cursor;

import java.io.FileReader;
import java.util.HashMap;

import android.net.wifi.ScanResult;

import com.xxun.xunlauncher.utils.TempControlUtils;

import android.net.wifi.WifiConfiguration;


import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Collections;


/**
 * @author lihaizhou
 * @time 2017.09.10
 * @class XunLauncher主界面
 */

public class MainActivity extends FragmentActivity implements MsgManager.OnMsgListener, ViewPager.OnPageChangeListener, LauncherPagerAdapter.OnPagerItemClickLitener {

    //Launcher diaplay releated
    private long mLastTime;
    private List<Integer> appsList;
    private int unreadMsgNum = -1;
    private LauncherViewPager mLauncherViewPager;
    private LauncherPagerAdapter launcherPagerAdapter;
    private AlertDialog helpDialog, loginhelpDialog;
    private View alertView;
    private boolean wifi_close_15_min = false;

    //Launcher's clock releated
    private int clockStyleIndex;
    private ClockStyleDialogFragment timeStyleFragment;

    //systemUI releated
    private int imsRegState;
    private boolean isCharging;
    private boolean isSlideBackup = true;
    private long lastBgCallTimeBackup = -1;
    private int currentPower;
    private ImageView powerImg;
    private ImageView insertSimAni;
    private ViewPager weatherPager;
    private TextView powerPercentTv;
    private int indicatorNum = 0;
    private int currentSignalLevel = 4;
    private TextView signalTypeTv;
    private ImageView netWorkTypeImg;
    private ImageView singalStrengthImg;
    private SystemUiPanel systemUiPanel;
    private WeatherFragAdapter adapter;
    private LinearLayout mIndicator = null;
    private ImageView powerSavingImg;
    private List<Fragment> weatherFragments;
    private LinearLayout indicatorLayout;
    private AnimationDrawable animationDrawable;
    private TelephonyManager telephonyManager;
    private ExecutorService fixedThreadExecutor;
    private int[] signalStrengthImgList = {R.drawable.signal_0, R.drawable.signal_1, R.drawable.signal_2, R.drawable.signal_3, R.drawable.signal_4, R.drawable.signal_5};
    private int[] wifiStrengthImgList = {R.drawable.connec_wifi01, R.drawable.connec_wifi02, R.drawable.connec_wifi03, R.drawable.connec_wifi04};
    private int[] wifiLoginStrengthImgList = {R.drawable.connec_wifi_01, R.drawable.connec_wifi_02, R.drawable.connec_wifi_03, R.drawable.connec_wifi_04};
    private int[] powerImgLists = {R.drawable.power_0, R.drawable.power_10, R.drawable.power_20, R.drawable.power_30, R.drawable.power_40, R.drawable.power_50, R.drawable.power_60, R.drawable.power_70, R.drawable.power_80, R.drawable.power_90, R.drawable.power_100};

    //server releated
    private XiaoXunNetworkManager mXiaoXunNetworkManager;

    //alarm releated
    private AlarmManager closeWifialarmManager;
    //Modify by xuzhonghong for XUN_SW710_A01-516 on 20180521 start
    public PendingIntent pendingIntent;
    //Modify by xuzhonghong for XUN_SW710_A01-516 on 20180521 end

    //broadcast releated
    private BatteryValueReceiver batteryValueReceiver;
    private BroadcastReceiver powerBtnClickReceiver;
    private BrightScreenReceiver brightScreenReceiver;
    private BatteryBroadcastReceiver batteryBroadcastReceiver;
    private LoginBroadcastReceiver loginBroadcastReceiver;
    private NetWorkBroadCastReciver mNetWorkBroadCastReciver;

    //database releated
    private String isQQFirstLaunch = "isQQFirstLaunch";
    private String qqNoticeSharePreName = "qqNoticeSharePreName";

    //ShutDown releated
    private long shutdownTickTime;
    private AudioManager mAudioManager;
    private ShutDownTimer shutDownTimer;
    private WifiManager wifiManager;

    private static final String TAG = "LauncherMainActivity";

    //*/ xiaoxun.zhangweinan, 20180203. for AP temperature warning
    AlertDialog forbidEnterDialog = null;
    private final int COUNTDOWNTIMER_DURATION = 5000;
    //*/
    //pengzhonghong add begin
    private long lastTime = 0;
    private long currentTime = 0;
    //pengzhonghong add bend

    //Add by xuzhonghong for show power connect or disconnect notification on 20180504 start
    private TelephonyManager telecomManager;
    private String isIncall = "false";
    private String isAlarmRing = "false";
    private MediaPlayer mMediaPlayer;
    //Add by xuzhonghong for show power connect or disconnect notification on 20180504 end
    //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 start
    private static MainActivity mainacticvityInstance;
    //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 end

    //[add by jxring 2018.6.8 start ]
    private SharedPreferences msp = null;
    //[add by jxring 2018.6.8 end!]

    //add by mayanjun.
    private int mLTEModeRequiredCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSystemUIView();
        initClockViews();
        //add zhj
        if (XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT)
            initHashmap();
        initLauncherApps();
        initLauncherService();
        checkSimCardIsOn();
        registerListener();
        registerReceiver();
        registerObserver();
        //[add by jxring for silence disturb 2017.11.24 start ]
        sendLauncherOkBroadCast();
        registerSilenceSQL();
        //[add by jxring for silence disturb 2017.11.24 end! ]
        //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 start
        mainacticvityInstance = this;
        //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 end
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    //add by mayanjun.for 2G 4G switch
    @Override
    protected void onResume() {
        super.onResume();
        initSomeData();
        Log.d(TAG, "onResume ");
        if (XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT) {
            tellXunInstallService();
        }

        if (mLTEModeRequiredCounter > 0) {
            mLTEModeRequiredCounter--;
            Boolean isUnderLTEMode = SystemProperties.getBoolean("persist.sys.lteswitchon", false);
            if (isUnderLTEMode /*&& !NetworkUtils.getNetWorkUtilsInstance().isWifiContected(this)*/) {
                if (mXiaoXunNetworkManager == null) {
                    mXiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
                }
                mXiaoXunNetworkManager.releaseLTEMode("xiaoxun.Launcher");
            }
        }

        //Add by lihaizhou 201806009
        if (isCharging) {
            showUserForbidActivity();
        }
    }

    //[标志量决定，除非回复出厂值，插入系统apk info信息只执行一次]
    private void tellXunInstallService() {
        if (msp == null) msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
        boolean hasInsert = msp.getBoolean("hasInsert", false);
        if (!hasInsert) {
            insertData_SystemAPk();
            SharedPreferences.Editor medit = msp.edit();
            medit.putBoolean("hasInsert", true);
            medit.commit();
        }
    }

    private void insertData_SystemAPk() {
        String[] mAppIds = getResources().getStringArray(R.array.applists);
        String[] mappNames = getResources().getStringArray(R.array.applists_name);
        String need_hiden = Settings.System.getString(getContentResolver(), "XunAppHiden"); //功能控制的旧数据
        if (need_hiden == null) need_hiden = "";

        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < mAppIds.length; i++) {
                if (!mAppIds[i].equals("ado.install.xiaoxun.com.xiaoxuninstallapk") && !mAppIds[i].equals("com.tencent.qqlite")) {
                    //add zhj modify
                    //if(!XiaoXunUtil.XIAOXUN_CONFIG_POINTSYSTEM_SUPPORT){

                    if (!mAppIds[i].equals("com.xxun.pointsystem")) {
                        ops.add(ContentProviderOperation.newInsert(XunConstant.CONTENT_URI)
                                .withValue(XunConstant.NAME, mappNames[i])
                                .withValue(XunConstant.TYPE, 2)
                                .withValue(XunConstant.APP_ID, mAppIds[i])
                                .withValue(XunConstant.ICON, mAppIds[i])
                                .withValue(XunConstant.STATUS, 0)
                                .withValue(XunConstant.HIDDEN, need_hiden.contains(mAppIds[i]) ? 1 : 0)
                                .withYieldAllowed(true)
                                .build());
                        //Log.d("zhj","mappNames[i] = "  + i + ",   name ="  +mappNames[i] );
                    }
                    /**}else{
                     ops.add(ContentProviderOperation.newInsert(XunConstant.CONTENT_URI)
                     .withValue(XunConstant.NAME, mappNames[i])
                     .withValue(XunConstant.TYPE, 2)
                     .withValue(XunConstant.APP_ID, mAppIds[i])
                     .withValue(XunConstant.ICON, mAppIds[i])
                     .withValue(XunConstant.STATUS, 0)
                     .withValue(XunConstant.HIDDEN, need_hiden.contains(mAppIds[i])?1:0)
                     .withYieldAllowed(true)
                     .build());
                     }   */
                }
            }
            getContentResolver().applyBatch(XunConstant.AUTHORITY, ops);
        } catch (RemoteException reception) {

        } catch (OperationApplicationException oaeception) {

        }
    }

    private void initSomeData() {
        android.provider.Settings.System.putString(getContentResolver(), "xun_video", "false");
        android.provider.Settings.System.putString(getContentResolver(), "isIncall", "false");
    }

    //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 start
    public static MainActivity getmainacticvityInstance() {
        return mainacticvityInstance;
    }
    //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 start

    /**
     * @author lihaizhou
     * @time 2018.03.26
     * @describe 初始化SystemUI资源
     */
    private void initSystemUIView() {
        int indicatorSize = 6;
        powerImg = (ImageView) findViewById(R.id.power_img);
        powerPercentTv = (TextView) findViewById(R.id.power_percent_tv);
        if (!Constant.PROJECT_NAME.equals("SW706")) {
            if (SystemProperties.get("ro.build.type").equals("user"))
                powerPercentTv.setVisibility(View.GONE);
        }

        powerImg.setImageResource(R.drawable.power_charging);
        animationDrawable = (AnimationDrawable) powerImg.getDrawable();
        signalTypeTv = (TextView) findViewById(R.id.signal_type_tv);
        powerSavingImg = (ImageView) findViewById(R.id.power_save_img);
        netWorkTypeImg = (ImageView) findViewById(R.id.network_type_img);
        singalStrengthImg = (ImageView) findViewById(R.id.signal_strength_img);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        updateNetworkInfo(NetworkUtils.getNetWorkUtilsInstance().getNetWorkInfo(this));
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        mIndicator = (LinearLayout) findViewById(R.id.mIndicator);//[add by jxring for silence disturb 2017.11.24 start ]
        indicatorLayout = (LinearLayout) findViewById(R.id.indicator_linear);
        weatherPager = (ViewPager) findViewById(R.id.weather_viewpager);
        weatherFragments = new ArrayList();
        weatherFragments.add(new WeatherFirstFragment());
        weatherFragments.add(new WeatherSecondFragment());
        weatherFragments.add(new WeatherThirdFragment());
        adapter = new WeatherFragAdapter(getSupportFragmentManager(), weatherFragments);
        weatherPager.setAdapter(adapter);
        fixedThreadExecutor = Executors.newFixedThreadPool(3);
        systemUiPanel = (SystemUiPanel) findViewById(R.id.sliding_layout);
        for (int i = 0; i < Constants.WEATHER_INDICATOR_SUM; i++) {
            View view = new View(MainActivity.this);
            view.setBackgroundResource(R.drawable.indicator_background);
            view.setEnabled(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
            if (i != 0) {
                layoutParams.leftMargin = 15;
            }
            indicatorLayout.addView(view, layoutParams);
        }
        indicatorLayout.getChildAt(0).setEnabled(true);
        //del by lihaizhou due to addOnPageChangeListener do not exist on 4.4
        //weatherPager.addOnPageChangeListener(this);
        //Add by lihaizhou for 4.4
        weatherPager.setOnPageChangeListener(this);
        updatePowerDisplay();
    }

    /**
     * @author lihaizhou
     * @createtime 2017.09.20
     * @describe 初始化Clock资源
     */
    private void initClockViews() {
        mLauncherViewPager = (LauncherViewPager) findViewById(R.id.viewpager);
        timeStyleFragment = new ClockStyleDialogFragment();
    }

    /**
     * @author lihaizhou
     * @createtime 2017.11.26
     * @describe 初始化Launcher界面的应用列表，废弃原先通过遍历已安装应用的方式
     */
    private void initLauncherApps() {
        ArrayList<Integer> syncServerIconList = new ArrayList<Integer>();
        String functions = Settings.System.getString(getContentResolver(), Constants.FUNCTIONLIST);
        Log.d(TAG, "initLauncherApps functions = :" + functions);
        try {
            syncServerIconList = JsonUtils.getJsonUtilsInstance().getLaestFuncationList(functions);
        } catch (JSONException e) {
            syncServerIconList = null;

            Log.d(TAG, "JSONException happens!");
        }

        Log.d(TAG, "zhj initLauncherApps iconList = :" + syncServerIconList + ",appsList = :" + appsList);
        if (syncServerIconList != null) {
            for (int i = 0; i < syncServerIconList.size(); i++) {
                //Log.d("zhj", "syncServerIconList " + "   i = " + getPackageName(syncServerIconList.get(i)));
            }
        }

        boolean isSame = true;
        if (Settings.System.getInt(getContentResolver(), "dobind", 0) == 1) {
            syncServerIconList = null;
        } else {
            if (XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT) {
                Intent mintent = new Intent();
                mintent.setComponent(new ComponentName("ado.install.xiaoxun.com.xiaoxuninstallapk", "com.xiaoxun.service.DealSubActionService"));
                mintent.setAction("com.xiaoxun.update.item.database");
                MainActivity.this.startService(mintent);
            }
        }


        //[add by jxring for silence Mode 2017.12.19 start]
        String result = Settings.System.getString(getContentResolver(), "SilenceList_result");
        System.out.println("jxring: result is :" + result);
        boolean SilenceList_result = (result == null ? false : Boolean.parseBoolean(result));
        if (SilenceList_result) {
            TempControlUtils.getTempControlUtilsInstance().forceStopPackage(getApplicationContext());
            SilenceModeAdapter();
            mIndicator.setBackgroundResource(R.drawable.xiaoxun_silencemode_systemui);
            //Add by lihaizhou,while slience mode on,we need send broadcast to qq
            sendBroadcastFromLauncher("com.tencent.qqlite.watch.COMPLETELY_CLOSE_MODE");
            //Add by lihaizhou,while slience mode on,viewpager could scroll
            mLauncherViewPager.setSlide(true);
        } else {
            mIndicator.setBackgroundResource(R.drawable.mark_up);
            if (appsList == null || syncServerIconList == null) {
                //Log.d("zhj", " is test 2222");
                //[add by jxring for silence Mode 2017.12.19 end!]
                if (appsList == null) {
                    appsList = new ArrayList<Integer>();
                } else {
                    appsList.clear();
                }
                if (TextUtils.isEmpty(functions) || syncServerIconList == null) {
                    if (XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_ENGLISH_STUDY && XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT) {
                        if (Constant.PROJECT_NAME.equals("SW706")) {
                            Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport_sw706,
                                    R.drawable.stopwatch, R.drawable.story, R.drawable.appstore, R.drawable.album, R.drawable.camera, R.drawable.alipay/*,R.drawable.friends*/, R.drawable.engstd_icon, R.drawable.voice_question};
                            appsList.addAll(Arrays.asList(toTransHidden(mNewIconList)));
                        } else if (Constant.PROJECT_NAME.equals("SW707")) {
                            Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
                                     R.drawable.appstore, R.drawable.img_recognition, R.drawable.word_recognition, R.drawable.engstd_icon, R.drawable.album, R.drawable.camera, R.drawable.alipay/*,R.drawable.friends*/, R.drawable.voice_question};
                            appsList.addAll(Arrays.asList(toTransHidden(mNewIconList)));
                            /* longcheer:lishuangwei on: Thu, 26 Dec 2019 15:07:11 +0800
                             */
                        } else if (Constant.PROJECT_NAME.equals("SW708")) {
                            Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
                                    R.drawable.stopwatch, R.drawable.story, R.drawable.appstore, R.drawable.img_recognition, R.drawable.word_recognition, R.drawable.album, R.drawable.camera, R.drawable.alipay, R.drawable.friends, R.drawable.engstd_icon, R.drawable.voice_question};
                            appsList.addAll(Arrays.asList(toTransHidden(mNewIconList)));
// End of longcheer:lishuangwei
                        } else {
                            Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
                                    R.drawable.stopwatch, R.drawable.story, R.drawable.appstore, R.drawable.img_recognition, R.drawable.album, R.drawable.camera, R.drawable.alipay/*,R.drawable.friends*/, R.drawable.engstd_icon, R.drawable.voice_question};
                            appsList.addAll(Arrays.asList(toTransHidden(mNewIconList)));
                        }
                    } else if (XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_ENGLISH_STUDY) {
                        Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
                                R.drawable.stopwatch, R.drawable.story, R.drawable.album, R.drawable.camera, R.drawable.alipay/*,R.drawable.friends*/, R.drawable.engstd_icon, R.drawable.voice_question};
                        appsList.addAll(Arrays.asList(toTransHidden(mNewIconList)));
                    } else if (XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT) {
                        Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
                                R.drawable.stopwatch, R.drawable.story, R.drawable.appstore, R.drawable.album, R.drawable.img_recognition, R.drawable.camera, R.drawable.alipay/*,R.drawable.friends*/, R.drawable.voice_question};
                        appsList.addAll(Arrays.asList(toTransHidden(mNewIconList)));
                    } else {
                        Integer[] allIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
                                R.drawable.stopwatch, R.drawable.story, R.drawable.album, R.drawable.camera, R.drawable.alipay/*,R.drawable.friends*/, R.drawable.voice_question};
                        appsList.addAll(Arrays.asList(toTransHidden(allIconList)));
                    }
                } else {
                    appsList.addAll(Arrays.asList(toTransHidden(syncServerIconList)));
                }
            } else {
                //Log.d("zhj", " is test 1111");
                List<Integer> tempList = new ArrayList<Integer>(appsList);
                appsList.clear();
                appsList.addAll(Arrays.asList(toTransHidden(syncServerIconList)));
                isSame = tempList.size() == appsList.size() && appsList.containsAll(tempList) && tempList.containsAll(appsList);
            }
            Log.d(TAG, "initLauncherApps isSame = :" + isSame);
            setPagerAdapter(isSame);
        }
    }


    private String getPackageName(Integer id) {
        String packName = "";
        String[] packageNameList = getResources().getStringArray(R.array.show_applists_name);
        Integer[] mNewIconList;
        if (Constant.PROJECT_NAME.equals("SW706")) {
            mNewIconList = new Integer[]{R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings,
                    R.drawable.pet, R.drawable.sport, R.drawable.stopwatch, R.drawable.story, R.drawable.appstore, R.drawable.album, R.drawable.camera,
                    R.drawable.alipay/*,R.drawable.friends*/, R.drawable.engstd_icon, R.drawable.voice_question};
        } else {
            mNewIconList = new Integer[]{R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings,
                    R.drawable.pet, R.drawable.sport, R.drawable.stopwatch, R.drawable.story, R.drawable.appstore, R.drawable.album, R.drawable.camera,
                    R.drawable.alipay/*,R.drawable.friends*/, R.drawable.engstd_icon, R.drawable.voice_question};
        }
        for (int i = 0; i < packageNameList.length; i++) {
            for (int j = 0; j < mNewIconList.length; j++) {
                if (mNewIconList[j].equals(id)) {
                    return packageNameList[j];
                }
            }

        }
        return packName;
    }

    private List<Integer> getHiddenList() {
        List<Integer> mlist = new ArrayList<Integer>();
        Cursor mcursor = getContentResolver().query(XunConstant.CONTENT_URI, new String[]{XunConstant.APP_ID}, XunConstant.HIDDEN + " = ? and " + XunConstant.TYPE + " = ? ", new String[]{"1", "2"}, null);
        if (mcursor != null) {
            while (mcursor.moveToNext()) {
                mlist.add(mAppIcon.get(mcursor.getString(mcursor.getColumnIndex(XunConstant.APP_ID))));
                Log.d("zhj", "getHiddenList  = :" + mcursor.getString(mcursor.getColumnIndex(XunConstant.APP_ID)));
            }
            mcursor.close();
        }
        return mlist;
    }

    HashMap<String, Integer> mAppIcon = new HashMap<String, Integer>();

    private void initHashmap() {
        mAppIcon.put("com.xxun.watch.xunstopwatch", R.drawable.stopwatch);
        mAppIcon.put("com.xxun.watch.xunfriends", R.drawable.friends);
        mAppIcon.put("com.xiaoxun.englishdailystudy", R.drawable.engstd_icon);
        mAppIcon.put("com.xiaoxun.dialer", R.drawable.phone);
        mAppIcon.put("com.xxun.watch.xunchatroom", R.drawable.voice_msg);
        mAppIcon.put("com.xxun.watch.xunsettings", R.drawable.settings);
        mAppIcon.put("com.xxun.watch.xunpet", R.drawable.pet);
        if (Constant.PROJECT_NAME.equals("SW706")) {
            mAppIcon.put("com.xxun.watch.xunsports", R.drawable.sport_sw706);
        } else {
            mAppIcon.put("com.xxun.watch.xunsports", R.drawable.sport);
        }
        mAppIcon.put("com.xxun.watch.storytall", R.drawable.story);
        mAppIcon.put("ado.install.xiaoxun.com.xiaoxuninstallapk", R.drawable.appstore);
        mAppIcon.put("com.xxun.xungallery", R.drawable.album);
        mAppIcon.put("com.xxun.xuncamera", R.drawable.camera);
        mAppIcon.put("com.eg.android.AlipayGphone", R.drawable.alipay);
        mAppIcon.put("com.xxun.xunimgrec", R.drawable.img_recognition);
        mAppIcon.put("com.xxun.xunwordsrec", R.drawable.word_recognition);
        //add zhj
        String[] packageNameList = getResources().getStringArray(R.array.applists);
        for (int i = 0; i < packageNameList.length; i++) {
            if (packageNameList[i].indexOf("xunbrain") != -1) {
                //Log.d("zhj","packageNameList[i] =" + packageNameList[i]);
                mAppIcon.put(packageNameList[i], R.drawable.voice_question);
                break;
            }
        }
    }


    private Integer[] toTransHidden(ArrayList<Integer> marray) {
        List<Integer> mls = new ArrayList<Integer>();
        List<Integer> mhiddenlist = getHiddenList();
        for (int i = 0; i < marray.size(); i++) {
            if (!mhiddenlist.contains(marray.get(i))) {
                mls.add(marray.get(i));
                //Log.d("zhj","show package name =" + getPackageName(marray.get(i)));
            } else {
                //Log.d("zhj","Hidden package name =" + getPackageName(marray.get(i)));
            }
        }

        Integer[] mreturnInt = new Integer[mls.size()];
        for (int i = 0; i < mls.size(); i++) {
            mreturnInt[i] = mls.get(i);
        }
        return mreturnInt;

    }

    private Integer[] toTransHidden(Integer[] mlist) {
        List<Integer> mls = new ArrayList<Integer>();
        List<Integer> mhiddenlist = getHiddenList();
        for (int i = 0; i < mlist.length; i++) {
            if (!mhiddenlist.contains(mlist[i])) {
                mls.add(mlist[i]);
                //Log.d("zhj","2 show package name =" + getPackageName(mlist[i]));
            } else {
                //Log.d("zhj","2 Hidden package name =" + getPackageName(mlist[i]));
            }
        }

        Integer[] mreturnInt = new Integer[mls.size()];
        for (int i = 0; i < mls.size(); i++) {
            mreturnInt[i] = mls.get(i);
        }
        return mreturnInt;
    }


    /**
     * @author lihaizhou
     * @createtime 2017.09.30
     * @describe Launcher服务, 后台做一些和UI无关的动作, 比如获取登陆状态, 同步时间....
     */
    private void initLauncherService() {
        Intent intent = new Intent(MainActivity.this, LauncherService.class);
        startService(intent);

        //启动NFC service
        Intent openhots = new Intent();
        openhots.setComponent(new ComponentName("com.miui.tsm.simulator", "com.miui.tsm.simulator.message.NfcSubActionService"));
        startService(openhots);
    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.22
     * @describe 启动Launcher时检测网络状态以及SIM卡是否在位
     */
    private void checkSimCardIsOn() {
        Log.d(TAG, "checkSimCardIsOn begin" + telephonyManager.getSimState());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //boolean isSimCardReady = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
                //if (!isSimCardReady) {
                //boolean isSimAbsent = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT;
                if (isSimAbsent()) {
                    showAlertDialog(Constants.SIM_ALERT_DIALOG);
                    if (!isCharging) {
                        startShutDownTimer();
                    }
                }
                updateNetworkInfo(NetworkUtils.getNetWorkUtilsInstance().getNetWorkInfo(MainActivity.this));
                Log.d(TAG, "isSimAbsent() = :" + isSimAbsent());
            }
        }, 7000);
    }

    private boolean isSimAbsent() {
        return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT;
    }

    class BatteryValueReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int currentPowerValue = intent.getExtras().getInt("level") * 100 / intent.getExtras().getInt("scale");
                Log.d(TAG, "voltage = : " + intent.getExtras().getInt("voltage"));
                if (intent.getExtras().getInt("voltage") > 6500 && animationDrawable != null) {
                    Log.d(TAG, "while voltge above 6.5V, stop play charging animation!");
                    animationDrawable.stop();
                    updatePowerDisplay();
                }
                currentPower = currentPowerValue;
                powerPercentTv.setText(currentPowerValue + "%");
                Log.d(TAG, "currentPowerValue = : " + currentPowerValue);
                if (isCharging) {
                    if (currentPowerValue == 100 && animationDrawable != null) {
                        animationDrawable.stop();
                        updatePowerDisplay();
                    }
                } else {
                    updatePowerDisplay();
                }
            }
        }
    }


    /**
     * @author lihaizhou
     * @createtime 2017.9.30
     * @describe 注册监听器, 比如power, Sim卡状态变化
     */
    private void registerListener() {
        //订阅消息
        MsgManager.getMsgManagerInstance().registerListener(MainActivity.class, this);

        /**
         *  @author lihaizhou
         *  @createtime 2017.9.30
         *  @describe 注册Power监听器, 监测当前电量值变化以及是否充电状态
         */
        PowerUtils.registerPowerListener(this, new BatteryReceiver.BatteryReceiverListener() {
            @Override
            public void currentPower(int power) {
                Log.d(TAG, "power:" + power);
                /*currentPower = power;
                powerPercentTv.setText(power + "%");
                if (isCharging) {
                    if (currentPower == 100 && animationDrawable != null) {
                        animationDrawable.stop();
                        updatePowerDisplay();
                    }
                } else {
                    updatePowerDisplay();
                }*/
            }
        });

        /**
         *  @author lihaizhou
         *  @createtime 2017.11.29
         *  @describe 注册Functionlist监听器
         */
        LauncherService.registerFunctionListener(new LauncherService.FunctionListChangeListener() {
            @Override
            public void updateFunctionlist() {
                Log.d(TAG, "MainActivity updateFunctionlist begin!");
                initLauncherApps();
            }
        });

        /**
         *  @author lihaizhou
         *  @createtime 2017.9.30
         *  @describe 注册SIM卡状态监听器, 监测SIM插拔状态
         */
        SimCardStateChangeReceiver.registerSimListener(new SimCardStateChangeReceiver.SimStateChangeListener() {
            @Override
            public void isSimCardValid(boolean isSimCardValid) {
                Log.d(TAG, "SimCardStateChangeReceiver: isSimCardValid:" + isSimCardValid);
                if (isSimCardValid) {
                    Log.d(TAG, "SimCardStateChangeReceiver: current SimCard is on,so do cancelShutDownTimer()" + shutDownTimer);
                    cancelShutDownTimer();
                } else if (!isSimCardValid && !isCharging) {
                    Log.d(TAG, "SimCardStateChangeReceiver: current SimCard is absent and charge state is off,so do startShutDownTimer()" + shutDownTimer + ",shutdownTickTime = " + shutdownTickTime);
                    if (shutDownTimer == null) {
                        startShutDownTimer();
                    }
                }
                if (isSimCardValid && !isAirplanModeOn()) {
                    singalStrengthImg.setImageResource(signalStrengthImgList[4]); //while sim is on, set default level is 4
                } else {
                    singalStrengthImg.setImageResource(signalStrengthImgList[0]); //while sim is absent, set level is 0
                }
            }
        });

        systemUiPanel.addPanelSlideListener(new SystemUiPanel.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SystemUiPanel.PanelState previousState, SystemUiPanel.PanelState newState) {
                Log.d(TAG, "onPanelStateChanged " + newState);
                if (newState == SystemUiPanel.PanelState.HIDDEN) {
                    weatherPager.setCurrentItem(0);
                } else if (newState == SystemUiPanel.PanelState.EXPANDED) {
                    updateNetworkInfo(NetworkUtils.getNetWorkUtilsInstance().getNetWorkInfo(MainActivity.this));
                    fixedThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            //pengzonghong add start
                            lastTime = Settings.System.getLong(getContentResolver(), "last pull weather time", 0);
                            currentTime = System.currentTimeMillis();
                            Log.d(TAG, "[weather]lastTime= " + lastTime + "  currenttime= " + currentTime);
                            if (lastTime == 0) {
                                String today_weather = Settings.System.getString(getContentResolver(), "today_weather");
                                Log.d(TAG, "[weather]lastTime=0,today_weather:" + today_weather + "isLoginOK():" + isLoginOK());
                                if (((today_weather == null) || (today_weather == "") || (today_weather.equals("empty"))) && isLoginOK()) {
                                    Log.d(TAG, "[weather]lastTime=0,update weather from status !!!");
                                    WeatherInfo weatherinfo = new WeatherInfo(MainActivity.this);
                                    weatherinfo.pullweatherinfostart();
                                }
                            } else {
                                if (((currentTime - lastTime) > 10800000) && NetworkUtils.getNetWorkUtilsInstance().isNetworkAvailable() && isLoginOK()) {
                                    Log.d(TAG, "[weather]status enter update weather 3 hour!!!");
                                    Settings.System.putLong(getContentResolver(), "last pull weather time", currentTime);
                                    WeatherInfo weatherinfo = new WeatherInfo(MainActivity.this);
                                    weatherinfo.pullweatherinfostart();
                                }
                            }
                            //pengzonghong add end
                        }
                    });

                    updateLoginState(NetworkUtils.getNetWorkUtilsInstance().getNetWorkInfo(MainActivity.this));
                    try {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, "[weather]NullPointerException happen!!!");
                    }
                    if (isCharging) {
                        if (currentPower == 100) {
                            updatePowerDisplay();
                        } else if (animationDrawable != null && currentPower != 100) {
                            playChargingAnimation();
                        }
                    } else {
                        updatePowerDisplay();
                    }
                }
            }
        });
    }

    /**
     * @author lihaizhou
     * @createtime 2017.09.20
     * @describe 注册广播
     */
    private void registerReceiver() {

        mNetWorkBroadCastReciver = new NetWorkBroadCastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(Constants.ACTION_IMS_STATE_CHANGED);
        registerReceiver(mNetWorkBroadCastReciver, intentFilter);

        powerBtnClickReceiver = new PowerBtnClickReceiver();
        final IntentFilter powerfilter = new IntentFilter();
        powerfilter.addAction(Intent.ACTION_SCREEN_OFF);
        powerfilter.addAction(Intent.ACTION_TIME_TICK);
        powerfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        powerfilter.addAction("com.xunlauncher.randomcode");
        powerfilter.addAction("com.xunlauncher.sos");
        powerfilter.addAction("com.xunlauncher.silence");
        registerReceiver(powerBtnClickReceiver, powerfilter);

        brightScreenReceiver = new BrightScreenReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.setPriority(1000);
        registerReceiver(brightScreenReceiver, filter);

        batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        batteryFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        //Add by xuzhonghong for show power connect or disconnect notification on 20180504 start
        batteryFilter.addAction("com.xxun.xunalarm.ringtone.action.STARTALARM");
        batteryFilter.addAction("com.xxun.xunalarm.ringtone.action.FINISHALARM");
        //Add by xuzhonghong for show power connect or disconnect notification on 20180504 end
        registerReceiver(batteryBroadcastReceiver, batteryFilter);

        IntentFilter batteryChargeFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = registerReceiver(null, batteryChargeFilter);
        if (batteryStatusIntent != null) {
            int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            if (isCharging) {
                Log.d(TAG, "first boot,check currentPower = :" + currentPower);
                showUserForbidActivity();
                SystemProperties.set("persist.sys.isUsbConfigured", "true");
                if (animationDrawable != null && currentPower != 100) {
                    Log.d(TAG, "start charging animation after boot");
                    playChargingAnimation();
                }
            } else {
                SystemProperties.set("persist.sys.isUsbConfigured", "false");
            }
        }
        IntentFilter airplanFilter = new IntentFilter();
        airplanFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        airplanFilter.addAction("com.xiaoxun.appstore.task.hiddenapp");
        registerReceiver(airplaneOrSilenceChangeReceiver, airplanFilter);

        batteryValueReceiver = new BatteryValueReceiver();
        IntentFilter powerValueFilter = new IntentFilter();
        powerValueFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryValueReceiver, powerValueFilter);

        loginBroadcastReceiver = new LoginBroadcastReceiver();
        IntentFilter loginFilter = new IntentFilter();
        loginFilter.addAction(Constants.ACTION_SESSION_PING_OK);
        loginFilter.addAction(Constants.ACTION_LOGIN_SUCCESS);
        loginFilter.addAction(Constants.ACTION_NET_SWITCH_SUCC);
        registerReceiver(loginBroadcastReceiver, loginFilter);
    }

    /**
     * @author lihaizhou
     * @createtime 2018.01.24
     * @describe 注册ContentObserver
     */
    private void registerObserver() {
        getContentResolver().registerContentObserver(Settings.System.getUriFor("ChatMissCount"), true, unReadMsgObserver);
        getContentResolver().registerContentObserver(Settings.System.getUriFor("clock_style"), true, clockStyleObserver);
    }

    /**
     * @author lihaizhou
     * @createtime 2018.01.24
     * @describe 语音未读消息监测, 更新icon图标
     */
    private ContentObserver unReadMsgObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            int currentMsgNum = Settings.System.getInt(getContentResolver(), "ChatMissCount", 0);
            Log.d(TAG, "unreadMsg num change!!,currentMsgNum = :" + currentMsgNum);
            if (currentMsgNum >= 0 && unreadMsgNum != currentMsgNum) {
                unreadMsgNum = currentMsgNum;
                if (launcherPagerAdapter != null) {
                    launcherPagerAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    //[add by jxring for silence disturb 2017.11.24 start ]
    private void sendLauncherOkBroadCast() {
        Intent silence = new Intent("com.xunlauncher.silence");
        sendBroadcast(silence);
    }
    //[add by jxring for silence disturb 2017.11.24 end! ]

    /**
     * @param isSame 比较前后两次应用列表是否一致,不一致则刷新列表
     * @author lihaizhou
     * @createtime 2017.11.26
     * @describe 设置Xunlauncher适配器, 当applist数目只有一个时即功能列表只有Launcher，此时禁止滑动
     */
    private void setPagerAdapter(boolean isSame) {
        Log.d(TAG, "setPagerAdapter appsList = " + appsList.size());
        if (appsList.size() == 1) {
            mLauncherViewPager.setSlide(false);
        } else {
            mLauncherViewPager.setSlide(true);
        }
        Settings.System.putString(getContentResolver(), "on_xunlauncher_homescreen", "true");
        if (appsList.size() != 0) {
            Constants.LAUNCHER_PAGE_DEFAULT_TIMES = (Constants.LAUNCHER_PAGE_MAX_SUM / appsList.size()) / 2;
        }
        if (launcherPagerAdapter == null) {
            launcherPagerAdapter = new LauncherPagerAdapter(this);
            launcherPagerAdapter.setOnPagerItemClickLitener(this);
            launcherPagerAdapter.setViews(appsList);
            mLauncherViewPager.setAdapter(launcherPagerAdapter);
            //[add by jxring for silence Mode 2017.12.19 start]
        } /*else {
                if (!isSame) {
                    launcherPagerAdapter.notifyDataSetChanged();
                }
            }*/
        mLauncherViewPager.setCurrentItem(appsList.size() * Constants.LAUNCHER_PAGE_DEFAULT_TIMES);
        LauncherApplication.setHomeScreenIndex(appsList.size() * Constants.LAUNCHER_PAGE_DEFAULT_TIMES);
        int currentMsgNum = Settings.System.getInt(getContentResolver(), "ChatMissCount", 0);
        Log.d(TAG, "setPagerAdapter,currentMsgNum = :" + currentMsgNum);
        launcherPagerAdapter.notifyDataSetChanged();
        //[add by jxring for silence Mode 2017.12.19 end!]
    }

    //add by jxring for silenceMode 2017.12.19 start
    private void registerSilenceSQL() {
        getContentResolver().registerContentObserver(Settings.System.getUriFor("SilenceList_result"), true, mSilenceResultObserver);
        if (com.xiaoxun.sdk.utils.Constant.PROJECT_NAME.equals("SW760"))
            getContentResolver().registerContentObserver(Settings.System.getUriFor("start_backcall"), true, backgroundCallStartObserver);
    }

    final private ContentObserver mSilenceResultObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            initLauncherApps();
        }
    };

    //add by mayanjun for background call;20190904;
    final private ContentObserver backgroundCallStartObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "backgroundCallStartObserver! notify Launcher to update lastBgCallTimeBackup = " + lastBgCallTimeBackup);
            if (launcherPagerAdapter != null) {
                try {
                    long startTime = Settings.System.getLong(getContentResolver(), "start_backcall");

                    if (startTime != -1) {
                        //if the bgcall not end, do not reset the slid Flag;
                        if (lastBgCallTimeBackup == -1) {
                            isSlideBackup = mLauncherViewPager.getSlide();
                            mLauncherViewPager.setSlide(false);
                        }
                        launcherPagerAdapter.setBackgroundCallStart(startTime);
                    } else {
                        mLauncherViewPager.setSlide(isSlideBackup);
                    }

                    lastBgCallTimeBackup = startTime;
                } catch (SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void SilenceModeAdapter() {
        if (appsList == null) {
            appsList = new ArrayList<Integer>();
        } else {
            appsList.clear();
        }
        Integer[] allIconList = {R.drawable.ic_launcher, R.drawable.xiaoxun_silencemode};
        appsList.addAll(Arrays.asList(allIconList));
        if (launcherPagerAdapter == null) {
            launcherPagerAdapter = new LauncherPagerAdapter(this);
            launcherPagerAdapter.setOnPagerItemClickLitener(this);
        }
        launcherPagerAdapter.setViews(appsList);
        mLauncherViewPager.setAdapter(launcherPagerAdapter);
        mLauncherViewPager.setCurrentItem(appsList.size() * Constants.LAUNCHER_PAGE_DEFAULT_TIMES);
        LauncherApplication.setHomeScreenIndex(appsList.size() * Constants.LAUNCHER_PAGE_DEFAULT_TIMES);
    }

    boolean isPkglimit(String pkg) {
        String mlist = android.provider.Settings.System.getString(getContentResolver(), "prevent_appList");
        if (mlist == null)
            mlist = "com.xxun.xunimgrec#com.xxun.watch.xunbrain.x2#com.xxun.duer.dcs#com.xxun.watch.xunbrain.c3#com.xxun.xunwordsrec";
        String[] mapplist = mlist.split("#");
        for (String mpkg : mapplist) {
            if (mpkg.equals(pkg)) {
                return true;
            }
        }
        return false;
    }
    //[add by jxring for SilenceMode 2017.12.19  end!]

    /**
     * @author lihaizhou
     * @createtime 2017.11.26
     * @describe 点击launcher上应用图标打开应用, 前后两次点击需间隔超过500ms
     */
    @Override
    public void onItemClick(int position) {
        long now = System.currentTimeMillis();
        long intervalTime = now - this.mLastTime;
        if (intervalTime > 500 || intervalTime < 0) {
            this.mLastTime = now;
            Log.i(TAG, "onItemClick, position = " + position);
            if (appsList != null && position < appsList.size()) {
                PackageManager packageManager = getPackageManager();
                //String[] packageNameList = new String[];
                //int[] appiconList = new int[];

                TypedArray typedArray = getResources().obtainTypedArray(R.array.appiconList);
                int length = typedArray.length();
                int[] appiconList = new int[length];
                for (int i = 0; i < length; i++) {
                    appiconList[i] = typedArray.getResourceId(i, 0);
                }
                typedArray.recycle();

                String[] packageNameList = getResources().getStringArray(R.array.applists);
                Boolean isNetwrokOk = NetworkUtils.getNetWorkUtilsInstance().isNetworkAvailable();
                Boolean isBinded = SystemProperties.getBoolean("persist.sys.isbinded", false);
                Boolean isUnderLTEMode = SystemProperties.getBoolean("persist.sys.lteswitchon", false);
                //*/ xiaoxun.zhangweinan, 20180122. for high AP temperature contol
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 10:52:04 +0800
                Boolean isHighTemper = ((SystemProperties.getInt("persist.sys.xxun.aptemper", 2) == Constants.XUN_TEMPER_HEAT)
                        || (SystemProperties.getInt("persist.sys.xxun.aptemper", 2) == Constants.XUN_TEMPER_OVERHEAT));
 */
                Boolean isHighTemper = ((SystemProperties.getInt("persist.sys.xxun.aptemper", -1) >= Constants.XUN_TEMPER_HEAT)
                        || (SystemProperties.getInt("persist.sys.xxun.aptemper", -1) >= Constants.XUN_TEMPER_OVERHEAT));
// End of longcheer:lishuangwei
                //*/
                Log.d(TAG, "isNetwrokOk = :" + isNetwrokOk + ",isBinded = :" + isBinded + ",isHighTemper = :" + isHighTemper);
                for (int i = 0; i < appiconList.length; i++) {
                    if (appsList.get(position) == appiconList[i]) {
                        //*/ xiaoxun.zhangweinan, 20180122. for high AP temperature contol
                        if (SystemProperties.getBoolean("persist.sys.temper.warning", false) && isHighTemper && appsList.get(position).intValue() != R.drawable.settings) {
                            warnForbidEnterDialog();
                        } else {
                            if ((appsList.get(position).intValue() == R.drawable.phone || appsList.get(position).intValue() == R.drawable.voice_msg) && !isBinded
                                    && !XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_CTA_TEST) {
                                if (isNetwrokOk) {
                                    if (isLoginOK()) {
                                        showQrcodeDialogFragment();
                                    } else {
                                        showLoginAlertDialog();
                                    }
                                } else {
                                    if (isSimAbsent()) {
                                        showAlertDialog(Constants.SIM_ALERT_DIALOG);
                                    } else {
                                        showLoginAlertDialog();
                                    }
                                }
                            } else {
                                if (MemoryUtils.getAvailableInternalMemorySize(this) < 30 && appsList.get(position).intValue() == R.drawable.appstore) {
                                    Log.d(TAG, "current system availMemory is too low");
                                    showLowMemDialogFragment();
                                } else {
                                    if (appsList.get(position).intValue() == R.drawable.wear_tencent && getSharedPreferences(qqNoticeSharePreName, 0).getBoolean(isQQFirstLaunch, true)) {
                                        showAlertDialog(Constants.QQ_NOTICE_DIALOG);
                                        getSharedPreferences(qqNoticeSharePreName, 0).edit().putBoolean(isQQFirstLaunch, false).apply();
                                    } else {
                                        try {
                                            String packageName = packageNameList[i];
                                            if (isUnderLTEMode && ("com.xxun.watch.xunbrain.x2".equals(packageName)
                                                    || "com.xxun.watch.xunbrain.c3".equals(packageName)
                                                    || "com.xiaoxun.englishdailystudy".equals(packageName)
                                                    || "com.xxun.xunimgrec".equals(packageName)
                                                    || "com.tencent.qqlite".equals(packageName)
                                                    || "com.xxun.xunwordsrec".equals(packageName)
                                                /*
                                                    delete by guohongcheng_20180514
                                                    此部分逻辑在XunGallery中实现
                                                 */
                                                    /*|| "com.xxun.xungallery".equals(packageName)*/
                                                    || "com.xxun.duer.dcs".equals(packageName))) {
                                                if (true/*!NetworkUtils.getNetWorkUtilsInstance().isWifiContected(this)*/) {
                                                    if (mXiaoXunNetworkManager == null) {
                                                        mXiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
                                                    }
                                                    mLTEModeRequiredCounter++;
                                                    mXiaoXunNetworkManager.requireLTEMode("xiaoxun.Launcher");
                                                }
                                            }
                                            //[add by jxring for prevent_addition 2018.6.8 start]
                                            if (msp == null)
                                                msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
                                            //boolean mCurrentPrevent = msp.getBoolean("isCurrentPrevent",false);
                                            boolean mCurrentPrevent = android.provider.Settings.System.getInt(getContentResolver(), "isCurrentPrevent", 0) == 1 ? true : false;
                                            Log.d("zhj", "onClickItem > mCurrentPrevent =" + mCurrentPrevent);
                                            long Free_time = android.provider.Settings.System.getInt(getContentResolver(), "prevent_preventDur", 900) * 1000;
                                            if (Free_time < 0) Free_time = 900 * 1000;
                                            int app_fcm_onoff = android.provider.Settings.System.getInt(getContentResolver(), "fcm_onoff", 1);
                                            boolean app_switch = (app_fcm_onoff == 0 ? false : true);//0:off;1:on;
                                            if (mCurrentPrevent && app_switch && isPkglimit(packageName)) {
                                                //long mMarkTime = msp.getLong("marktime",-1);
                                                long mMarkTime = android.provider.Settings.System.getLong(getContentResolver(), "marktime", 0);
                                                Log.d("zhj", "onClickItem > mMarkTime =" + mMarkTime);
                                                if ((System.currentTimeMillis() - mMarkTime) < Free_time) {
                                                    warnPreventEnterDialog(Free_time - (System.currentTimeMillis() - mMarkTime));
                                                } else {
                                                    //SharedPreferences.Editor medit = msp.edit();
                                                    //medit.putBoolean("isCurrentPrevent", false);
                                                    //medit.commit();
                                                    android.provider.Settings.System.putInt(getContentResolver(), "isCurrentPrevent", 0);

                                                    Log.d(TAG, "ghc packageNameList[i] 11" + packageNameList[i]);
                                                    // add by guohongcheng_20190111 start
                                                    // 启动支付宝应用时，先进入应用锁界面，输入密码后才能进入支付宝
                                                    if ("com.eg.android.AlipayGphone".equals(packageName)) {
                                                        Log.d(TAG, "start AlipayGphone..");
                                                        inputPwdAli();
                                                    } else if ("com.xxun.xungallery".equals(packageName)) {
                                                        Log.d(TAG, "ghc start xungallery sendBroadcast MEDIA_SCANNER_SCAN_DIR");
                                                        File file = new File("/storage/emulated/0/DCIM/Camera/");
                                                        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR", Uri.fromFile(file)));

                                                        startActivity(packageManager.getLaunchIntentForPackage(packageNameList[i]));
                                                    } else {
                                                        startActivity(packageManager.getLaunchIntentForPackage(packageNameList[i]));
                                                    }
                                                    // add by guohongcheng_20190111 end
                                                    // startActivity(packageManager.getLaunchIntentForPackage(packageNameList[i]));
                                                }


                                            } else {
                                                //点击小爱的时候判断一下，如果当前在监控模式下，即关闭监控模式。
                                                if ("com.xxun.watch.xunbrain.x2".equals(packageName)
                                                        || "com.xxun.watch.xunbrain.c3".equals(packageName)) {
                                                    String value = android.provider.Settings.System.getString(getContentResolver(), "watch_subaction");
                                                    if (value != null) {
                                                        if (value.equals("WatchMode")) {
                                                            //end call;
                                                            Intent endcall = new Intent("com.xunend.call.action");
                                                            endcall.setPackage("com.xiaoxun.dialer");
                                                            sendBroadcast(endcall);
                                                        }
                                                    }
                                                }

                                                //[add by jxring end!]
                                                Log.d(TAG, "ghc packageNameList[i] 22" + packageNameList[i]);
                                                // add by guohongcheng_20190111 start
                                                // 启动支付宝应用时，先进入应用锁界面，
                                                // 输入密码后才能进入支付宝
                                                if ("com.eg.android.AlipayGphone".equals(packageName)) {
                                                    Log.d(TAG, "start AlipayGphone..");
                                                    inputPwdAli();
                                                } else if ("com.xxun.xungallery".equals(packageName)) {
                                                    Log.d(TAG, "ghc start xungallery sendBroadcast MEDIA_SCANNER_SCAN_DIR");
                                                    File file = new File("/storage/emulated/0/DCIM/Camera/");
                                                    sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR", Uri.fromFile(file)));

                                                    startActivity(packageManager.getLaunchIntentForPackage(packageNameList[i]));
                                                } else if ("com.xxun.watch.xunbrain.c3".equals(packageName) && (Constant.PROJECT_NAME.equals("SW706"))) {
                                                    startActivity(packageManager.getLaunchIntentForPackage("com.xxun.watch.xunbrain.x2"));
                                                } else if ("com.xxun.xunimgrec".equals(packageName) || "com.xxun.xunwordsrec".equals(packageName)) {
                                                    if (isNetwrokOk) {
                                                        startActivity(packageManager.getLaunchIntentForPackage(packageNameList[i]));
                                                    } else {
                                                        showLoginAlertDialog();
                                                    }
                                                } else {
                                                    startActivity(packageManager.getLaunchIntentForPackage(packageNameList[i]));
                                                }
                                                // add by guohongcheng_20190111 end
                                                //startActivity(packageManager.getLaunchIntentForPackage(packageNameList[i]));
                                            }
                                        } catch (NullPointerException ex) {
                                            Toast.makeText(MainActivity.this, "NullPointerException happen while click icon!" + packageNameList[i], Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                            }
                        }
                        //*/
                    }
                }
            }
        }
    }

    /**
     * @author 郭洪成 guohongcheng
     * 进入支付宝前，先要输入应用锁密码
     */
    public void inputPwdAli() {
        // int isSetPwd = android.provider.Settings.System.getInt(LauncherApplication.getInstance().getContentResolver(), Constant.SETTING_PAY_ONOFF, 0);
        // PayDialogFragment payDialogFragment = new PayDialogFragment();
        // payDialogFragment.show(getSupportFragmentManager(), "payFragment");
        // modify by guohongcheng_20181012 start
        // 通过activity的方式启动
        // 为了支持小爱同学等第三方应用启动应用锁，需要添加activity
        // 添加activity后，之前的启动方式会有错误
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName cn = new ComponentName("com.xxun.xunlauncher",
                "com.xxun.xunlauncher.activity.AliPwdActivity");
        intent.setComponent(cn);
        startActivity(intent);
        // modify by guohongcheng_20181012 end
    }

    //*/ xiaoxun.zhangweinan, 20180203. for AP temperature warning
    public void warnForbidEnterDialog() {
        View view = View.inflate(this, R.layout.forbid_enter_app, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (forbidEnterDialog == null) {
            forbidEnterDialog = builder.create();
            forbidEnterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            forbidEnterDialog.show();
            setDialogStyleTemp(view, forbidEnterDialog);
        } else {
            forbidEnterDialog.show();
        }
        ImageButton callcancel = (ImageButton) view.findViewById(R.id.cancel);
        callcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (forbidEnterDialog != null) {
                    forbidEnterDialog.dismiss();
                    forbidEnterDialog = null;
                }
            }
        });
        mCountDwonTimer.start();
    }

    private CountDownTimer mCountDwonTimer = new CountDownTimer(COUNTDOWNTIMER_DURATION, 100000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mCountDwonTimer.cancel();
            if (forbidEnterDialog != null) {
                forbidEnterDialog.dismiss();
                forbidEnterDialog = null;
            }
        }
    };
    //*/


    //[add by jxring for pervent_addition dialog 2018.6.8 start]
    AlertDialog mPrevent_dialog = null;

    public void warnPreventEnterDialog(long needtime) {
        View view = View.inflate(this, R.layout.xiaoxun_prevent, null);
        TextView mtextview = (TextView) view.findViewById(R.id.prevent);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mPrevent_dialog = builder.create();
        mPrevent_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        int count_second = Integer.valueOf(String.valueOf(needtime)) / 1000;
        long hour = count_second % (24 * 60 * 60) / (60 * 60);
        long min = count_second % (60 * 60) / 60;
        long second = count_second % 60;
        if (min > 0) {
            mtextview.setText(getResources().getString(R.string.prevent_dialog_show_two, String.valueOf(min)));
        } else {
            mtextview.setText(getResources().getString(R.string.prevent_dialog_show_three, String.valueOf(second)));
        }


        mPrevent_dialog.show();
        setDialogStyleTemp(view, mPrevent_dialog);
        ImageButton callcancel = (ImageButton) view.findViewById(R.id.cancel);
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

    /**
     * @author lihaizhou
     * @time 2017.12.13
     * @describe 当手表未绑定且网络ok情况下, 点击百科问答, 电话, 交友应用, 显示二维码窗口
     */
    public void showQrcodeDialogFragment() {
        QrcodeDialogFragment qrcodefragment = new QrcodeDialogFragment(MainActivity.this);
        qrcodefragment.show(getFragmentManager(), "qrcode");
    }

    /**
     * @author lihaizhou
     * @time 2018.03.01
     * @describe 当手表内存不足30M时，点击应用弹出内存不足提示框，禁止进入
     */
    public void showLowMemDialogFragment() {
        LowMemDialogFragment lowMemDialogFragment = new LowMemDialogFragment();
        lowMemDialogFragment.show(getFragmentManager(), "lowmem");
    }

    /**
     * @author lihaizhou
     * @createtime 2019.01.21
     * @describe Login fail Alert dialog
     */
    private void showLoginAlertDialog() {
        View loginFailView = getLayoutInflater().inflate(R.layout.login_fail, null);
        ImageView helpImg = (ImageView) loginFailView.findViewById(R.id.login_help);
        helpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginFailHelpDialog();
            }
        });
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        setDialogStyleTemp(loginFailView, dialog);
    }

    /**
     * @author lihaizhou
     * @createtime 2019.01.21
     * @describe login fail Help describe Dialog
     */
    private void showLoginFailHelpDialog() {
        View view = getLayoutInflater().inflate(R.layout.login_fail_help, null);
        loginhelpDialog = new AlertDialog.Builder(this).create();
        loginhelpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loginhelpDialog.show();
        setDialogStyleTemp(view, loginhelpDialog);
    }

    /**
     * @author lihaizhou
     * @createtime 2018.01.08
     * @describe Alert dialog
     */
    private void showAlertDialog(final int dialogType) {
        if (dialogType == Constants.SIM_ALERT_DIALOG) {
            alertView = getLayoutInflater().inflate(R.layout.sim_absent, null);
            ImageView helpImg = (ImageView) alertView.findViewById(R.id.sim_insert_help);
            helpImg.setImageResource(R.drawable.insert_sim_help);
            ImageView rebootWatch = (ImageView) alertView.findViewById(R.id.reboot_watch);
            helpImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (helpDialog == null) {
                        showInsertHelpDialog(dialogType);
                    } else {
                        helpDialog.show();
                    }
                }
            });
            rebootWatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("android.intent.action.REBOOT");
                    intent.putExtra("nowait", 1);
                    intent.putExtra("interval", 1);
                    intent.putExtra("window", 0);
                    sendBroadcast(intent);
                }
            });
        } else if (dialogType == Constants.QQ_NOTICE_DIALOG) {
            alertView = getLayoutInflater().inflate(R.layout.qq_first_alert, null);
        }
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (!isFinishing()) {
            dialog.show();
        }
        setDialogStyleTemp(alertView, dialog);
    }

    /**
     * @author lihaizhou
     * @createtime 2018.01.08
     * @describe Help describe Dialog
     */
    private void showInsertHelpDialog(int dialogType) {
        View view = getLayoutInflater().inflate(R.layout.insert_sim_help, null);
        helpDialog = new AlertDialog.Builder(this).create();
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        helpDialog.show();
        setDialogStyleTemp(view, helpDialog);
        TextView helpTv = (TextView) view.findViewById(R.id.insert_sim_tv);
        ImageView gigVew = (ImageView) view.findViewById(R.id.insert_gif);
        gigVew.setVisibility(View.GONE);

        if (dialogType == Constants.SERVER_LOGIN_FAIL) {
            helpTv.setText(R.string.login_fail_help);
            return;
        }
        
     /*   if(Constant.PROJECT_NAME.equals("SW707")){
           helpTv.setText(R.string.insert_sim_help_tv_707);
        } */

        if (Constant.PROJECT_NAME.equals("SW760")) {
            helpTv.setText(R.string.insert_sim_help_tv_760);
            gigVew.setVisibility(View.VISIBLE);
            //gigVew.setMovieResource(R.raw.sim_insert);
        }else if(Constant.PROJECT_NAME.equals("SW707")){
            helpTv.setText(R.string.insert_sim_help_tv_707);
            gigVew.setVisibility(View.VISIBLE);
        }

    }

    /**
     * @author lihaizhou
     * @createtime 2018.01.08
     * @describe Dialog style releated template
     */
    private void setDialogStyleTemp(View view, AlertDialog dialog) {
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(view);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        window.setAttributes(lp);
    }

    /**
     * @author lihaizhou
     * @time 2017.12.28
     * @class describe update power display
     */
    private void updatePowerDisplay() {
        int currentPowerLevel = PowerUtils.getCurrentPowerLevel(currentPower);
        powerImg.setImageResource(powerImgLists[currentPowerLevel]);
    }

    /**
     * @author lihaizhou
     * @time 2017.12.21
     * @class describe 在SIM卡不在位&未充电情况下,一小时到达时关机;期间有插入SIM或者充电,则定时复位
     */
    public class ShutDownTimer extends CountDownTimer {
        public ShutDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            shutdownTickTime = millisUntilFinished / 1000;
            Log.d(TAG, "onTick time is =: " + shutdownTickTime);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "ShutDownTimer finish countDown,and isSimAbsent() = :" + isSimAbsent());
            cancelShutDownTimer();
            if (!isMusicActive() && isSimAbsent()) {
                ShutDownUtils.getShutDownUtilsInstance().shutDownWatch();
            } else {
                startShutDownTimer();
            }
        }
    }

    /**
     * @author lihaizhou
     * @time 2018.03.30
     * @class describe judge current tellStory and other music weather playing on
     */
    public boolean isMusicActive() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        boolean isMusicActive = mAudioManager.isMusicActive();
        Log.d(TAG, "[isMusicActive] isMusicActive: " + isMusicActive);
        return isMusicActive;
    }


    /**
     * @author lihaizhou
     * @time 2017.12.21
     * @class describe 开始关机倒计时,只有在未插卡且未充电情况下
     */
    private void startShutDownTimer() {
        if (shutDownTimer == null) {
            shutDownTimer = new ShutDownTimer(Constants.SHUTDOWN_TOTAL_TIME, Constants.SHUTDOWN_INTERVAL);
        } else {
            shutDownTimer.cancel();
        }
        Log.d(TAG, "startShutDownTimer()");
        shutDownTimer.start();
    }

    /**
     * @author lihaizhou
     * @time 2017.12.21
     * @class describe 取消关机倒计时,当插卡或者充电触发
     */
    private void cancelShutDownTimer() {
        Log.d(TAG, "cancelShutDownTimer()" + shutDownTimer);
        if (shutDownTimer != null) {
            shutDownTimer.cancel();
        }
        shutDownTimer = null;
    }

    /**
     * @author lihaizhou
     * @time 2018.03.19
     * @class describe set alarm to close wifi after 5 min
     */
    public void setCloseWifiAlarm() {
        closeWifialarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, LauncherAlarmReceiver.class);
        intent.setAction(Constants.KEEP_WIFI_CONNECT);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, Constants.WIFI_CLOSE_TIME);
        closeWifialarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private boolean shouldKeepWifiConnect() {
        int keepvalue = Settings.System.getInt(getContentResolver(), "keep_wifi_connect", 1);
        if (keepvalue == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author lihaizhou
     * @time 2018.03.19
     * @class describe set alarm to close wifi after 5 min
     */
    public void cancelCloseWifiAlarm() {
        Log.d(TAG, "cancelCloseWifiAlarm pendingIntent = : " + pendingIntent);
        if (pendingIntent != null) {
            closeWifialarmManager.cancel(pendingIntent);
        }
    }

    /**
     * @author lihaizhou
     * @time 2018.03.07
     * @class describe get current wifi Strength
     */
    public int getWifiStrength() {
        if (wifiManager == null) {
            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            int strength = WifiManager.calculateSignalLevel(info.getRssi(), 4);
            Log.d(TAG, "strength = :" + strength);
            if (strength > 3) {
                strength = 3;
            }
            return strength;
        }
        return 3;

    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.30
     * @describe 接收网络状态改变的广播, 并更新状态栏信号类型
     */
    class NetWorkBroadCastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String networkName = NetworkUtils.getNetWorkUtilsInstance().getNetWorkInfo(context);
            Log.d(TAG, "action = :" + intent.getAction());
            if (Constants.ACTION_IMS_STATE_CHANGED.equals(intent.getAction())) {
                int isVolteOpend = Settings.Global.getInt(context.getContentResolver(), "volte_vt_enabled", 0);
                imsRegState = intent.getIntExtra(Constants.EXTRA_IMS_REG_STATE_KEY, 1);
                Log.d(TAG, "LauncherMainactivity imsRegState :" + imsRegState);
                if (imsRegState == 1) {
                    if (isVolteOpend == 0) {
                        ImsManager.setEnhanced4gLteModeSetting(context, false);
                    } else {
                        networkName = Constants.NETWORK_VOLTE;
                        Settings.System.putString(getContentResolver(), "volte_on", "true");
                    }
                } else {
                    Settings.System.putString(getContentResolver(), "volte_on", "false");
                }
            }
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                if (wifiManager == null) {
                    wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                }

                Log.d(TAG, "SCAN_RESULTS_AVAILABLE_ACTION");
                Log.d(TAG + " SCAN_RESULTS_AVAILABLE_ACTION", "shouldKeepWifiConnect=" + shouldKeepWifiConnect());
                if (!shouldKeepWifiConnect()) {
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
                    Log.d(TAG + " SCAN_RESULTS_AVAILABLE_ACTION", "existingConfigs=" + existingConfigs + " shouldKeepWifiConnect=" + shouldKeepWifiConnect()
                            + " size=" + existingConfigs.size() + "\n");
                    if (isCharging) {
                        if (existingConfigs.size() > 0) {
                            if (scanResults.size() > 0) {
                                for (WifiConfiguration existingConfig : existingConfigs) {
                                    Log.d(TAG + " getAvailableConnWifi", "existingConfig.SSID=" + existingConfig.SSID + "\n");
                                    if ("\"null\"".equals(existingConfig.SSID)) {
                                        continue;
                                    }
                                    for (ScanResult scanResult : scanResults) {
                                        Log.d(TAG + " getAvailableConnWifi", "scanResult.SSID=" + scanResult.SSID + "existingConfig.SSID=" + existingConfig.SSID +
                                                " removeDoubleQuotes(existingConfig.SSID)" + removeDoubleQuotes(existingConfig.SSID));
                                        if (scanResult.SSID.equals(removeDoubleQuotes(existingConfig.SSID))) {
                                            wifiManager.setWifiEnabled(true);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                Log.d(TAG, "SCAN_RESULTS_AVAILABLE_ACTION" + " wifi scanResults  null");
                            }
                        } else {
                            Log.d(TAG, "SCAN_RESULTS_AVAILABLE_ACTION" + "wifi save null");
                        }
                    }
                    if (wifi_close_15_min) {
                        closeWifiScan();
                    }

                }
            }
            updateNetworkInfo(networkName);
            Log.d(TAG, "NetWorkBroadCastReciver = :" + networkName);
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2017.12.27
     * @describe 接收屏幕亮屏, 灭屏广播
     */
    class BrightScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //judge current page whether in the call.if so,do not show charging page. by lihaizhou on 2018.01.31 begin
            TelephonyManager telecomManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            boolean isOnCallStateIdle = telecomManager.getCallState() == TelephonyManager.CALL_STATE_IDLE ? true : false;
            //judge current page whether in the call.if so,do not show charging page. by lihaizhou on 2018.01.31 end
            //Modify by xuzhonghong for XUN_SW710_A01-410 on 20180323 start
            String isRunintest = SystemProperties.get("persist.sys.runin");
            Log.d(TAG, "isOnIdle = " + isOnCallStateIdle + ",isRunintest = " + isRunintest);
            //Modify by xuzhonghong for XUN_SW710_A01-410 on 20180323 end
            if (!isCharging && !isMusicActive() && isSimAbsent()) {
                startShutDownTimer();
            }
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Log.d(TAG, "screen on");
                cancelCloseWifiAlarm();

                if (isCharging) {
                    if (currentPower < 100) {
                        showCurrentPowerValueToast("当前电量:" + currentPower + "%");
                    } else {
                        if (isChargingFull()) {
                            showCurrentPowerValueToast("充电完成");
                        } else {
                            showCurrentPowerValueToast("当前电量:" + currentPower + "%");
                        }
                    }
                }
                // if the bgcall startflasg not released,
                if (lastBgCallTimeBackup != -1 && isOnCallStateIdle && launcherPagerAdapter != null) {
                    mLauncherViewPager.setSlide(isSlideBackup);
                    lastBgCallTimeBackup = -1;
                }

            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Log.d(TAG, "screen off");

                Log.d(TAG, "shouldKeepWifiConnect = " + shouldKeepWifiConnect() + "isCharging=" + isCharging);
                if (!isCharging) {
                    if (!shouldKeepWifiConnect()) {
                        if (NetworkUtils.getNetWorkUtilsInstance().isWifiContected(context)) {
                            setCloseWifiAlarm();
                        } else {
                            if (wifiManager == null) {
                                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            }
                            if (wifiManager.isWifiEnabled()) {
                                setCloseWifiAlarm();
                            }
                        }
                    }
                }


                if (systemUiPanel.getPanelState() == SystemUiPanel.PanelState.EXPANDED) {
                    Log.d(TAG, "screen off and current Panel is expanded,so we need close it");
                    systemUiPanel.setPanelState(SystemUiPanel.PanelState.HIDDEN);
                }
            }
        }
    }


    class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean underLTEMode = SystemProperties.getBoolean("persist.sys.lteswitchon", false);
            //Add by xuzhonghong for show power connect or disconnect notification on 20180504 start
            if ("com.xxun.xunalarm.ringtone.action.STARTALARM".equals(action)) {
                Log.d(TAG, "STARTALARM.. ");
                SystemProperties.set("persist.sys.isAlarmRing", "true");
            } else if ("com.xxun.xunalarm.ringtone.action.FINISHALARM".equals(action)) {
                Log.d(TAG, "FINISHALARM.. ");
                SystemProperties.set("persist.sys.isAlarmRing", "false");
            }
            telecomManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            isIncall = android.provider.Settings.System.getString(context.getContentResolver(), "isIncall");
            Log.d(TAG, "telecomManager.getCallState() = " + telecomManager.getCallState() + ",isIncall = " + isIncall);
            //Add by xuzhonghong for show power connect or disconnect notification on 20180504 end
            if (Intent.ACTION_POWER_CONNECTED.equals(action)) {

                Log.d(TAG, "ACTION_POWER_CONNECTED");
                //open wifi
                if (wifiManager == null) {
                    wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                }
                Log.d(TAG, "ACTION_POWER_CONNECTED isWifiEnabled=" + wifiManager.isWifiEnabled());
                Log.d(TAG, "ACTION_POWER_CONNECTED shouldKeepWifiConnect = " + shouldKeepWifiConnect());
                if (!shouldKeepWifiConnect()) {
                    if (!wifiManager.isWifiEnabled()) {//wifi not open
                        if (getAvailableConnWifi(wifiManager)) {
//                    wifiManager.setWifiEnabled(true);
                            scanWifi();//扫描WiFi
                        }
                    }
                }


                //Add by xuzhonghong for XUN_SW730_A01-408 on 20180524 start
                SystemProperties.set("persist.sys.isUsbConfigured", "true");
                //Add by xuzhonghong for XUN_SW730_A01-408 on 20180524 end
                Log.d(TAG, "power connected,do cancelshutdownTimer, underLTEMode = " + underLTEMode);

                cancelShutDownTimer();
                isCharging = true;
                if (animationDrawable != null && currentPower != 100) {
                    playChargingAnimation();
                }

                UploadStatusUtils.getUploadStatusUtilsInstance().setChargeStatus("1");
                UploadStatusUtils.getUploadStatusUtilsInstance().uploadStatus(context);
                //Add by xuzhonghong for show power connect or disconnect notification on 20180504 start
                isAlarmRing = SystemProperties.get("persist.sys.isAlarmRing", "false");
                Log.d(TAG, "isAlarmRing " + isAlarmRing);
                if ((telecomManager.getCallState() == android.telephony.TelephonyManager.CALL_STATE_IDLE)
                        && !"true".equals(isIncall)
                        && !"true".equals(isAlarmRing)) {
                    showPowerStatusToast("充电器已连接", R.drawable.powerconnect);
                }
                //Add by xuzhonghong for show power connect or disconnect notification on 20180504 end
                if (telecomManager.getCallState() == android.telephony.TelephonyManager.CALL_STATE_IDLE) {
                    showUserForbidActivity();
                }
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                closeUserForbidActivity();
                Log.d(TAG, "power disconnected,and current isSimAbsent() = : " + isSimAbsent());
                if (isSimAbsent()) {
                    startShutDownTimer();
                }
                isCharging = false;
                if (animationDrawable != null) {
                    animationDrawable.stop();
                    updatePowerDisplay();
                }

                UploadStatusUtils.getUploadStatusUtilsInstance().setChargeStatus("0");
                UploadStatusUtils.getUploadStatusUtilsInstance().uploadStatus(context);
                //Add by xuzhonghong for show power connect or disconnect notification on 20180504 start
                isAlarmRing = SystemProperties.get("persist.sys.isAlarmRing", "false");
                Log.d(TAG, "isAlarmRing " + isAlarmRing);
                if ((telecomManager.getCallState() == android.telephony.TelephonyManager.CALL_STATE_IDLE)
                        && !"true".equals(isIncall)
                        && !"true".equals(isAlarmRing)) {
                    showPowerStatusToast("充电器已移除", R.drawable.powerdisconnect);
                }
                //Add by xuzhonghong for show power connect or disconnect notification on 20180504 end

                //Add by xuzhonghong for XUN_SW730_A01-408 on 20180524 start
                SystemProperties.set("persist.sys.isUsbConfigured", "false");
                //Add by xuzhonghong for XUN_SW730_A01-408 on 20180524 end
            }
        }
    }

    public void scanWifi() {
        //openWifi();
        if (!wifiManager.isWifiEnabled()) {
            openWifiScan();
        } else {
            wifiManager.startScan();
        }
    }

    public void openWifiScan() {
        Log.d(TAG, "openWifiScan: ");
        //add for 9820e
        try {
            wifi_close_15_min = true;
            Settings.Global.putInt(this.getContentResolver(), "wifi_scan_always_enabled", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 3; i++) {
            wifiManager.startScan();
        }
    }

    public void closeWifiScan() {
        Log.d(TAG, "closeWifiScan: ");
        //add for 9820e
        try {
            wifi_close_15_min = false;
            Settings.Global.putInt(this.getContentResolver(), "wifi_scan_always_enabled", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean getAvailableConnWifi(WifiManager wifiManager) {
        Log.d(TAG, " getAvailableConnWifi");
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        Log.d(TAG + " getAvailableConnWifi", "existingConfigs=" + existingConfigs + " size=" + existingConfigs.size() + "\n");
        if (existingConfigs.size() > 0) {
            Log.d(TAG + " getAvailableConnWifi", "save wifi is not null");
            return true;
        } else {
            Log.d(TAG + " getAvailableConnWifi", "save wifi is null");
            return false;
        }
    }

    private String removeDoubleQuotes(String string) {
        if (string == null) {
            return "null";
        }
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"')
                && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }

    //do not run these code for the time being, use xiaoxunnetService's isLoginOk() instead
    class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "MainActivity receive login success");
            String action = intent.getAction();
            if (Constants.ACTION_LOGIN_SUCCESS.equals(action)
                    || Constants.ACTION_SESSION_PING_OK.equals(action)
                    || Constants.ACTION_NET_SWITCH_SUCC.equals(action)) {
                updateLoginState(NetworkUtils.getNetWorkUtilsInstance().getNetWorkInfo(MainActivity.this));
            }
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.06.08
     * @describe 弹出全局禁用窗口，只有在插入充电器下才会弹出
     */
    private void showUserForbidActivity() {
        try {
            if (SystemProperties.get("ro.build.type").equals("user") && !"true".equals(Settings.System.getString(getContentResolver(), "isMidtest")) && Settings.System.getInt(getContentResolver(), "is_localprop_exist") == 0) {
                Intent intent = new Intent(this, UserForbidActivity.class);
                startActivity(intent);
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.06.08
     * @describe 关闭全局禁用窗口，只有在移除充电器下才会触发
     */
    private void closeUserForbidActivity() {
        try {
            if (SystemProperties.get("ro.build.type").equals("user") && !"true".equals(Settings.System.getString(getContentResolver(), "isMidtest")) && Settings.System.getInt(getContentResolver(), "is_localprop_exist") == 0) {
                LauncherApplication.destoryActivity("UserForbidActivity");
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.03.21
     * @describe play Charging Animation
     */
    private void playChargingAnimation() {
        powerImg.setImageResource(R.drawable.power_charging);
        animationDrawable = (AnimationDrawable) powerImg.getDrawable();
        animationDrawable.start();
    }

    /**
     * @author lihaizhou
     * @createtime 2018.02.27
     * @describe while insert USB,show current power value like notification
     */
    private void showCurrentPowerValueToast(String chargingStatus) {
        new StyleableToast
                .Builder(this)
                .backgroundColor(Color.BLACK)
                .text(chargingStatus)
                .length(Toast.LENGTH_SHORT)
                .cornerRadius(20)
                .iconResLeft(R.drawable.charing)
                .show();
    }

    /**
     * @author xuzhonghong
     * @createtime 2018.05.04
     * @describe while insert or pull out the USB or AC charger,show power status like notification
     */
    private void showPowerStatusToast(String powerStatus, int imageSource) {
        //modify by liaoyi 移除插拔充电器时弹出提示框
//        new StyleableToast
//                .Builder(this)
//                .backgroundColor(Color.BLACK)
//                .text(powerStatus)
//                .length(Toast.LENGTH_SHORT)
//                .cornerRadius(20)
//                .iconResLeft(imageSource)
//                .show();
        //end
        int charging_ringtone = android.provider.Settings.System.getInt(getContentResolver(), "charging_ringtone", 0);
        if (charging_ringtone != 1) {
            return;
        }
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
            } catch (IllegalStateException e) {
                mMediaPlayer = null;
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = MediaPlayer.create(this, R.raw.power);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "Media play complete, release the source.");
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        });
    }

    /**
     * @author lihaizhou
     * @createtime 2018.04.11
     * @describe judge current charging status
     */
    private boolean isChargingFull() {
        IntentFilter batteryChargeFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = registerReceiver(null, batteryChargeFilter);
        if (batteryStatusIntent != null) {
            int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                return false;
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                return true;
            }
        }
        return false;
    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.30
     * @describe 接收信号强度改变的广播, 并更新状态栏信号强度
     */
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        //@RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            currentSignalLevel = signalStrength.getLevel();
            Log.d(TAG, "currentSignalLevel =:" + currentSignalLevel + ",isSimAbsent() = " + isSimAbsent());
            if (isAirplanModeOn()) {
                singalStrengthImg.setImageResource(signalStrengthImgList[0]);
            } else {
                if (!isSimAbsent()) {
                    singalStrengthImg.setImageResource(signalStrengthImgList[currentSignalLevel]);
                }
            }

            fixedThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    //Modify by xuzhonghong for upload watch signal level at 20180129 start
                    UploadStatusUtils.getUploadStatusUtilsInstance().setSignalLevel(Integer.toString(currentSignalLevel));
                    //Modify by xuzhonghong for upload watch signal level at 20180129 end
                }
            });
        }
    };

    /**
     * @author lihaizhou
     * @createtime 2018.01.03
     * @describe 接收飞行模式状态变更广播
     */
    private BroadcastReceiver airplaneOrSilenceChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                Log.d(TAG, "airplaneChangeReceiver isAirplanModeOn = :" + isAirplanModeOn());
                //[add by jxring for rest_mode 2018.01.04 start]
                String result = Settings.System.getString(getContentResolver(), "SilenceList_result");
                boolean SilenceList_result = (result == null ? false : Boolean.parseBoolean(result));
                //[add by jxring for rest_mode 2018.01.04 end!]
                if (isAirplanModeOn()) {
                    Log.d(TAG, "airplane mode on,so we need send broadcast to qq!");
                    sendBroadcastFromLauncher("com.tencent.qqlite.watch.COMPLETELY_CLOSE_MODE");
                    singalStrengthImg.setImageResource(signalStrengthImgList[0]);//update signal's display as soon as receive airplan change
                    if (!SilenceList_result && mIndicator != null)
                        mIndicator.setBackgroundResource(R.drawable.xiaoxun_rest_mode);//[add by jxring for rest_mode 2018.01.04 start]
                } else {
                    if (!SilenceList_result && mIndicator != null)
                        mIndicator.setBackgroundResource(R.drawable.mark_up);//[add by jxring for rest_mode 2018.01.04 start]
                }

            } else if (action.equals("com.xiaoxun.appstore.task.hiddenapp")) {
                Log.d("zhj", "hiddenapp >>>>   initLauncherApps ");
                initLauncherApps();
            }
        }
    };

    /**
     * @author lihaizhou
     * @createtime 2018.01.03
     * @describe send广播
     */
    public void sendBroadcastFromLauncher(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    /**
     * @author lihaizhou
     * @createtime 2018.01.03
     * @describe 判断飞行模式状态
     */
    private boolean isAirplanModeOn() {
        boolean isAirplaneModeOn = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        return isAirplaneModeOn;
    }

    /**
     * @param networkName 网络类型,比如wifi,2G,3G,4G
     * @author lihaizhou
     * @createtime 2017.10.30
     * @describe 更新状态栏显示, 包括网络类型
     */
    private void updateNetworkInfo(String networkName) {
        Log.d(TAG, "updateNetworkInfo networkName =:" + networkName);
        if (Constants.NETWORK_UNAVIABLE.equals(networkName)) {
            signalTypeTv.setText("");
            powerSavingImg.setVisibility(View.GONE);
            if (isSimAbsent()) {
                netWorkTypeImg.setVisibility(View.VISIBLE);
                netWorkTypeImg.setImageResource(R.drawable.network_unavailable);
            } else {
                netWorkTypeImg.setVisibility(View.INVISIBLE);
            }
        } else if (Constants.NETWORK_WIFI.equals(networkName)) {
            netWorkTypeImg.setVisibility(View.VISIBLE);
            if (isLoginOK()) {
                netWorkTypeImg.setImageResource(wifiLoginStrengthImgList[getWifiStrength()]);
            } else {
                netWorkTypeImg.setImageResource(wifiStrengthImgList[getWifiStrength()]);
            }
            signalTypeTv.setText("");
            powerSavingImg.setVisibility(View.GONE);
        } else if (Constants.NETWORK_VOLTE.equals(networkName) || Constants.NETWORK_4G.equals(networkName) || Constants.NETWORK_2G.equals(networkName) || Constants.NETWORK_3G.equals(networkName)) {
            netWorkTypeImg.setVisibility(View.VISIBLE);
            if (Constants.NETWORK_VOLTE.equals(networkName) && NetworkUtils.getNetWorkUtilsInstance().isWifiContected(this)) {
                Log.d(TAG, "network change to 4G+,but current wifi is still connected,so show wifidisplay icon");
                if (isLoginOK()) {
                    netWorkTypeImg.setImageResource(wifiLoginStrengthImgList[getWifiStrength()]);
                } else {
                    netWorkTypeImg.setImageResource(wifiStrengthImgList[getWifiStrength()]);
                }
                signalTypeTv.setText("");
                powerSavingImg.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "mobile network is on, our current isLoginOK() = :" + isLoginOK());
                if (isLoginOK()) {
                    netWorkTypeImg.setImageResource(R.drawable.connec_date);
                } else {
                    netWorkTypeImg.setImageResource(R.drawable.connec_date_);
                }
                if (Constants.NETWORK_VOLTE.equals(networkName)) {
                    signalTypeTv.setVisibility(View.VISIBLE);
                    signalTypeTv.setText("4G+");
                    powerSavingImg.setVisibility(View.GONE);
                } else {
                    //Modified by lihaizhou for smartPowerSaving,if smartPowerSaving switch on,hide 2G text and show 2G powerSaving icon begin 20180719
                    int imsState = Settings.System.getInt(getContentResolver(), "ImsState", 0);
                    Log.d("lihaizhou", "networkName = " + networkName + ", isonsmart = :" + SystemProperties.getBoolean("persist.sys.lteswitchon", false) + ",imsState = " + imsState);
                    if (Constants.NETWORK_2G.equals(networkName) && SystemProperties.getBoolean("persist.sys.lteswitchon", false)) {
                        signalTypeTv.setVisibility(View.GONE);
                        powerSavingImg.setVisibility(View.VISIBLE);
                    } else if (Constants.NETWORK_4G.equals(networkName) && imsState == 1) {
                        signalTypeTv.setVisibility(View.VISIBLE);
                        signalTypeTv.setText("4G+");
                        powerSavingImg.setVisibility(View.GONE);
                    } else {
                        signalTypeTv.setVisibility(View.VISIBLE);
                        signalTypeTv.setText(networkName);
                        powerSavingImg.setVisibility(View.GONE);
                    }
                    //Modified by lihaizhou for smartPowerSaving,if smartPowerSaving switch on,hide 2G text and show 2G powerSaving icon end 20180719
                }
            }
            cancelShutDownTimer();
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.04.26
     * @describe only update login state
     */
    public void updateLoginState(String networkName) {
        if (Constants.NETWORK_WIFI.equals(networkName)) {
            if (isLoginOK()) {
                netWorkTypeImg.setImageResource(wifiLoginStrengthImgList[getWifiStrength()]);
            } else {
                netWorkTypeImg.setImageResource(wifiStrengthImgList[getWifiStrength()]);
            }
        } else if (Constants.NETWORK_VOLTE.equals(networkName) || Constants.NETWORK_4G.equals(networkName) || Constants.NETWORK_2G.equals(networkName) || Constants.NETWORK_3G.equals(networkName)) {
            if (Constants.NETWORK_VOLTE.equals(networkName) && NetworkUtils.getNetWorkUtilsInstance().isWifiContected(this)) {
                if (isLoginOK()) {
                    netWorkTypeImg.setImageResource(wifiLoginStrengthImgList[getWifiStrength()]);
                } else {
                    netWorkTypeImg.setImageResource(wifiStrengthImgList[getWifiStrength()]);
                }
            } else {
                if (isLoginOK()) {
                    netWorkTypeImg.setImageResource(R.drawable.connec_date);
                } else {
                    netWorkTypeImg.setImageResource(R.drawable.connec_date_);
                }
            }
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.04.01
     * @describe return result whether login server success
     */
    public boolean isLoginOK() {
        if (mXiaoXunNetworkManager == null) {
            mXiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        }
        return mXiaoXunNetworkManager.isLoginOK();
    }

    /*
      add by guohongcheng_20180329
      处理Camera相关标志位
     */
    private void handleCamera() {
        String isCameraUsing = android.provider.Settings.System.getString(
                getContentResolver()
                , "camera_isRecording");
        // 退出Camera后，标志位没有被重置
        if ("true".equals(isCameraUsing)) {
            // setScreenOffTime(8000);
            setIsCameraUsing("false");
            // 通知 消息应用，当前已经退出了Camera 
            Intent intent = new Intent("com.xxun.xuncamera.quitrecord");
            sendBroadcast(intent);
        }
    }

    /*
      add by guohongcheng_20180329
      设置系统灭屏时间
     */
    // private void setScreenOffTime(int paramInt) {
    //     Log.d(TAG, "setScreenOffTime " + paramInt);
    //     try {
    //         android.provider.Settings.System.putInt(
    //                 getContentResolver(),
    //                 android.provider.Settings.System.SCREEN_OFF_TIMEOUT,
    //                 paramInt);
    //     } catch (Exception localException) {
    //         localException.printStackTrace();
    //     }
    // }

    /*
      add by guohongcheng_20180329
      设置标志位，表明当前已经进入相机界面，远程后台拍照功能无法使用
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

    /**
     * @author lihaizhou
     * @createtime 2017.09.30
     * @describe 重写系统方法, 判断手势滑动, 当下拉时且满足条件打开状态;
     * 处于Launcher界面时，上滑进入小爱同学
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return systemUiPanel.onLauncherTouchEvent(ev, super.dispatchTouchEvent(ev));
    }

    /**
     * @author lihaizhou
     * @createtime 2018.05.23
     * @describe 此属性存储了当前是否高温，2为正常，7为超过43度，3为超过46度
     */
    //
    private boolean isUnderHighTemp() {
        int currentTmp = Integer.valueOf(SystemProperties.get("persist.sys.xxun.aptemper"));
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 10:56:09 +0800
        if (currentTmp == 7 || currentTmp == 3) {
 */
        if (currentTmp >= Constants.XUN_TEMPER_OVERHEAT) {
// End of longcheer:lishuangwei
            return true;
        }
        return false;
    }

    /*
     * msg依据名称来选择不同行为 by lihaizhou 20190312
     */
    @Override
    public void onMsg(String msg) {
        if ("showClockDialog".equals(msg)) {
            showClockDialog();
        }

    }

    /**
     * @author lihaizhou
     * @createtime 2017.09.20
     * @describe 长按待机界面后弹出选择框的接口回调方法
     * 修改：730手表长按进入小爱快捷页面  by huangyouyang at 2018.05.21
     */
    public void showClockDialog() {

        // do nothing when in silence , add by huangyouyang at 2018.05.23
        String result = Settings.System.getString(getContentResolver(), "SilenceList_result");
        boolean SilenceList_result = (result == null ? false : Boolean.parseBoolean(result));
        if (SilenceList_result)
            return;

        //[add by jxring for prevent_addition 2018.6.11 start]
        if (msp == null) msp = getSharedPreferences("Contact_data", Context.MODE_PRIVATE);
        //boolean mCurrentPrevent = msp.getBoolean("isCurrentPrevent",false);
        boolean mCurrentPrevent = android.provider.Settings.System.getInt(getContentResolver(), "isCurrentPrevent", 0) == 1 ? true : false;
        Log.d("zhj", "showClockDialog > mCurrentPrevent =" + mCurrentPrevent);
        long Free_time = android.provider.Settings.System.getInt(getContentResolver(), "prevent_preventDur", 900) * 1000;
        int app_fcm_onoff = android.provider.Settings.System.getInt(getContentResolver(), "fcm_onoff", 1);
        boolean app_switch = (app_fcm_onoff == 0 ? false : true);//0:off;1:on;
        if (mCurrentPrevent && app_switch) {
            //long mMarkTime = msp.getLong("marktime",-1);
            long mMarkTime = android.provider.Settings.System.getLong(getContentResolver(), "marktime", 0);
            Log.d("zhj", "showClockDialog > mMarkTime =" + mMarkTime);
            if ((System.currentTimeMillis() - mMarkTime) < Free_time) {
                warnPreventEnterDialog(Free_time - (System.currentTimeMillis() - mMarkTime));
            } else {
                //SharedPreferences.Editor medit = msp.edit();
                //medit.putBoolean("isCurrentPrevent", false);
                //medit.commit();
                android.provider.Settings.System.putInt(getContentResolver(), "isCurrentPrevent", 0);
            }
        }
        //[add by jxring for prevent_addition 2018.6.11 end!]
        if (!mCurrentPrevent) {
            timeStyleFragment.show(getFragmentManager(), "showClockDialog");
        }
    }


    /**
     * @author lihaizhou
     * @createtime 2017.11.26
     * @describe 监测待机时钟样式存储的值变化, 比如:用户长按待机界面,选择其它样式后,时钟样式存储值变化,此时会走这里,通知适配器数据更新！
     */

    private ContentObserver clockStyleObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (launcherPagerAdapter != null) {
                launcherPagerAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * @author lihaizhou
     * @createtime 2017.09.20
     * @describe 下拉状态栏中天气部分ViewPager的回调方法
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    /**
     * @author lihaizhou
     * @createtime 2017.09.20
     * @describe 下拉状态栏中天气部分ViewPager的回调方法
     */
    @Override
    public void onPageSelected(int position) {
        adapter.notifyDataSetChanged();//每次切换时主动通知刷新数据
        indicatorLayout.getChildAt(indicatorNum).setEnabled(false);
        indicatorLayout.getChildAt(position).setEnabled(true);
        indicatorNum = position;
    }

    /**
     * @author lihaizhou
     * @createtime 2017.09.20
     * @describe 下拉状态栏中天气部分ViewPager的回调方法
     */
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LauncherApplication.getHomeScreenIndex();
        if (hasFocus && mLauncherViewPager.getCurrentItem() % appsList.size() == 0) {
            Log.d(TAG, "onWindowFocusChanged:" + "true, and current index = : " + mLauncherViewPager.getCurrentItem() % appsList.size());
            Settings.System.putString(getContentResolver(), "on_xunlauncher_homescreen", "true");
        } else {
            Settings.System.putString(getContentResolver(), "on_xunlauncher_homescreen", "false");
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2017.11.28
     * @describe 系统回调方法, 栈顶重新回到该Activity时调用, 当前Activity为SingleInstance模式
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "MainActivity onNewIntent");
        // add by guohongcheng_20180329
        // 返回桌面时，设置亮屏时间8000 + 设置camera标志位 false
        handleCamera();
        Settings.System.putString(getContentResolver(), "on_xunlauncher_homescreen", "true");
        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_right);
        mLauncherViewPager.setCurrentItem(appsList.size() * Constants.LAUNCHER_PAGE_DEFAULT_TIMES);
        LauncherApplication.setHomeScreenIndex(appsList.size() * Constants.LAUNCHER_PAGE_DEFAULT_TIMES);
        if (systemUiPanel.getPanelState() == SystemUiPanel.PanelState.EXPANDED) {
            systemUiPanel.setPanelState(SystemUiPanel.PanelState.HIDDEN);
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.20
     * @describe 系统回调方法, 因系统层右滑代替返回键，Launcher中需屏蔽返回键,否则会导致Launcher中的viewpager右滑状态错乱
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mSilenceResultObserver);
        getContentResolver().unregisterContentObserver(backgroundCallStartObserver);
        getContentResolver().unregisterContentObserver(unReadMsgObserver);
        getContentResolver().unregisterContentObserver(clockStyleObserver);
        unregisterReceiver(powerBtnClickReceiver);
        unregisterReceiver(batteryBroadcastReceiver);
        unregisterReceiver(brightScreenReceiver);
        unregisterReceiver(batteryValueReceiver);
        unregisterReceiver(loginBroadcastReceiver);
        LauncherService.unRegisterFunctionListener();
        unregisterReceiver(mNetWorkBroadCastReciver);
        unregisterReceiver(airplaneOrSilenceChangeReceiver);
        SimCardStateChangeReceiver.unRegisterSimListener();
        PowerUtils.unRegisterPowerListener(this);
        cancelShutDownTimer();
        //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 start
        mainacticvityInstance = null;
        //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 end
        MsgManager.getMsgManagerInstance().unregisterlistener(MainActivity.class);
    }

}
