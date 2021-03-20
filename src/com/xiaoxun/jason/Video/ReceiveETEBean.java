package com.xiaoxun.jason.Video;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2018/8/28.
 */
public class ReceiveETEBean {
    /**
     * channelName : channelHyy
     * SN : 1110089044
     * appId : 91c1e48cadbd4353a5e69fffcf38d5b2
     * uidOther : 2
     * sub_action : 116
     * SEID : 24675CBADF93806745165D14A68FFDD1
     * tokenOther : lalala
     * result : 2
     */

    @SerializedName("channelName")
    public String channelName;
    @SerializedName("SN")
    public int SN;
    @SerializedName("appId")
    public String appId;
    @SerializedName("uidOther")
    public int uidOther;
    @SerializedName("sub_action")
    public int subAction;
    @SerializedName("SEID")
    public String SEID;
    @SerializedName("tokenOther")
    public String tokenOther;
    @SerializedName("result")
    public int result;
    @SerializedName("tutkType")
    public String tutkType;

    public static ReceiveETEBean objectFromData(String str) {

        return new Gson().fromJson(str, ReceiveETEBean.class);
    }

    public static ReceiveETEBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), ReceiveETEBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ReceiveETEBean> arrayPLBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<ReceiveETEBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<ReceiveETEBean> arrayPLBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<ReceiveETEBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
