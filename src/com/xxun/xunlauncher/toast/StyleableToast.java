package com.xxun.xunlauncher.toast;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
//import android.support.annotation.ColorInt;
//import android.support.annotation.DrawableRes;
//import android.support.annotation.NonNull;
//import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.xxun.xunlauncher.R;

public class StyleableToast extends LinearLayout{

    private int cornerRadius = -1;
    private int backgroundColor;
    private int strokeColor;
    private int strokeWidth;
    private int iconResLeft;
    private int iconResRight;
    private int textColor;
    private int length;
    private int style;
    private float textSize;
    private boolean isTextSizeFromStyleXml = false;
    private boolean hasAnimation;
    private boolean solidBackground;
    private boolean textBold;
    private String text;
    private TypedArray typedArray;
    private TextView textView;
    private Typeface typeface;
    private ImageView iconLeft;
    private Toast styleableToast;
    private LinearLayout rootLayout;

    public static StyleableToast makeText(Context context, String text, int length,int style) {
        return new StyleableToast(context, text, length, style);
    }

    public static StyleableToast makeText(Context context, String text,int style) {
        return new StyleableToast(context, text, Toast.LENGTH_SHORT, style);
    }

    private StyleableToast(Context context, String text, int length,int style) {
        super(context);
        this.text = text;
        this.length = length;
        this.style = style;
    }

    private StyleableToast(Builder builder) {
        super(builder.context);
        this.backgroundColor = builder.backgroundColor;
        this.cornerRadius = builder.cornerRadius;
        this.iconResRight = builder.iconResRight;
        this.iconResLeft = builder.iconResLeft;
        this.strokeColor = builder.strokeColor;
        this.strokeWidth = builder.strokeWidth;
        this.hasAnimation = builder.hasAnimation;
        this.solidBackground = builder.solidBackground;
        this.textColor = builder.textColor;
        this.textSize = builder.textSize;
        this.textBold = builder.textBold;
        this.typeface = builder.typeface;
        this.text = builder.text;
        this.length = builder.length;
    }

    private void initStyleableToast() {
        View v = inflate(getContext(), R.layout.styleable_layout, null);
        rootLayout = (LinearLayout) v.findViewById(R.id.root);
        textView = (TextView) v.findViewById(R.id.textview);
        iconLeft = (ImageView) v.findViewById(R.id.icon_left);
        if (style > 0) {
            typedArray = getContext().obtainStyledAttributes(style, R.styleable.StyleableToast);
        }

        makeShape();
        makeTextView();
        makeIcon();

        // Very important to recycle AFTER the make() methods!
        if (typedArray != null) {
            typedArray.recycle();
        }
        if (hasAnimation) {
            iconLeft.setAnimation(getAnimation());
        }
    }

