package com.zpj.widget.toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.RelativeLayout;

import com.zpj.utils.ColorUtils;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;

public abstract class BaseToolBar extends RelativeLayout implements ViewStub.OnInflateListener {

    protected boolean fillStatusBar;                      // 是否撑起状态栏, true时,标题栏浸入状态栏
    protected Drawable background;
    protected boolean backgroundFillStatusBar;
    protected int titleBarColor;                          // 标题栏背景颜色
    protected int statusBarColor;                         // 状态栏颜色
    protected int statusBarMode;                          // 状态栏模式
    protected int statusBarHeight = 0;                    // 标题栏高度

    protected int titleBarHeight;                         // 标题栏高度
    protected int toolbarPadding;

    protected boolean showBottomLine;                     // 是否显示底部分割线
    protected int bottomLineColor;                        // 分割线颜色
    protected float bottomShadowHeight;                   // 底部阴影高度
    protected int bottomShadowStartColor;                   // 底部阴影起始颜色
    protected int bottomShadowEndColor;                   // 底部阴影终止颜色

    protected int bottomHeight;

    protected RelativeLayout toolbarContainer;
    protected ViewStub vsLeftContainer;
    protected ViewStub vsMiddleContainer;
    protected ViewStub vsRightContainer;

    protected View inflatedLeft;
    protected View inflatedMiddle;
    protected View inflatedRight;

    protected View viewStatusBarFill;                     // 状态栏填充视图
    private View viewBottomLine;                        // 分隔线视图
    private GradientView viewBottomShadow;                      // 底部阴影

    protected boolean isLightStyle;
    protected boolean forceStyle;

    public BaseToolBar(Context context) {
        this(context, null);
    }

    public BaseToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttribute(context, attrs);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        if (fillStatusBar) {
            transparentStatusBar();
        }

        if (background == null) {
            background = new ColorDrawable(titleBarColor);
        }
        setBackground(background, backgroundFillStatusBar);

        // 构建分割线视图
        if (showBottomLine) {
//            setClipChildren(true);
            // 已设置显示标题栏分隔线,5.0以下机型,显示分隔线
            viewBottomLine = new View(context);
            viewBottomLine.setId(generateViewId());
            viewBottomLine.setBackgroundColor(bottomLineColor);
            bottomHeight = Math.max(1, ScreenUtils.dp2pxInt(context, 0.4f));
            LayoutParams bottomLineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bottomHeight);
//            bottomLineParams.addRule(ALIGN_PARENT_BOTTOM);
//            bottomLineParams.bottomMargin = -bottomHeight;
            addView(viewBottomLine, bottomLineParams);

        } else if (bottomShadowHeight > 0) {
//            setClipChildren(true);
//            viewBottomShadow = new View(context);
            viewBottomShadow = new GradientView(context);
            viewBottomShadow.setStartColor(bottomShadowStartColor);
            viewBottomShadow.setEndColor(bottomShadowEndColor);
            viewBottomShadow.setId(generateViewId());
//            viewBottomShadow.setBackgroundResource(R.drawable.comm_titlebar_bottom_shadow);
            bottomHeight = ScreenUtils.dp2pxInt(context, bottomShadowHeight);
            LayoutParams bottomShadowParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bottomHeight);
//            bottomShadowParams.bottomMargin = -bottomHeight;
//            bottomShadowParams.addRule(ALIGN_PARENT_BOTTOM);
            addView(viewBottomShadow, bottomShadowParams);
        }

        inflateLeftContainer(vsLeftContainer);
        inflateRightContainer(vsRightContainer);
        inflateMiddleContainer(vsMiddleContainer);

        if (inflatedLeft == null && inflatedRight == null && inflatedMiddle == null) {
            if (viewStatusBarFill == null) {
                viewStatusBarFill = new View(context);
                viewStatusBarFill.setId(generateViewId());
                viewStatusBarFill.setBackgroundColor(titleBarColor);
                LayoutParams statusBarParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleBarHeight);
                addView(viewStatusBarFill, statusBarParams);
            } else {
                LayoutParams statusBarParams = (LayoutParams) viewStatusBarFill.getLayoutParams();
                statusBarParams.height = statusBarHeight + titleBarHeight;
            }
        }

        if (viewStatusBarFill != null) {
            LayoutParams layoutParams = (LayoutParams) toolbarContainer.getLayoutParams();
            layoutParams.addRule(BELOW, viewStatusBarFill.getId());
        }


        if (viewBottomLine != null) {
            LayoutParams layoutParams = (LayoutParams) viewBottomLine.getLayoutParams();
            layoutParams.addRule(BELOW, R.id.toolbar_container);
        } else if (viewBottomShadow != null) {
            LayoutParams layoutParams = (LayoutParams) viewBottomShadow.getLayoutParams();
            layoutParams.addRule(BELOW, R.id.toolbar_container);
        }

