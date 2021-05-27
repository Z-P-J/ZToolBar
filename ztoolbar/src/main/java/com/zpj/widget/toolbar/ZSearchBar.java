package com.zpj.widget.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.utils.ScreenUtils;

public class ZSearchBar extends BaseToolBar {

    protected static final int TYPE_SEARCH_RIGHT_DELETE = 0;
    protected static final int TYPE_SEARCH_RIGHT_VOICE = 1;

    protected int leftImageResource;
    protected int rightImageResource;

    private String defaultText;
    private int textColor;
    private int textSize;
    private int textGravity;
    private String hintText;
    private int hintTextColor;
    protected boolean showLeftImageButton = true;
    protected boolean showRightImageButton = true;
    protected boolean editable;                // 搜索框是否可输入
    protected int searchBgResource = 0;                 // 搜索框背景图片
    protected int rightType;                  // 搜索框右边按钮类型  0: voice 1: delete

    private AutoCompleteTextView editor;
    private ImageButton ivClear;
    private ImageButton ibSearch;

    private OnSearchListener listener;

    private final OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (rightType == TYPE_SEARCH_RIGHT_DELETE) {
                String input = editor.getText().toString();
                if (hasFocus && !TextUtils.isEmpty(input)) {
                    ivClear.setVisibility(View.VISIBLE);
                } else {
                    ivClear.setVisibility(View.GONE);
                }
            }
        }
    };


    public ZSearchBar(Context context) {
        super(context);
    }

    public ZSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZSearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initAttribute(Context context, AttributeSet attrs) {
        super.initAttribute(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZSearchBar);
        defaultText = array.getString(R.styleable.ZSearchBar_z_toolbar_content_text);
        textSize = array.getDimensionPixelSize(R.styleable.ZSearchBar_z_toolbar_content_text_size, ScreenUtils.dp2pxInt(context, 16));
        textColor = array.getColor(R.styleable.ZSearchBar_z_toolbar_content_text_color, Color.TRANSPARENT);
        textGravity = array.getInt(R.styleable.ZSearchBar_z_toolbar_content_text_gravity, Gravity.CENTER_VERTICAL);
        hintText = array.getString(R.styleable.ZSearchBar_z_toolbar_hint_text);
        if (TextUtils.isEmpty(hintText)) {
            hintText = context.getResources().getString(R.string.toolbar_search_hint);
        }
        hintTextColor = array.getColor(R.styleable.ZSearchBar_z_toolbar_hint_text_color, Color.TRANSPARENT); // Color.TRANSPARENT
        showLeftImageButton = array.getBoolean(R.styleable.ZSearchBar_z_toolbar_showLeftImageButton, true);
        showRightImageButton = array.getBoolean(R.styleable.ZSearchBar_z_toolbar_showRightImageButton, true);
        editable = array.getBoolean(R.styleable.ZSearchBar_z_toolbar_centerSearchEditable, true);
        searchBgResource = array.getResourceId(R.styleable.ZSearchBar_z_toolbar_centerSearchBg, 0);
        rightType = array.getInt(R.styleable.ZSearchBar_z_toolbar_centerSearchRightType, TYPE_SEARCH_RIGHT_DELETE);
        array.recycle();
        array = context.obtainStyledAttributes(attrs, R.styleable.ZToolBar);

        if (showLeftImageButton) {
            leftImageResource = array.getResourceId(R.styleable.ZToolBar_z_toolbar_leftImageResource, R.drawable.ic_arrow_back_black_24dp);
        }

        if (showRightImageButton) {
            rightImageResource = array.getResourceId(R.styleable.ZToolBar_z_toolbar_rightImageResource, R.drawable.ic_search_black_24dp);
        }
        array.recycle();
    }

    @Override
    protected void inflateLeftContainer(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.z_toolbar_image_button);
        viewStub.setInflatedId(generateViewId());
        ImageButton button = (ImageButton) viewStub.inflate();
        if (leftImageResource > 0) {
            button.setImageResource(leftImageResource);
            button.setColorFilter(isLightStyle ? Color.WHITE : Color.BLACK);
        } else {
            button.setVisibility(GONE);
        }
    }

    @Override
    protected void inflateMiddleContainer(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.z_search_bar_center_edittext);
        viewStub.setInflatedId(generateViewId());
        editor = (AutoCompleteTextView)viewStub.inflate();
        if (searchBgResource != 0) {
            editor.setBackgroundResource(searchBgResource);
        }
        editor.setHint(hintText);
        editor.setGravity(textGravity);
        editor.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        if (textColor == Color.TRANSPARENT) {
            editor.setTextColor(isLightStyle ? Color.WHITE : Color.BLACK);
        } else {
            editor.setTextColor(textColor);
        }
        if (hintTextColor == Color.TRANSPARENT) {
            editor.setHintTextColor(Color.parseColor(isLightStyle ? "#fafafa" : "#cccccc") );
        } else {
            editor.setHintTextColor(hintTextColor);
        }
        initEditor();
        if (rightType == TYPE_SEARCH_RIGHT_DELETE) {
            ivClear.setImageResource(R.drawable.ic_clear_black_24dp);
            ivClear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.setText(null);
                }
            });
        } else if (rightType == TYPE_SEARCH_RIGHT_VOICE) {
            ivClear.setImageResource(R.drawable.ic_keyboard_voice_black_24dp);
        }
