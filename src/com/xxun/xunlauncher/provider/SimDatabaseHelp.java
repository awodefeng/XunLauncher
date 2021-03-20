package com.xxun.xunlauncher.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SimDatabaseHelp extends SQLiteOpenHelper {

    private Context mContext;
    public static final String CREATE_SIM_SERIAL_NUMBER = "create table sim_serial_number ("
            + "id integer primary key autoincrement, "
            + "sn text,"
            + "status integer)";

    public SimDatabaseHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SIM_SERIAL_NUMBER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
