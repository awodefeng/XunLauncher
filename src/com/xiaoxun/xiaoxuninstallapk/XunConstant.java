package com.xiaoxun.xiaoxuninstallapk;

import android.net.Uri;

/**
 * Created by jixiang on 2018/12/27.
 */

public class XunConstant {
    public static final String ISSYSTEM = "isSystem";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String APP_ID = "app_id";
    public static final String EID = "EID";
    public static final String GID = "GID";
    public static final String OPTYPE = "optype";
    public static final String ICON = "icon";
    public static final String STATUS = "status";
    public static final String HIDDEN = "hidden";
    public static final String VERSION = "version";
    public static final String VERSION_CODE = "version_code";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String WIFI = "wifi";
    public static final String SIZE = "size";
    public static final String MD5 = "md5";
    public static final String UPDATES = "updateTS";

    public static final String VERSION_CODE_NEW = "version_code_new";
    public static final String VERSION_NEW = "version_new";
    public static final String TYPE_NEW = "type_new";
    public static final String ICON_NEW = "icon_new";
    public static final String DOWNLOAD_URL_NEW = "download_url_new";
    public static final String SIZE_NEW = "size_new";
    public static final String MD5_NEW = "md5_new";
    public static final String ATTR = "blank_one";

    public static final String AUTHORITY = "com.xiaoxun.appinfo.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/xxappinfo");
    
    public static final String XUN_INSERTDATABASE_INIT_ACTION = "com.xiaoxun.init.getsystem.app";
    public static final String XUN_UPLOAD_SYS_APP_TO_SERVER = "com.xiaoxun.upload.system.toserver";
    
    public static final String XUN_UPLOAD_UPDATE_SOME_STATES = "com.xiaoxun.upload.some.states";
    public static final String XUN_APPSTORE_REMOVE_APP_BRO = "com.xiaoxun.uninstall.app";
    public static final String XUN_APPSTORE_UPDATE_LOCAL = "com.xiaoxun.update.local.database";
}
