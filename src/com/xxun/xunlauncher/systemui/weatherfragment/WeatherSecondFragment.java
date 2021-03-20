package com.xxun.xunlauncher.systemui.weatherfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;
import com.xxun.xunlauncher.utils.NetworkUtils;
import com.xxun.xunlauncher.R;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.xunlauncher.activity.MainActivity;
import android.widget.ImageView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.provider.Settings;

public class WeatherSecondFragment extends Fragment {

    private TextView mText;
    private String tomorrow_weather;
    private String tomorrow_icon;
    private WeatherChangeRecevive weatherchangerecevive;
    private IntentFilter intentFilter;
    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        	// TODO Auto-generated method stub
        	View view = inflater.inflate(R.layout.weather_second, container, false);

        	activity = (MainActivity) getActivity();

        	intentFilter = new IntentFilter();
        	intentFilter.addAction("send weather update");

        	weatherchangerecevive = new WeatherChangeRecevive();
        	activity.registerReceiver(weatherchangerecevive, intentFilter);

        	mText = (TextView) view.findViewById(R.id.second_day_text);
		ImageView img_weather = (ImageView)view.findViewById(R.id.image_second);	
		
                tomorrow_weather = Settings.System.getString(activity.getContentResolver(),"tomorrow_weather");

		Log.d("TAG","[weather]@@@tomorrow,Text:" +tomorrow_weather);

		if((null != tomorrow_weather) && (!"".equals(tomorrow_weather))&&(!"empty".equals(tomorrow_weather)))
                {
				mText.setText(tomorrow_weather);
		
				if(tomorrow_weather.indexOf("云")!=-1){
					
					img_weather.setImageResource(R.drawable.cloud);
				}
				else if(tomorrow_weather.indexOf("晴")!=-1)
				{
					Log.d("Weather","@@@tomorrow_weather:setting image icon");
					img_weather.setImageResource(R.drawable.sun);
				}
				else if(tomorrow_weather.indexOf("阴")!=-1)
				{
					img_weather.setImageResource(R.drawable.overcast);
				}
				else if(tomorrow_weather.indexOf("雨")!=-1)
				{
					img_weather.setImageResource(R.drawable.rain);
				}
				else if(tomorrow_weather.indexOf("雪")!=-1)
				{
					img_weather.setImageResource(R.drawable.snow);
				}
				else if(tomorrow_weather.indexOf("雾")!=-1)
				{
					img_weather.setImageResource(R.drawable.fog);
				}
				else if(tomorrow_weather.indexOf("霾")!=-1)
				{
					img_weather.setImageResource(R.drawable.fog);
				}
				else
				{
					Log.d("TAG","[weather]@@@tomorrow_weather，error！！！");
					mText.setText(R.string.no_weather_info);
					img_weather.setImageResource(R.drawable.invaild);
				}
		}
	        else
                {
			
			mText.setText(R.string.no_weather_info);
			img_weather.setImageResource(R.drawable.invaild);
			
			Log.d("TAG","[weather]@@@tomorrow_weather:no_weather_info" );
		}
           
     

        Log.d("Weather", "@@@tomorrow_weather:return view");
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(weatherchangerecevive);
    }

    class WeatherChangeRecevive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TextView text = (TextView)findViewById(R.id.broadcast);
            //text.setText("OKOKOKO");
            Log.d("TAG", "AAAAAAAAAAAAAAAAAAAAAAAA");
        }
    }

}
