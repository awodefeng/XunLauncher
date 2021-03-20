package com.xxun.xunlauncher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.xxun.xunlauncher.R;

public class AlertDialogActivity extends Activity {

    private ImageView cancle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telecom_unsupport_dialog);
        cancle = (ImageView) findViewById(R.id.cancel);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogActivity.this.finish();
            }
        });
    }

}
