package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.xxun.xunlauncher.R;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @author lihaizhou
 * @time 2019.01.04
 * @class describe 线下默认表盘样式
 */
public class EarthThree extends BaseWatchView {

    private String timeState;
    private ImageView mImgH_1;
    private ImageView mImgH_2;
    private ImageView mImgM_1;
    private ImageView mImgM_2;
    private TextView mTxtAM;
    private TextView mTxtDay;
    private TextView mWeekDay;
    private int[] rightup;
    private int[] rightdown;
    private int[] leftup;
    private int[] leftdown;
    private String[] weekDays;

    private static final String TAG = "EarthThree";

    public EarthThree(Context context) {
        this(context,null);
    }

    public EarthThree(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public EarthThree(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        rightup = new int[10];
        rightdown = new int[10];
        leftup = new int[3];
        leftdown = new int[6];
        weekDays = new String[7];
        initDatas();
        initView(LayoutInflater.from(context).inflate(R.layout.earth_three, this, true));
    }

    protected void initDatas() {
        rightup = new int[]{
                R.drawable.rightup0,R.drawable.rightup1,R.drawable.rightup2,R.drawable.rightup3,R.drawable.rightup4,R.drawable.rightup5,R.drawable.rightup6,
                R.drawable.rightup7,R.drawable.rightup8,R.drawable.rightup9};
                
        rightdown = new int[]{
                R.drawable.rightdown0,R.drawable.rightdown1,R.drawable.rightdown2,R.drawable.rightdown3,R.drawable.rightdown4,R.drawable.rightdown5,R.drawable.rightdown6,
                R.drawable.rightdown7,R.drawable.rightdown8,R.drawable.rightdown9};
                
                
        leftup = new int[]{R.drawable.leftup0,R.drawable.leftup1,R.drawable.leftup2};
        
        leftdown = new int[]{R.drawable.leftdown0,R.drawable.leftdown1,R.drawable.leftdown2,R.drawable.leftdown3,R.drawable.leftdown4,R.drawable.leftdown5};
        
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
        mTxtDay.setText(mm + "月" + day + "日");
        mWeekDay.setText(this.weekDays[this.dayOfWeek]);
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
            mImgH_1.setImageResource(leftup[1]);
            mImgH_2.setImageResource(rightup[2]);
            return;
        }
        mImgH_1.setImageResource(leftup[h_1]);
        mImgH_2.setImageResource(rightup[h_2]);
    }

    @Override
    protected void updateMM(int m) {
        super.updateMM(m);
        int m_2 = m % 10;
        mImgM_1.setImageResource(leftdown[m / 10]);
        mImgM_2.setImageResource(rightdown[m_2]);
    }
}
