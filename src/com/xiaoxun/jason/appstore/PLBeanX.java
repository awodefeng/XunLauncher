package com.xiaoxun.appstore;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2019/1/7.
 */
public class PLBeanX {
    /**
     * EID : 493A0A41CCD1D5F0F1825C9A3E24B8A1
     * wifi : 1
     * sub_action : 308
     * GID : 341E2EEBE740A10999D8A5C42801B0EA
     * hidden : 0
     * icon : m2
     * version_code : 8
     * version : 1.1.1.1
     * optype : 1
     * size : 2573070
     * download_url : m1
     * name : 支付宝
     * app_id : com.alipay
     * updateTS : 20190107193914063
     * status : 1
     * md5 : db9d75fcc51ff53a299dc428d534967d
     */

    @SerializedName("EID")
    public String EID;
    @SerializedName("wifi")
    public int wifi;
    @SerializedName("sub_action")
    public int subAction;
    @SerializedName("GID")
    public String GID;
    @SerializedName("type")
    public int type;
    @SerializedName("hidden")
    public int hidden;
    @SerializedName("icon")
    public String icon;
    @SerializedName("version_code")
    public int versionCode;
    @SerializedName("version")
    public String version;
    @SerializedName("optype")
    public int optype;
    @SerializedName("size")
    public int size;
    @SerializedName("download_url")
    public String downloadUrl;
    @SerializedName("name")
    public String name;
    @SerializedName("app_id")
    public String appId;
    @SerializedName("updateTS")
    public String updateTS;
    @SerializedName("status")
    public int status;
    @SerializedName("md5")
    public String md5;
    @SerializedName("attr")
    public String attr;


    public static PLBeanX objectFromData(String str) {

        return new Gson().fromJson(str, PLBeanX.class);
    }

    public static PLBeanX objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), PLBeanX.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PLBeanX> arrayPLBeanXFromData(String str) {

        Type listType = new TypeToken<ArrayList<PLBeanX>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<PLBeanX> arrayPLBeanXFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<PLBeanX>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
