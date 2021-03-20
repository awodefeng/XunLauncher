/**
 * 作者：郭洪成
 * 时间：20180102
 * 
 * 此类功能：用于后台拍照并上传的工具类
 * 主要有两个过程：1、调用摄像头拍照； 2、上传照片
 * 调用方式：CameraUtil.getCameraInstance().takePhoto(getApplicationContext());
 */

package com.xxun.xunlauncher.utils;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.provider.MediaStore;
import android.os.Environment;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.view.SurfaceView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xiaoxun.sdk.utils.Constant;

import com.xiaoxun.smart.uploadfile.OnUploadResult;
import com.xiaoxun.smart.uploadfile.ProgressListener;
import com.xiaoxun.smart.uploadfile.UploadFile;

public class CameraUtil {
    private final static String TAG = "CameraUtil";

    private static final String IMAGE_TYPE = "photo";
    private String mEID = null;
    private String mGID = null;
    private String mToken = null;
    private String mAES_KEY = null;

    // 后台拍照保存路径，也是上传图片的路径
    private String mImgOriginalPath = "null";

    private XiaoXunNetworkManager mXunNetworkManager;

    // 预览宽、高； 图片保存宽、高
    // private final int PREVIEW_SIZE_WIDTH = 240;
    private final int PREVIEW_SIZE_WIDTH_705 = 240;
    private final int PREVIEW_SIZE_HEIGHT_705 = 240;
    private final int PICTURE_SIZE_WIDTH_705 = 1200;
    private final int PICTURE_SIZE_HEIGHT_705 = 1200;


    private String xunSn = SystemProperties.get("persist.sys.xxun.sn");
    private final String xun_703_productId1 = "SWX003";
    private final String xun_703_productId2 = "SWX004";
    private final String xun_703_productId3 = "SWF005";
    private final String xun_703_productId4 = "SWF006"; 

    // 照片路径240
    private String IMG_FILE_PATH
            = Environment.getExternalStoragePublicDirectory("DCIM").getAbsolutePath() + "/Camera";

    private Camera mCamera;

    // 默认前置或者后置相机 0:后置 1:前置
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Camera.Parameters mParameters;

    private float picScaleSize = 1;

    private Context mContext;

    public CameraUtil() {
    }

    public static CameraUtil getCameraInstance() {
        return CameraUtilHolder.cameraUtil;
    }

    public static class CameraUtilHolder {

