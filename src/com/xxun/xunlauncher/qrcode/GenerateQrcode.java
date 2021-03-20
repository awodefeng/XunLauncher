package com.xxun.xunlauncher.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;
import com.xxun.xunlauncher.qrcode.zxing.BarcodeFormat;
import com.xxun.xunlauncher.qrcode.zxing.EncodeHintType;
import com.xxun.xunlauncher.qrcode.zxing.WriterException;
import com.xxun.xunlauncher.qrcode.zxing.common.BitMatrix;
import com.xxun.xunlauncher.qrcode.zxing.qrcode.QRCodeWriter;
import com.xxun.xunlauncher.qrcode.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;

import android.os.Environment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.app.Activity;

import android.content.res.Resources;

import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import com.xxun.xunlauncher.R;


public class GenerateQrcode {

    private static final int QR_WIDTH = 240;
    private static final int QR_HEIGHT = 240;
    private static final int QR_WIDTH_730 = 320;
    private static final int QR_HEIGHT_730 = 320;
    private static String TAG = "GenerateQrcode";

    private Context mcontext;

    public GenerateQrcode(Context context) {
        mcontext = context;
    }

    public void xunCreatQRcodeBitmap(Resources res,String code_url){

        Log.d(TAG,"[lunch service]code_url:" +code_url);

   	Bitmap qrcode_map;

        Bitmap logo_bitmap = BitmapFactory.decodeResource(res, R.drawable.qrcode_logo);
	qrcode_map = createQRImage(code_url);
        Bitmap qrcode_logo = addLogo(qrcode_map,logo_bitmap);	
        String save_string = convertIconToString(qrcode_logo);
      
        //Log.d(TAG,"[lunch service]save_string: " +save_string);
        xunDeleteQrcodeUrlFile();
        
        xunWriteQrcodeUrlToFile(save_string);

        
	}


    private static Bitmap createQRImage(String url) {

        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }

	    Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
	    hints.put(EncodeHintType.MARGIN, 1);
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        //pixels[y * QR_WIDTH + x] = 0xffffffff;
                        pixels[y * QR_WIDTH + x] = 0xFFF1C900;  //设置图片填充色
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


         private static Bitmap createQRImage730(String url) {

        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }

	    Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
	    hints.put(EncodeHintType.MARGIN, 1);
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH_730, QR_HEIGHT_730, hints);
            int[] pixels = new int[QR_WIDTH_730 * QR_HEIGHT_730];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT_730; y++) {
                for (int x = 0; x < QR_WIDTH_730; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH_730 + x] = 0xff000000;
                    } else {
                        //pixels[y * QR_WIDTH + x] = 0xffffffff;
                        pixels[y * QR_WIDTH_730 + x] = 0xFFF1C900;  //设置图片填充色
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH_730, QR_HEIGHT_730, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH_730, 0, 0, QR_WIDTH_730, QR_HEIGHT_730);
            //显示到一个ImageView上面
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

   private void xunDeleteQrcodeUrlFile()
   {
	  try {
            File file = new File(Environment.getExternalStorageDirectory()+"/QRCode","xun_qrcode.txt");
            if(file.exists())
            {
		file.delete();
                Log.d(TAG,"[lunch service]file delete success!!!");
	    }
            else
            {
		Log.d(TAG,"[lunch service]file not exect!!!");
	    }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
   }

   private void xunWriteQrcodeUrlToFile(String code_url)
   {
	  try {

	    File file_dir = new File(Environment.getExternalStorageDirectory()+"/QRCode");  
	    if (!file_dir.exists()) {  
		    file_dir.mkdirs();  
	    }  
            File file = new File(Environment.getExternalStorageDirectory()+"/QRCode","xun_qrcode.txt");

            FileOutputStream fos = new FileOutputStream(file);
 
            fos.write(code_url.getBytes());
            fos.close();
            Log.d(TAG,"[lunch service]file write success!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }

   }


    private static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();//
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }


    private static Bitmap convertStringToIcon(String st)
    {
        // OutputStream out;
        Bitmap bitmap = null;
        try
        {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
    }


}
