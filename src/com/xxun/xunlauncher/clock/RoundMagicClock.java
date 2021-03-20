package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.xxun.xunlauncher.R;

/**
 * @author lihaizhou
 * @time 2018.05.25
 * @class describe 圆形magic时钟样式
 */
public class RoundMagicClock extends BaseWatchView {

    private WatchRotateView mImgSwordHour;
    private WatchRotateView mImgSwordMinute;
    private WatchRotateView mImgSwordSecond;

    private final String TAG = "RoundMagicClock";

    public RoundMagicClock(Context context) {
        this(context,null);
    }

    public RoundMagicClock(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RoundMagicClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(LayoutInflater.from(context).inflate(R.layout.round_magic_clock, this, true));
    }

    protected void initView(View rootView) {
        Log.d(TAG, "initView");
        mImgSwordHour = (WatchRotateView) rootView.findViewById(R.id.sword_hour);
        mImgSwordMinute = (WatchRotateView) rootView.findViewById(R.id.sword_minute);
        mImgSwordSecond = (WatchRotateView) rootView.findViewById(R.id.sword_second);
        updateMM(minute);
        updateHH(hour);
        updateSS(second);
	BaseWatchView.setRefreshClockTime(1000);
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
