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

public class WatchMode {

    /**
     * CID : 30012
     * SN : 143939973
     * Version : 00040000
     * PL : {"EID":"8F16216E30CDD2DDDF51BEA62184B5C6","phone_num":"13817873481","timestamp":"20171125071042947","type":2,"sub_action":303,"RC":1}
     * SEID : 5E0FBF12F7728F1B40F71855998DA312
     */

    @SerializedName("CID")
    public int CID;
    @SerializedName("SID")
    public String SID;
    @SerializedName("SN")
    public int SN;
    @SerializedName("Version")
    public String Version;
    @SerializedName("PL")
    public PLBean PL;
    @SerializedName("SEID")
    public String SEID;
    @SerializedName("TEID")
    public String[] TEID;

    public static WatchMode objectFromData(String str) {

        return new Gson().fromJson(str, WatchMode.class);
    }

    public static WatchMode objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), WatchMode.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<WatchMode> arrayWatchModeFromData(String str) {

        Type listType = new TypeToken<ArrayList<WatchMode>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<WatchMode> arrayWatchModeFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<WatchMode>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