    public void show() {
        initStyleableToast();
        styleableToast = new Toast(getContext());
        styleableToast.setGravity(Gravity.TOP,0,0);
        styleableToast.setDuration(length == Toast.LENGTH_LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        styleableToast.setView(rootLayout);
        styleableToast.show();
    }

    public void cancel() {
        if (styleableToast != null) {
            styleableToast.cancel();
        }
    }

    @Deprecated
    public Toast getStyleableToast() {
        return styleableToast;
    }


    private void makeShape() {
        //loadShapeAttributes();//due to already have userforbidActivity,remove by lihaizhou avoid compile error 20180710 begin
        GradientDrawable gradientDrawable = (GradientDrawable) rootLayout.getBackground();
        gradientDrawable.setCornerRadius(cornerRadius != -1 ? toDp(cornerRadius) : R.dimen.default_corner_radius);
        gradientDrawable.setStroke(toDp(strokeWidth), strokeColor);

        if (backgroundColor != 0) {
            gradientDrawable.setColor(backgroundColor);
        }

        if (solidBackground) {
            gradientDrawable.setAlpha(getResources().getInteger(R.integer.fullBackgroundAlpha));
        } else {
            gradientDrawable.setAlpha(getResources().getInteger(R.integer.defaultBackgroundAlpha));
        }

        rootLayout.setBackground(gradientDrawable);
    }

    private void makeTextView() {
        loadTextViewStyleAttributes();
        textView.setText(text);

        if (textColor != 0) {
            textView.setTextColor(textColor);
        }

        if (textSize > 0) {
            textView.setTextSize(isTextSizeFromStyleXml ? 0 : TypedValue.COMPLEX_UNIT_SP, textSize);
        }

        if (typeface != null) {
            textView.setTypeface(typeface, textBold ? Typeface.BOLD : Typeface.NORMAL);
        } else if (textBold) {
            textView.setTypeface(textView.getTypeface(), textBold ? Typeface.BOLD : Typeface.NORMAL);
        }
    }


    private void makeIcon() {
        loadIconAttributes();
        if (iconResLeft > 0 || iconResRight > 0) {
            int horizontalPadding = (int) getResources().getDimension(R.dimen.toast_horizontal_padding_with_icon);
            int verticalPadding = (int) getResources().getDimension(R.dimen.toast_vertical_padding);
            rootLayout.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        }
        if (iconResLeft > 0) {
            iconLeft.setBackgroundResource(iconResLeft);
            iconLeft.setVisibility(VISIBLE);
        }

    }

    /**
     * loads style attributes from styles.xml if a style resource is used.
     */
    //due to already have userforbidActivity,remove by lihaizhou avoid compile error 20180710
    /*private void loadShapeAttributes() {
        if (style == 0) {
            return;
        }

        solidBackground = typedArray.getBoolean(R.styleable.StyleableToast_solidBackground, false);
        backgroundColor = typedArray.getColor(R.styleable.StyleableToast_colorBackground, ContextCompat.getColor(getContext(), R.color.defaultBackgroundColor));
        cornerRadius = (int) typedArray.getDimension(R.styleable.StyleableToast_cornerRadius, R.dimen.default_corner_radius);

        if (typedArray.hasValue(R.styleable.StyleableToast_length)) {
            length = typedArray.getInt(R.styleable.StyleableToast_length, 0);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            if (typedArray.hasValue(R.styleable.StyleableToast_strokeColor) && typedArray.hasValue(R.styleable.StyleableToast_strokeWidth)) {
                strokeWidth = (int) typedArray.getDimension(R.styleable.StyleableToast_strokeWidth, 0);
                strokeColor = typedArray.getColor(R.styleable.StyleableToast_strokeColor, Color.TRANSPARENT);
            }
        }
    }*/

    private void loadTextViewStyleAttributes() {
        if (style == 0) {
            return;
        }
        textColor = typedArray.getColor(R.styleable.StyleableToast_textColor, Color.WHITE);
        textBold = typedArray.getBoolean(R.styleable.StyleableToast_textBold, false);
        textSize = typedArray.getDimension(R.styleable.StyleableToast_textSize, 0);
        isTextSizeFromStyleXml = textSize > 0;
    }

    private void loadIconAttributes() {
        if (style == 0) {
            return;
        }
        iconResLeft = typedArray.getResourceId(R.styleable.StyleableToast_iconLeft, 0);
    }

    @Override
    public Animation getAnimation() {
        if (hasAnimation) {
            RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(1000);
            return anim;
        }
        return null;

    }

    private int toDp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public static class Builder {
        private int cornerRadius = -1;
        private int backgroundColor;
        private int strokeColor;
        private int strokeWidth;
        private int iconResLeft;
        private int iconResRight;
        private int textColor;
        private int length;
        private float textSize;
        private boolean solidBackground;
        private boolean hasAnimation;
        private boolean textBold;
        private String text;
        private Typeface typeface;
        private final Context context;
        private StyleableToast styleableToast;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder textBold() {
            this.textBold = true;
            return this;
        }

        public Builder textSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        @Deprecated
        public Builder typeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder solidBackground() {
            this.solidBackground = true;
            return this;
        }

        public Builder stroke(int strokeWidth,int strokeColor) {
            this.strokeWidth = strokeWidth;
            this.strokeColor = strokeColor;
            return this;
        }

        /**
         * @param cornerRadius Sets the corner radius of the StyleableToast's shape.
         */
        public Builder cornerRadius(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public Builder iconResLeft(int iconResLeft) {
            this.iconResLeft = iconResLeft;
            return this;
        }

        public Builder iconResRight(int iconResRight) {
            this.iconResRight = iconResRight;
            return this;
        }

        /**
         * Enables spinning animation of the passed iconResLeft by its around its own center.
         */
        @Deprecated
        public Builder spinIcon() {
            this.hasAnimation = true;
            return this;
        }

        /**
         * @param length {@link Toast#LENGTH_SHORT} or {@link Toast#LENGTH_LONG}
         */
        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public void show() {
            styleableToast = new StyleableToast(this);
            styleableToast.show();
        }

    }
}
