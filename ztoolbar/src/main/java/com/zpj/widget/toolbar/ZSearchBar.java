package com.zpj.widget.toolbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.widget.tinted.TintedImageButton;
import com.zpj.widget.tinted.TintedImageView;

public class ZSearchBar extends BaseToolBar {

    protected static final int TYPE_SEARCH_RIGHT_DELETE = 0;
    protected static final int TYPE_SEARCH_RIGHT_VOICE = 1;

    protected int leftImageResource;
    protected int rightImageResource;

    private String defaultText;
    private String hintText;
    protected boolean showLeftImageButton = true;
    protected boolean showRightImageButton = true;
    protected boolean editable;                // 搜索框是否可输入
    protected int searchBgResource = 0;                 // 搜索框背景图片
    protected int rightType;                  // 搜索框右边按钮类型  0: voice 1: delete

    private AutoCompleteTextView editor;
    private TintedImageView ivClear;
    private TintedImageButton ibSearch;

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
        hintText = array.getString(R.styleable.ZSearchBar_z_toolbar_hint_text);
        if (TextUtils.isEmpty(hintText)) {
            hintText = context.getResources().getString(R.string.toolbar_search_hint);
        }
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
        TintedImageButton button = (TintedImageButton) viewStub.inflate();
        if (leftImageResource > 0) {
            button.setImageResource(leftImageResource);
            button.setTint(ColorStateList.valueOf(isLightStyle ? Color.WHITE : Color.BLACK));
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
        if (isLightStyle) {
            editor.setTextColor(Color.WHITE);
            editor.setHintTextColor(Color.parseColor("#fafafa"));
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
        ivClear.setTint(ColorStateList.valueOf(isLightStyle ? Color.LTGRAY : Color.GRAY));
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
            ibSearch.setTint(ColorStateList.valueOf(isLightStyle ? Color.WHITE : Color.BLACK));
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
                    // ZSearchbar.this.performClick();
                }
            });
        }
    }

    public void setText(CharSequence text) {
        if (editor != null) {
            editor.setText(text);
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

    public ImageButton getLeftImageButton() {
        return (ImageButton) inflatedLeft;
    }

    public AutoCompleteTextView getEditor() {
        return editor;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        initEditor();
    }

    public interface OnSearchListener {
        void onSearch(String keyword);
    }

}