        public static CameraUtil cameraUtil = new CameraUtil();
    }

// =======================================================================
// =======================================================================
    /**
     * 开始预览
     * 打开相机---设置相机参数---打开预览界面
     */
    private void startPreview() {
        Log.d(TAG, "[startPreview] >> begin.");
        if (mCamera == null) {
            try {
                mCamera = Camera.open(mCameraId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setCameraParameters();
        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        try {
	    //Add by lihaizhou for avoid crash while remote photo taking begin 20190405 
	    if(mCamera==null){
	       //Toast.makeText(LauncherApplication.getInstance(), "相机当前被占用，无法远程拍照", Toast.LENGTH_LONG).show();
	       Log.d(TAG,"mCamera is null, so just return");
	       return;
	    }
	    //Add by lihaizhou for avoid crash while remote photo taking end 20190405 
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera mCamera) {
                // 启动存储照片的线程
                final File dir = new File(IMG_FILE_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();      // 创建文件夹
                }
                String name = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance()) + ".jpg";
                final File file = new File(dir, name);
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(file);
                    Log.d(TAG, "[onPictureTaken] >> PROJECT_NAME:" + Constant.PROJECT_NAME);
                    if (Constant.PROJECT_NAME.equals("SW760")) {
                        outputStream.write(rotateAndMirror(data, -90, true));     // 写入sd卡中
                    } else {
                        outputStream.write(rotateAndMirror(data, 0, true));
                    }                // 写入sd卡中
                    outputStream.close();                      // 关闭输出流
                }catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }  catch (IOException e) {
                    e.printStackTrace();
                }

                mImgOriginalPath = file.getAbsolutePath();
                shareImage();   

                releaseCamera();
            }
        });
    }

    /**
     * 释放相机资源
     * 拍照完成后释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 设置Camera参数
     * 设置预览界面的宽高，图片保存的宽、高
     */
    private void setCameraParameters() {
        if (mCamera != null) {
            Log.d(TAG, "setCameraParameters >> begin. mCamera != null");
            if (mParameters == null) {
                mParameters = mCamera.getParameters();
            }

            int PICTURE_SIZE_WIDHT = 1200;
            int PICTURE_SIZE_HEIGHT = 1200;

            if(xunSn.contains(xun_703_productId1) 
                || xunSn.contains(xun_703_productId2)){ // 703线上版
                        PICTURE_SIZE_WIDHT = 1200;
                        PICTURE_SIZE_HEIGHT = 1200;
                        picScaleSize = (float) 1;
            }else if (xunSn.contains(xun_703_productId3) 
                || xunSn.contains(xun_703_productId4)) {// 703线下版
                        PICTURE_SIZE_WIDHT = 1200;
                        PICTURE_SIZE_HEIGHT = 1200;
                        picScaleSize = (float) 1.46;
            } else { //760
                PICTURE_SIZE_WIDHT = 480;
                PICTURE_SIZE_HEIGHT = 480;
            }

            Log.d(TAG, "setCameraParameters >> PICTURE_SIZE_WIDHT:" + PICTURE_SIZE_WIDHT);
            
            mParameters.setPreviewSize(PREVIEW_SIZE_WIDTH_705, PREVIEW_SIZE_HEIGHT_705);
            mParameters.setPictureSize(PICTURE_SIZE_WIDHT, PICTURE_SIZE_HEIGHT);
                       
            mCamera.setParameters(mParameters);
        } else {
            Log.e(TAG, "setCameraParameters >> mCamera == null!!");
        }
    }

    /**
     * 开始拍照
     */
    public void takePhoto(Context context, final XiaoXunNetworkManager xiaoXunNetworkManager) {
        Log.d(TAG, "takePhoto >> ");
        mContext = context;
        initXunNetManager(xiaoXunNetworkManager);
        startPreview();

    }