//        int id = NO_ID;
//        if (inflatedLeft != null) {
//            id = getInflatedId(vsLeftContainer, inflatedLeft);
//        } else if (inflatedRight != null) {
//            id = getInflatedId(vsRightContainer, inflatedRight);
//        } else if (inflatedMiddle != null) {
//            id = getInflatedId(vsMiddleContainer, inflatedMiddle);
//        } else if (viewStatusBarFill != null) {
//            id = viewStatusBarFill.getId();
//        }
//        if (id != NO_ID) {
//            if (viewBottomLine != null) {
//                LayoutParams layoutParams = (LayoutParams) viewBottomLine.getLayoutParams();
//                layoutParams.addRule(BELOW, id);
//            } else if (viewBottomShadow != null) {
//                LayoutParams layoutParams = (LayoutParams) viewBottomShadow.getLayoutParams();
//                layoutParams.addRule(BELOW, id);
//            }
//        }
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.z_toolbar, this, true);
		toolbarContainer = findViewById(R.id.toolbar_container);
        vsLeftContainer = findViewById(R.id.vs_left_container);
        vsMiddleContainer = findViewById(R.id.vs_middle_container);
        vsRightContainer = findViewById(R.id.vs_right_container);

        vsLeftContainer.setOnInflateListener(this);
        vsMiddleContainer.setOnInflateListener(this);
        vsRightContainer.setOnInflateListener(this);
    }
    public void initAttribute(final Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BaseToolBar);

        fillStatusBar = array.getBoolean(R.styleable.BaseToolBar_z_toolbar_fillStatusBar, false);
        titleBarColor = array.getColor(R.styleable.BaseToolBar_z_toolbar_titleBarColor, Color.WHITE);
		background = array.getDrawable(R.styleable.BaseToolBar_z_toolbar_background);
		backgroundFillStatusBar = array.getBoolean(R.styleable.BaseToolBar_z_toolbar_background_fill_status_bar, false);

        toolbarPadding = (int) array.getDimension(R.styleable.BaseToolBar_z_toolbar_padding, 0);
        if (array.hasValue(R.styleable.BaseToolBar_z_toolbar_isLightStyle)) {
            isLightStyle = array.getBoolean(R.styleable.BaseToolBar_z_toolbar_isLightStyle, false);
            forceStyle = true;
        } else {
            forceStyle = false;
            isLightStyle = ColorUtils.isDarkenColor(titleBarColor);
        }
