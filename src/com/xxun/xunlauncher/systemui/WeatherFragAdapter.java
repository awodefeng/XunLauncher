package com.xxun.xunlauncher.systemui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @author lihaizhou
 * @createtime 2017.10.20
 * @class describe 下拉状态栏天气适配器
 */
public class WeatherFragAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    public WeatherFragAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        // TODO Auto-generated constructor stub
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return mFragments.get(arg0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mFragments.size();
    }

    /**
     * @author lihaizhou
     * @createtime 2017.11.29
     * @describe 系统回调方法, 因天气fragment每次滑动需更新数据, 此处去掉缓存机制, 每次滑动时刷新界面
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
