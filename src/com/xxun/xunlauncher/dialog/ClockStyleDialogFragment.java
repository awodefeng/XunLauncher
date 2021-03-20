package com.xxun.xunlauncher.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.content.Context;
import com.xxun.xunlauncher.R;
import android.os.Handler;
import android.util.Log;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import com.xxun.xunlauncher.Constants;
import android.provider.Settings;
import android.widget.ImageView;
import android.database.ContentObserver;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xxun.xunlauncher.service.LauncherService;
import com.xxun.xunlauncher.gif.GifTempletView;
import android.util.XiaoXunUtil;
import com.xiaoxun.sdk.utils.Constant;

/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe 长按待机时钟弹出的选择时钟样式选择框
 */
public class ClockStyleDialogFragment extends DialogFragment {

    private View view;
    //gif索引
    private int index = 0;
    private GifTempletView gifView;
    private ImageButton confirmBtn;
    //private Context mContext;
    private String launcherSharePreName = "launcherSharePre";
    private String choosedClockStyle = "choosedClockStyle";
    
    private Integer[] defaultClockGifArray = new Integer[]{R.raw.digit, R.raw.digit2, R.raw.wind, R.raw.squareclock, R.raw.roundclock, R.raw.ladybird};
    private List<Integer> clockGifList = new LinkedList<Integer>();
    private static final String TAG = "ClockStyleDialogFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.time_style, container);
	setgifList();
        setupViews();
        setListener();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.black);
        getDialog().getWindow().setAttributes(layoutParams);
    }
    
    //del by lihaizhou due to 4.4 not support this method 20180712
    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }*/
    
    private ContentObserver powerkeyObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
	    getDialog().dismiss(); 
        }
    };
    
    private void setgifList(){
      if(clockGifList.size()==0){
	 clockGifList.addAll(Arrays.asList(defaultClockGifArray));
         if(XiaoXunUtil.XIAOXUN_CONFIG_SW_NAME.contains("760")){
                clockGifList.add(R.raw.earth);
                clockGifList.add(R.raw.huoxing);
                clockGifList.add(R.raw.mohuan);
         }
         if(Constant.PROJECT_NAME.equals("SW706")){
             clockGifList.add(R.raw.flamingo);
             clockGifList.add(R.raw.motion_pink);
             clockGifList.add(R.raw.motion_red);
             clockGifList.add(R.raw.motion_blue);
             clockGifList.add(R.raw.motion_green);
             clockGifList.add(R.raw.track);
             clockGifList.add(R.raw.xunmotion);
         }
        if(LauncherApplication.defalutClockStyleIndex == 1){
            clockGifList.add(R.raw.online);
         }else if(LauncherApplication.defalutClockStyleIndex == 2){
            clockGifList.add(R.raw.offline);
         }
      } 
    }

    private void setupViews() {
	Log.d(TAG, "ClockStyleDialogFragment setupViews");
	Settings.System.putString(LauncherApplication.getInstance().getContentResolver(), "on_xunlauncher_homescreen", "false");
	LauncherApplication.getInstance().getContentResolver().registerContentObserver(Settings.System.getUriFor(Constants.NOTIFY_LAUNCHER_CHANGE), true, powerkeyObserver);
        RelativeLayout timeLayout = (RelativeLayout) view.findViewById(R.id.time_layout);
        //因上一个，下一个按钮图标太小，不容易点击，此处用判断手指落下位置来切换上一个下一个
        timeLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
			 if (event.getY() > 70 && event.getY() < 170 && event.getX() > 180 && event.getX() <= 240) {
                              switchToNextGif();
                         } else if (event.getY() > 70 && event.getY() < 170 && event.getX() >= 0 && event.getX() < 60) {
                              switchToPreGif();
                         }
                        break;
                    default:
                        break;

                }
                return true;
            }
        });

        gifView = (GifTempletView) view.findViewById(R.id.time_gif);
        confirmBtn = (ImageButton) view.findViewById(R.id.confirm_btn);
        index = Settings.System.getInt(LauncherApplication.getInstance().getContentResolver(), "clock_style",0);
        // 根据activity中传过来的Tag即当前的时钟下标，显示对应的时钟gif资源
	if (index<clockGifList.size()) {
            gifView.setMovieResource(clockGifList.get(index));
        } else{
	    gifView.setMovieResource(clockGifList.get(0));
	}
    }

    private void setListener() {
	view.findViewById(R.id.pre_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
	       switchToPreGif();
	    }
        });

	view.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
	       switchToNextGif();
	    }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
		Log.d("sylar", "confirmBtn index = "+index);
		if(index>=0&&index<clockGifList.size()){
		   Settings.System.putInt(LauncherApplication.getInstance().getContentResolver(),"clock_style",index);
		}else{
		   Log.d("sylar", "confirmBtn index should never happen = "+index);
		   Settings.System.putInt(LauncherApplication.getInstance().getContentResolver(),"clock_style",0);
		}
		
		new Handler().postDelayed(new Runnable() {
            	    public void run() {
		      if(getDialog()!=null){
		         getDialog().dismiss();
		      }
            	    }
        	}, 200);


            }
        });
    }
    
    private void switchToPreGif(){
	index--;
	Log.d("lihaizhou", "switchToPreGif clockGifList.size() = "+clockGifList.size()+", index = "+index);
	if(index < 0) {
           index = clockGifList.size()-1;
        }
     	gifView.setMovieResource(clockGifList.get(index));
    }
    
    private void switchToNextGif(){
	index++;
	Log.d("lihaizhou", "switchToNextGif clockGifList.size() = "+clockGifList.size()+", index = "+index);
        if (index>=clockGifList.size()) {
            index = 0;
        }
        gifView.setMovieResource(clockGifList.get(index));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
	Log.d(TAG, "ClockStyleDialogFragment onDestroy");
	Settings.System.putString(LauncherApplication.getInstance().getContentResolver(), "on_xunlauncher_homescreen", "true");
	LauncherApplication.getInstance().getContentResolver().unregisterContentObserver(powerkeyObserver);
    }

}
