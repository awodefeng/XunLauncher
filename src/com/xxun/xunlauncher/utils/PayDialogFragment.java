package com.xxun.xunlauncher.utils;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
//import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;

import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import com.xiaoxun.sdk.utils.Constant;
import com.xxun.xunlauncher.activity.AliPwdActivity;
import com.xxun.xunlauncher.R;
import android.content.Context;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.xiaoxun.statistics.XiaoXunStatisticsManager;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by guohongcheng on 2018/8/23.
 * 逻辑：
 * 1.输入密码，正确时进入支付宝，并且重置错误次数（WRONG_COUNT）标志位
 * 2.输入密码错误，连续4次将会锁定10分钟
 * 3.10分钟后，取消锁定
 */

public class PayDialogFragment extends DialogFragment implements PwdEditText.OnTextInputListener, View.OnClickListener {

    private static final String TAG = "PayDialogFragment";
    private static final String FLAG_ZHIFUBAO_PWD = "password_zhifubao";
    private static final String WRONG_COUNT = "WrongCount_SP";
    private static final String WRONG_LOCKTIME = "WrongTime_SP";

    private static final String VERIFYCODE = "SP_VERIFYCODE";

    // 最多失误次数：4次
    private static final int DEFAULT_WRONG_TIME = 4;
    // 锁定时长：10分钟
    private static final long MIN_5 = 5;
    private PwdEditText editText;
    private int mWrongCount = 0;
    private TextView mLockView, mPlzSetPwd, mGuideText;
    private Button btnSendCode, btnInputCode, btnInputSucess;
    private FrameLayout frameSendCode, frameInputSucess;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private PwdKeyboardView keyboardView, keyViewVerifyCode;

    private static final int NULL_ONOFF = 404;
    private static final String NULL_ALI_PWD = "NULL_ALI_PWD";

    private static final String NULL_VERIFY = "NULL_VERIFY";

    private static final int MODE_DEFAULT = 2;
    private static final int MODE_VERIFYCODE = 1; // 输入验证码模式
    private static final int MODE_PWD = 2; // 输入应用锁密码模式

    private int isEnablePwd = NULL_ONOFF;
    private String aliPwd = NULL_ALI_PWD;

    private XiaoXunStatisticsManager statisticsManager;

    private String g_sendVerifyCode = NULL_VERIFY;

    private static final String send_content1 = "支付宝因连续输错4次密码应用已被锁定，验证码："; 
    private static final String send_content2 = "，输入验证码后解锁应用，原应用密码不变，验证码5分钟内有效。";

    private String[] project = new String[]{"mimetype", "data1", "data2", "data3", "data4", "data5", "data6", "data7"
            , "data8", "data9", "data10", "data11", "data12"
            , "data13", "data14", "data15"};

    private OnBackListener mOnBackListener;

    private boolean isReceiveMoney = false;
    
    public PayDialogFragment(boolean isReceiveMoneyMode) {
        isReceiveMoney = isReceiveMoneyMode;
    }
	
	private Context mContext = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        // getDefaultGidBy20041();
        // afterLock();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        //去掉dialog的标题，需要在setContentView()之前
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.layout_pay_dialog, null);

        // 逻辑处理内容
        sharedPreferences = getActivity().getSharedPreferences(FLAG_ZHIFUBAO_PWD, MODE_PRIVATE);
		mContext = (Context) getActivity();
        editor = sharedPreferences.edit();
        mWrongCount = sharedPreferences.getInt(WRONG_COUNT, 0);
        Log.d(TAG, "mWrongCount: " + mWrongCount);

        btnSendCode = (Button) view.findViewById(R.id.btn_send_verifycode);
        btnSendCode.setOnClickListener(this);
        btnInputCode = (Button) view.findViewById(R.id.btn_input_verifycode);
        btnInputCode.setOnClickListener(this);
        btnInputSucess = (Button) view.findViewById(R.id.btn_input_confirm);
        btnInputSucess.setOnClickListener(this);


        frameSendCode = (FrameLayout) view.findViewById(R.id.frame_sendcode);
        frameInputSucess = (FrameLayout) view.findViewById(R.id.frame_inputcodesucess);
        mGuideText = (TextView) view.findViewById(R.id.input_pwd);

