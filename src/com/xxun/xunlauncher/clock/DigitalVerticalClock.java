package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.xxun.xunlauncher.R;
import android.widget.ImageView;
import android.widget.TextView;

public class DigitalVerticalClock extends BaseWatchView {

    private String timeState;
    private int[] hourResources;
    private ImageView mImgH_1;
    private ImageView mImgH_2;
    private ImageView mImgM_1;
    private ImageView mImgM_2;
    private TextView mTxtAM;
    private TextView mTxtDay;
    private int[] minuteResources;
    private String[] weekDays;

    private static final String TAG = "DigitalVerticalClock";

    public DigitalVerticalClock(Context context) {
        this(context,null);
    }

    public DigitalVerticalClock(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public DigitalVerticalClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        hourResources = new int[10];
        minuteResources = new int[10];
        weekDays = new String[7];
        initDatas();
        initView(LayoutInflater.from(context).inflate(R.layout.digital_vertical_clock, this, true));
    }

    protected void initDatas() {
        hourResources = new int[]{
                R.drawable.digital_h_0,R.drawable.digital_h_1,R.drawable.digital_h_2,R.drawable.digital_h_3,R.drawable.digital_h_4,R.drawable.digital_h_5,R.drawable.digital_h_6,
                R.drawable.digital_h_7,R.drawable.digital_h_8,R.drawable.digital_h_9};
        minuteResources = new int[]{
                R.drawable.digital_m_0,R.drawable.digital_m_1,R.drawable.digital_m_2,R.drawable.digital_m_3,R.drawable.digital_m_4,R.drawable.digital_m_5,R.drawable.digital_m_6,
                R.drawable.digital_m_7,R.drawable.digital_m_8,R.drawable.digital_m_9};
        weekDays = new String[]{"周日","周一","周二","周三","周四","周五","周六"};
	BaseWatchView.setRefreshClockTime(1000);
    }

    protected void initView(View rootView) {
        Log.d(TAG, "initView");
        mImgH_1 = (ImageView) rootView.findViewById(R.id.h_1);
        mImgH_2 = (ImageView) rootView.findViewById(R.id.h_2);
        mImgM_1 = (ImageView) rootView.findViewById(R.id.m_1);
        mImgM_2 = (ImageView) rootView.findViewById(R.id.m_2);
        mTxtDay = (TextView) rootView.findViewById(R.id.txt_day);
        mTxtAM = (TextView) rootView.findViewById(R.id.txt_am);
        updateMM(this.minute);
        updateHH(this.hour);
        updateButtomText();
    }

    private void updateButtomText() {
        String mm = "";
        if (this.month < 10) {
            mm = "0" + this.month;
        } else {
            mm = "" + this.month;
        }
        String day = "";
        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = "" + dayOfMonth;
        }
        mTxtDay.setText(mm + "月" + day + "日" + " " + this.weekDays[this.dayOfWeek] + " "+timeState);
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
            mImgH_1.setImageResource(hourResources[1]);
            mImgH_2.setImageResource(hourResources[2]);
            return;
        }
        mImgH_1.setImageResource(hourResources[h_1]);
        mImgH_2.setImageResource(hourResources[h_2]);
    }

    @Override
    protected void updateMM(int m) {
        super.updateMM(m);
        int m_2 = m % 10;
        mImgM_1.setImageResource(minuteResources[m / 10]);
        mImgM_2.setImageResource(minuteResources[m_2]);
    }
}
