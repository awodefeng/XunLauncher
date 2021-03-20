package com.xiaoxun.jason.preventaddition;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2018/6/8.
 */

public class PreventObj {


    /**
     * on_off : 1
     * total_dur : 1800
     * allow_dur : 900
     * prevent_dur : 900
     * vcall_limit: 600
     * app_list : ["com.xxun.xunimgrec ","com.xxun.watch.xunbrain "]
     */

    @SerializedName("on_off")
    public int onOff;
    @SerializedName("total_dur")
    public int totalDur;
    @SerializedName("allow_dur")
    public int allowDur;
    @SerializedName("prevent_dur")
    public int preventDur;
    @SerializedName("vcall_limit")
    public int vcallLimit;
    @SerializedName("app_list")
    public List<String> appList;

    public static PreventObj objectFromData(String str) {

        return new Gson().fromJson(str, PreventObj.class);
    }

    public static PreventObj objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), PreventObj.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PreventObj> arrayPreventObjFromData(String str) {

        Type listType = new TypeToken<ArrayList<PreventObj>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<PreventObj> arrayPreventObjFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<PreventObj>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
