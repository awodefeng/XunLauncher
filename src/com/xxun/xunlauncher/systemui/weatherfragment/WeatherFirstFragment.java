package com.xxun.xunlauncher.systemui.weatherfragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;
import android.widget.ImageView;
import com.xxun.xunlauncher.utils.NetworkUtils;
import com.xxun.xunlauncher.R;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.xunlauncher.activity.MainActivity;
import android.provider.Settings;

public class WeatherFirstFragment extends Fragment {
    private TextView mText;
    public String today_weather;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       		 // TODO Auto-generated method stub
        	View view = inflater.inflate(R.layout.weather_first, container, false);
        	mText = (TextView) view.findViewById(R.id.first_day_text);
        	ImageView img_weather = (ImageView)view.findViewById(R.id.image_first);
        	MainActivity activity = (MainActivity) getActivity();
 
                today_weather = Settings.System.getString(activity.getContentResolver(),"today_weather");

		Log.d("TAG","[weather]@@@today_weather:" +today_weather);

                if((null != today_weather) && (!"".equals(today_weather))&&(!"empty".equals(today_weather)))
                {
		                mText.setText(today_weather);

		                if(today_weather.indexOf("多云")!=-1){
					img_weather.setImageResource(R.drawable.cloud);
				}
				else if(today_weather.indexOf("晴")!=-1)
				{
					img_weather.setImageResource(R.drawable.sun);
				}
				else if(today_weather.indexOf("阴")!=-1)
				{
					img_weather.setImageResource(R.drawable.overcast);
				}
				else if(today_weather.indexOf("雨")!=-1)
				{
					img_weather.setImageResource(R.drawable.rain);
				}
				else if(today_weather.indexOf("雪")!=-1)
				{
					img_weather.setImageResource(R.drawable.snow);
				}
				else if(today_weather.indexOf("雾")!=-1)
				{
					img_weather.setImageResource(R.drawable.fog);
				}
				else if(today_weather.indexOf("霾")!=-1)
				{
					img_weather.setImageResource(R.drawable.fog);
				}
				else
				{
					Log.d("TAG","[weather]@@@today_weather，error！！！");
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
