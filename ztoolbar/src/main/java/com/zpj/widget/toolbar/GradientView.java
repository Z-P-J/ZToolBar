package com.zpj.widget.toolbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class GradientView extends View {

    private Paint paint = new Paint();
    private LinearGradient backGradient;

    private int startColor = Color.GRAY;
    private int endColor = Color.TRANSPARENT;

    public GradientView(Context context) {
        this(context, null);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取View的宽高
        int width = getWidth();
        int height = getHeight();

        if (backGradient == null) {
            backGradient = new LinearGradient(
                    0, 0, 0, height,
                    new int[]{startColor, endColor},
                    null,
                    Shader.TileMode.CLAMP
            );
        }

        paint.setShader(backGradient);
        canvas.drawRect(0, 0, width, height, paint);
    }

    public void setStartColor(int startColor) {
        if (this.startColor != startColor) {
            this.startColor = startColor;
            postInvalidate();
        }
    }

    public void setEndColor(int endColor) {
        if (this.endColor != endColor) {
            this.endColor = endColor;
            postInvalidate();
        }
    }

}
