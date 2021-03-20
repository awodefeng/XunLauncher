package com.xxun.xunlauncher.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.xxun.xunlauncher.R;
import android.os.Environment;
import java.io.File;
import android.view.Window;
import android.view.Gravity;
import android.view.WindowManager;
import java.io.FileInputStream;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.Base64;
import android.widget.TextView;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import com.xxun.xunlauncher.activity.MainActivity;
/**
 * @author lihaizhou
 * @time 2017.12.13
 * @class describe 生成二维码界面
 */
public class QrcodeDialogFragment extends DialogFragment {

    private static final String TAG = "QrcodeDialogFragment";

    private Context mcontext;

    public QrcodeDialogFragment(Context context) {
        mcontext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.qrcode_fragment, container, false);
        ImageView qrcodeImg = (ImageView) view.findViewById(R.id.qrcode_idle);
        TextView tipMsg = (TextView)view.findViewById(R.id.tip);

        String qrcodeContent =  xunReadQrcodeUrlFile();
        if((null == qrcodeContent) || ("".equals(qrcodeContent)))
        {
            qrcodeImg.setImageResource(R.drawable.high_temper_warn_bg);
            tipMsg.setText(R.string.qr_tip);
            
            Log.d(TAG,"[QRCODE] send qrcode brocast!!!");
	    Intent mintent = new Intent("brocast_qrcode_resqust");
	    MainActivity activity = (MainActivity) getActivity();
	    activity.sendBroadcast(mintent);

        }else {
                Log.d(TAG,"[QRCODE] read,qrcodeContent.length()="+qrcodeContent.length());
                Bitmap qrcodeBitmap = convertStringToIcon(qrcodeContent);
                qrcodeImg.setImageBitmap(qrcodeBitmap);
        }
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity= Gravity.BOTTOM;
        layoutParams.width = layoutParams.MATCH_PARENT;
        layoutParams.height = layoutParams.MATCH_PARENT;
        getDialog().getWindow().getDecorView().setPadding(0,0,0,0);
        getDialog().getWindow().setAttributes(layoutParams);
    }

    private String xunReadQrcodeUrlFile() {
        String readResult = "";
        try {
            //File file = new File(Environment.getExternalStorageDirectory(), "xun_qrcode.txt");
	    File file_dir = new File(Environment.getExternalStorageDirectory()+"/QRCode");  
	    if (!file_dir.exists()) {  
		file_dir.mkdirs();  
	    }  
            File file = new File(Environment.getExternalStorageDirectory()+"/QRCode","xun_qrcode.txt");

            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                byte[] b = new byte[is.available()];
                is.read(b);
                readResult = new String(b);
                is.close();
                return readResult;
            } else {
                return readResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return readResult;
        }
    }

    private static Bitmap convertStringToIcon(String st) {
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            Bitmap bitmap =BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            return bitmap;
        }
        catch (Exception e) {
            return null;
        }
    }
}
