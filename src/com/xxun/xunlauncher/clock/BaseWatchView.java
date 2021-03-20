package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import java.util.Calendar;
import com.xxun.xunlauncher.R;
import java.util.TimeZone;
import com.xxun.xunlauncher.Constants;
import android.app.AlarmManager;
import java.util.GregorianCalendar;
import android.provider.Settings;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xiaoxun.sdk.utils.Constant;
import android.provider.Settings;

/**
 * @author lihaizhou
 * @time 2017.12.18
 * @class describe 时钟样式基类,，定义基础的行为供子类复写
 */

public class BaseWatchView extends FrameLayout {

    private Context mContext;

    protected String timeState;
    protected int dayOfMonth;
    protected int dayOfWeek;
    protected int hour;
    protected int minute;
    protected int month;
    protected int second;
    protected int year;
    protected boolean isAttached = true;
    protected boolean isScreenON = true;
    private static long refreshClockIntervalTime = 1000;    

    protected int step;
    protected int card;

    private final String TAG = "BaseWatchView";

    private LauncherApplication launcherApplication;

    Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            long delayTime = refreshClockIntervalTime - (SystemClock.uptimeMillis() % 1000);
            if (BaseWatchView.this.isAttached && BaseWatchView.this.isScreenON) {
                BaseWatchView.this.postDelayed(this, delayTime);
            }
            BaseWatchView.this.updateTime();
        }
    };
    
    public static void setRefreshClockTime(long refreshIntervalTime) {
	refreshClockIntervalTime = refreshIntervalTime;	
    }   

    public BaseWatchView(Context context) {
        super(context);
        mContext =context;
        initData();
    }

    public BaseWatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext= context;
        initData();
    }

    public BaseWatchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext= context;
        initData();
    }

    protected void initData() {
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        year = calendar.get(1);
        month = calendar.get(2) + 1;
        dayOfMonth = calendar.get(5);
        dayOfWeek = calendar.get(7) - 1;
        hour = calendar.get(10);
        minute = calendar.get(12);
        second = calendar.get(13);
        step = 0;
        card = 0;
        post(this.mRunable);
    }

    protected void updateWeakday(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        Log.d(this.TAG, " screenState = " + screenState);
        if (screenState == 0) {
            this.isScreenON = false;
            removeCallbacks(this.mRunable);
        } else if (screenState == 1) {
            this.isScreenON = true;
            post(this.mRunable);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(this.TAG, " onAttachedToWindow");
        this.isAttached = true;
        post(this.mRunable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(this.TAG, " onDetachedFromWindow");
        this.isAttached = false;
        removeCallbacks(this.mRunable);
    }

    private void updateTime() {
	Log.d(TAG,"BaseWatchView updateTime");
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(1);
        month = calendar.get(2) + 1;
        dayOfMonth = calendar.get(5);
        dayOfWeek = calendar.get(7) - 1;
	
	String formate = Settings.System.getString(LauncherApplication.getInstance().getContentResolver(),Constants.CLOCK_FORMATE_NAME);
        if("".equals(formate)||formate==null) {
             hour = calendar.get(10);
	     Settings.System.putString(LauncherApplication.getInstance().getContentResolver(),"clock_formate","12");
        }else {
	  if(Constants.CLOCK_TWELVE_FORMATE.equals(formate)){
	     hour = calendar.get(10);
	  }else{
	     hour = calendar.get(Calendar.HOUR_OF_DAY);
	  }
        }
	int twentyHour = calendar.get(Calendar.HOUR_OF_DAY);
	if(0<=twentyHour&&twentyHour<6){
            timeState = LauncherApplication.getInstance().getResources().getString(R.string.daybreak);
          }else if(6<=twentyHour&&twentyHour<12){
            timeState = LauncherApplication.getInstance().getResources().getString(R.string.morning);
          }else if (12<=twentyHour&&twentyHour<19){
            timeState = LauncherApplication.getInstance().getResources().getString(R.string.afternoon);
          }else if (19<=twentyHour&&twentyHour<24){
            timeState = LauncherApplication.getInstance().getResources().getString(R.string.evening);
          }

	if(Constant.PROJECT_NAME.equals("SW706")){
         step = launcherApplication.getStatisticsManager().getTodayStepStats();
        int weight =18;
        int height = 100;

        String getweight = Settings.System.getString(LauncherApplication.getInstance().getContentResolver(), "childweight");
        String getheight = Settings.System.getString(LauncherApplication.getInstance().getContentResolver(), "childHeight");

        if ((null != getweight) && !"0".equals(getweight) && (!"".equals(getweight)) && !"null".equals(getweight)) {
            weight =(int)Float.parseFloat(getweight);
        }

        if ((null != getheight) && !"0".equals(getheight) && (!"".equals(getheight)) && !"null".equals(getheight)) {
            height =(int)Float.parseFloat(getheight);
        }

        card = getCalorie(step,weight,height);
        updateStep(this.step);
        updateCard(this.card);
    }

        minute = calendar.get(12);
        second = calendar.get(13);
        updateSS(this.second);
        updateMM(this.minute);
        updateHH(this.hour);
        updateAMPM(this.timeState);
        updateDD(this.dayOfMonth);
        updateWeakday(this.dayOfWeek);
        updateMonth(this.month);
        updateYear(this.year);
    }

    protected void updateYear(int year) {
        this.year = year;
    }

    protected void updateMonth(int month) {
        this.month = month;
    }

    protected void updateDD(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    protected void updateAMPM(String timestate) {
        this.timeState = timestate;
    }

    protected void updateHH(int hour) {
        this.hour = hour;
    }

    protected void updateMM(int minute) {
        this.minute = minute;
    }

    protected void updateSS(int second) {
        this.second = second;
    }

    protected void updateStep(int step) {this.step = step;}

    protected void updateCard(int card) {this.card=card;};

    public static int getCalorie(int steps, int weight, int height) {
        return (int) Math.round(getDistance(steps, height) / 1000d * 1.036 * weight);
    }

    public static int getDistance(int steps, int height) {
        return (int) Math.round(steps * height * 0.45 * 0.011);
    }

}
