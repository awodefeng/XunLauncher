package com.xxun.xunlauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import android.util.Log;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ServiceManager;
import android.content.Context;
import android.os.SystemProperties;

import java.util.ArrayList;
import java.util.List;

/*Parse the phasecheck as the little endian*/
public class PhaseCheckParse {
    private static String TAG = "PhaseCheckParse";
    private static int MAX_SN_LEN = 20;
    private static int SP09_MAX_SN_LEN = MAX_SN_LEN;
    private static int SP09_MAX_STATION_NUM = 15;
    private static int SP09_MAX_STATION_NAME_LEN = 10;
    private static int SP09_SPPH_MAGIC_NUMBER = 0x53503039;
    private static int SP05_SPPH_MAGIC_NUMBER = 0x53503035;
    private static int SP09_MAX_LAST_DESCRIPTION_LEN = 32;

    private static int SN1_START_INDEX = 4;
    private static int SN2_START_INDEX = SN1_START_INDEX + SP09_MAX_SN_LEN + 4;

    private static int STATION_START_INDEX = 56;
    private static int TESTFLAG_START_INDEX = 252;
    private static int RESULT_START_INDEX = 254;

    private static int TYPE_WRITE_STATION_TESTED = 2;
    private static int TYPE_WRITE_STATION_PASS = 3;
    private static int TYPE_WRITE_STATION_FAIL = 4;
    private static String PHASE_CHECKE_FILE = "miscdata";

    private byte[] stream = new byte[1000 * 1024];
    private IBinder binder;
    private boolean mIsSP15 = false;
    private static int SP15_MAX_SN_LEN = 64;
    private static int SP15_MAX_STATION_NUM = 20;
    private static int SP15_MAX_STATION_NAME_LEN = 15;
    private static int SP15_SPPH_MAGIC_NUMBER = 0x53503135;
    private static int SP15_MAX_LAST_DESCRIPTION_LEN = 32;

    private static int SP15_SN1_START_INDEX = 4;
    private static int SP15_SN2_START_INDEX = SP15_SN1_START_INDEX + SP15_MAX_SN_LEN;

    private static int SP15_STATION_START_INDEX = 136;
    private static int SP15_TESTFLAG_START_INDEX = 482;
    private static int SP15_RESULT_START_INDEX = 486;

    private static int TYPE_GET_MIDEVICEINFO = 11;
    private static int MI_DEVICE_INFO_START_INDEX = 1024 * 800;
    private static int MI_DEVICE_INFO_MAX_LEN = 7;

	List<String> mAllStationNameAndResult = null;

