package com.xxun.xunlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xxun.xunlauncher.activity.QQMsgActivity;
import com.xxun.xunlauncher.activity.NewQQMsgAlertActivity;
import com.xxun.xunlauncher.Constants;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

/**
 * @author lihaizhou
 * @createtime 2017.11.20
 * @class describe 接收QQ新消息的广播类(QQ有新消息时,QQ应用会发出广播)
 */
public class QQMsgReceiver extends BroadcastReceiver {
    
    private Context mContext;
    private static final String TAG = "QQMsgReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
	mContext = context;
        if (Constants.BROADCAST_NEW_MESSAGE_NOFITY_WATCH.equalsIgnoreCase(intent.getAction())) {
	    if(!isUnderSilence() && !isUnderCallOrVideoCall() && Settings.System.getInt(context.getContentResolver(), "qq_exit", 1) == 1){
	        if(isChargeForbidden()){
                   Intent newMsgAlertIntent = new Intent();
                   newMsgAlertIntent.setClass(context, NewQQMsgAlertActivity.class);
                   context.startActivity(newMsgAlertIntent);
                }else{
                   Intent bindrequestIntent = new Intent();
                   bindrequestIntent.setClass(context, QQMsgActivity.class);
                   bindrequestIntent.putExtra("qqintent", intent);
                   context.startActivity(bindrequestIntent);
                }
	     }    
         }
    }

    /**
     * @author lihaizhou
     * @createtime 2018.06.22
     * @describe 判断当前是否处于充电禁用状态
     */
    private boolean isChargeForbidden() {
        try{
            if (SystemProperties.get("ro.build.type").equals("user") 
                && !"true".equals(Settings.System.getString(mContext.getContentResolver(), "isMidtest")) 
                && Settings.System.getInt(mContext.getContentResolver(),"is_localprop_exist") == 0
                &&  "true".equals(SystemProperties.get("persist.sys.isUsbConfigured"))
                ) {
                return true; 
            }
        }catch (SettingNotFoundException e){
            e.printStackTrace();
        }

        return false; 
    }
   
    /**
    *  @author lihaizhou
    *  @time 2018.08.13
    *  @describe 判断当前是否Under Silence
    */
   private boolean isUnderSilence(){
     String result = Settings.System.getString(mContext.getContentResolver(), "SilenceList_result");
     boolean SilenceList_result = (result == null ? false : Boolean.parseBoolean(result));
     return SilenceList_result; 
   }

   /**
    *  @author lihaizhou
    *  @time 2018.08.13
    *  @describe 判断当前是否处于Call or VideoCall
    */
   private boolean isUnderCallOrVideoCall(){
     String isIncall = Settings.System.getString(mContext.getContentResolver(), "isIncall") == null?"false":(Settings.System.getString(mContext.getContentResolver(), "isIncall"));
     String isInVideocall = Settings.System.getString(mContext.getContentResolver(), "xun_video") == null?"false":(Settings.System.getString(mContext.getContentResolver(), "xun_video"));
     if ("true".equals(isIncall) || "true".equals(isInVideocall)) {
	 return true;
     }
     return false;
   }

}
