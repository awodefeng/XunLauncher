package com.xxun.xunlauncher.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.xxun.xunlauncher.R;
import java.io.File;
import android.view.Window;
import android.view.Gravity;
import android.view.WindowManager;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author lihaizhou
 * @time 2018.03.01
 * @class describe 当手表内存不足50M时，点击应用弹出内存不足提示框
 */
public class LowMemDialogFragment extends DialogFragment {

    private static final String TAG = "LowMemDialogFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.low_mem, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text_result);
        textView.setText("手表空间已满，请在设置-手表空间中管理");
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity= Gravity.BOTTOM;
        layoutParams.width = layoutParams.MATCH_PARENT;
        layoutParams.height = layoutParams.MATCH_PARENT;
        getDialog().getWindow().getDecorView().setPadding(0,0,0,0);
        getDialog().getWindow().setAttributes(layoutParams);
    }

   
}
