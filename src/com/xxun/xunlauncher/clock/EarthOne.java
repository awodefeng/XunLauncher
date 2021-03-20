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


/**
 * @author jxring
 * @time 2019.01.04
 * @class describe earth one
 */
public class EarthOne extends BaseWatchView {

    private String timeState;
    private ImageView mImgH_1;
    private ImageView mImgH_2;
    private ImageView mImgM_1;
    private ImageView mImgM_2;
    private TextView mTxtAM;
    private TextView mTxtDay;
    private TextView mWeekDay;
    private int[] numberResources;
    private String[] weekDays;

    private static final String TAG = "EarthOne";

    public EarthOne(Context context) {
        this(context,null);
    }

    public EarthOne(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public EarthOne(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        numberResources = new int[10];
        weekDays = new String[7];
        initDatas();
        initView(LayoutInflater.from(context).inflate(R.layout.earth_one, this, true));
    }

    protected void initDatas() {
        numberResources = new int[]{
                R.drawable.earth0,R.drawable.earth1,R.drawable.earth2,R.drawable.earth3,R.drawable.earth4,R.drawable.earth5,R.drawable.earth6,
                R.drawable.earth7,R.drawable.earth8,R.drawable.earth9};
        weekDays = new String[]{"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
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
        if(mTxtDay != null) mTxtDay.setText(mm + "月" + day + "日");
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
            mImgH_1.setImageResource(numberResources[1]);
            mImgH_2.setImageResource(numberResources[2]);
            return;
        }
        mImgH_1.setImageResource(numberResources[h_1]);
        mImgH_2.setImageResource(numberResources[h_2]);
    }

    @Override
    protected void updateMM(int m) {
        super.updateMM(m);
        int m_2 = m % 10;
        mImgM_1.setImageResource(numberResources[m / 10]);
        mImgM_2.setImageResource(numberResources[m_2]);
    }
}
