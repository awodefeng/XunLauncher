package com.xxun.xunlauncher.callback;

import android.util.Log;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xiaoxun.sdk.ResponseData;


public class ChinaTelecomVolteCallBack extends IResponseDataCallBack.Stub {

  private static final String TAG = "ChinaTelecomVolte";
  public ChinaTelecomVolteCallBack() {
  }

  @Override
  public void onSuccess(ResponseData responseData) {
    Log.d(TAG, "ChinaTelecomVolteCallBack success!!!" + responseData);
  }

  @Override
  public void onError(int i, String s) {
    Log.d(TAG, "ChinaTelecomVolteCallBack failed!!!" + i +" ,s: " + s);
  }
}
