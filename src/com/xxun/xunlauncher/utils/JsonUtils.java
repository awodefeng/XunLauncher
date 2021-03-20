package com.xxun.xunlauncher.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import java.util.ArrayList;
import com.xiaoxun.sdk.utils.Constant;
import com.xxun.xunlauncher.R;
import android.util.Log;
import android.util.XiaoXunUtil;
import java.util.Arrays;
import android.provider.Settings;
import android.os.SystemProperties;
import android.util.XiaoXunUtil;
import com.xxun.xunlauncher.application.LauncherApplication;

/**
 * @author lihaizhou
 * @createtime 2017.09.29
 * @describe json工具类
 */
public class JsonUtils {
    private JSONObject jsonObject;

    private JsonUtils() {}

    public static JsonUtils getJsonUtilsInstance() {
        return JsonUtilsHolder.jsonUtilsInstance;
    }

    private static class JsonUtilsHolder {
        private static JsonUtils jsonUtilsInstance = new JsonUtils();
    }

    /**
     * @param json 服务器最新的功能设置json数据
     * @author lihaizhou
     * @createtime 2017.11.29
     * @describe 将应用开关的值以数组形式返回
     */
    public ArrayList<Integer> getLaestFuncationList(String json) throws JSONException {
	
        if (json == null || json.length() <= 0) {
            return null;
        }

	JSONArray jsonArray = new JSONArray(json);

	/*ArrayList<Integer> functionList_703 = new ArrayList<Integer>(Arrays.asList(R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
                                R.drawable.story, R.drawable.album, R.drawable.camera, R.drawable.alipay, R.drawable.navigation,R.drawable.engstd_icon, R.drawable.voice_question));*/
         //Modify by jxring for AppIconShow
        ArrayList<Integer> functionList_760 = new ArrayList<Integer>(Arrays.asList(getAppShowIcons()));         

	return removeFunctionLimitApp(functionList_760,jsonArray);               
    }

    private ArrayList removeFunctionLimitApp(ArrayList<Integer> functionList,JSONArray jsonArray) throws JSONException{
	  StringBuffer mNeedHidenIcon  = new StringBuffer();
	  	  StringBuffer mNeedShowAttr  = new StringBuffer();
	  for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
		
            if ("0".equals(jsonObject.get("onoff").toString())) {
		String removeName = jsonObject.get("name").toString();
		if("dialer".equals(removeName)){
		   functionList.remove(functionList.indexOf(R.drawable.phone));
		   mNeedHidenIcon.append("com.xiaoxun.dialer#");
		}else if("chatroom".equals(removeName)){
		   functionList.remove(functionList.indexOf(R.drawable.voice_msg));
		   mNeedHidenIcon.append("com.xxun.watch.xunchatroom#");
		   Settings.System.putInt(LauncherApplication.getInstance().getContentResolver(),"chatroom_exit", 0);
		}else if("setting".equals(removeName)){
		   mNeedHidenIcon.append("com.xxun.watch.xunsettings#");
		   functionList.remove(functionList.indexOf(R.drawable.settings));
		}else if("pets".equals(removeName)){
		   mNeedHidenIcon.append("com.xxun.watch.xunpet#");
		   functionList.remove(functionList.indexOf(R.drawable.pet));
		}else if("sport".equals(removeName)){
		   mNeedHidenIcon.append("com.xxun.watch.stepstart#");
		   if (Constant.PROJECT_NAME.equals("SW706")) {
			   functionList.remove(functionList.indexOf(R.drawable.sport_sw706));
		   } else {
			   functionList.remove(functionList.indexOf(R.drawable.sport));
		   }
		}else if("stopwatch".equals(removeName)){
		   mNeedHidenIcon.append("com.xxun.watch.xunstopwatch#");
		   functionList.remove(functionList.indexOf(R.drawable.stopwatch));
		}else if("audioplayer".equals(removeName)){
		   mNeedHidenIcon.append("com.xxun.watch.storytall#");
		   functionList.remove(functionList.indexOf(R.drawable.story));
	           Intent intent = new Intent();
        	   intent.setAction("com.xiaoxun.xxun.story.finish");
        	   LauncherApplication.getInstance().sendBroadcast(intent);
		}else if("camera".equals(removeName)){
		   mNeedHidenIcon.append("com.xxun.xuncamera#");
		   functionList.remove(functionList.indexOf(R.drawable.camera));
		}else if("imageviewer".equals(removeName)){
		   mNeedHidenIcon.append("com.xxun.xungallery#");
		   functionList.remove(functionList.indexOf(R.drawable.album));
		}else if("alipay".equals(removeName)){
		   mNeedHidenIcon.append("com.eg.android.AlipayGphone#");
		   functionList.remove(functionList.indexOf(R.drawable.alipay));
		}/**else if("friendsxun".equals(removeName)){
			mNeedHidenIcon.append("com.xxun.watch.xunfriends#");
			functionList.remove(functionList.indexOf(R.drawable.friends));
		}*/else if("aivoice".equals(removeName)){
		   mNeedHidenIcon.append("com.xxun.watch.xunbrain.c3#");
		   functionList.remove(functionList.indexOf(R.drawable.voice_question));
		   Settings.System.putInt(LauncherApplication.getInstance().getContentResolver(),"xiaoai_exit", 0);
		} else if("english".equals(removeName) && XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_ENGLISH_STUDY){
		   mNeedHidenIcon.append("com.xiaoxun.englishdailystudy#");
		   functionList.remove(functionList.indexOf(R.drawable.engstd_icon));
		}
        }else{
              if("aivoice".equals(jsonObject.get("name").toString())){
		    Settings.System.putInt(LauncherApplication.getInstance().getContentResolver(),"xiaoai_exit", 1);
		}else if("chatroom".equals(jsonObject.get("name").toString())){
		    Settings.System.putInt(LauncherApplication.getInstance().getContentResolver(),"chatroom_exit", 1);
		}
            }
	   if(jsonObject.toString().contains("attr")){
	if("1".equals(jsonObject.get("attr").toString())){
		String attrName = jsonObject.get("name").toString();
				if("dialer".equals(attrName)){
				   mNeedShowAttr.append("com.xiaoxun.dialer#");
				}else if("chatroom".equals(attrName)){
				   mNeedShowAttr.append("com.xxun.watch.xunchatroom#");
				}else if("setting".equals(attrName)){
				   mNeedShowAttr.append("com.xxun.watch.xunsettings#");
				}else if("pets".equals(attrName)){
				   mNeedShowAttr.append("com.xxun.watch.xunpet#");
				}else if("sport".equals(attrName)){
				   mNeedShowAttr.append("com.xxun.watch.stepstart#");
				}else if("stopwatch".equals(attrName)){
					mNeedShowAttr.append("com.xxun.watch.xunstopwatch#");
				}else if("audioplayer".equals(attrName)){
				   mNeedShowAttr.append("com.xxun.watch.storytall#");
				}else if("camera".equals(attrName)){
				   mNeedShowAttr.append("com.xxun.xuncamera#");
				}else if("imageviewer".equals(attrName)){
				   mNeedShowAttr.append("com.xxun.xungallery#");
				}else if("alipay".equals(attrName)){
				   mNeedShowAttr.append("com.eg.android.AlipayGphone#");
				}/**else if("friendsxun".equals(removeName)){
					mNeedHidenIcon.append("com.xxun.watch.xunfriends#");
					functionList.remove(functionList.indexOf(R.drawable.friends));
				}*/else if("aivoice".equals(attrName)){
				   mNeedShowAttr.append("com.xxun.watch.xunbrain.c3#");
				} else if("english".equals(attrName) && XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_ENGLISH_STUDY){
				   mNeedShowAttr.append("com.xiaoxun.englishdailystudy#");
				}
		}
	   	}
        }

