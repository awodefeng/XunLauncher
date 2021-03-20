package com.xxun.xunlauncher.activity;

import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;
import com.xxun.xunlauncher.R;
import com.xxun.xunlauncher.utils.ShutDownUtils;

public class ShowUnbindAlertActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unbind_alert);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
	    //After receive unbind request, do factory reset;
	    ShutDownUtils.getShutDownUtilsInstance().factoryResetWatch();
            }
        },1000);
    }
}