// =======================================================================
// =======================================================================
    /**
     * 初始化网络层相关参数
     */
    private void initXunNetManager(XiaoXunNetworkManager xiaoXunNetworkManager) {
        if (xiaoXunNetworkManager != null) {
            mXunNetworkManager = xiaoXunNetworkManager;
        } else {
            mXunNetworkManager = (XiaoXunNetworkManager) mContext.getSystemService("xun.network.Service");
        }        
        mGID = mXunNetworkManager.getWatchGid();
        mEID = mXunNetworkManager.getWatchEid();
        mToken = mXunNetworkManager.getSID();
        mAES_KEY = mXunNetworkManager.getAESKey();
    }

    /**
     * 图片上传过程
     * 1.首先握手：通过setMapMSetValue实现
     * 2.图片上传：握手成功调用uploadFilesLocal方法上传图片
     * 3.APP端收到图片：图片上传成功后，服务器将图片转发给APP端
     */
    private void shareImage() {
        Log.d(TAG, "shareImage >> 开始上传 mImgOriginalPath " + mImgOriginalPath);
        // 上传之前写一次mGID，实现固件端与服务器的权限授予
        ShakeHandsCallback callback = new ShakeHandsCallback();
        if (mXunNetworkManager != null && mImgOriginalPath != "null") {
            mXunNetworkManager.setMapMSetValue(mGID, new String[]{"test"}, new String[]{"test"}, callback);
        }else {
            Log.d(TAG, "shareImage >> mXunNetworkManager =" + mXunNetworkManager 
                + " mImgOriginalPath: " + mImgOriginalPath );
        }
    }

    /**
    * 握手并上传
    */
    private class ShakeHandsCallback extends IResponseDataCallBack.Stub{
           @Override
           public void onSuccess(ResponseData responseData) {        
               uploadFilesLocal(mToken, IMAGE_TYPE, mEID, mGID, mImgOriginalPath, mImgOriginalPath); 
               Log.d(TAG,"ShakeHandsCallback 握手成功:" + responseData);
           }
           @Override
           public void onError(int i, String s) {
               Log.d(TAG,"ShakeHandsCallback 握手失败>> i :" + i + " ; s : " + s);
          }    
    }

    /**
     * [uploadFilesLocal 上传文件]
     * @param token           [标志位]
     * @param type            [文件类型]
     * @param eid             [EID]
     * @param gid             [GID]
     * @param filePath        [原文件路径]
     * @param previewFilePath [预览文件路径]
     *
     *  上传结果通过OnUploadResult返回
     *  文件上传成功只是表示服务器收到了文件，最终需要服务器发送给家长APP端才是真正的分享成功
     *  服务器发送文件给家长APP端通过uploadNotice完成，结果在UploadFileCallback回调
     */
    private void uploadFilesLocal(final String token, final String type, final String eid, final String gid, final String filePath, final String previewFilePath) {
        UploadFile mUploadFile = new UploadFile(mContext, mToken, mAES_KEY);
        Log.d(TAG, "uploadFilesLocal >> filePath 文件路径" + filePath + " 预览路径" + previewFilePath);
        mUploadFile.uploadFile(token, type, eid, gid, filePath, previewFilePath, 
            new ProgressListener() {
                @Override
                public void transferred(long l) {
                    Log.d(TAG, "transferred >> l 传输进度: " + l);
                }
            }, 
            new OnUploadResult() {
                @Override
                public void onResult(String s) {
                    if (s.contains("GP")) {
                        // upload success
                        // 最终APP端收到图片
                        UploadNoticeCallback callback = new UploadNoticeCallback();                        
                        mXunNetworkManager.uploadNotice(eid, gid, type, s, callback);
                        Log.d(TAG, "OnUploadResult >> onResult 上传成功: " + s);
                        deleteImage(mImgOriginalPath);
                    } else {
                        Log.e(TAG, "OnUploadResult >> onResult 上传失败: " + s);
                        deleteImage(mImgOriginalPath);
                    }
                }
            });
    }

    /**
     * 发送消息回执，服务器收到固件端的图片后，还需要发送给手机APP端，才算真正上传成功
     */
    private class UploadNoticeCallback extends IResponseDataCallBack.Stub{
           @Override
           public void onSuccess(ResponseData responseData) {         
               Log.d(TAG, "UploadNoticeCallback 回执消息成功 :" + responseData);
           }
           @Override
           public void onError(int i, String s) {
               Log.d(TAG, "UploadNoticeCallback 回执消息失败 i :" + i + " ; s : " + s);
          }    
    }

    // 分享完成后删除该照片，避免出现在相册中
    private void deleteImage(String imgPath) {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath}, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = resolver.delete(uri, null, null);
            result = count == 1;
            Log.d(TAG, "deleteImage, cursor: " + result);
        } else {
            File file = new File(imgPath);
            Log.d(TAG, "deleteImage, file: " + file.exists());
            if (file.exists()) { // 判断文件是否存在
                result = file.delete();
            }
        }
    }

    /**
     * 实现反转、镜像
     * @param  data    [源数据]
     * @param  degrees [旋转角度]
     * @param  mirror  [是否镜像]
     * @return         [description]
     */
    public byte[] rotateAndMirror(byte[] data, int degrees, boolean mirror) {
        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
        if ((degrees != 0 || mirror) && b != null) {
            Matrix m = new Matrix();
            // Mirror first.
            // horizontal flip + rotation = -rotation + horizontal flip
            if (mirror) {
                m.postScale(-1, 1);
                degrees = (degrees + 360) % 360;
                if (degrees == 0 || degrees == 180) {
                    m.postTranslate(b.getWidth(), 0);
                } else if (degrees == 90 || degrees == 270) {
                    m.postTranslate(b.getHeight(), 0);
                } else {
                    throw new IllegalArgumentException("Invalid degrees=" + degrees);
                }
            }
            if (degrees != 0) {
                // clockwise
                m.postRotate(degrees,
                        (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            }

            try {
                // Matrix scaleMatrix = new Matrix();
                m.postScale(picScaleSize, picScaleSize);
                Log.d(TAG, "rotateAndMirror>> picScaleSize:" + picScaleSize);
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] scaleData = outputStream.toByteArray();
                b.recycle();
                return scaleData;
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
                ex.printStackTrace();
            }
        }
        return null;
    }

}
