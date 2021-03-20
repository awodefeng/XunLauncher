package com.xiaoxun.jason.WatchMode;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2017/11/25.
 */
public class PLBean {
    /**
     * EID : 8F16216E30CDD2DDDF51BEA62184B5C6
     * phone_num : 13817873481
     * timestamp : 20171125071042947
     * type : 2
     * sub_action : 303
     * RC : 1
     */

    @SerializedName("EID")
    public String EID;
    @SerializedName("phone_num")
    public String phoneNum;
    @SerializedName("timestamp")
    public String timestamp;
    @SerializedName("type")
    public int type;
    @SerializedName("sub_action")
    public int subAction;
    @SerializedName("RC")
    public int RC;

    public static PLBean objectFromData(String str) {

        return new Gson().fromJson(str, PLBean.class);
    }

    public static PLBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), PLBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PLBean> arrayPLBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<PLBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<PLBean> arrayPLBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<PLBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
