package com.xxun.xunlauncher.utils;

import android.app.ActivityManager.MemoryInfo;
import java.io.File;
import android.os.Environment;
import android.os.StatFs;
//import com.xxun.xunlauncher.utils.InterfaceInfo;
import com.xxun.xunlauncher.utils.ImageInfo;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;
import android.util.Log;
import com.xiaoxun.jason.Contacts.Contacts;
import com.xiaoxun.jason.Contacts.SyncArrayBean;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;
import android.app.ActivityManager;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import com.xiaoxun.sdk.bean.MessageData;
import com.xiaoxun.sdk.interfaces.IMessageCallBack;
import java.util.ArrayList;
import java.util.List;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.IOException;



/**
 * @author zhanghaijun
 * @createtime 2019-04-15
 * @class describe 获取联系人数据接口
 */
public class ContactsInterface {

	private Contacts mUpdateContacts = null;
    private XiaoXunNetworkManager mxiaoXunNetworkManager = null;
	private String deviceEid = "";
	private static ContactsInterface ContactsInterface;
	private Context mContext;
	private  String imageData = "";
	//private List<InterfaceInfo> mInterfaceInfo = new ArrayList<>();
    public static File dir = new File(Environment.getExternalStorageDirectory() + "/.xx/json/");

	public ContactsInterface (Context context){
		mContext = context;
	}

	public  String getDeviceEid(){

		if(mxiaoXunNetworkManager == null)
		mxiaoXunNetworkManager = (XiaoXunNetworkManager)mContext.getSystemService("xun.network.Service");
		
		if(mxiaoXunNetworkManager != null){
			deviceEid = mxiaoXunNetworkManager.getWatchEid();
		}

		SystemProperties.set("persist.sys.xxun.deviceeid", base64Encoder(deviceEid));
		return deviceEid;
	}

	public void  setContactInfo(){
		String devicesEid =  getDeviceEid();
		//Log.d("zhj","devicesEid =" + devicesEid);
		setImageInfo(devicesEid);
		SystemProperties.set("persist.sys.xxun.deviceeType", "SW760");
	}


	private void  GetImageData(String eid, final IResponseDataCallBack callBack){
		
		if(mxiaoXunNetworkManager == null)
		  mxiaoXunNetworkManager = (XiaoXunNetworkManager)mContext.getSystemService("xun.network.Service");

		mxiaoXunNetworkManager.getHeadImage(eid, callBack);

	}


	private void setImageInfo( String eid){
	GetImageData(eid , new mImageData() {
			@Override
			public void onSuccess(ResponseData responseData) {

						String imageData = responseData.toString();
						Log.d("zhj","imageData =" + imageData);
						String splitStr = imageData.substring(3, imageData.length());
						List<ImageInfo> imageList = parseImageJson("[" + splitStr + "]");
					    saveImageToFile(getImageBitmap(imageList.get(0).getHead_image_date()));
					}
					@Override
					public void onError(int i, String s) {
							System.out.println("zhj"+",first insert error:");
					}
			});
	}

    private List<ImageInfo> parseImageJson(String json) {
        List<ImageInfo> userInfo = new ArrayList<ImageInfo>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                ImageInfo person = new ImageInfo();

                person.setHead_image_date(jsonObject.getString("head_image_date"));
                userInfo.add(person);
            }
        } catch (Exception e) {

            Log.d("zhj", "json parse error ");

            e.printStackTrace();
        }

        return userInfo;
    }

    private String base64Decoder(String text){
        String result = "";
        if (text != null) {
            try {
                result = new String(Base64.decode(text.getBytes("utf-8"), Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String base64Encoder(String text){
        String result = "";
        if (text != null) {
            try {
                result = new String(Base64.encode(text.getBytes("utf-8"), Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    private  void saveImageToFile (final Bitmap bmp){

	 new Thread(new Runnable() {
		@Override
		public void run() {
		File appDir = new File(Environment.getExternalStorageDirectory(), "/.xx/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName =  "self.png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
			String path = Environment.getExternalStorageDirectory() + "/.xx/" + fileName;
			SystemProperties.set("persist.sys.xxun.image_self", path);
		
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
			}
		}).start();

        
    }

	private  Bitmap getImageBitmap(String output){
			  Bitmap  bitmap = null;

			   try {
				   byte[] bitmapArray;
				   bitmapArray = Base64.decode(output, Base64.DEFAULT);
				   bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
						   bitmapArray.length);

			   } catch (Exception e) {
				   Log.e("zhj", "getImageBitmap Exception ");
			   }

			   return bitmap;
	}


    public class mContactsData extends IResponseDataCallBack.Stub{
           @Override
           public void onSuccess(ResponseData responseData) {
		   }
           @Override
           public void onError(int i, String s) {} 
    }

    public class mImageData extends IResponseDataCallBack.Stub{
           @Override
           public void onSuccess(ResponseData responseData) {
		   }
           @Override
           public void onError(int i, String s) {}    
    }	
    
}
