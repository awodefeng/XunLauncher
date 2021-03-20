package com.xxun.xunlauncher.launcherpager;

//import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.xxun.xunlauncher.Constants;
import com.xxun.xunlauncher.R;
import java.util.List;
import android.provider.Settings;
import com.xxun.xunlauncher.MsgManager;
import android.util.XiaoXunUtil;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xiaoxun.sdk.utils.Constant;
import android.widget.Toast;
/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe XunLauncher适配器
 */

public class LauncherPagerAdapter extends PagerAdapter implements View.OnClickListener {

    private Context mContext;

    //待机部分
    private FrameLayout homescreenRootLayout;
    private View watchView;
    private com.xxun.xunlauncher.launcherpager.LauncherPagerAdapter.OnPagerItemClickLitener onPagerItemClickLitener;
    private boolean isTouch = false;
    private boolean mCallMoved = false;
    private float mDownX;
    private float mDownY;
    private int mTouchSlop = 40;
    private List<Integer> mView;
    private int mode;
    private int unreadMsgNum = -1;
    private long mBgCallStartTime = -1;
    private long mBgCallToastTime = -1;

    private static final String TAG = "LauncherPagerAdapter";

    /**
     * @author lihaizhou
     * @createtime 2017.11.20
     * @describe 点击XunLauncher上应用icon打开对应应用
     */
    public interface OnPagerItemClickLitener {
        void onItemClick(int position);
    }

    public LauncherPagerAdapter(Context context) {
        mContext = context;
    }

    public void setViews(List<Integer> views) {
        mView = views;
    }