        Settings.System.putString(LauncherApplication.getInstance().getContentResolver(),"XunAppHiden",(mNeedHidenIcon.length() <=0) ?"":mNeedHidenIcon.toString());
	    Settings.System.putString(LauncherApplication.getInstance().getContentResolver(),"XunAppAttr",(mNeedShowAttr.length() <=0) ?"":mNeedShowAttr.toString());
        return functionList;
    }
    
    //[add by jxring for hiden apps 2019.4.18 start]
    private Integer[] getAppShowIcons(){
        if(XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_ENGLISH_STUDY && XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT){
			if (Constant.PROJECT_NAME.equals("SW706")) {
				Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings,
						R.drawable.pet, R.drawable.sport_sw706, R.drawable.stopwatch, R.drawable.story, R.drawable.appstore, R.drawable.album, R.drawable.camera,
						R.drawable.alipay/*,R.drawable.friends*/, R.drawable.engstd_icon, R.drawable.voice_question};
				return mNewIconList;
			}else{
				Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone, R.drawable.voice_msg, R.drawable.settings,
						R.drawable.pet, R.drawable.sport, R.drawable.stopwatch, R.drawable.story, R.drawable.appstore, R.drawable.album, R.drawable.camera,
						R.drawable.alipay/*,R.drawable.friends*/, R.drawable.engstd_icon, R.drawable.voice_question};
				return mNewIconList;
			}
		}else if(XiaoXunUtil.XIAOXUN_CONFIG_APPSTORE_SUPPORT){
				Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone,R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
													R.drawable.stopwatch,/**R.drawable.friends,*/R.drawable.story,R.drawable.appstore,R.drawable.album, R.drawable.camera, R.drawable.alipay, R.drawable.voice_question};
				return mNewIconList;
        }else if(XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_ENGLISH_STUDY){
				Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone,R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
													R.drawable.stopwatch,/**R.drawable.friends,*/R.drawable.story,R.drawable.appstore,R.drawable.album, R.drawable.camera, R.drawable.alipay, R.drawable.engstd_icon,R.drawable.voice_question};
				return mNewIconList;
        }else{
				
				Integer[] mNewIconList = {R.drawable.ic_launcher, R.drawable.phone,R.drawable.voice_msg, R.drawable.settings, R.drawable.pet, R.drawable.sport,
									R.drawable.stopwatch/**,R.drawable.friends*/,R.drawable.story,R.drawable.appstore,R.drawable.album, R.drawable.camera, R.drawable.alipay, R.drawable.voice_question };
                return mNewIconList;
        }
        //return null;
    
    } 

}