    public PhaseCheckParse() {

        FileInputStream in = null;
        String filePath = SystemProperties.get("ro.product.partitionpath") + PHASE_CHECKE_FILE;
        File fp = new File(filePath);
        if (!fp.exists()) {
            Log.d(TAG, filePath + "not exist!");
            stream = null;
            return;
        }
        Log.d(TAG, "PhaseCheckParse filePath="+filePath);
        try {
            in = new FileInputStream(fp);
            if (in != null) {
                in.read(stream, 0, stream.length);
                Log.d(TAG, "read stream.length="+stream.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException io) {
                Log.e(TAG, "close in err");
            }
        }

        if (!checkPhaseCheck()) {
            stream = null;
        }

        //ssp
        binder = ServiceManager.getService("phasechecknative");
        if (binder != null) {
            Log.e(TAG, "Get The service connect!");
        } else {
            Log.e(TAG, "connect Error!!");
        }
        if (checkPhaseCheck()) {
            mAllStationNameAndResult = new ArrayList<String>(16);
            for (int i = 0; i < SP09_MAX_STATION_NUM; i++) {
                if (0 == stream[STATION_START_INDEX + i * SP09_MAX_STATION_NAME_LEN]) {
                    Log.d(TAG, "no station, i=: " + i);
                    break;
                }
                String stationName = new String(stream, STATION_START_INDEX +
                        i * SP09_MAX_STATION_NAME_LEN, SP09_MAX_STATION_NAME_LEN);
                Log.d(TAG, "has station, i=: " + i + " stationName=:" + stationName);
                mAllStationNameAndResult.add(stationName);
            }
        }
    }

    private int getStationPosition(String stationName) {
        int size = mAllStationNameAndResult.size();
        Log.d(TAG, "size=:" + size + ", stationName=:" + stationName);
        for (int i = 0; i < size; i++) {
            if (mAllStationNameAndResult.get(i).startsWith("MMI")) {
                Log.d(TAG, "return i=:" + i);
                return i;
            } else {
                continue;
            }
        }
        return -1;
    }

    private boolean checkPhaseCheck() {
        Log.d(TAG, "checkPhaseCheck " + stream[0] + stream[1] + stream[2] + stream[3]);
//        if ((stream[0] == '9' || stream[0] == '5')
//                && stream[1] == '0'
//                && stream[2] == 'P'
//                && stream[3] == 'S') {
//        	mIsSP15 = false;
//            return true;
//        }
//        /*SPRD bug :Support SP15*/
//        else if (stream[0] == '5'
//                && stream[1] == '1'
//                && stream[2] == 'P'
//                && stream[3] == 'S') {
//        	mIsSP15 = true;
//            return true;
//        }

        return true;
    }

    public String getMiDeviceInfo() {
        byte[] temp = new byte[150];
        if (stream == null) {
            return "#Invalid mi device info!";
        }
        if (!isAscii(stream[MI_DEVICE_INFO_START_INDEX])) {
            Log.d(TAG, "##Invalid mi device info!");
            return "##Invalid mi device info!";
        }
        for(int i = 0; i < 150; i++){
            Log.d(TAG, "stream[MI_DEVICE_INFO_START_INDEX]" + i + " = " + stream[MI_DEVICE_INFO_START_INDEX + i]);
            if (0 == stream[MI_DEVICE_INFO_START_INDEX + i]) {
                Log.d(TAG, "get MiDeviceInfo completely");
                break;
            }
            if(isAscii(stream[MI_DEVICE_INFO_START_INDEX + i])){
                temp[i] = stream[MI_DEVICE_INFO_START_INDEX + i];
            }else{
                break;
            }
        }
        String miDeviceInfo = new String(temp, 0, temp.length);
        return miDeviceInfo;
    }

    public String getSn1() {
        if (stream == null) {
            return "Invalid Sn1!";
        }
        if (!isAscii(stream[SN1_START_INDEX])) {
            Log.d(TAG, "Invalid Sn1!");
            return "Invalid Sn1!";
        }

        String sn1 = new String(stream, SN1_START_INDEX, SP09_MAX_SN_LEN);
        if(mIsSP15){
        	sn1 = new String(stream, SP15_SN1_START_INDEX, SP15_MAX_SN_LEN);
        	Log.d(TAG, "mIsSP15 sn1:" + sn1);
        }
        Log.d(TAG, sn1);
        return sn1;
    }

    public String getSn2() {
        byte[] temp = new byte[15];
        int j = 0;
        if (stream == null) {
            return "Invalid Sn2!";
        }
        if (!isAscii(stream[SN2_START_INDEX])) {
            Log.d(TAG, "Invalid Sn2!");
            return "Invalid Sn2!";
        }
        for(int i = 0; i < 15; i++){
            Log.d(TAG, "stream[SN2_START_INDEX]" + i + " = " + stream[SN2_START_INDEX + i]);
            if (0 == stream[SN2_START_INDEX + i]) {
                Log.d(TAG, "get getSn2 has extra char");
                continue;
            }
            if(isAscii(stream[SN2_START_INDEX + i])){
                temp[j] = stream[SN2_START_INDEX + i];
                j++;
            }else{
                continue;
            }
        }

        String sn2 = new String(temp, 0, temp.length);
        if(mIsSP15){
        	sn2 = new String(temp, 0, temp.length);
        	Log.d(TAG, "mIsSP15 sn2:" + sn2);
        }
        Log.d(TAG, sn2);
        return sn2;
    }

    private static int RESERVED_START_INDEX = 206;
    private static int RESERVED_LEN = 13;
    public String getSn3() {
        if (stream == null) {
            return "Invalid Sn3!";
        }
        if (!isAscii(stream[RESERVED_START_INDEX])) {
            Log.d(TAG, "Invalid Sn3!");
            return "Invalid Sn3!";
        }

        String sn3 = new String(stream, RESERVED_START_INDEX, RESERVED_LEN);
        Log.d(TAG, "getSn3 sn3="+sn3);
        return sn3;
    }

    public boolean writeStationTested(String stationName) {
        Log.d(TAG, "writeStationTested");
        int station = getStationPosition(stationName);
        Log.d(TAG, "station = " + station);
        try{
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            data.writeInt(station);
            binder.transact(TYPE_WRITE_STATION_TESTED, data, reply, 0);
            Log.e(TAG, "data = " + data.readInt() + " SUCESS!!");
            data.recycle();
            return true;
        }catch (Exception ex) {
            Log.e(TAG, "Exception " + ex.getMessage());
            return false;
        }
    }

    public boolean writeStationPass(String stationName) {
        Log.d(TAG, "writeStationPass");
        int station = getStationPosition(stationName);
        try{
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            data.writeInt(station);
            binder.transact(TYPE_WRITE_STATION_PASS, data, reply, 0);
            Log.e(TAG, "data = " + data.readInt() + " SUCESS!!");
            data.recycle();
            return true;
        }catch (Exception ex) {
            Log.e(TAG, "Exception " + ex.getMessage());
            return false;
        }
    }

    public boolean writeStationFail(String stationName) {
        Log.d(TAG, "writeStationFail");
        int station = getStationPosition(stationName);
        try{
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            data.writeInt(station);
            binder.transact(TYPE_WRITE_STATION_FAIL, data, reply, 0);
            Log.e(TAG, "data = " + data.readInt() + " SUCESS!!");
            data.recycle();
            return true;
        }catch (Exception ex) {
            Log.e(TAG, "Exception " + ex.getMessage());
            return false;
        }
    }

    private boolean isAscii(byte b) {
        if (b >= 0 && b <= 127) {
            return true;
        }
        return false;
    }

    public String getTestsAndResult() {
    	if(mIsSP15){
    		String text = getTestsAndResultSP15();
    		Log.d(TAG, "getTestsAndResult mIsSP15="+mIsSP15);
    		Log.d(TAG, "getTestsAndResult text="+text);
    		return text;
    	}
        if (stream == null) {
            return "Invalid Phase check!";
        }

        if (!isAscii(stream[STATION_START_INDEX])) {
            Log.d(TAG, "Invalid Phase check!");
            return "Invalid Phase check!";
        }
        String testResult = null;
        String allResult = "";

        int flag = 1;
        for (int i = 0; i < SP09_MAX_STATION_NUM; i++) {
            if (0 == stream[STATION_START_INDEX + i * SP09_MAX_STATION_NAME_LEN]) {
                Log.d(TAG, "break " + i);
                break;
            }
            testResult = new String(stream, STATION_START_INDEX + i * SP09_MAX_STATION_NAME_LEN,
                    SP09_MAX_STATION_NAME_LEN);
            if (!isStationTest(i)) {
                testResult += " Not test";
            } else if (isStationPass(i)) {
                testResult += " Pass";
            } else {
                testResult += " Failed";
            }
            flag = flag << 1;
            Log.d(TAG, i + " " + testResult);
            allResult += testResult + "\n";
        }
        return allResult;
    }

    public String getTestsAndResultSP15() {
        if (stream == null) {
            return "Invalid Phase check!";
        }

        if (!isAscii(stream[SP15_STATION_START_INDEX])) {
            Log.d(TAG, "Invalid Phase check!");
            return "Invalid Phase check!";
        }
        String testResult = null;
        String allResult = "";

        int flag = 1;
        for (int i = 0; i < SP15_MAX_STATION_NUM; i++) {
            if (0 == stream[SP15_STATION_START_INDEX + i * SP15_MAX_STATION_NAME_LEN]) {
                Log.d(TAG, "break " + i);
                break;
            }
            testResult = new String(stream, SP15_STATION_START_INDEX + i * SP15_MAX_STATION_NAME_LEN,
            		SP15_MAX_STATION_NAME_LEN);
            if (!isStationTestSP15(i)) {
                testResult += " Not test";
            } else if (isStationPassSP15(i)) {
                testResult += " Pass";
            } else {
                testResult += " Failed";
            }
            flag = flag << 1;
            Log.d(TAG, i + " " + testResult);
            allResult += testResult + "\n";
        }
        return allResult;
    }

    private boolean isStationTestSP15(int station) {
    	int flag = 1;
    	return (0 == ((flag << station) & stream[SP15_TESTFLAG_START_INDEX]));
    }

    private boolean isStationPassSP15(int station) {
    	int flag = 1;
    	return (0 == ((flag << station) & stream[SP15_RESULT_START_INDEX]));
    }

    private boolean isStationTest(int station) {
        byte flag = 1;
        if (station < 8) {
            return (0 == ((flag << station) & stream[TESTFLAG_START_INDEX]));
        } else if (station >= 8 && station < 16) {
            return (0 == ((flag << (station - 8)) & stream[TESTFLAG_START_INDEX + 1]));
        }
        return false;
    }

    private boolean isStationPass(int station) {
        byte flag = 1;
        if (station < 8) {
            return (0 == ((flag << station) & stream[RESULT_START_INDEX]));
        } else if (station >= 8 && station < 16) {
            return (0 == ((flag << (station - 8)) & stream[RESULT_START_INDEX + 1]));
        }
        return false;
    }
}