//        ImageView exitImgView = (ImageView) view.findViewById(R.id.iv_exit);
//        exitImgView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PayDialogFragment.this.dismiss();
//            }
//        });
        editText = (PwdEditText) view.findViewById(R.id.et_input);
        editText.setOnTextInputListener(this);
        keyboardView = (PwdKeyboardView) view.findViewById(R.id.key_board);
//        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyListener(new PwdKeyboardView.OnKeyListener() {
            @Override
            public void onInput(String text) {
                Log.d(TAG, "onInput: text = " + text);
                editText.append(text);
                String content = editText.getText().toString();
                Log.d(TAG, "onInput: content = " + content);
            }

            @Override
            public void onDelete() {
                Log.d(TAG, "onDelete: ");
                String content = editText.getText().toString();
                if (content.length() > 0) {
                    editText.setText(content.substring(0, content.length() - 1));
                }
            }
        });

        // // 输入验证码
        // keyViewVerifyCode = (PwdKeyboardView) view.findViewById(R.id.key_board_verifycode);
        // keyboardView.setOnKeyListener(new PwdKeyboardView.OnKeyListener() {
        //     @Override
        //     public void onInput(String text) {
        //         Log.d(TAG, "onInput: text = " + text);
        //         editText.append(text);
        //         String content = editText.getText().toString();
        //         Log.d(TAG, "onInput: content = " + content);
        //     }

        //     @Override
        //     public void onDelete() {
        //         Log.d(TAG, "onDelete: ");
        //         String content = editText.getText().toString();
        //         if (content.length() > 0) {
        //             editText.setText(content.substring(0, content.length() - 1));
        //         }
        //     }
        // });

        /**
         * isEnablePwd： 0 1 1024
         * 0：不开启应用锁功能，直接进入支付宝
         * 1：开启应用锁功能，输入密码后进入支付宝
         * 1024：没有进行相关设置，无法进入支付宝，需要在APP端进行设置
         *
         */
        /*isEnablePwd = android.provider.Settings.System.getInt(
            com.xxun.xunlauncher.application.LauncherApplication.getInstance().getContentResolver()
            , com.xiaoxun.sdk.utils.Constant.SETTING_PAY_ONOFF
            , NULL_ONOFF);*/

        aliPwd = android.provider.Settings.System.getString(
                com.xxun.xunlauncher.application.LauncherApplication.getInstance().getContentResolver()
                , com.xiaoxun.sdk.utils.Constant.SETTING_PAY_PWD);

        // 此处是密码log，不要打印，只能debug用
        Log.d(TAG, "isEnablePwd: " + isEnablePwd + " aliPwd:" + aliPwd + ";");

        // 重要逻辑：
        // 当进入此界面时，要先判断是否锁定
        mLockView = (TextView) view.findViewById(R.id.lockview);
        mPlzSetPwd = (TextView) view.findViewById(R.id.plz_set_pwd);
        if (aliPwd == null) { // APP端没有设置密码
            mLockView.setText(R.string.no_pwd);
            mLockView.setVisibility(View.VISIBLE);
            keyboardView.setVisibility(View.GONE);

            // dismissAfter5Seconds();
        } /*else if (isEnablePwd == 0) {
            android.content.pm.PackageManager packageManager = getActivity().getPackageManager();
            startActivity(packageManager.getLaunchIntentForPackage("com.eg.android.AlipayGphone"));
        }*/ else if (mWrongCount >= DEFAULT_WRONG_TIME) { // 密码错误次数超过4次
            // 与上次密码输入错误的时间差：是否已经超过10分钟
            if (isLastVerifyCodeAvailable()) {
                // 上次的验证码，还有失效，5分钟内有效
                showLockInputCodeView();

                // 输入验证码界面，不消失
                // dismissAfter5Seconds();
            } else {
                // 上次的验证码已经失效
                // dismissLockView();
                showLockSendCodeView();

                // 重置密码输入错误次数：0次
                // mWrongCount = 0;
                // editor.putInt(WRONG_COUNT, mWrongCount);
                // editor.commit();
            }
        } else {
            // 显示密码输入界面
            showPwdInputView();
        }

        mOnBackListener = (OnBackListener) (AliPwdActivity) getActivity();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.windowAnimations = R.style.DialogFragmentAnimation;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        /*if (isEnablePwd == 0) {
            dismiss();
        }*/

    }

    @Override
    public void onComplete(String result) {
        Log.d(TAG, "onComplete: result = " + result);
        String content = editText.getText().toString();

        int minputMode = getInputMode();

        switch (minputMode) {
            // 当前是 密码输入模式
            case MODE_PWD:
                // 显示密码输入界面
                showPwdInputView();
                if (content.equals(aliPwd)) {
                    // Toast.makeText(getContext(), "成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                    android.content.pm.PackageManager packageManager = getActivity().getPackageManager();
                    // startActivity(packageManager.getLaunchIntentForPackage("com.eg.android.AlipayGphone"));
                    // modify by guohongcheng_20190403 
                    // 新增打开收款码功能
                    Log.d(TAG, "isReceiveMoney: " + isReceiveMoney);
                    if (isReceiveMoney) {
                        Log.d(TAG, "startActivity showpage receiveMoney");
                        Intent intent = new Intent();
                        intent.setPackage("com.eg.android.AlipayGphone");
                        intent.setData(Uri.parse("alipays://showpage=receiveMoney"));
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "startActivity showpage normal");
                        startActivity(packageManager.getLaunchIntentForPackage("com.eg.android.AlipayGphone"));
                    }
                    // 发送使用次数
                    if (statisticsManager == null) {
                        statisticsManager = (XiaoXunStatisticsManager) getActivity().getSystemService("xun.statistics.service");
                    }
                    statisticsManager.stats(XiaoXunStatisticsManager.STATS_ALI_PAY_OPEN);
                    
                    // 重置密码输入错误次数：0次
                    mWrongCount = 0;
                    editor.putInt(WRONG_COUNT, mWrongCount);
                    editor.commit();
                } else {
                    // Toast.makeText(getContext(), "密码错误！", Toast.LENGTH_SHORT).show();
                    // showWrongPwdDialog();
                    editText.setText(null);
                    mWrongCount++;

                    if (mWrongCount < 3) {
                        showWrongPwdDialog();
                    }

                    if (mWrongCount == 3) {
                        showWrongPwdDialog2();
                    }

                    // 将密码输入失败次数持久存储
                    editor.putInt(WRONG_COUNT, mWrongCount);
                    editor.commit();

                    // 密码输入次数超过4次
                    if (mWrongCount >= DEFAULT_WRONG_TIME) {
                        // 发送使用次数
                        if (statisticsManager == null) {
                            statisticsManager = (XiaoXunStatisticsManager) getActivity().getSystemService("xun.statistics.service");
                        }
                        statisticsManager.stats(XiaoXunStatisticsManager.STATS_ALI_PAY_LOCKING);
                        // 验证码值，置为空，防止误判
                        saveVerifyCode(NULL_VERIFY);
                        showLockSendCodeView();

                        // 输入错误4次，将最后一次输入错误的时间持久化存储
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = sDateFormat.format(new java.util.Date());
                        Log.d(TAG, "date " + date);
                        editor.putString(WRONG_LOCKTIME, date);
                        editor.commit();
                    } else {
                        // 显示密码输入界面
                        showPwdInputView();
                    }
                }
                break;

            // 当前是 验证码输入模式
            case MODE_VERIFYCODE:
                showVerifyCodeInputView();
                String savedVerifyCode = getSavedVerifyCode();
                Log.d(TAG, "savedVerifyCode " + savedVerifyCode);
                if (content.equals(savedVerifyCode)) {
                    // 重置密码输入错误次数：0次
                    mWrongCount = 0;
                    editor.putInt(WRONG_COUNT, mWrongCount);
                    editor.commit();
                    editText.setText(null);
                    // 显示密码输入界面
                    showPwdInputView();
                    // 验证码，使用过一次后，失效
                    saveVerifyCode(NULL_VERIFY);
                    showVerifyCodeInputView();
                    frameInputSucess.setVisibility(View.VISIBLE);
                    dismissFrameInputSucess();
                    
                } else {
                    showWrongVerifyCodeDialog();
                    editText.setText(null);
                }
                break;
        }


    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "v.getId(): " + v.getId());
        switch (v.getId()) {
            // 点击了【发送】验证码按钮
            case R.id.btn_send_verifycode:
                // 向服务器端发送验证码，并将该验证码写入本地
                sendVerifyCode();
                // 点击了 发送 验证码按钮后，显示验证码输入界面
                showVerifyCodeInputView();
                break;

            // 点击了【输入】验证码按钮
            case R.id.btn_input_verifycode:
                inputCode();
                // dismissLockView();
                // 点击了 输入 验证码按钮后，显示验证码输入界面
                showVerifyCodeInputView();
                break;

            case R.id.btn_input_confirm:
                frameInputSucess.setVisibility(View.GONE);                
                showPwdInputView();
                break;

            default:
                break;
        }
    }

    /**
     * 有一种情况：
     * 用户输错四次密码，但是没有点击发送验证码直接退出了，那么下次进入时，需要显示发送验证码
     * @return [description]
     */
    private boolean isLastVerifyCodeAvailable() {
        // 5分钟后，验证码失效
        Log.d(TAG, "isLastVerifyCodeAvailable " + isInLockTime() + "  " + getSavedVerifyCode());
        if (!isInLockTime()) {
            saveVerifyCode(NULL_VERIFY);
            return false;
        }
        String savedVerifyCode = getSavedVerifyCode();
        return (!NULL_VERIFY.equals(savedVerifyCode)) && isInLockTime();
    }

    private boolean isInLockTime() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String timeLast = sharedPreferences.getString(WRONG_LOCKTIME, "2006-05-26 12:00:00");
            String timeNow = format.format(new java.util.Date());
            Log.d(TAG, "timeLast " + timeLast + " timeNow " + timeNow);

            Date lastTime = format.parse(timeLast);
            Date nowTime = format.parse(timeNow);

            long diff = nowTime.getTime() - lastTime.getTime(); //两时间差，精确到毫秒
            long minDiff = diff / (60 * 1000);

            Log.d(TAG, " minDiff " + minDiff);
            return minDiff < MIN_5;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 5S后，弹窗自动消失
     */
    private void dismissAfter5Seconds() {
        /** 倒计时10秒，一次1秒 */
        CountDownTimer timer = new CountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onTick >> ");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish >> ");
                dismiss();
            }
        }.start();
    }

    private void showWrongPwdDialog() {
		if (mContext == null) {
			Log.d(TAG, "showWrongPwdDialog mContext == null");
			return;
		}
        Toast toast = Toast.makeText(mContext, "密码错误 \n重新输入", Toast.LENGTH_SHORT);
        LinearLayout view = (LinearLayout) toast.getView();
        // view.setBackgroundColor(Color.parseColor("#FFf0f0f0"));
        TextView textView = (TextView) view.getChildAt(0);
        int pwd_reinput_seize = (int) getResources().getDimension(R.dimen.pwd_reinput_seize);
        textView.setTextSize(pwd_reinput_seize);
        toast.setGravity(Gravity.CENTER, 0, 60);
        toast.setView(view);
        toast.show();
    }

    private void showWrongPwdDialog2() {
		if (mContext == null) {
			Log.d(TAG, "showWrongPwdDialog2 mContext == null");
			return;
		}
        Toast toast = Toast.makeText(mContext, "密码错误, 再次错误应用将被锁定", Toast.LENGTH_SHORT);
        LinearLayout view = (LinearLayout) toast.getView();
        // view.setBackgroundColor(Color.parseColor("#FFf0f0f0"));
        TextView textView = (TextView) view.getChildAt(0);
        int pwd_reinput_seize = (int) getResources().getDimension(R.dimen.pwd_reinput_seize);
        textView.setTextSize(pwd_reinput_seize);
        toast.setGravity(Gravity.CENTER, 0, 60);
        toast.setView(view);
        toast.show();
    }

    private void showWrongVerifyCodeDialog() {
		if (mContext == null) {
			Log.d(TAG, "showWrongVerifyCodeDialog mContext == null");
			return;
		}
        Toast toast = Toast.makeText(mContext, "验证码错误 \n重新输入", Toast.LENGTH_SHORT);
        LinearLayout view = (LinearLayout) toast.getView();
        // view.setBackgroundColor(Color.parseColor("#FFf0f0f0"));
        TextView textView = (TextView) view.getChildAt(0);
        int pwd_reinput_seize = (int) getResources().getDimension(R.dimen.pwd_reinput_seize);
        textView.setTextSize(pwd_reinput_seize);
        toast.setGravity(Gravity.CENTER, 0, 60);
        toast.setView(view);
        toast.show();
    }

    private void sendVerifyCode() {
        getDefaultGidBy20041();
    }

    private void inputCode() {
        // getDefaultGidBy20041();
    }

    private void getDefaultGidBy20041() {
        Log.d(TAG, "getDefaultGidBy20041 ..");
        String sendBuff = null;
        String msg_key = null;
        JSONObject msg = new JSONObject();
        XiaoXunNetworkManager networkService = (XiaoXunNetworkManager) getActivity().getSystemService("xun.network.Service");
        msg.put(CloudBridgeUtil.KEY_NAME_SN, networkService.getMsgSN());
        msg.put(CloudBridgeUtil.KEY_NAME_VERSION, CloudBridgeUtil.PROTOCOL_VERSION);
        msg.put(CloudBridgeUtil.KEY_NAME_CID, 20041);
        msg.put(CloudBridgeUtil.KEY_NAME_SID, networkService.getSID());

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_GID, networkService.getWatchGid());
        msg.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        sendBuff = msg.toJSONString();
        Log.d(TAG, "sendContent= " + sendBuff);
        networkService.sendJsonMessage(sendBuff, new SendMsgCallback());
    }

    /*
    {
    "CID":70091,
    "Version":"00020000",
    "SN":1610112335,
    "SID":"8EDA4F434FFB447D8F5AB880330DE2E8",
    "PL":{
        "TGID":"EE6C3D342235940B294CC786FAC03725",
        "Value":{
            "EID":"42AE72BEFCFBC1200AEC3247BB16EA13",
            "Duration":0,
            "Type":"text",
            "Content":"支付宝因连续输错4次密码应用已被锁定，验证码：9382，输入验证码后解锁应用，原应用密码不变，验证码5分钟内有效。"
        },
        "Key":"GP/EE6C3D342235940B294CC786FAC03725/MSG/#TIME#"
    }
    }
     */
    private void sendJson2APP(String user_gid) {
        String sendBuff = null;
        String msg_key = null;
        JSONObject msg = new JSONObject();
        XiaoXunNetworkManager networkService = (XiaoXunNetworkManager) getActivity().getSystemService("xun.network.Service");
        // CID_SEND_BYTE_MESSAGE:70091
        msg.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_SEND_BYTE_MESSAGE);
        msg.put(CloudBridgeUtil.KEY_NAME_VERSION, CloudBridgeUtil.PROTOCOL_VERSION);
        msg.put(CloudBridgeUtil.KEY_NAME_SN, networkService.getMsgSN());
        msg.put(CloudBridgeUtil.KEY_NAME_SID, networkService.getSID());

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, user_gid);

        JSONObject value = new JSONObject();
        value.put(CloudBridgeUtil.KEY_NAME_EID, networkService.getWatchEid());
        value.put(CloudBridgeUtil.KEY_NAME_TYPE, "text");
        value.put(CloudBridgeUtil.KEY_NAME_DURATION, 0);
        // 更改为文字内容
        value.put(CloudBridgeUtil.KEY_NAME_CONTENT, createSendContent());
        pl.put(CloudBridgeUtil.KEY_NAME_VALUE, value);

        msg_key = "GP/" + user_gid + "/MSG/#TIME#";
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, msg_key);
        msg.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        sendBuff = msg.toJSONString();

        Log.d(TAG, "sendJson2APP sendBContent = " + sendBuff);
        networkService.sendJsonMessage(sendBuff, new SendMsgCallback());
    }

    /*
    {
    "RC": 1,
    "Version": "00020000",
    "SN": 235959034,
    "PL": {
        "Type": 0,
        "GID": "49239B74A0C88176A925E3BA5174C14A",
        "AdminEid": "6160A32FDCA26181C74AF4E14AACE87F",
        "CreateTime": "20171107085340051"
    },
    "CID": 20042
    }
     */
    class SendMsgCallback extends IResponseDataCallBack.Stub {
        public void onSuccess(ResponseData responseData) {
            Log.d(TAG, "callBack succ112 " + responseData);
            JSONObject resJson = (JSONObject) JSONValue.parse(responseData.getResponseData());
            String resCID = resJson.get("CID").toString();
            String resRC = resJson.get("RC").toString();
            Log.d(TAG, "callBack_20042 resCID " + resCID + " resRC " + resRC);
            // getDefaultGidBy20041 的反馈结果
            if ("1".equals(resRC) && "20042".equals(resCID)) {
                // Log.d(TAG, "callBack_20042 resAdminEid..");
                JSONObject resPL = (JSONObject) resJson.get("PL");
                String resAdminEid = resPL.get("AdminEid").toString();
                Log.d(TAG, "callBack_20042 resAdminEid" + resAdminEid);
                // 拿到默认分组的eid后，在本地的联系人中，查询该eid对应的gid
                String user_gid = getUserGidBySuerEid(resAdminEid);
                if (user_gid != null) {
                    // 得到GID后，发送验证码
                    sendJson2APP(user_gid);
                }
            }

            // 验证码发送成功
            if ("1".equals(resRC) && "70092".equals(resCID)) {
                saveVerifyCode(g_sendVerifyCode);
                modifyTime();
            }
        }

        public void onError(int i, String s) {
            Log.d(TAG, "callBack fail error i= " + i + " error string= " + s);
        }

        
    }

    private String getUserGidBySuerEid(String sied){
        Log.d(TAG, "getUserGidBySuerEid eid:" + sied);
        String AUTHORITY = "com.xiaoxun.contacts.provider";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/xxcontacts");
        String USER_EID = "user_eid";
        String USER_GID = "user_gid";
        String result = null;

        Cursor mcursor = getActivity().getContentResolver().query(
            CONTENT_URI
            , new String[]{USER_GID}
            , USER_EID + " =?", new String[]{sied}
            , null);
        if(mcursor != null){
            while(mcursor.moveToNext()){
                result = mcursor.getString(mcursor.getColumnIndex(USER_GID));
                Log.d(TAG, "getUserGidBySuerEid result:" + result);
            }
            mcursor.close();
        }
        return result;                
    }

    /*public String getUserGidBySuerEid(String sied) {
        Log.d(TAG, "getUserGidBySuerEid eid:" + sied);
        String AUTHORITY = "com.xiaoxun.contacts.provider";
        Uri uri = Uri.parse("content://" + AUTHORITY + "/xxcontacts");
        ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        String result = "";
        if (cursor != null) {
            Log.d(TAG, "getUserGidBySuerEid cursor != null");
            while (cursor.moveToNext()) {
                Log.d(TAG, "getUserGidBySuerEid moveToNext");
                int contactsId = cursor.getInt(0);
                uri = Uri.parse("content://" + AUTHORITY  + contactsId + "/data");
                Cursor dataCursor = resolver.query(uri, project, null, null, null);
                Log.d(TAG, "getUserGidBySuerEid dataCursor");

                String[] mdata = new String[2];
                while (dataCursor.moveToNext()) {
                    String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                    Log.d(TAG, "getUserGidBySuerEid type:" + type);
                    if ("vnd.android.cursor.item/nickname".equals(type)) {
                        mdata[0] = dataCursor.getString(dataCursor.getColumnIndex(project[4]));
                        mdata[1] = dataCursor.getString(dataCursor.getColumnIndex(project[5]));
                        Log.d(TAG, "mdata[0] " + mdata[0]);
                        Log.d(TAG, "mdata[1] " + mdata[1]);
                    }
                }

                if (mdata[0] != null) {
                    if (mdata[0].equals(sied)) {
                        result = mdata[1];
                        Log.d(TAG, "result " + result);
                    }
                }

                if (mdata[0] != null) {
                    if (mdata[0].equals(sied)) {
                        break;
                    }
                }
            }
            cursor.close();
            return result;
        }
        return null;
    }*/

    /**
     * 显示锁定界面，并提示 发送 验证码
     */
    private void showLockSendCodeView() {
        if (mLockView != null && keyboardView != null) {
            frameSendCode.setVisibility(View.VISIBLE);
            // mLockView.setText(R.string.plz_send_veriftcode);
            // mLockView.setVisibility(View.VISIBLE);
            btnSendCode.setVisibility(View.VISIBLE);
            btnInputCode.setVisibility(View.GONE);
            keyboardView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示锁定界面，并提示 输入 验证码
     */
    private void showLockInputCodeView() {
        if (mLockView != null && keyboardView != null) {
            frameSendCode.setVisibility(View.VISIBLE);
            // mLockView.setText(R.string.plz_send_veriftcode);
            // mLockView.setVisibility(View.VISIBLE);
            btnSendCode.setVisibility(View.GONE);
            btnInputCode.setVisibility(View.VISIBLE);
            keyboardView.setVisibility(View.GONE);
        }
    }

    private void dismissLockView() {
        if (mLockView != null && keyboardView != null) {
            frameSendCode.setVisibility(View.GONE);
            mLockView.setVisibility(View.GONE);
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 展示验证码输入界面
     */
    private void showVerifyCodeInputView() {
        if (mLockView != null && keyboardView != null) {
            frameSendCode.setVisibility(View.GONE);
            mLockView.setVisibility(View.GONE);
            mGuideText.setText(R.string.input_verifycode);
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 展示密码输入界面
     */
    private void showPwdInputView() {
        if (mLockView != null && keyboardView != null) {
            frameSendCode.setVisibility(View.GONE);
            mLockView.setVisibility(View.GONE);
            mGuideText.setText(R.string.input_pwd);
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 将验证码保存到本地
     * @param savedCode [随机验证码]
     */
    private void saveVerifyCode(String savedCode) {
        Log.d(TAG, "saveVerifyCode savedCode:" + savedCode);
        if (editor != null) {
            editor.putString(VERIFYCODE, savedCode);
            editor.commit();
        }
    }

    /**
     * [获取本地存储的验证码]
     * @return [返回本地存储的验证码]
     */
    private String getSavedVerifyCode() {
        try {
            String savedVerifyCode = sharedPreferences.getString(VERIFYCODE, NULL_VERIFY);
            return savedVerifyCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NULL_VERIFY;
    }

    /*
      判断当前模式，是验证码输入还是密码输入
     */
    private int getInputMode() {
        try {
            if (sharedPreferences == null) {
                sharedPreferences = getActivity().getSharedPreferences(FLAG_ZHIFUBAO_PWD, MODE_PRIVATE);
            }
            mWrongCount = sharedPreferences.getInt(WRONG_COUNT, 0);
            Log.d(TAG, "getInputMode mWrongCount:" + mWrongCount);
            if (mWrongCount >= 4) {
                return MODE_VERIFYCODE;
            } else {
                return MODE_PWD;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 404;
    }

    /*
     * 1、生成发送验证码内容
     * 2、将该验证码保存到本地
     * 3、将此时的时间保存到本地
     */
    private String createSendContent() {
        //生成1111~9998之间的随机数
        Random random = new Random();
        int num = random.nextInt(9998 - 1111) + 1111;
        g_sendVerifyCode = String.valueOf(num);
        Log.d(TAG, "createSendContent g_sendVerifyCode:" + g_sendVerifyCode);
        // 先置为null，然后发送验证码成功回执后，置为真正的值
        saveVerifyCode(NULL_VERIFY);
        return send_content1 + g_sendVerifyCode + send_content2;
    }

    /**
     * 更新验证码产生的时间，用于判断5分钟内有效
     */
    private void modifyTime() {
        // 输入错误4次，将最后一次输入错误的时间持久化存储
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        Log.d(TAG, "date " + date);
        if (editor != null) {
            editor.putString(WRONG_LOCKTIME, date);
            editor.commit();
        }
        
    }

    /**
     * 5S后，弹窗自动消失
     */
    private void dismissFrameInputSucess() {
        /** 倒计时10秒，一次1秒 */
        CountDownTimer timer = new CountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onTick >> ");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish >> ");
                frameInputSucess.setVisibility(View.GONE);
                showPwdInputView();
            }
        }.start();
    }

    public interface OnBackListener {
        void onBackListener();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss..");
        mOnBackListener.onBackListener();
        super.onDismiss(dialog);;
    }

}
