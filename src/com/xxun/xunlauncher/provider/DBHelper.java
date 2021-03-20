package com.xxun.xunlauncher.provider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Created by Carson_Ho on 17/6/6.
 */
public class DBHelper extends SQLiteOpenHelper {

    // 数据库名
    private static final String DATABASE_NAME = "scoresytem.db";

    // 表名
    public static final String integral_TABLE_NAME = "intergral";
  //  public static final String USER_TABLE_NAME = "user";
   // public static final String JOB_TABLE_NAME = "job";

    private static final int DATABASE_VERSION = 1;
    //数据库版本号

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	Log.i("yuanshuai", "DBHelperCreate");
        // 创建积分表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + integral_TABLE_NAME
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "moduleid INTEGER,"    //用来标识是哪个模块
                + "type INTEGER,"   //0金币收入，1.金币兑换
                +" getgold INTEGER," //根据规则算出获取金币
                + "timestamp TEXT," //传递当前完成任务的时间
                + "flag INTEGER);");  // 0上传，1.表示未上传过，默认传1
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)   {

    }
}
