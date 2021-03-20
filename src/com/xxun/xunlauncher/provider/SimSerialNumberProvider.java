package com.xxun.xunlauncher.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class SimSerialNumberProvider extends ContentProvider {

    public static final int SN_DIR = 0;

    public static final int SN_ITEM = 1;

    private static final String TABLE = "sim_serial_number";

    public static final String AUTHORITY = "com.xxun.xunlauncher.provider";

    private static UriMatcher uriMatcher;
    private SimDatabaseHelp dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "sim_serial_number", SN_DIR);
        uriMatcher.addURI(AUTHORITY, "sim_serial_number/#", SN_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new SimDatabaseHelp(getContext(),"SimSNLib.db",null,1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case SN_DIR:
                cursor = db.query(TABLE,projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SN_ITEM:
                String sn = uri.getPathSegments().get(1);
                cursor = db.query(TABLE,projection,"id = ?",new String[] {sn},null,null,sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)) {
            case SN_DIR:
            case SN_ITEM:
                long newSN = db.insert(TABLE, null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/sim_serial_number/" + newSN);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case SN_DIR:
                deletedRows = db.delete(TABLE, selection, selectionArgs);
                break;
            case SN_ITEM:
                String sn = uri.getPathSegments().get(1);
                deletedRows = db.delete(TABLE, "id = ?", new String[] { sn });
                break;
            default:
                break;
        }
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (uriMatcher.match(uri)) {
            case SN_DIR:
                updatedRows = db.update(TABLE, values, selection, selectionArgs);
                break;
            case SN_ITEM:
                String sn = uri.getPathSegments().get(1);
                updatedRows = db.update(TABLE, values, "id = ?", new String[] { sn });
                break;
            default:
                break;
        }
        return updatedRows;
    }
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SN_DIR:
                return "vnd.android.cursor.dir/vnd.com.xxun.xunlauncher.provider.sim_serial_number";
            case SN_ITEM:
                return "vnd.android.cursor.item/vnd.com.xxun.xunlauncher.provider.sim_serial_number";
        }
        return null;
    }
}
