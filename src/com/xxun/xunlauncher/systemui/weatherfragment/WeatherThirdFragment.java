package com.xxun.xunlauncher.systemui.weatherfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import com.xxun.xunlauncher.R;
import com.xxun.xunlauncher.utils.NetworkUtils;
import android.widget.TextView;
import android.widget.ImageView;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.xunlauncher.activity.MainActivity;
import android.provider.Settings;

public class WeatherThirdFragment extends Fragment {
    private TextView mText;
    private String third_weather;
    private String third_Icon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        	// TODO Auto-generated method stub
        	View view = inflater.inflate(R.layout.weather_third, container, false);

        	MainActivity activity = (MainActivity) getActivity();
 
		third_weather = Settings.System.getString(activity.getContentResolver(),"third_weather");
		mText = (TextView) view.findViewById(R.id.third_day_text);	
		ImageView img_weather = (ImageView)view.findViewById(R.id.image_third);
		Log.d("Weather","[weather]@@@third,Text:" +third_weather);

                if((null != third_weather) && (!"".equals(third_weather))&&(!"empty".equals(third_weather)))
                {
				
				mText.setText(third_weather);
			
			
				if(third_weather.indexOf("云")!=-1){
					img_weather.setImageResource(R.drawable.cloud);
				}
				else if(third_weather.indexOf("晴")!=-1)
				{
					img_weather.setImageResource(R.drawable.sun);
				}
				else if(third_weather.indexOf("阴")!=-1)
				{
					img_weather.setImageResource(R.drawable.overcast);
				}
				else if(third_weather.indexOf("雨")!=-1)
				{
					img_weather.setImageResource(R.drawable.rain);
				}
				else if(third_weather.indexOf("雪")!=-1)
				{
					img_weather.setImageResource(R.drawable.snow);
				}
				else if(third_weather.indexOf("雾")!=-1)
				{
					img_weather.setImageResource(R.drawable.fog);
				}
				else if(third_weather.indexOf("霾")!=-1)
				{
					img_weather.setImageResource(R.drawable.fog);
				}
				else
				{
					Log.d("TAG","[weather]@@@third_weather，error！！！");
					mText.setText(R.string.no_weather_info);
					img_weather.setImageResource(R.drawable.invaild);
				}
		   }else{
			
 			Log.d("TAG","[weather]@@@today_weather:no_weather_info" );
			mText.setText(R.string.no_weather_info);
			img_weather.setImageResource(R.drawable.invaild);
		}
         
        return view;
    }

}
