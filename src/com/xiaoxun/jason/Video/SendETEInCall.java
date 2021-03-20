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

public class SendETEInCall {


    /**
     * CID : 30011
     * Version : 00040000
     * SN : 1110089044
     * TEID : ["F86134AE870872123E561AE8540D43E5"]
     * SID : E0609D20142142FA992441D9096DB806
     * PL : {"result":2,"sub_action":116,"SEID":"24675CBADF93806745165D14A68FFDD1"}
     * “RC” : 1
     */

    @SerializedName("CID")
    public int CID;
    @SerializedName("Version")
    public String Version;
    @SerializedName("SN")
    public int SN;
    @SerializedName("SID")
    public String SID;
    @SerializedName("PL")
    public InCallBean PL;
    @SerializedName("RC")
    public int RC; // FIXME check this code
    @SerializedName("TEID")
    public String[] TEID;

    public static SendETEInCall objectFromData(String str) {

        return new Gson().fromJson(str, SendETEInCall.class);
    }

    public static SendETEInCall objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), SendETEInCall.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<SendETEInCall> arraySendETEInCallFromData(String str) {

        Type listType = new TypeToken<ArrayList<SendETEInCall>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<SendETEInCall> arraySendETEInCallFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<SendETEInCall>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
