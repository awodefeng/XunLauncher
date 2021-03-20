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

public class DealInstallSubAciton {

    /**
     * SN : 73914070
     * SEID : 5E0FBF12F7728F1B40F71855998DA312
     * PL : {"EID":"493A0A41CCD1D5F0F1825C9A3E24B8A1","wifi":1,"sub_action":308,"GID":"341E2EEBE740A10999D8A5C42801B0EA","hidden":0,"icon":"m2","version_code":8,"version":"1.1.1.1","optype":1,"size":2573070,"download_url":"m1","name":"支付宝","app_id":"com.alipay","updateTS":"20190107193914063","status":1,"md5":"db9d75fcc51ff53a299dc428d534967d"}
     */

    @SerializedName("SN")
    public int SN;
    @SerializedName("SEID")
    public String SEID;
    @SerializedName("PL")
    public PLBeanX PL;

    public static DealInstallSubAciton objectFromData(String str) {

        return new Gson().fromJson(str, DealInstallSubAciton.class);
    }

    public static DealInstallSubAciton objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), DealInstallSubAciton.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<DealInstallSubAciton> arrayDealInstallSubAcitonFromData(String str) {

        Type listType = new TypeToken<ArrayList<DealInstallSubAciton>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<DealInstallSubAciton> arrayDealInstallSubAcitonFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<DealInstallSubAciton>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
