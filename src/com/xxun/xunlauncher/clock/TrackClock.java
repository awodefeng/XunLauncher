package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.xxun.xunlauncher.R;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.XiaoXunUtil;


public class TrackClock extends BaseWatchView{
    private String timeState;
    private ImageView mImgH_1;
    private ImageView mImgH_2;
    private ImageView mImgM_1;
    private ImageView mImgM_2;
    private TextView mTxtAM;
    private TextView mTxtDay;
    private TextView mWeekDay;
    private int[] numberHourResources;
    private int[] numberMinResources;
    private String[] weekDays;

    private static final String TAG = "TrackClock";

    public TrackClock(Context context) {
        this(context,null);
    }

    public TrackClock(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public TrackClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        numberMinResources = new int[10];
        numberHourResources = new int[10];
        weekDays = new String[7];
        initDatas();
        initView(LayoutInflater.from(context).inflate(R.layout.track, this, true));
    }

    protected void initDatas() {
        numberMinResources = new int[]{
                R.drawable.track_minute0,R.drawable.track_minute1,R.drawable.track_minute2,R.drawable.track_minute3,R.drawable.track_minute4,R.drawable.track_minute5,R.drawable.track_minute6,
                R.drawable.track_minute7,R.drawable.track_minute8,R.drawable.track_minute9};
        numberHourResources = new int[]{
                R.drawable.track_hour0,R.drawable.track_hour1,R.drawable.track_hour2,R.drawable.track_hour3,R.drawable.track_hour4,R.drawable.track_hour5,
                R.drawable.track_hour6, R.drawable.track_hour7,R.drawable.track_hour8,R.drawable.track_hour9};
        weekDays = new String[]{"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
        BaseWatchView.setRefreshClockTime(60000);
    }

    protected void initView(View rootView) {
        Log.d(TAG, "initView");
        mImgH_1 = (ImageView) rootView.findViewById(R.id.h_1);
        mImgH_2 = (ImageView) rootView.findViewById(R.id.h_2);
        mImgM_1 = (ImageView) rootView.findViewById(R.id.m_1);
        mImgM_2 = (ImageView) rootView.findViewById(R.id.m_2);
        mTxtDay = (TextView) rootView.findViewById(R.id.txt_day);
        mWeekDay = (TextView) rootView.findViewById(R.id.week_day);
        updateMM(this.minute);
        updateHH(this.hour);
        updateButtomText();
    }

    private void updateButtomText() {
        String mm = "";
        if (this.month < 10) {
            mm = ""+ this.month;
        } else {
            mm = "" + this.month;
        }
        String day = "";
        if (dayOfMonth < 10) {
            day = "" + dayOfMonth;
        } else {
            day = "" + dayOfMonth;
        }
        if(mTxtDay != null) mTxtDay.setText(mm + "月" + day +"日");
        if(mWeekDay != null) mWeekDay.setText(this.weekDays[this.dayOfWeek]);
    }

    @Override
    protected void updateMonth(int month) {
        super.updateMonth(month);
        updateButtomText();
    }

    @Override
    protected void updateWeakday(int weekday) {
        super.updateWeakday(weekday);
        updateButtomText();
    }

    @Override
    protected void updateDD(int day) {
        super.updateDD(day);
        updateButtomText();
    }

    @Override
    protected void updateAMPM(String timestate) {
        super.updateAMPM(timestate);
        this.timeState = timestate;
        updateButtomText();
    }

    @Override
    protected void updateHH(int h) {
        super.updateHH(h);
        int h_1 = h / 10;
        int h_2 = h % 10;
        if (h_1 == 0 && h_2 == 0) {
            mImgH_1.setImageResource(numberHourResources[1]);
            mImgH_2.setImageResource(numberHourResources[2]);
            return;
        }
        mImgH_1.setImageResource(numberHourResources[h_1]);
        mImgH_2.setImageResource(numberHourResources[h_2]);
    }

    @Override
    protected void updateMM(int m) {
        super.updateMM(m);
        int m_2 = m % 10;
        mImgM_1.setImageResource(numberMinResources[m / 10]);
        mImgM_2.setImageResource(numberMinResources[m_2]);
    }
}
