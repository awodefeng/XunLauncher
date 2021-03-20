package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.xxun.xunlauncher.R;
import android.widget.ImageView;

/**
 * @author lihaizhou
 * @time 2017.12.18
 * @class describe 方形时钟样式
 */
public class SquareAnalogClock extends BaseWatchView {

    private ImageView mImgDayMonth;
    private ImageView mImgDayWeek;
    private int[] mMonthDays;
    private int[] mWeekDays;
    private WatchRotateView mImgSwordHour;
    private WatchRotateView mImgSwordMinute;
    private WatchRotateView mImgSwordSecond;

    private final String TAG = "SquareAnalogClock";

    public SquareAnalogClock(Context context) {
        this(context,null);
    }

    public SquareAnalogClock(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SquareAnalogClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mWeekDays = new int[7];
        mMonthDays = new int[31];
        initDatas(context);
        initView(LayoutInflater.from(context).inflate(R.layout.square_clock, this, true));
    }

    protected void initDatas(Context context) {

        TypedArray montTypedArray = context.getResources().obtainTypedArray(R.array.month_days);
        int len = montTypedArray.length();
        mMonthDays = new int[len];
        for (int i = 0; i < len; i++){
            mMonthDays[i] = montTypedArray.getResourceId(i, 0);
        }
        montTypedArray.recycle();

        TypedArray weekTypedArray = context.getResources().obtainTypedArray(R.array.weeks);
        int weekLen = weekTypedArray.length();
        mWeekDays = new int[weekLen];
        for (int i = 0; i < weekLen; i++){
            mWeekDays[i] = weekTypedArray.getResourceId(i, 0);
        }
        weekTypedArray.recycle();
        BaseWatchView.setRefreshClockTime(1000);
    }

    protected void initView(View rootView) {
        Log.d(TAG, "initView");
        mImgDayWeek = (ImageView) rootView.findViewById(R.id.week_day);
        mImgDayMonth = (ImageView) rootView.findViewById(R.id.month_day);
        mImgSwordHour = (WatchRotateView) rootView.findViewById(R.id.sword_hour);
        mImgSwordMinute = (WatchRotateView) rootView.findViewById(R.id.sword_minute);
        mImgSwordSecond = (WatchRotateView) rootView.findViewById(R.id.sword_second);
        updateMM(minute);
        updateHH(hour);
        updateSS(second);
        updateWeekDay();
    }

    private void updateWeekDay() {
        this.mImgDayWeek.setImageResource(mWeekDays[dayOfWeek]);
        if (dayOfMonth != 31) {
            mImgDayMonth.setImageResource(mMonthDays[dayOfMonth]);
        } else {
            mImgDayMonth.setImageResource(mMonthDays[0]);
        }
    }

    @Override
    protected void updateMonth(int month) {
        super.updateMonth(month);
        updateWeekDay();
    }

    @Override
    protected void updateWeakday(int weekday) {
        super.updateWeakday(weekday);
        updateWeekDay();
    }

    @Override
    protected void updateDD(int day) {
        super.updateDD(day);
        updateWeekDay();
    }

    @Override
    protected void updateHH(int h) {
        super.updateHH(h);
        mImgSwordHour.setDegree(((float) (h * 30)) + (((float) minute) / 2.0f));
    }

    @Override
    protected void updateMM(int m) {
        super.updateMM(m);
        mImgSwordMinute.setDegree(((float) (m * 6)) + (((float) second) / 10.0f));
    }

    @Override
    protected void updateSS(int second) {
        super.updateSS(second);
        mImgSwordSecond.setDegree((float) (second * 6));
    }
}