    @Override
    public int getCount() {
        return Constants.LAUNCHER_PAGE_MAX_SUM;
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.20
     * @describe 长按待机界面弹出时钟样式选择界面
     */
    /*public interface ShowClockStyleCallback {
        void showClockDialog();
    }*/

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.launcher_item_layout, null);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        if (position % mView.size() == 0) {
            mode = Settings.System.getInt(mContext.getContentResolver(), "clock_style",-1);
            Log.d(TAG,"adapter mode is = : "+mode);
            if(mode == -1){
                Settings.System.putInt(mContext.getContentResolver(),"clock_style",0);
                if(LauncherApplication.defalutClockStyleIndex == 1){
                    Settings.System.putInt(mContext.getContentResolver(),"clock_style",6);
                    watchView = View.inflate(mContext, R.layout.digit_online, null);
                } else if(LauncherApplication.defalutClockStyleIndex == 2){
                    Settings.System.putInt(mContext.getContentResolver(),"clock_style",6);
                    watchView = View.inflate(mContext, R.layout.digit_offline, null);
                } else{
                    if(Constant.PROJECT_NAME.equals("SW706")){
                        Settings.System.putInt(mContext.getContentResolver(), "clock_style", 6);
                        watchView = View.inflate(mContext, R.layout.digit_vertical, null);
                    }else {
                        Settings.System.putInt(mContext.getContentResolver(), "clock_style", XiaoXunUtil.XIAOXUN_CONFIG_SW_NAME.contains("760") ? 8 : 0);
                        watchView = View.inflate(mContext, R.layout.digit_vertical, null);
                    }
                }
            }else{
                if (mode == Constants.VERTICAL_CLOCK_INDEX) {
                    watchView = View.inflate(mContext, R.layout.digit_vertical, null);
                } else if (mode == Constants.HORIZE_CLOCK_INDEX) {
                    watchView = View.inflate(mContext, R.layout.digit_horiz, null);
                } else if (mode == Constants.ANALOG_CLOCK_INDEX) {
                    watchView = View.inflate(mContext, R.layout.clock_style, null);
                } else if (mode == Constants.SQUARE_ANALOG_CLOCK_INDEX) {
                    watchView = View.inflate(mContext, R.layout.square_clock_base, null);
                } else if (mode == Constants.ROUND_ANALOG_CLOCK_INDEX) {
                    watchView = View.inflate(mContext, R.layout.round_clock_base, null);
                } else if (mode == Constants.LADYBIRD_ANALOG_CLOCK_INDEX) {
                    watchView = View.inflate(mContext, R.layout.ladybird_clock_base, null);
                }else if(mode == Constants.EARTH_ONE){
                    if(XiaoXunUtil.XIAOXUN_CONFIG_SW_NAME.contains("760")){
                        watchView = View.inflate(mContext, R.layout.earth_one_base, null);
                    }else if(Constant.PROJECT_NAME.equals("SW706")){
                        watchView = View.inflate(mContext, R.layout.flamingo_base, null);
                    }else{
                        if(LauncherApplication.defalutClockStyleIndex == 1){
                            watchView = View.inflate(mContext, R.layout.digit_online, null);
                        } else if(LauncherApplication.defalutClockStyleIndex == 2){
                            watchView = View.inflate(mContext, R.layout.digit_offline, null);
                        } else{
                            watchView = View.inflate(mContext, R.layout.digit_vertical, null);
                        }
                    }
                }else if(mode == Constants.EARTH_TWO){
                    if(Constant.PROJECT_NAME.equals("SW706")){
                        watchView = View.inflate(mContext, R.layout.motion_style_clock_base, null);
                        ((ImageView)watchView.findViewById(R.id.motion_image)).setBackgroundResource(R.drawable.motion_pink);
                    }else {
                        watchView = View.inflate(mContext, R.layout.earth_two_base, null);
                    }
                }else if(mode == Constants.EARTH_THTEE){
                    if(Constant.PROJECT_NAME.equals("SW706")){
                        watchView = View.inflate(mContext, R.layout.motion_style_clock_base, null);
                        ((ImageView)watchView.findViewById(R.id.motion_image)).setBackgroundResource(R.drawable.motion_red);
                    }else {
                        watchView = View.inflate(mContext, R.layout.earth_three_base, null);
                    }
                } else if (mode == 9){
                    if(Constant.PROJECT_NAME.equals("SW706")) {
                        watchView = View.inflate(mContext, R.layout.motion_style_clock_base, null);
                        ((ImageView)watchView.findViewById(R.id.motion_image)).setBackgroundResource(R.drawable.motion_blue);
                    } else {
                        if (LauncherApplication.defalutClockStyleIndex == 1) {
                            watchView = View.inflate(mContext, R.layout.digit_online, null);
                        } else if (LauncherApplication.defalutClockStyleIndex == 2) {
                            watchView = View.inflate(mContext, R.layout.digit_offline, null);
                        } else {
                            watchView = View.inflate(mContext, R.layout.digit_vertical, null);
                        }
                    }
                }else if(mode ==Constants.MOTION_STYLE){
                    watchView = View.inflate(mContext, R.layout.motion_style_clock_base, null);
                    ((ImageView)watchView.findViewById(R.id.motion_image)).setBackgroundResource(R.drawable.motion_green);
                }else if(mode ==Constants.TRACK_STYLE){
                    watchView = View.inflate(mContext, R.layout.track_base, null);
                }else if(mode ==Constants.XUNMOTION_STYLE){
                    watchView = View.inflate(mContext, R.layout.xunmotion_base, null);
                }else{
                    Log.d(TAG,"should never happen!");
                    Settings.System.putInt(mContext.getContentResolver(),"clock_style",0);
                    watchView = View.inflate(mContext, R.layout.digit_vertical, null);
                }
            }
            homescreenRootLayout = (FrameLayout) view.findViewById(R.id.root_view);
            homescreenRootLayout.removeAllViews();
            homescreenRootLayout.addView(watchView);
            homescreenRootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(isTouch && !XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_CTA_TEST ){
                        //((ShowClockStyleCallback) mContext).showClockDialog();
                        //采用事件发到MsgManager中转，从而降低耦合
                        MsgManager.getMsgManagerInstance().sendMsg("showClockDialog");

                    }
                    return false;
                }
            });
        } else {
            int iconId = mView.get(position % mView.size());
            view.findViewById(R.id.launcher_item_view).setBackgroundResource(iconId);

            if (R.drawable.voice_msg == iconId) {
                TextView msgNumTv = (TextView)view.findViewById(R.id.unread_msg_num);
                unreadMsgNum = Settings.System.getInt(mContext.getContentResolver(),"ChatMissCount",0);
                if (unreadMsgNum > 0) {
                    if (unreadMsgNum >= 10) {
                        msgNumTv.setText("");
                    } else {
                        msgNumTv.setText(unreadMsgNum + "");
                    }
                    msgNumTv.setVisibility(View.VISIBLE);
                } else {
                    msgNumTv.setVisibility(View.INVISIBLE);
                }
            }

        }
        view.setTag(position);
        //此处通过手势判断是否一次有效的点击
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = event.getX();
                        mDownY = event.getY();
                        mCallMoved = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(mCallMoved){
                            //Log.d(TAG,"ontouch up" );
                            mCallMoved = false;
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        }

                        //Log.d(TAG,"ontouch up1" );
                        if (Math.abs(event.getY() - mDownY) <= ((float) mTouchSlop) && Math.abs(event.getX() - mDownX) <= ((float) mTouchSlop)) {
                            isTouch = true;
                            break;
                        }
                        //Log.d(TAG,"ontouch up2" );
                        isTouch = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //add by mayanjun.20190904;
                        if(mBgCallStartTime != -1 ){
                            Log.d(TAG,"move mBgCallStartTime =  "  + mBgCallStartTime);
                            TelephonyManager telecomManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                            boolean inCall = (telecomManager.getCallState() != TelephonyManager.CALL_STATE_IDLE);

                            if((System.currentTimeMillis() - mBgCallStartTime) < 5000 || inCall) {
                                mCallMoved = true;
                                isTouch = false;
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                                if(System.currentTimeMillis() - mBgCallToastTime > 5000) {
                                    mBgCallToastTime = System.currentTimeMillis();
                                    Toast.makeText(mContext, R.string.bgcall_noti, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }

                        if(mBgCallStartTime != -1) {
                            android.provider.Settings.System.putLong(mContext.getContentResolver(), "start_backcall", -1);
                            mBgCallStartTime = -1;
                        }

                        //Log.d(TAG,"move1 mBgCallStartTime =  "  + mBgCallStartTime);

                        if ((Math.abs(event.getY() - mDownY) <= ((float) mTouchSlop) && Math.abs(event.getX() - mDownX) <= ((float) mTouchSlop))) {
                            isTouch = true;
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        }else if(Math.abs(event.getX() - mDownX) > ((float) mTouchSlop)){
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        //Log.d(TAG,"move2 mBgCallStartTime =  "  + mBgCallStartTime);
                        isTouch = false;
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        if (onPagerItemClickLitener != null) {
            view.setOnClickListener(this);
        }
        container.addView(view, 0);
        return view;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object.equals(homescreenRootLayout)) {
            Settings.System.putString(mContext.getContentResolver(), "on_xunlauncher_homescreen", "true");
        } else {
            Settings.System.putString(mContext.getContentResolver(), "on_xunlauncher_homescreen", "false");
        }
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public int getItemPosition(Object object) {
        return -2;
    }

    @Override
    public void onClick(final View view) {
        if (view != null && getIsTouch()) {
            int pos = Integer.parseInt(view.getTag().toString());
            if (mView != null) {
                onPagerItemClickLitener.onItemClick(pos % mView.size());
            }
        }
    }

    /**
     * @param
     * @return true表示有效点击
     * @author lihaizhou
     * @createtime 2017.11.26
     * @describe 判断是否是有效的点击
     */
    private boolean getIsTouch() {
        return isTouch;
    }

    public void setOnPagerItemClickLitener(com.xxun.xunlauncher.launcherpager.LauncherPagerAdapter.OnPagerItemClickLitener pagerItemClickLitener) {
        onPagerItemClickLitener = pagerItemClickLitener;
    }

    public void setBackgroundCallStart(long startMillsecs){
        Log.d(TAG,"adapter setBackgroundCallStart = : " + startMillsecs);
        mBgCallStartTime = startMillsecs;
    }
}