//        isLightStyle = !isLightStyle && ColorUtils.isDarkenColor(titleBarColor);
        titleBarHeight = (int) array.getDimension(R.styleable.BaseToolBar_z_toolbar_titleBarHeight, ScreenUtils.dp2pxInt(context, 56));
        statusBarColor = array.getColor(R.styleable.BaseToolBar_z_toolbar_statusBarColor, Color.WHITE);
        statusBarMode = array.getInt(R.styleable.BaseToolBar_z_toolbar_statusBarMode, -1);
        if (statusBarMode == -1) {
            statusBarMode = ColorUtils.isDarkenColor(statusBarColor) ? 1 : 0;
        }

        showBottomLine = array.getBoolean(R.styleable.BaseToolBar_z_toolbar_showBottomLine, false);
        bottomLineColor = array.getColor(R.styleable.BaseToolBar_z_toolbar_bottomLineColor, Color.parseColor("#f8f8f8"));
        bottomShadowHeight = array.getDimension(R.styleable.BaseToolBar_z_toolbar_bottomShadowHeight, 0);
        bottomShadowStartColor = array.getColor(R.styleable.BaseToolBar_z_toolbar_bottomShadowStartColor, Color.GRAY);
        bottomShadowEndColor = array.getColor(R.styleable.BaseToolBar_z_toolbar_bottomShadowEndColor, Color.TRANSPARENT);
        array.recycle();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        setUpImmersionTitleBar();
    }

    @Override
    public void onInflate(ViewStub stub, View inflated) {
        LayoutParams params = (LayoutParams) inflated.getLayoutParams();
        if (viewStatusBarFill != null) {
            params.addRule(BELOW, viewStatusBarFill.getId());
        }
        params.height = titleBarHeight - 2 * toolbarPadding;
//        inflated.setBackgroundColor(titleBarColor);

        if (stub == vsLeftContainer) {
            inflatedLeft = inflated;
            params.addRule(ALIGN_PARENT_START);
        } if (stub == vsRightContainer) {
            inflatedRight = inflated;
            params.addRule(ALIGN_PARENT_END);
        } if (stub == vsMiddleContainer) {
            inflatedMiddle = inflated;
            if (inflatedLeft != null) {
                params.addRule(END_OF, getInflatedId(vsLeftContainer, inflatedLeft));
            } else {
                params.addRule(ALIGN_PARENT_START);
                inflatedMiddle.setPadding(
                        ScreenUtils.dp2pxInt(getContext(), 16),
                        inflatedMiddle.getPaddingTop(),
                        inflatedMiddle.getPaddingEnd(),
                        inflatedMiddle.getPaddingBottom()
                );
            }
            if (inflatedRight != null) {
                params.addRule(START_OF, getInflatedId(vsRightContainer, inflatedRight));
            } else {
                params.addRule(ALIGN_PARENT_END);
                inflatedMiddle.setPadding(
                        inflatedMiddle.getPaddingStart(),
                        inflatedMiddle.getPaddingTop(),
                        ScreenUtils.dp2pxInt(getContext(), 16),
                        inflatedMiddle.getPaddingBottom()
                );
            }
        }
    }

    protected int getInflatedId(ViewStub stub, View inflated) {
        if (stub.getInflatedId() == NO_ID) {
            if (inflated.getId() == NO_ID) {
                inflated.setId(generateViewId());
            }
            return inflated.getId();
        } else {
            return stub.getInflatedId();
        }
    }

    public void transparentStatusBar() {
        boolean transparentStatusBar = StatusBarUtils.supportTransparentStatusBar();
        if (transparentStatusBar) {
            statusBarHeight = StatusBarUtils.getStatusBarHeight(getContext());
            viewStatusBarFill = new View(getContext());
            viewStatusBarFill.setId(generateViewId());
            viewStatusBarFill.setBackgroundColor(statusBarColor);
            LayoutParams statusBarParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            statusBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            addView(viewStatusBarFill, statusBarParams);
        }
    }

    private void setUpImmersionTitleBar() {
        Window window = getWindow();
        if (window == null) return;
        // 设置状态栏背景透明
        StatusBarUtils.transparentStatusBar(window);
        // 设置图标主题
        if (statusBarMode == 0) {
            StatusBarUtils.setDarkMode(window);
        } else {
            StatusBarUtils.setLightMode(window);
        }
    }

    private Window getWindow() {
        Context context = getContext();
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else if (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
            if (context instanceof Activity) {
                activity = (Activity) ((ContextWrapper) context).getBaseContext();
            }
        }
        if (activity != null) {
            return activity.getWindow();
        }
        return null;
    }

