package com.zpj.widget.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zpj.utils.ColorUtils;
import com.zpj.utils.ScreenUtils;

public class ZToolBar extends BaseToolBar {

    protected static final int TYPE_LEFT_NONE = 0;
    protected static final int TYPE_LEFT_TEXTVIEW = 1;
    protected static final int TYPE_LEFT_IMAGEBUTTON = 2;
    protected static final int TYPE_LEFT_CUSTOM_VIEW = 3;
    protected static final int TYPE_RIGHT_NONE = 0;
    protected static final int TYPE_RIGHT_TEXTVIEW = 1;
    protected static final int TYPE_RIGHT_IMAGEBUTTON = 2;
    protected static final int TYPE_RIGHT_CUSTOM_VIEW = 3;
    protected static final int TYPE_CENTER_NONE = 0;
    protected static final int TYPE_CENTER_TEXTVIEW = 1;
    protected static final int TYPE_CENTER_SEARCHVIEW = 2;
    protected static final int TYPE_CENTER_CUSTOM_VIEW = 3;

    protected int leftType;                               // 左边视图类型
    protected String leftText;                            // 左边TextView文字
    protected int leftTextColor;                          // 左边TextView颜色
    protected float leftTextSize;                         // 左边TextView文字大小
    protected int leftDrawable;                           // 左边TextView drawableLeft资源
    protected float leftDrawablePadding;                  // 左边TextView drawablePadding
    protected int leftImageResource;                      // 左边图片资源
    protected int leftCustomViewRes;                      // 左边自定义视图布局资源

    protected int rightType;                              // 右边视图类型
    protected String rightText;                           // 右边TextView文字
    protected int rightTextColor;                         // 右边TextView颜色
    protected float rightTextSize;                        // 右边TextView文字大小
    protected int rightImageResource;                     // 右边图片资源
    protected int rightCustomViewRes;                     // 右边自定义视图布局资源

    protected int centerType;                             // 中间视图类型
    protected String centerText;                          // 中间TextView文字
    protected int centerTextColor;                        // 中间TextView字体颜色
    protected float centerTextSize;                       // 中间TextView字体大小
    protected int centerTextGravity;
    protected boolean centerTextMarquee;                  // 中间TextView字体是否显示跑马灯效果
    protected String centerSubText;                       // 中间subTextView文字
    protected int centerSubTextColor;                     // 中间subTextView字体颜色
    protected float centerSubTextSize;                    // 中间subTextView字体大小
    protected int centerCustomViewRes;                    // 中间自定义布局资源

    protected TextView tvTitle;
    protected TextView tvSubTitle;

    public ZToolBar(Context context) {
        super(context);
    }

