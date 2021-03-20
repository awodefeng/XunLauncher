package com.xxun.xunlauncher.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.xxun.xunlauncher.Constants;
import com.xxun.xunlauncher.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import android.view.Gravity;
import android.content.Context;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.widget.Toast;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.graphics.drawable.BitmapDrawable;
//del by lihaizhou due to not exist on 4.4 begin
//import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
//import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
//del by lihaizhou due to not exist on 4.4 end

/**
 *  @author lihaizhou
 *  @time 2017.12.12
 *  @describe XunLauncher中显示QQ消息,好友请求,请求好友结果界面
 */
	 
public class QQMsgActivity extends Activity implements View.OnClickListener{

    private ImageView closeImg;
    private ImageView face;
    private TextView showDealwithFriendMsgTv;
    private TextView showRequestFriendResultTv;
    private TextView nickNameTv;
    private Button agreeAddBtn;
    private Button refuseBtn;
    private Button replyBtn;    

    private String faceUrl;
    private String contactName;
    private String contactID;
    private String buddydetailinfo;
    private String conversationType;
    private Toast toast;
    private Intent receiveIntent;
    
    private PowerManager.WakeLock wakeLock;
    private PowerManager powerManager = null;

    private static final String TAG = "QQMsgActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wear_qq_sysmsg_add_request);
        initViews();
	lightScreen();
        controlLayoutDisplay();
    }

    /**
     *  @author lihaizhou
     *  @time 2017.12.12
     *  @describe 初始化控件资源
     */
    private void initViews(){
        closeImg = (ImageView) findViewById(R.id.close_btn);
        face = (ImageView) findViewById(R.id.face);
        showDealwithFriendMsgTv = (TextView) findViewById(R.id.msg_dealwith_friend);
        showRequestFriendResultTv = (TextView) findViewById(R.id.msg_dealwith_friend_result);
        nickNameTv = (TextView) findViewById(R.id.nickname);
        agreeAddBtn = (Button) findViewById(R.id.agree_add);
        refuseBtn = (Button) findViewById(R.id.refuse);
	replyBtn = (Button) findViewById(R.id.reply);
        closeImg.setOnClickListener(this);
        agreeAddBtn.setOnClickListener(this);
        refuseBtn.setOnClickListener(this);
	replyBtn.setOnClickListener(this);
    }

    /**
     *  @author lihaizhou
     *  @time 2017.12.12
     *  @describe 根据传入的消息类型动态改变布局
     */
    private void controlLayoutDisplay(){
        try {
	    receiveIntent = getIntent().getParcelableExtra("qqintent");
            conversationType = receiveIntent.getExtras().getString("conversationType");
        }catch (NullPointerException e){
            Log.d(TAG, "展示QQ消息时getIntent().getExtras()发生空指针");
            Toast.makeText(this, "展示QQ消息时getIntent().getExtras()发生空指针", Toast.LENGTH_LONG).show();
            return;
        }
        faceUrl = receiveIntent.getExtras().getString("contactAvatarUri");

        if (!"".equals(faceUrl)){
	    File file = new File(faceUrl);
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(faceUrl);	
		//del by lihaizhou due to RoundedBitmapDrawable not exist on 4.4 				
		//RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bm);
		//roundedBitmapDrawable.setCircular(true);
		//face.setImageDrawable(roundedBitmapDrawable);

		//Add by lihaizhou for 4.4 20180712
		face.setImageDrawable(new BitmapDrawable(bm));
            }
        }

        contactID = receiveIntent.getExtras().getString("contactID");
        contactName = receiveIntent.getExtras().getString("contactName");
        nickNameTv.setText(contactName);

        buddydetailinfo = receiveIntent.getExtras().getString("conversationContent");
        showDealwithFriendMsgTv.setText(buddydetailinfo);
	
	Log.d(TAG, "faceUrl = "+faceUrl+",contactID = :"+contactID+",contactName = :"+contactName+",buddydetailinfo = "+buddydetailinfo);
	
	if(Constants.NOTIFICATION_TYPE_ADDFRIEND.equals(conversationType)){
	   replyBtn.setVisibility(View.GONE);
           agreeAddBtn.setVisibility(View.VISIBLE);
           refuseBtn.setVisibility(View.VISIBLE);
	}else if(Constants.NOTIFICATION_TYPE_MESSAGE.equals(conversationType)){
	   replyBtn.setVisibility(View.VISIBLE);
           agreeAddBtn.setVisibility(View.GONE);
           refuseBtn.setVisibility(View.GONE);
	}else if(Constants.NOTIFICATION_TYPE_ADDFRIEND_RESULT.equals(conversationType)){
	   replyBtn.setVisibility(View.INVISIBLE);
           agreeAddBtn.setVisibility(View.INVISIBLE);
           refuseBtn.setVisibility(View.INVISIBLE);
	}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_btn:
                finish();
                break;
            case R.id.agree_add:
                if(isUnderHighTemp()){
                   break;
                }else if(isChargeForbidden()){
                   showForbidToast();
                   break;
                }
		sendQQBroadcast(Constants.ACTION_ACCEPT);
		finish();
                break;
            case R.id.refuse:
                 if(isUnderHighTemp()){
                    break;
                 }else if(isChargeForbidden()){
                    showForbidToast();
                    break;
                 }
	         sendQQBroadcast(Constants.ACTION_REJECT);
                 finish();
	    case R.id.reply:
                 if(isUnderHighTemp()){
                    break;
                 }else if(isChargeForbidden()){
                    showForbidToast();
                    break;
                 }
                 sendQQBroadcast(Constants.ACTION_REPLY);
                 finish();
            default:
                break;
        }
    }

    private void sendQQBroadcast(String action){
        Intent intent = new Intent();
        intent.setAction(action);
        //newMsgIntent.setPackage("com.tencent.qqlite");//指定广播的接受者
	Intent sendToQQIntent = receiveIntent.getParcelableExtra("intent");
        intent.putExtra("intent",sendToQQIntent);
        sendBroadcast(intent);
    }
	
     /**
     *  @author lihaizhou
     *  @time 2017.12.12
     *  @describe 该消息界面SingleInstance, 全局只存在一个, 当处于前台再次收到消息时会回调该方法
     *  setIntent(intent);该行至关重要,刷新获取的intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
	setIntent(intent);
        controlLayoutDisplay();
	lightScreen();
    }

    private void lightScreen(){
        if (powerManager == null) {
            powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        }
	if(!powerManager.isScreenOn()){
	    wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "filtercamera");
            wakeLock.acquire(500);
	}
    }
    
    //此属性存储了当前是否高温，2为正常，7为超过43度，3为超过46度
    private boolean isUnderHighTemp(){
	int currentTmp = Integer.valueOf(SystemProperties.get("persist.sys.xxun.aptemper"));         
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 10:54:53 +0800
	if(currentTmp == 7 || currentTmp == 3){
 */
	if (currentTmp >= Constants.XUN_TEMPER_OVERHEAT) {
// End of longcheer:lishuangwei
           return true;
	}
	return false;
    }

    /**
     *  @author lihaizhou
     *  @time 2018.06.14
     *  @describe 充电器在位有禁用窗口存在，此时弹出该QQ消息界面时，上面的操作按钮不可操作并提示用户移除充电器
     */  
    private void showForbidToast(){
        if(toast == null){
           toast = Toast.makeText(this, "请移除充电器使用", Toast.LENGTH_LONG); 
           TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
           v.setTextSize(30);
           toast.setGravity(Gravity.TOP, 0, 0); 
        } 
        toast.show();  
    } 

    /**
     *  @author lihaizhou
     *  @time 2018.06.14
     *  @describe 判断当前充电器是否在位
     */  
    private boolean isChargeForbidden() {
        try{
            if (SystemProperties.get("ro.build.type").equals("user") 
                && !"true".equals(Settings.System.getString(getContentResolver(), "isMidtest")) 
                && Settings.System.getInt(getContentResolver(),"is_localprop_exist") == 0
                &&  "true".equals(SystemProperties.get("persist.sys.isUsbConfigured"))) {
                return true; 
            }
        }catch (SettingNotFoundException e){
            e.printStackTrace();
        }
        return false; 
    }

}
