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
public class InCallBean {
    /**
     * result : 2
     * sub_action : 116
     * SEID : 24675CBADF93806745165D14A68FFDD1
     */

    @SerializedName("result")
    public int result;
    @SerializedName("sub_action")
    public int subAction;
    @SerializedName("SEID")
    public String SEID;

    public static InCallBean objectFromData(String str) {

        return new Gson().fromJson(str, InCallBean.class);
    }

    public static InCallBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), InCallBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<InCallBean> arrayPLBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<InCallBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<InCallBean> arrayPLBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<InCallBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