//    @Deprecated
//    @Override
//    public void setBackground(Drawable background) {
//        super.setBackground(background);
//    }
//
//    @Deprecated
//    @Override
//    public void setBackgroundColor(int color) {
//        super.setBackgroundColor(color);
//    }
//
//    @Deprecated
//    @Override
//    public void setBackgroundResource(int resid) {
//        super.setBackgroundResource(resid);
//    }
//
//    @Override
//    public void setBackgroundDrawable(Drawable background) {
//        super.setBackgroundDrawable(background);
//    }

    public void setBackground(Drawable background, boolean fillStatusBar) {
        this.background = background;
        if (fillStatusBar) {
            setBackgroundColor(Color.TRANSPARENT, true);
            super.setBackground(background);
        } else {
            toolbarContainer.setBackground(background);
            super.setBackground(new ColorDrawable(titleBarColor));
        }
//        toolbarContainer.setPadding(toolbarPadding, toolbarPadding, toolbarPadding, toolbarPadding);
//        setPadding(toolbarPadding, toolbarPadding, toolbarPadding, toolbarPadding);
        setPadding(toolbarPadding, 0, toolbarPadding, 0);
        toolbarContainer.setPadding(0, toolbarPadding, 0, toolbarPadding);
    }

    @Override
    public void setBackground(Drawable background) {
//        this.background = background;
//        toolbarContainer.setBackground(background);
//        toolbarContainer.setPadding(toolbarPadding, toolbarPadding, toolbarPadding, toolbarPadding);
        setBackground(background, false);
    }

    public void setBackgroundColor(int color, boolean fillStatusBar) {
        if (fillStatusBar && viewStatusBarFill != null) {
            viewStatusBarFill.setBackgroundColor(color);
            statusBarColor = color;
        }
        titleBarColor = color;
//        this.background = new ColorDrawable(titleBarColor);
//        toolbarContainer.setBackground(background);
//        toolbarContainer.setPadding(toolbarPadding, toolbarPadding, toolbarPadding, toolbarPadding);
        toolbarContainer.setBackgroundColor(titleBarColor);
//        setBackground(new ColorDrawable(titleBarColor), fillStatusBar);
    }

    @Override
    public void setBackgroundColor(int color) {
//        if (viewStatusBarFill != null) {
//            viewStatusBarFill.setBackgroundColor(color);
//            statusBarColor = color;
//        }
//        titleBarColor = color;
//        this.background = new ColorDrawable(titleBarColor);
//        toolbarContainer.setBackground(background);
//        toolbarContainer.setPadding(toolbarPadding, toolbarPadding, toolbarPadding, toolbarPadding);
        setBackgroundColor(color, true);
    }

    public void setBackgroundResource(int resource, boolean fillStatusBar) {
//        setBackgroundColor(Color.TRANSPARENT);
        this.background = getResources().getDrawable(resource);
//        if (fillStatusBar) {
//            super.setBackground(background);
//        } else {
//            setBackground(background);
//        }
        setBackground(background, fillStatusBar);
    }

    @Override
    public void setBackgroundResource(int resource) {
//        setBackgroundColor(Color.TRANSPARENT);
//        this.background = getResources().getDrawable(resource);
//        setBackground(background);
        setBackgroundResource(resource, false);
    }

//    @Override
//    public void setBackgroundDrawable(Drawable background) {
//        super.setBackground(background);
//    }

    //    public void setToolBarBackground(Drawable background) {
//        this.background = background;
//        toolbarContainer.setBackground(background);
//        toolbarContainer.setPadding(toolbarPadding, toolbarPadding, toolbarPadding, toolbarPadding);
//    }
//
//    public void setToolBarBackgroundColor(int color) {
//        if (viewStatusBarFill != null) {
//            viewStatusBarFill.setBackgroundColor(color);
//        }
//        titleBarColor = color;
//        setToolBarBackground(new ColorDrawable(titleBarColor));
//    }
//
//
//    public void setToolBarBackgroundResource(int resource) {
//        setToolBarBackgroundColor(Color.TRANSPARENT);
//        setToolBarBackground(getResources().getDrawable(resource));
//    }

    public void setStatusBarColor(int color) {
        if (viewStatusBarFill != null) {
            viewStatusBarFill.setBackgroundColor(color);
        }
    }

    /**
     * 是否填充状态栏
     *
     * @param show
     */
    public void showStatusBar(boolean show) {
        if (viewStatusBarFill != null) {
            viewStatusBarFill.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void toggleStatusBarMode() {
//        Window window = getWindow();
//        if (window == null) return;
//        StatusBarUtils.transparentStatusBar(window);
//        if (statusBarMode == 0) {
//            statusBarMode = 1;
//            StatusBarUtils.setLightMode(window);
//        } else {
//            statusBarMode = 0;
//            StatusBarUtils.setDarkMode(window);
//        }

        if (statusBarMode == 0) {
            lightStatusBar();
        } else {
            darkStatusBar();
        }
    }

    public void darkStatusBar() {
        Window window = getWindow();
        if (window == null) return;
        StatusBarUtils.transparentStatusBar(window);
        statusBarMode = 0;
        StatusBarUtils.setDarkMode(window);
    }

    public void lightStatusBar() {
        Window window = getWindow();
        if (window == null) return;
        StatusBarUtils.transparentStatusBar(window);
        statusBarMode = 1;
        StatusBarUtils.setLightMode(window);
    }

    public boolean isLightStyle() {
        return isLightStyle;
    }

    public void setLightStyle(boolean lightStyle) {
        isLightStyle = lightStyle;
    }

    protected abstract void inflateLeftContainer(ViewStub viewStub);

    protected abstract void inflateMiddleContainer(ViewStub viewStub);

    protected abstract void inflateRightContainer(ViewStub viewStub);

    protected int getStyleColor(int defaultColor) {
        if (ColorUtils.isDarkenColor(defaultColor)) {
            return isLightStyle ? Color.WHITE : defaultColor;
        } else {
            return isLightStyle ? defaultColor : Color.BLACK;
        }
    }

}
