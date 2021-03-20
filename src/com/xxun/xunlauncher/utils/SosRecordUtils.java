package com.xxun.xunlauncher.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import net.minidev.json.JSONObject;
import android.content.Context;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import android.util.Base64;
import android.widget.Toast;
import android.os.Vibrator;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xxun.xunlauncher.utils.NetworkUtils;

/**
 * 录音工具类
 * 长按电源键3s后，给服务器发送7s录音数据
 */

public class SosRecordUtils {

    private File audioFile;
    private MediaRecorder recorder;
    private Handler recordHandler;
    private boolean isRecoding = false;
    private XiaoXunNetworkManager mXiaoXunNetworkManager;
    
    public static final String KEY_PL="PL";
    public static final String KEY_NAME="Key";
    public static final String KEY_VALUE="Value";
    public static final String KEY_CID="CID";
    public static final String KEY_SID="SID";
    public static final String KEY_SN="SN";
    public static final String KEY_TYPE="Type";
    public static final String KEY_EID="EID";
    public static final String KEY_TGID="TGID";
    public static final String KEY_CONTENT= "Content";
    public static final String KEY_VERSION="Version";
    public static final String KEY_DURATION="Duration";
    
    private static final int MESSAGE_STOP_RECORD_WHAT = 0;
    public static final int sosRecordDuration = 7;
    private Vibrator mVibrator;  //声明一个振动器对象
	
    private static final String TAG = "SosRecordUtils";
    
    private SosRecordUtils() {}

    public static SosRecordUtils getRecordUtilsInstance() {
        return RecordUtilshodler.recordUtilsInstance;
    }

    private static class RecordUtilshodler {
        private static SosRecordUtils recordUtilsInstance = new SosRecordUtils();
    }

    /*
    * 长按开机键3秒,发出定位数据和7秒录音
    * 录音,并发送7s延迟消息给Handler进行停止录音处理
    * */
    public void sendSos(Context context){
        mXiaoXunNetworkManager = (XiaoXunNetworkManager)context.getSystemService("xun.network.Service");
	mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	//四个参数就是——停止 开启 停止 开启 -1不重复
	mVibrator.vibrate(new long[]{0, 500, 0, 0}, -1);
	//new Handler().postDelayed(new Runnable(){    
    	  //@Override
          //public void run() {
	    //mVibrator.cancel();   
    	//}}, 1000); 

        long delayTime = 7500;
        recorder = new MediaRecorder();

        //初始化设置参数,播放源:麦克风, 输入格式, 编码:AMR
	recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncodingBitRate(5150);
	recorder.setMaxDuration(15900);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //初始化录音环境,如文件以及路径创建
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SosRecorderFile");
        if(!path.exists()) {
            path.mkdirs();
        }
        try {
            audioFile=new File(path,"sos.amr");
            if(audioFile.exists()) {
                audioFile.delete();
            }
            audioFile.createNewFile();//创建文件
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare();
        } catch (IllegalStateException e) {
            //throw new RuntimeException("IllegalStateException on MediaRecorder.prepare", e);
	    //Toast.makeText(context, "while SOS record,IllegalStateException happens!", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"while SOS record,IllegalStateException happens!");
        } catch (IOException e) {
            //throw new RuntimeException("IOException on MediaRecorder.prepare", e);
	    //Toast.makeText(context, "while SOS record,IOException happens!", Toast.LENGTH_SHORT).show();
	    Log.d(TAG,"while SOS record,IOException happens!");
        }

        //开始录音
	Log.d(TAG,"isRecoding = : "+isRecoding);
	if(!isRecoding){
	    new Handler().postDelayed(new Runnable(){    
    	        @Override
                public void run() {
		  try{
		     isRecoding = true; 
		     recorder.start();
		  }catch(IllegalStateException e){
		     if(recorder!=null){
			recorder.setOnErrorListener(null);
			recorder.setOnInfoListener(null);
			recorder.setPreviewDisplay(null);
	       		//recorder.stop();//del by lihaizhou 20190409
               		recorder.release();
               		isRecoding = false;
               		recordHandler.removeMessages(MESSAGE_STOP_RECORD_WHAT);
	    	     }
            	     recorder = null;
		     isRecoding = false; 
            	     Log.d(TAG,"while recorder start,IllegalStateException happens!");
		  }  
    	       }},500); 
	}

        //7s后发送消息给Handler处理停止录音
        recordHandler = new RecordHandler();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                recordHandler.sendEmptyMessage(MESSAGE_STOP_RECORD_WHAT);
            }
        },delayTime);

    }

    class RecordHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_STOP_RECORD_WHAT:
                    stopRecord();
                    try {
		        Log.d(TAG,"send to server");
		        mXiaoXunNetworkManager.sendJsonMessage(getRecordData(),new SendMessageCallback());
                    } catch (Exception e) {
                        Log.d(TAG,"while send to server,Exception happens and ex = : "+e);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class SendMessageCallback extends IResponseDataCallBack.Stub{
           @Override
           public void onSuccess(ResponseData responseData) {
		Log.d(TAG,"send success!"+responseData);
	   }
           @Override
           public void onError(int i, String s) {
		Log.d(TAG,"send error! i = "+i+"s = :"+s);
	   }    
    }

    private void stopRecord() {
        if(isRecoding) {
	    try {
	    if(recorder!=null){
	       recorder.stop();
               recorder.release();
               isRecoding = false;
               recordHandler.removeMessages(MESSAGE_STOP_RECORD_WHAT);
	    }
            recorder = null;
	   }catch(IllegalStateException e){
		Log.d(TAG,"IllegalStateException happens");
	   }
        }
    }


    /*
    * 将录音数据以Base64 String形式发送至服务器
    * */
    private String getRecordData(){
	JSONObject msg = new JSONObject();
	msg.put(KEY_CID, CloudBridgeUtil.CID_SEND_BYTE_MESSAGE);
        msg.put(KEY_SID, mXiaoXunNetworkManager.getSID());
        msg.put(KEY_SN, mXiaoXunNetworkManager.getMsgSN());
        msg.put(KEY_VERSION,CloudBridgeUtil.PROTOCOL_VERSION);
        JSONObject value = new JSONObject();
        value.put(KEY_EID, mXiaoXunNetworkManager.getWatchEid());
        value.put(KEY_TYPE, "sos");
        value.put(KEY_DURATION, sosRecordDuration);
	try{
	  String base64AudioData = encodeBase64File(audioFile.getAbsolutePath());
	  value.put(KEY_CONTENT, base64AudioData);
	}catch(Exception e){
	  Log.d(TAG,"exception = :"+e);
	}
	JSONObject pl = new JSONObject();
        pl.put(KEY_VALUE,value);
        pl.put(KEY_TGID,mXiaoXunNetworkManager.getWatchGid());
        String msg_key="GP/"+mXiaoXunNetworkManager.getWatchGid()+"/MSG/#TIME#";
        pl.put(KEY_NAME,msg_key);
        msg.put(KEY_PL, pl);
        String sendData= msg.toJSONString();
        return sendData;
    }

   /**
   * encodeBase64File:(将文件转成base64字符串)
   * @param path 文件路径
   * @return String
   * @throws Exception
   */

   public String encodeBase64File(String path) throws Exception{
	File file = new File(path);
	FileInputStream inputFile = new FileInputStream(file);
	byte[] buffer = new byte[(int)file.length()];
	inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer,Base64.DEFAULT);
   }


}
