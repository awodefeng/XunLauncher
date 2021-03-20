package com.xxun.xunlauncher.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.EditText;

import com.xxun.xunlauncher.R;


/**
 */

public class PwdEditText extends EditText {

    private static final String TAG = "PwdEditText";

    private final int STYLE_RECTANGLE = 0;

    private final int STYLE_ROUND_RECTANGLE = 1;

    private final int DEFAULT_STYLE = STYLE_RECTANGLE;

    private final int DEFAULT_PWD_COUNT = 6;

    private final float DEFAULT_STROKE_RADIUS = dp2Px(6);

    private final float DEFAULT_STROKE_WIDTH = dp2Px(1);

    private final int DEFAULT_STROKE_COLOR = Color.BLACK;

    private final int DEFAULT_DOT_COLOR = Color.BLACK;

    private final float DEFAULT_DOT_RADIUS = dp2Px(4);

    private int style;   // 控件的样式，矩形或圆角矩形

    private float strokeRadius;  // 边框圆角的半径

    private float strokeWidth;   // 边框宽度

    private int strokeColor;  // 边框颜色

    private int pwdDotColor;  // 密码圆点颜色

    private float pwdDotRadius;  // 密码圆点半径

    private int mWidth;  // 控件宽度

    private int mHeight;  // 控件高度

    private Paint strokePaint;    // 绘制边框paint

    private Paint pwdDotPaint;    // 绘制密码圆点paint

    private int mCount;   // 密码框个数

    private float cellWidth;   // 每个密码框的宽度

    private float halfStrokeWidth;

    private int mCurInputCount;  // 当前输入字符个数

    private int textSize;

    private String mInputNumber;
    private OnTextInputListener onTextInputListener;
    private OnTextChangedListener onTextChangedListener;

    public PwdEditText(Context context) {
        this(context, null);
        Log.d(TAG, "one params");
    }

    /**
     * 无论xml布局文件中有没有写自定义属性，都调用两个参数的构造函数
     */
    public PwdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "two params");
        initAttrs(context, attrs);
        init();
    }

    /**
     * 当有自定义的样式时，调用三个参数的构造函数
     */
    public PwdEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "three params");
    }

    /**
     * 初始化自定义属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PwdEditText);
        style = typedArray.getInt(R.styleable.PwdEditText_style, DEFAULT_STYLE);
        mCount = typedArray.getInt(R.styleable.PwdEditText_pwdCount, DEFAULT_PWD_COUNT);
//        strokeColor = typedArray.getColor(R.styleable.PwdEditText_strokeColor, DEFAULT_STROKE_COLOR);
        strokeColor = Color.WHITE;
        strokeWidth = typedArray.getDimension(R.styleable.PwdEditText_strokeWidthPWD, DEFAULT_STROKE_WIDTH);
        strokeRadius = typedArray.getDimension(R.styleable.PwdEditText_strokeRadius, DEFAULT_STROKE_RADIUS);
        pwdDotColor = typedArray.getColor(R.styleable.PwdEditText_dotColor, DEFAULT_DOT_COLOR);
        pwdDotRadius = typedArray.getDimension(R.styleable.PwdEditText_dotRadius, DEFAULT_DOT_RADIUS);
        mWidth = typedArray.getInt(R.styleable.PwdEditText_mWidth, 200);
        textSize = typedArray.getInt(R.styleable.PwdEditText_mTextSize, 35);

        typedArray.recycle();
    }

    /**
     * 初始化操作
     */
    private void init() {
        // 初始化边框画笔
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // 初始化圆点画笔
        pwdDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pwdDotPaint.setStyle(Paint.Style.FILL);
        pwdDotPaint.setColor(Color.WHITE);
        pwdDotPaint.setTextSize(textSize);

        halfStrokeWidth = strokeWidth / 2;

        // 设置光标不可见
        setCursorVisible(false);
        // 设置限定最大长度
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(mCount)});

        setBackgroundColor(Color.BLACK);

        setMaxLines(1);

        setFocusable(false);

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (onTextChangedListener != null) {
                    onTextChangedListener.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d(TAG, "onTextChanged s: " + s + " start " + start + " before " + before);
                mInputNumber = s.toString()/*.substring(start)*/;
                Log.d(TAG, "mInputNumber " + mInputNumber);
                if (onTextChangedListener != null) {
                    onTextChangedListener.onTextChanged(s, start, before, count);
                }
                mCurInputCount = s.toString().length();

                // 输入完成的回调
                if (mCurInputCount == mCount) {
                    if (onTextInputListener != null) {
                        onTextInputListener.onComplete(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged s:" + s);
                if (onTextChangedListener != null) {
                    onTextChangedListener.afterTextChanged(s);
                }
            }
        });


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        cellWidth = (mWidth) / mCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawStroke(canvas);
        drawPwdDot(canvas);
    }

    private void drawPwdDot(Canvas canvas) {
        for (int i = 1; i <= mCurInputCount; i++) {
            if (mInputNumber != null) {
                canvas.drawText(mInputNumber.substring(i - 1, i), cellWidth / 6 + cellWidth * (i - 1), (mHeight) * 4 / 5,
                        pwdDotPaint);
            }
        }
    }

    private void drawStroke(Canvas canvas) {
        for (int i = 0; i < mCount; i++) {
			// guohongcheng_20190111
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.line_09)
                    , cellWidth * i,
                    mHeight - 8,
                    strokePaint);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private float dp2Px(int dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    public void setOnTextInputListener(OnTextInputListener onTextInputListener) {
        this.onTextInputListener = onTextInputListener;
    }

    public void addTextChangedListener(OnTextChangedListener listener) {
        this.onTextChangedListener = listener;
    }


    public interface OnTextChangedListener {
        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s);
    }

    public interface OnTextInputListener {
        void onComplete(String result);
    }
}