    public ZToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initAttribute(final Context context, AttributeSet attrs) {
        super.initAttribute(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZToolBar);

        leftType = array.getInt(R.styleable.ZToolBar_z_toolbar_leftType, TYPE_LEFT_NONE);
        if (leftType == TYPE_LEFT_TEXTVIEW) {
            leftText = array.getString(R.styleable.ZToolBar_z_toolbar_leftText);
            leftTextColor = array.getColor(R.styleable.ZToolBar_z_toolbar_leftTextColor, getResources().getColor(R.color.z_toolbar_text_selector));
            leftTextSize = array.getDimensionPixelSize(R.styleable.ZToolBar_z_toolbar_leftTextSize, ScreenUtils.dp2pxInt(context, 16));
            leftDrawable = array.getResourceId(R.styleable.ZToolBar_z_toolbar_leftDrawable, 0);
            leftDrawablePadding = array.getDimension(R.styleable.ZToolBar_z_toolbar_leftDrawablePadding, 5);
        } else if (leftType == TYPE_LEFT_IMAGEBUTTON) {
            leftImageResource = array.getResourceId(R.styleable.ZToolBar_z_toolbar_leftImageResource, R.drawable.ic_arrow_back_black_24dp);
        } else if (leftType == TYPE_LEFT_CUSTOM_VIEW) {
            leftCustomViewRes = array.getResourceId(R.styleable.ZToolBar_z_toolbar_leftCustomView, 0);
        }

        rightType = array.getInt(R.styleable.ZToolBar_z_toolbar_rightType, TYPE_RIGHT_NONE);
        if (rightType == TYPE_RIGHT_TEXTVIEW) {
            rightText = array.getString(R.styleable.ZToolBar_z_toolbar_rightText);
            rightTextColor = array.getColor(R.styleable.ZToolBar_z_toolbar_rightTextColor, getResources().getColor(R.color.z_toolbar_text_selector));
            rightTextSize = array.getDimensionPixelSize(R.styleable.ZToolBar_z_toolbar_rightTextSize, ScreenUtils.dp2pxInt(context, 16));
        } else if (rightType == TYPE_RIGHT_IMAGEBUTTON) {
            rightImageResource = array.getResourceId(R.styleable.ZToolBar_z_toolbar_rightImageResource, 0);
        } else if (rightType == TYPE_RIGHT_CUSTOM_VIEW) {
            rightCustomViewRes = array.getResourceId(R.styleable.ZToolBar_z_toolbar_rightCustomView, 0);
        }

        centerType = array.getInt(R.styleable.ZToolBar_z_toolbar_centerType, TYPE_CENTER_NONE);
        centerText = array.getString(R.styleable.ZToolBar_z_toolbar_centerText);
        if (TextUtils.isEmpty(centerText)) {
            if (centerType == TYPE_CENTER_TEXTVIEW) {
                centerText = "Title";
            } else {
                centerCustomViewRes = array.getResourceId(R.styleable.ZToolBar_z_toolbar_centerCustomView, 0);
                if (centerType != TYPE_CENTER_CUSTOM_VIEW) {
                    centerType = TYPE_CENTER_CUSTOM_VIEW;
                }
            }
        } else {
            centerType = TYPE_CENTER_TEXTVIEW;
        }
        if (centerType == TYPE_CENTER_TEXTVIEW) {
            centerTextColor = array.getColor(R.styleable.ZToolBar_z_toolbar_centerTextColor, Color.parseColor("#333333"));
            centerTextSize = array.getDimensionPixelSize(R.styleable.ZToolBar_z_toolbar_centerTextSize, ScreenUtils.dp2pxInt(context, 18));
            centerTextMarquee = array.getBoolean(R.styleable.ZToolBar_z_toolbar_centerTextMarquee, true);
            centerSubText = array.getString(R.styleable.ZToolBar_z_toolbar_centerSubText);
            centerSubTextColor = array.getColor(R.styleable.ZToolBar_z_toolbar_centerSubTextColor, Color.parseColor("#666666"));
            centerSubTextSize = array.getDimensionPixelSize(R.styleable.ZToolBar_z_toolbar_centerSubTextSize, ScreenUtils.dp2pxInt(context, 11));
            centerTextGravity = array.getInt(R.styleable.ZToolBar_z_toolbar_center_text_gravity, Gravity.CENTER_VERTICAL);
        }
        array.recycle();
    }

    @Override
    protected void inflateLeftContainer(ViewStub viewStub) {
        viewStub.setInflatedId(generateViewId());
        if (leftType == TYPE_LEFT_IMAGEBUTTON) {
            viewStub.setLayoutResource(R.layout.z_toolbar_image_button);
            ImageButton button = (ImageButton) viewStub.inflate();
            button.setImageResource(leftImageResource);
            button.setColorFilter(isLightStyle ? Color.WHITE : Color.BLACK);
        } else if (leftType == TYPE_LEFT_TEXTVIEW) {
            viewStub.setLayoutResource(R.layout.z_toolbar_text_view);
            TextView textView = (TextView) viewStub.inflate();
            textView.setText(leftText);
            textView.setTextColor(getStyleColor(leftTextColor));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        } else if (leftType == TYPE_LEFT_CUSTOM_VIEW && leftCustomViewRes != 0) {
            viewStub.setLayoutResource(leftCustomViewRes);
            viewStub.inflate();
        }
    }

