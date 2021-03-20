package com.xxun.xunlauncher.clock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import com.xxun.xunlauncher.R;
import android.view.View;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter; 

/**
 * @author lihaizhou
 * @time 2017.12.18
 * @class describe 提供时针,分针,秒针绘制的基础类
 */
public class WatchRotateView extends View {
    
    private Bitmap bmp;
    private int resId;
    private Matrix matrix;
    private float degree = 0.0f;
    private PaintFlagsDrawFilter pfd; 

    private final String TAG = "WatchRotateView";

    public WatchRotateView(Context context, AttributeSet atrr) {
        super(context, atrr);
        TypedArray a = context.obtainStyledAttributes(atrr, R.styleable.WatchView);
        resId = a.getResourceId(0, R.drawable.simulation_hour_sword_01);
        a.recycle();
        try {
            bmp = BitmapFactory.decodeResource(getResources(), resId);
        } catch (OutOfMemoryError error) {
            Log.e(TAG, "OutOfMemoryError " + error.toString());
        }
        matrix = new Matrix();
	pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG); 
    }

    public void setDegree(float d) {
        this.degree = d;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bmp == null) {
            Log.e(TAG, "onDraw bmp == null");
            try {
                bmp = BitmapFactory.decodeResource(getResources(), resId);
                return;
            } catch (OutOfMemoryError error) {
                Log.e(TAG, "OutOfMemoryError " + error.toString());
                return;
            }
        }
        matrix.reset();
        matrix.postRotate(degree, (float) (getHeight() / 2), (float) (getWidth() / 2));
	canvas.setDrawFilter(pfd);
        canvas.drawBitmap(bmp, matrix, null);
    }
}
