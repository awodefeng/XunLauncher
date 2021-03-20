package com.xxun.xunlauncher.activity;

import com.xxun.xunlauncher.R;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import com.xxun.xunlauncher.gif.GifTempletView;
//import android.support.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * @author lihaizhou
 * @time 2018.06.22
 * @class describe 充电禁用状态下，来QQ消息弹出的提醒界面
 */

public class NewQQMsgAlertActivity extends Activity {
    
    private ImageView confirmBtn;
    private static final String TAG = "NewQQMsgAlertActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_msg);
	confirmBtn = (ImageView) findViewById(R.id.confirm);
	confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }    
}
