package com.xxun.xunlauncher.launcherpager;

import com.xxun.xunlauncher.R;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.content.Context;
import android.util.AttributeSet;

/**
 * @author lihaizhou
 * @createtime 2018.04.09
 * @class describe XunLauncher viewpager
 */

public class LauncherViewPager extends ViewPager{

    private boolean isAllowSlide = true;
    
    public LauncherViewPager(Context context) {
        super(context);
    }
    
    public LauncherViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSlide(boolean allowSlide) {
        isAllowSlide = allowSlide;
    }

    public boolean getSlide(){
        return  isAllowSlide;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
	    if(!isAllowSlide){
	       return false;
	    }
        //多点触摸时会低概率出现IllegalArgumentException，谷歌问题，这里使用catch来规避
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