    @Override
    protected void inflateMiddleContainer(ViewStub viewStub) {
        if (centerType == TYPE_CENTER_NONE) {
            return;
        }
        viewStub.setInflatedId(generateViewId());
        if (centerType == TYPE_CENTER_TEXTVIEW) {
            viewStub.setLayoutResource(R.layout.z_toolbar_center_layout);
            viewStub.inflate();
            tvTitle = findViewById(R.id.tv_title);
            tvSubTitle = findViewById(R.id.tv_sub_title);
            tvTitle.setGravity(centerTextGravity);
            tvSubTitle.setGravity(centerTextGravity);
            tvTitle.setText(centerText);
            tvTitle.setTextColor(getStyleColor(centerTextColor));
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, centerTextSize); // TypedValue.COMPLEX_UNIT_PX,
            if (centerTextMarquee){
                tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tvTitle.setMarqueeRepeatLimit(-1);
                tvTitle.requestFocus();
                tvTitle.setSelected(true);
            }
            if (TextUtils.isEmpty(centerSubText)) {
                tvSubTitle.setVisibility(GONE);
            } else {
                tvSubTitle.setVisibility(VISIBLE);
                tvSubTitle.setText(centerSubText);
                tvSubTitle.setTextColor(getStyleColor(centerSubTextColor));
                tvSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, centerSubTextSize); // TypedValue.COMPLEX_UNIT_PX,
            }
        } else if (centerType == TYPE_CENTER_SEARCHVIEW) {
            viewStub.setLayoutResource(R.layout.z_toolbar_center_layout);
            viewStub.inflate();
        } else if (centerType == TYPE_CENTER_CUSTOM_VIEW && centerCustomViewRes != 0) {
            viewStub.setLayoutResource(centerCustomViewRes);
            viewStub.inflate();
        }
    }

    @Override
    protected void inflateRightContainer(ViewStub viewStub) {
        viewStub.setInflatedId(generateViewId());
        if (rightType == TYPE_RIGHT_IMAGEBUTTON) {
            viewStub.setLayoutResource(R.layout.z_toolbar_image_button);
            ImageButton button = (ImageButton) viewStub.inflate();
            button.setImageResource(rightImageResource);
            button.setColorFilter(isLightStyle ? Color.WHITE : Color.BLACK);
        } else if (rightType == TYPE_RIGHT_TEXTVIEW) {
            viewStub.setLayoutResource(R.layout.z_toolbar_text_view);
            TextView textView = (TextView) viewStub.inflate();
            textView.setText(rightText);
            textView.setTextColor(getStyleColor(rightTextColor));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
        } else if (rightType == TYPE_RIGHT_CUSTOM_VIEW && rightCustomViewRes != 0) {
            viewStub.setLayoutResource(rightCustomViewRes);
            viewStub.inflate();
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        isLightStyle = forceStyle ? isLightStyle : ColorUtils.isDarkenColor(titleBarColor);
        resetStyle();
    }

    @Override
    public void setLightStyle(boolean lightStyle) {
        super.setLightStyle(lightStyle);
        resetStyle();
    }

    private void resetStyle() {
        if (inflatedLeft instanceof ImageButton) {
            ((ImageButton) inflatedLeft).setColorFilter(isLightStyle ? Color.WHITE : Color.BLACK);
        } else if (inflatedLeft instanceof TextView){
            ((TextView) inflatedLeft).setTextColor(getStyleColor(leftTextColor));
        }
        if (tvTitle != null) {
            tvTitle.setTextColor(getStyleColor(centerTextColor));
        }
        if (tvSubTitle != null) {
            tvSubTitle.setTextColor(getStyleColor(centerSubTextColor));
        }
        if (inflatedRight instanceof ImageButton) {
            ((ImageButton) inflatedRight).setColorFilter(isLightStyle ? Color.WHITE : Color.BLACK);
        } else if (inflatedRight instanceof TextView){
            ((TextView) inflatedRight).setTextColor(getStyleColor(rightTextColor));
        }

    }


    //--------------------------------------LeftView------------------------------------------------

    public View getLeftView() {
        return inflatedLeft;
    }

    public View getLeftCustomView() {
        if (leftType == TYPE_LEFT_CUSTOM_VIEW) {
            return inflatedLeft;
        }
        return null;
    }

    //-------------------------------------LeftTextView--------------------------------------------

    public TextView getLeftTextView() {
        if (leftType == TYPE_LEFT_TEXTVIEW && inflatedLeft instanceof TextView) {
            return (TextView) inflatedLeft;
        }
        return null;
    }

    public void setLeftText(String text) {
        TextView left = getLeftTextView();
        if (left != null) {
            left.setText(text);
        }
    }

    public void setLeftTextColor(int color) {
        TextView left = getLeftTextView();
        if (left != null) {
            left.setTextColor(color);
        }
    }

    public void setLeftTextSize(float size) {
        TextView left = getLeftTextView();
        if (left != null) {
            left.setTextSize(size);
        }
    }

    public void setLeftTextSize(int unit, float size) {
        TextView left = getLeftTextView();
        if (left != null) {
            left.setTextSize(unit, size);
        }
    }
    public void setLeftTextGravity(int gravity) {
        TextView left = getLeftTextView();
        if (left != null) {
            left.setGravity(gravity);
        }
    }


    //---------------------------------------LeftImageButton---------------------------------------

    public ImageButton getLeftImageButton() {
        if (leftType == TYPE_LEFT_IMAGEBUTTON && inflatedLeft instanceof ImageButton) {
            return (ImageButton) inflatedLeft;
        }
        return null;
    }

    public void setLeftButtonTint(int color) {
        if (inflatedLeft instanceof ImageButton) {
            ((ImageButton) inflatedLeft).setColorFilter(color);
        }
    }
    public void setLeftButtonImage(int resId) {
        if (inflatedLeft instanceof ImageButton) {
            ((ImageButton) inflatedLeft).setImageResource(resId);
        }
    }

    public void setLeftButtonImage(Drawable drawable) {
        if (inflatedLeft instanceof ImageButton) {
            ((ImageButton) inflatedLeft).setImageDrawable(drawable);
        }
    }

    public void setLeftButtonImage(Bitmap bitmap) {
        if (inflatedLeft instanceof ImageButton) {
            ((ImageButton) inflatedLeft).setImageBitmap(bitmap);
        }
    }





    //-----------------------------------------RightView----------------------------------------
    public View getRightView() {
        return inflatedRight;
    }

    public View getRightCustomView() {
        if (rightType == TYPE_RIGHT_CUSTOM_VIEW) {
            return inflatedRight;
        }
        return null;
    }

    //-------------------------------------RightTextView----------------------------------------
    public TextView getRightTextView() {
        if (rightType == TYPE_RIGHT_TEXTVIEW  && inflatedRight instanceof TextView) {
            return (TextView) inflatedRight;
        }
        return null;
    }

    public void setRightText(String text) {
        TextView right = getRightTextView();
        if (right != null) {
            right.setText(text);
        }
    }

    public void setRightTextColor(int color) {
        TextView right = getRightTextView();
        if (right != null) {
            right.setTextColor(color);
        }
    }

    public void setRightTextSize(float size) {
        TextView right = getRightTextView();
        if (right != null) {
            right.setTextSize(size);
        }
    }

    public void setRightTextSize(int unit, float size) {
        TextView right = getRightTextView();
        if (right != null) {
            right.setTextSize(unit, size);
        }
    }
    public void setRightTextGravity(int gravity) {
        TextView right = getRightTextView();
        if (right != null) {
            right.setGravity(gravity);
        }
    }




    //----------------------------------------RightImageButton-------------------------------------

    public ImageButton getRightImageButton() {
        if (rightType == TYPE_RIGHT_IMAGEBUTTON && inflatedRight instanceof ImageButton) {
            return (ImageButton) inflatedRight;
        }
        return null;
    }

    public void setRightButtonTint(int color) {
        if (inflatedRight instanceof ImageButton) {
            ((ImageButton) inflatedRight).setColorFilter(color);
        }
    }

    public void setRightButtonImage(int resId) {
        if (inflatedRight instanceof ImageButton) {
            ((ImageButton) inflatedRight).setImageResource(resId);
        }
    }

    public void setRightButtonImage(Drawable drawable) {
        if (inflatedRight instanceof ImageButton) {
            ((ImageButton) inflatedRight).setImageDrawable(drawable);
        }
    }

    public void setRightButtonImage(Bitmap bitmap) {
        if (inflatedRight instanceof ImageButton) {
            ((ImageButton) inflatedRight).setImageBitmap(bitmap);
        }
    }



    //----------------------------------------CenterView----------------------------------------
    public View getCenterView() {
//        if (centerType == TYPE_CENTER_TEXTVIEW && inflatedMiddle instanceof LinearLayout) {
//            return (LinearLayout) inflatedMiddle;
//        }
//        return null;
        return inflatedMiddle;
    }

    public View getCenterCustomView() {
        if (centerType == TYPE_CENTER_CUSTOM_VIEW) {
            return inflatedMiddle;
        }
        return null;
    }

    //-------------------------------------CenterTextView---------------------------------------

    public TextView getCenterTextView() {
        return tvTitle;
    }

    public TextView getCenterSubTextView() {
        return tvSubTitle;
    }

    public void setCenterText(String text) {
        this.centerText = text;
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }

    public void setCenterTextColor(int color) {
        this.centerTextColor = color;
        if (tvTitle != null) {
            tvTitle.setTextColor(color);
        }
    }

    public void setCenterTextGravity(int gravity) {
        if (tvTitle != null) {
            tvTitle.setGravity(gravity);
        }
    }

    public void setCenterTextSize(float size) {
        if (tvTitle != null) {
            tvTitle.setTextSize(size);
        }
    }

    public void setCenterTextSize(int unit, float size) {
        if (tvTitle != null) {
            tvTitle.setTextSize(unit, size);
        }
    }

    public void setCenterSubText(String text) {
        this.centerText = text;
        if (tvSubTitle != null) {
            tvSubTitle.setText(text);
        }
    }

    public void setCenterSubTextColor(int color) {
        this.centerSubTextColor = color;
        if (tvSubTitle != null) {
            tvSubTitle.setTextColor(color);
        }
    }

    public void setCenterSubTextGravity(int gravity) {
        if (tvSubTitle != null) {
            tvSubTitle.setGravity(gravity);
        }
    }

    public void setCenterSubTextSize(float size) {
        if (tvSubTitle != null) {
            tvSubTitle.setTextSize(size);
        }
    }

    public void setCenterSubTextSize(int unit, float size) {
        if (tvSubTitle != null) {
            tvSubTitle.setTextSize(unit, size);
        }
    }

    public String getCenterText() {
        return centerText;
    }

    public String getCenterSubText() {
        return centerSubText;
    }
}
