package com.xxun.xunlauncher.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;

import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import com.xxun.xunlauncher.R;
import com.xxun.xunlauncher.receiver.BindRequestReceiver;
import com.xxun.xunlauncher.gif.GifTempletView;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;

import android.os.SystemProperties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.os.PowerManager;

import org.json.JSONException;
import net.minidev.json.JSONValue;

import android.view.KeyEvent;
import android.content.Context;

import com.xxun.xunlauncher.application.LauncherApplication;

import static com.xiaoxun.sdk.utils.CloudBridgeUtil.KEY_NAME_PL;

/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe 收到服务端下发的绑定请求时展示的界面
 */
public class ShowBindRequestActivity extends Activity implements View.OnClickListener {


    private int currentGifId;
    private boolean isTimeout = false;

    private Timer timer;
    private String bindRequest;
    private JSONObject bindRequestObject;
    private PowerManager.WakeLock wakeLock;
    private GifTempletView waitConfirmGifView;
    private XiaoXunNetworkManager mXiaoXunNetworkManager;
    private ConfirmBindTimeHandler confirmBindTimeHandler;

    private static final int MSG_TIMEOUT_ALERT = 0;
    private static final String TAG = "ShowBindRequestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_activity);
        initViews();
        setListeners();
        timeoutCountingTask();
    }

    private void initViews() {
        waitConfirmGifView = (GifTempletView) findViewById(R.id.wait_confirm_gif);
        waitConfirmGifView.setMovieResource(R.raw.wait_confirm);
        setCurrentGifId(R.raw.wait_confirm);
        mXiaoXunNetworkManager = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        bindRequest = getIntent().getStringExtra("bindRequest");

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bindwatch");
        wakeLock.acquire(70000);
    }

    private void setListeners() {
        waitConfirmGifView.setOnClickListener(this);

	/*
        * 监测当前Bind状态
        * */
        BindRequestReceiver.registerBindListener(new BindRequestReceiver.BindStateChangeListener() {
            @Override
            public void isBindSuccessNow(boolean isBindSuccessNow) {
                waitConfirmGifView.clearAnimation();
                Log.d(TAG, "isBindSuccessNow = :" + isBindSuccessNow);
                if (isBindSuccessNow) {
                    Log.d(TAG, "isBindSuccessNow true so display bind_success");
                    waitConfirmGifView.setMovieResource(R.raw.bind_success);
                    setCurrentGifId(R.raw.bind_success);

                } else {
                    Log.d(TAG, "isBindSuccessNow false so display bind_fail");
                    waitConfirmGifView.setMovieResource(R.raw.bind_fail);
                    setCurrentGifId(R.raw.bind_fail);
                }
                gobackToLauncher();
            }
        });

	/*
        * 监测当前Confirm状态
        * */
        BindRequestReceiver.registerConfirmListener(new BindRequestReceiver.ConfirmStateChangeListener() {
            @Override
            public void isConfirmBindNow(boolean isConfirmBindNow) {
                waitConfirmGifView.clearAnimation();
                Log.d(TAG, "isConfirmBindNow = :" + isConfirmBindNow + ",isTimeout = :" + isTimeout);
                if (!isTimeout) {
                    if (isConfirmBindNow) {
                        Log.d(TAG, "isConfirmBindNow true so display count_bind");
                        waitConfirmGifView.setMovieResource(R.raw.count_bind);
                        setCurrentGifId(R.raw.count_bind);
                        sendBindRequest();
                    } else {
                        Log.d(TAG, "isConfirmBindNow false so display wait_confirm");
                        waitConfirmGifView.setMovieResource(R.raw.wait_confirm);
                        setCurrentGifId(R.raw.wait_confirm);
                    }
                }
            }
        });

    }

    /*手表最后给服务器发送绑定请求,requestJson:收到的绑定请求内容
     * @param result      绑定结果，0：拒绝，1：同意
     * @param sn          收到的绑定请求序号
     * @param targetEid   绑定请求方的EID
     * @param requestJson 收到的绑定请求内容
     * @param callBack    状态返回：
     *                    成功回调onSuccess：ResponseData，responseCode int 100，绑定成功
     *                    失败回调onError：errorCode int 错误码
     *                    errorMessage String 错误信息
    */
    public void sendBindRequest() {
        int result = 1;
        if (!"".equals(bindRequest)) {
            try {
                bindRequestObject = new JSONObject(bindRequest);
                int SN = Integer.parseInt(bindRequestObject.getString("SN"));

                //start, add by mayanjun for get login key;20190415;
                net.minidev.json.JSONObject recvData = (net.minidev.json.JSONObject)JSONValue.parse(bindRequest);;
                net.minidev.json.JSONObject pl = (net.minidev.json.JSONObject) recvData.get(KEY_NAME_PL);

                Log.d(TAG, "get login key : " + bindRequestObject.toString());
                if (pl != null) {
                    Object obj = pl.get(CloudBridgeUtil.KEY_NAME_LOGIN_KEY);
                    if(obj != null){
                        String login_key = obj.toString();
                        saveLoginKey(login_key);
                    }
                }
                //end, by mayanjun;

                mXiaoXunNetworkManager.sendBindResult(result, SN, mXiaoXunNetworkManager.getWatchEid(), bindRequestObject.get("PL").toString(), new SendBindResultCallback());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            LauncherApplication launcherApplication = (LauncherApplication) getApplication();
        }
    }

    private class SendBindResultCallback extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
        }

        @Override
        public void onError(int i, String s) {
        }
    }

    private void timeoutCountingTask() {
        long delayTime = 1000 * 60;
        int intervalTime = 1000 * 60;
        confirmBindTimeHandler = new ConfirmBindTimeHandler();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                confirmBindTimeHandler.sendEmptyMessage(MSG_TIMEOUT_ALERT);
            }
        }, delayTime, intervalTime);
    }

    public void stopTimerTask() {
        Log.d(TAG, "timer cancel");
        timer.cancel();
    }

    private class ConfirmBindTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIMEOUT_ALERT:
                    Log.d(TAG, "60s timeout up,judge current");
                    isTimeout = true;
                    if (getCurrentGifId() == R.raw.wait_confirm || getCurrentGifId() == R.raw.count_bind) {
                        Log.d(TAG, "displayBindFail");
                        waitConfirmGifView.clearAnimation();
                        waitConfirmGifView.setMovieResource(R.raw.bind_fail);
                        setCurrentGifId(R.raw.bind_fail);
                        gobackToLauncher();
                    }
                    if (timer != null) {
                        stopTimerTask();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public int getCurrentGifId() {
        return currentGifId;
    }

    public void setCurrentGifId(int currentId) {
        currentGifId = currentId;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wait_confirm_gif:
                if (waitConfirmGifView.getMovieResourceId() == R.raw.bind_fail || waitConfirmGifView.getMovieResourceId() == R.raw.bind_success) {
                    waitConfirmGifView.clearFocus();
                    finish();
                    Intent homeIntent = new Intent(ShowBindRequestActivity.this, MainActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                }
                break;
            default:
                break;
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.03.15
     * @describe goback to Launcher when bind fail or bind success  after 5 seconds
     */
    private void gobackToLauncher() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                waitConfirmGifView.clearFocus();
                finish();
                Intent homeIntent = new Intent(ShowBindRequestActivity.this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        }, 5000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "bindrequest page hasFocus:" + hasFocus);
        if (hasFocus) {
            SystemProperties.set("persist.sys.isconfirmstate", "true");
        } else {
            SystemProperties.set("persist.sys.isconfirmstate", "false");
        }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.03.12
     * @describe 系统回调方法, 因系统层右滑代替返回键，Bind中屏蔽返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            stopTimerTask();
        }

        SystemProperties.set("persist.sys.isconfirmstate", "false");
        BindRequestReceiver.unRegisterBindListener();
        BindRequestReceiver.unRegisterConfirmListener();
    }

    //start, add by mayanjun for get login key;20190415;
    private void saveLoginKey(String key) {
        Log.d(TAG, "saveLoginKey : " + key);
        try {
            File f = new File("/productinfo/login_key");
            FileWriter w = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(w);
            bw.write(key);
            bw.flush();
            bw.close();
            w.close();
        } catch (IOException ioe) {
            Log.d(TAG, "can't write file", ioe);
        }
        //backup the ck_key to system setting;
        android.provider.Settings.System.putString(getContentResolver(), "login_key_value", key);
    }
    //end, by mayanjun;
}
