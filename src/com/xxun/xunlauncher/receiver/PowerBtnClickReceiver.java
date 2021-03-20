package com.xxun.xunlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.telephony.TelephonyManager;
import com.xxun.xunlauncher.Constants;
import com.xxun.xunlauncher.utils.SosRecordUtils;
import com.xxun.xunlauncher.activity.MainActivity;
//[add by jxring for silenceDisturb 2017.11.22 start]
import java.util.Calendar;
import com.xiaoxun.jason.silence.SilenceDisturb;
import java.sql.Time;
import java.util.List;
//[add by jxring for silenceDisturb 2017.11.22 end!]

/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe 接收电源键相关操作下发的广播
 */
public class PowerBtnClickReceiver extends BroadcastReceiver {

    public static final String SILENCE_DISTURB = "com.xunlauncher.silence";

    private static final String TAG = "PowerBtnClickReceiver";

    public PowerBtnClickReceiver() {}

    //[add by jxring for silenceDisturb 2017.11.22 start]
    private long markTime = 0;
    public static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin, String days) {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                i = 6;
                break;
            case 2:
                i = 0;
                break;
            case 3:
                i = 1;
                break;
            case 4:
                i = 2;
                break;
            case 5:
                i = 3;
                break;
            case 6:
                i = 4;
                break;
            case 7:
                i = 5;
                break;
            default:
                break;
        }
        if (Integer.valueOf(String.valueOf(days.toCharArray()[i])) == 0) {
            return false;

        }

        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Time now = new Time(currentTimeMillis);

        Time startTime = new Time(currentTimeMillis);
        startTime.setHours(beginHour);
        startTime.setMinutes(beginMin);

        Time endTime = new Time(currentTimeMillis);
        endTime.setHours(endHour);
        endTime.setMinutes(endMin);

        if (endTime.before(startTime)) {
            startTime.setTime(startTime.getTime() - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime);

            Time startTimeInThisDay = new Time(startTime.getTime() + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            result = !now.before(startTime) && !now.after(endTime);
        }
        return result;
    }

    ;
    //[add by jxring for silenceDisturb 2017.11.22 end!]

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Constants.SEND_SOS_ACTION.equals(action)) {
            Log.d(TAG, "receive SOS broadcast from PMS!");
	    //SOS发送前判断是否处于通话状态，是的话发送挂断电话广播给电话模块 begin
	    TelephonyManager telecomManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            boolean isOnCallStateIdle = telecomManager.getCallState() == TelephonyManager.CALL_STATE_IDLE ? true : false;
	    if(!isOnCallStateIdle){
	       Intent hangupIntent = new Intent("com.xiaoxun.end.call");
               context.sendBroadcast(hangupIntent);
	    }
	    //SOS发送前判断是否处于通话状态，是的话发送挂断电话广播给电话模块 end

            //发送SOS之前发送广播给到离线模块，离线模块中进行是否需要退离线的判断
            Intent exitIntent = new Intent();
            exitIntent.putExtra("exitOfflineType","sos");
            context.sendBroadcast(exitIntent);
            SosRecordUtils.getRecordUtilsInstance().sendSos(context);
        //[add by jxring for silenceDisturb 2017.11.22 start]
        } else if (Intent.ACTION_BATTERY_CHANGED.equals(action) || SILENCE_DISTURB.equals(action) || Intent.ACTION_TIME_TICK.equals(action)) {
            long currentTime  = System.currentTimeMillis();
            long spendTime  = currentTime - markTime;
            if((spendTime < 1000*60) && Intent.ACTION_BATTERY_CHANGED.equals(action)) return;
            markTime = currentTime;
            String value = android.provider.Settings.System.getString(context.getContentResolver(), "SilenceList");
            String result = android.provider.Settings.System.getString(context.getContentResolver(), "SilenceList_result");
            boolean SilenceList_result = (result == null?false:Boolean.parseBoolean(result));
            boolean initValue = false;
            int advanceop = android.provider.Settings.System.getInt(context.getContentResolver(), "advanceop",-1);
            if (value != null) {
                 List<SilenceDisturb> mlist = SilenceDisturb.arraySilenceDisturbFromData(value);
                 if(mlist != null){
                     for(SilenceDisturb msilence:mlist){
                            if(isCurrentInTimeScope(Integer.valueOf(msilence.starthour)
                                ,Integer.valueOf(msilence.startmin),Integer.valueOf(msilence.endhour),Integer.valueOf(msilence.endmin),msilence.days)){
                                    initValue = true;
                                    if(advanceop != msilence.advanceop){
                                        android.provider.Settings.System.putInt(context.getContentResolver(), "advanceop",msilence.advanceop);
                                    }
                            }
                     }
                 }
            }
            if(initValue != SilenceList_result){
                android.provider.Settings.System.putString(context.getContentResolver(), "SilenceList_result",String.valueOf(initValue));
                if(initValue){
                   Intent silence = new Intent("com.xiaoxun.time.silence");
                   context.sendBroadcast(silence);
                
                   Intent intent_home = new Intent(Intent.ACTION_MAIN);
		   intent_home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		   intent_home.addCategory(Intent.CATEGORY_HOME);
		   context.startActivity(intent_home);
                }
            }

        }
        //[add by jxring for silenceDisturb 2017.11.22 end!]
    }

}
