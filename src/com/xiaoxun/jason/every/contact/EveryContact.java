package com.xiaoxun.jason.every.contact;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2017/9/20.
 */

public class EveryContact {

    /**
     * id : 20170915144759289
     * updateTS : 20170915144821387
     */

    @SerializedName("id")
    public String id;
    @SerializedName("updateTS")
    public String updateTS;

    public static EveryContact objectFromData(String str) {

        return new Gson().fromJson(str, EveryContact.class);
    }

    public static EveryContact objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), EveryContact.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<EveryContact> arrayEveryContactFromData(String str) {

        Type listType = new TypeToken<ArrayList<EveryContact>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<EveryContact> arrayEveryContactFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<EveryContact>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
