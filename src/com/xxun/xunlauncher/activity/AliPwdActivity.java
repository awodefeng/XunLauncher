package com.xxun.xunlauncher.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.content.Intent;

import com.xxun.xunlauncher.R;
import com.xxun.xunlauncher.utils.PayDialogFragment;
import android.provider.Settings;

/**
 * @author lihaizhou
 * @time 2018.06.08
 * @describe 插入充电器后显示的全局提示窗口
 * 拔掉充电器后窗口消失且窗口不可滑动
 */

public class AliPwdActivity extends FragmentActivity implements PayDialogFragment.OnBackListener {

    private static final String TAG = "AliPwdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ali_pwd_activity);
        inputPwdAli();
	Settings.System.putString(getContentResolver(), "on_xunlauncher_homescreen", "false");
    }

    /**
     * @author 郭洪成 guohongcheng
     * 进入支付宝前，先要输入应用锁密码
     */
    public void inputPwdAli() {
        boolean isReceiveMoneyMode = false;
        // modify by guohongcheng_20190403 start
        // 新增打开收款码功能
        // params为null，表示指令为“打开支付宝”指令；
        // params不为null且其中的type值为"pay_money"，表示“付钱”指令；
        // params不为null且其中的type值为"collect_money"，表示“收钱”指令。
        if (getIntent().hasExtra("params")) {
            // int extra = getIntent().getIntExtra("pay_money", 0);
            Bundle buddleParams = getIntent().getBundleExtra("params");
            String payType = buddleParams.getString("type");
            Log.d(TAG, "inputPwdAli payType " + payType);
            if ("collect_money".equals(payType)) { // 打开收款码
                isReceiveMoneyMode = true;
            } else {
                isReceiveMoneyMode = false;
            }
        } else {
            isReceiveMoneyMode = false;
        }
        Log.d(TAG, "inputPwdAli isReceiveMoneyMode " + isReceiveMoneyMode);
        PayDialogFragment payDialogFragment = new PayDialogFragment(isReceiveMoneyMode);
        // modify by guohongcheng_20190403 end
        payDialogFragment.show(getSupportFragmentManager(), "payFragment");

    }

    @Override
    public void onBackListener() {
        Log.d(TAG, "onBackListener..");
        finish();
    }
}
