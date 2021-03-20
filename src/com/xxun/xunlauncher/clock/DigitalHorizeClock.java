package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.xxun.xunlauncher.R;
import android.widget.ImageView;
import android.widget.TextView;

public class DigitalHorizeClock extends BaseWatchView {

    private final String[] AM_PM;
    private int[] timedigits;
    private ImageView mImgH_1;
    private ImageView mImgH_2;
    private ImageView mImgM_1;
    private ImageView mImgM_2;
    private TextView mTxtDay;
    private TextView mTxtWeek;
    private String[] weekDays;

    private static final String TAG = "DigitalVerticalClock";

    public DigitalHorizeClock(Context context) {
        this(context,null);
    }

    public DigitalHorizeClock(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public DigitalHorizeClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        timedigits = new int[10];
        weekDays = new String[7];
        AM_PM = new String[2];
        initDatas();
        initView(LayoutInflater.from(context).inflate(R.layout.digit_horize_style, this, true));
    }

    protected void initDatas() {
        timedigits = new int[]{
                R.drawable.digit0,R.drawable.digit1,R.drawable.digit2,R.drawable.digit3,R.drawable.digit4,R.drawable.digit5,R.drawable.digit6,
                R.drawable.digit7,R.drawable.digit8,R.drawable.digit9
        };
        weekDays = new String[]{"周日","周一","周二","周三","周四","周五","周六"};
        AM_PM[0] = "上午";
        AM_PM[1] = "下午";
	BaseWatchView.setRefreshClockTime(1000);
    }

    protected void initView(View rootView) {
        Log.d(TAG, "initView");
        mImgH_1 = (ImageView) rootView.findViewById(R.id.h_1);
        mImgH_2 = (ImageView) rootView.findViewById(R.id.h_2);
        mImgM_1 = (ImageView) rootView.findViewById(R.id.m_1);
        mImgM_2 = (ImageView) rootView.findViewById(R.id.m_2);
        mTxtDay = (TextView) rootView.findViewById(R.id.txt_day);
        mTxtWeek = (TextView) rootView.findViewById(R.id.week);
        updateMM(this.minute);
        updateHH(this.hour);
        updateButtomText();
    }

    private void updateButtomText() {
        String year = this.year+"";

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
        mTxtDay.setText(year+"年"+mm + "月" + day + "日");
        mTxtWeek.setText(this.weekDays[this.dayOfWeek]);
    }

    @Override
    protected void updateYear(int year) {
        super.updateYear(year);
        updateButtomText();
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
    protected void updateHH(int h) {
        super.updateHH(h);
        int h_1 = h / 10;
        int h_2 = h % 10;
        if (h_1 == 0 && h_2 == 0) {
            mImgH_1.setImageResource(timedigits[1]);
            mImgH_2.setImageResource(timedigits[2]);
            return;
        }
        mImgH_1.setImageResource(timedigits[h_1]);
        mImgH_2.setImageResource(timedigits[h_2]);
    }

    @Override
    protected void updateMM(int m) {
        super.updateMM(m);
        int m_2 = m % 10;
        mImgM_1.setImageResource(timedigits[m / 10]);
        mImgM_2.setImageResource(timedigits[m_2]);
    }
}
