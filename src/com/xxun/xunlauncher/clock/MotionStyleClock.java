package com.xxun.xunlauncher.clock;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.xxun.xunlauncher.gif.GifTempletView;
import com.xxun.xunlauncher.R;
import android.widget.TextView;

public class MotionStyleClock extends BaseWatchView {

    private WatchRotateView mImgSwordHour;
    private WatchRotateView mImgSwordMinute;
    private TextView mTxtDay;
    private TextView mWeekDay;

    private String[] weekDays;


    private static final String TAG = "MotionStyleClock";

    public MotionStyleClock(Context context) {
        this(context,null);
    }

    public MotionStyleClock(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public MotionStyleClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(LayoutInflater.from(context).inflate(R.layout.motion_style_clock, this, true));
    }

    protected void initView(View rootView) {
        Log.d(TAG, "initView");
//        mImgWindmill = (GifTempletView) rootView.findViewById(R.id.bg1);
//        mImgWindmill.setMovieResource(R.raw.wind_bg);
        mImgSwordHour = (WatchRotateView) rootView.findViewById(R.id.sword_hour);
        mImgSwordMinute = (WatchRotateView) rootView.findViewById(R.id.sword_minute);
        mTxtDay = (TextView) rootView.findViewById(R.id.txt_day);
        mWeekDay = (TextView) rootView.findViewById(R.id.week_day);
        weekDays = new String[7];
        weekDays = new String[]{"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
        updateMM(minute);
        updateHH(hour);
        updateButtomText();
        BaseWatchView.setRefreshClockTime(60000);
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
        if(mTxtDay != null) mTxtDay.setText(mm + "/" + day );
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
        mImgSwordHour.setDegree(((float) (h * 30)) + (((float) this.minute) / 2.0f));
    }

    @Override
    protected void updateMM(int m) {
        super.updateMM(m);
        mImgSwordMinute.setDegree(((float) (m * 6)) + (((float) this.second) / 10.0f));
    }
}
