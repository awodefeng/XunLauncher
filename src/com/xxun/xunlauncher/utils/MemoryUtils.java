package com.xxun.xunlauncher.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
//import android.support.annotation.RequiresApi;
import android.text.format.Formatter;
import java.io.File;
import android.util.Log;

/**
 * @author lihaizhou
 * @time 2018.03.01
 * @class describe 手表内存工具类
 */
public class MemoryUtils {
    /**
     * 获取手表内部可用存储空间
     * @param context
     * @return 以M,G为单位的容量
     */
    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableInternalMemorySize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
	long availMem = ((availableBlocksLong*blockSizeLong)/1024)/1024;
        return availMem;
    }

    /**
     * 获取手机内部存储空间
     * 预留接口,暂时未用到
     * @param context
     * @return 以M,G为单位的容量
     */
    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getInternalMemorySize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        long size = blockCountLong * blockSizeLong;
        return Formatter.formatFileSize(context, size);
    }

}
