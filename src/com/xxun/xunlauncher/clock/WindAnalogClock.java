package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.xxun.xunlauncher.gif.GifTempletView;
import com.xxun.xunlauncher.R;

public class WindAnalogClock extends BaseWatchView {
    
    private WatchRotateView mImgSwordHour;
    private WatchRotateView mImgSwordMinute;
    private GifTempletView mImgWindmill;
	
    private static final String TAG = "WindAnalogClock";
	
    public WindAnalogClock(Context context) {
        this(context,null);
    }

    public WindAnalogClock(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public WindAnalogClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(LayoutInflater.from(context).inflate(R.layout.wind_analog_clock, this, true));
    }

    protected void initView(View rootView) {
        Log.d(TAG, "initView");
        mImgWindmill = (GifTempletView) rootView.findViewById(R.id.bg1);
	mImgWindmill.setMovieResource(R.raw.wind_bg);
        mImgSwordHour = (WatchRotateView) rootView.findViewById(R.id.sword_hour);
        mImgSwordMinute = (WatchRotateView) rootView.findViewById(R.id.sword_minute);
        updateMM(minute);
        updateHH(hour);
	BaseWatchView.setRefreshClockTime(60000);
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