//        ivClear.setTint(isLightStyle ? Color.LTGRAY : Color.GRAY);
        ivClear.setColorFilter(isLightStyle ? Color.WHITE : Color.BLACK);
    }

    @Override
    protected void inflateRightContainer(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.z_search_bar_right_layout);
        viewStub.setInflatedId(generateViewId());
        viewStub.inflate();
        ivClear = findViewById(R.id.iv_clear);
        ibSearch = findViewById(R.id.ib_search);
        if (rightImageResource > 0) {
            ibSearch.setImageResource(rightImageResource);
            ibSearch.setColorFilter(isLightStyle ? Color.WHITE : Color.BLACK);
        } else {
            ibSearch.setVisibility(GONE);
        }
        ibSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (TextUtils.isEmpty(editor.getText().toString())) {
                        Toast.makeText(getContext(), "不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listener.onSearch(editor.getText().toString());
                }
            }
        });
    }

    private void initEditor() {
        if (editable) {
            editor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (rightType == TYPE_SEARCH_RIGHT_VOICE) {
                        if (TextUtils.isEmpty(s)) {
                            ivClear.setImageResource(R.drawable.ic_keyboard_voice_black_24dp);
                        } else {
                            ivClear.setImageResource(R.drawable.ic_clear_black_24dp);
                        }
                    } else {
                        if (TextUtils.isEmpty(s)) {
                            ivClear.setVisibility(View.GONE);
                        } else {
                            ivClear.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            editor.setOnFocusChangeListener(focusChangeListener);
            editor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (listener != null && actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (TextUtils.isEmpty(v.getText().toString())) {
                            Toast.makeText(getContext(), "不能为空！", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        listener.onSearch(v.getText().toString());
                    }
                    return false;
                }
            });
//            editor.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    editor.setCursorVisible(true);
//                }
//            });
            editor.setText(defaultText);
        } else {
            ivClear.setVisibility(GONE);
            editor.setCursorVisible(false);
            editor.clearFocus();
            editor.setFocusable(false);
            editor.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                     ZSearchBar.this.performClick();
                }
            });
        }
    }

    public void setText(CharSequence text) {
        if (editor != null) {
            editor.setText(text);
        }
    }

    public void setText(int resId) {
        if (editor != null) {
            editor.setText(resId);
        }
    }

    public void setTextSize(float size) {
        if (editor != null) {
            editor.setTextSize(size);
        }
    }

    public void setTextSize(int unit, float size) {
        if (editor != null) {
            editor.setTextSize(unit, size);
        }
    }

    public void setHintText(CharSequence text) {
        if (editor != null) {
            editor.setHint(text);
            this.hintText = editor.getHint().toString();
        }
    }

    public void setHintText(int resId) {
        if (editor != null) {
            editor.setHint(resId);
            this.hintText = editor.getHint().toString();
        }
    }

    public void setHintTextColor(int color) {
        if (editor != null) {
            this.hintTextColor = color;
            editor.setHintTextColor(color);
        }
    }

    public void selectAll() {
        if (editor != null) {
            editor.selectAll();
        }
    }

    public void setOnSearchListener(OnSearchListener listener) {
        this.listener = listener;
    }

    public void setOnLeftButtonClickListener(OnClickListener listener) {
        inflatedLeft.setOnClickListener(listener);
    }

    public void addTextWatcher(TextWatcher textWatcher) {
        if (editor != null) {
            editor.addTextChangedListener(textWatcher);
        }
    }

    public AutoCompleteTextView getEditor() {
        return editor;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        initEditor();
    }

    public ImageButton getClearButton() {
        return ivClear;
    }

    public void setClearButtonTint(int color) {
        ivClear.setColorFilter(color);
    }

    public void setClearButtonImage(int resId) {
        ivClear.setImageResource(resId);
    }

    public void setClearButtonImage(Drawable drawable) {
        ivClear.setImageDrawable(drawable);
    }

    public void setClearButtonImage(Bitmap bitmap) {
        ivClear.setImageBitmap(bitmap);
    }

    public ImageButton getLeftImageButton() {
        return (ImageButton) inflatedLeft;
    }

    public void setLeftButtonTint(int color) {
        ((ImageButton) inflatedLeft).setColorFilter(color);
    }
    public void setLeftButtonImage(int resId) {
        ((ImageButton) inflatedLeft).setImageResource(resId);
    }

    public void setLeftButtonImage(Drawable drawable) {
        ((ImageView) inflatedLeft).setImageDrawable(drawable);
    }

    public void setLeftButtonImage(Bitmap bitmap) {
        ((ImageView) inflatedLeft).setImageBitmap(bitmap);
    }

    public ImageButton getRightImageButton() {
        return ibSearch;
    }

    public void setRightButtonTint(int color) {
        ibSearch.setColorFilter(color);
    }

    public void setRightButtonImage(int resId) {
        ibSearch.setImageResource(resId);
    }

    public void setRightButtonImage(Drawable drawable) {
        ibSearch.setImageDrawable(drawable);
    }

    public void setRightButtonImage(Bitmap bitmap) {
        ibSearch.setImageBitmap(bitmap);
    }

    public interface OnSearchListener {
        void onSearch(String keyword);
    }

}
