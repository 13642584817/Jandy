package com.jandy.jwidget.imageView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.jandy.jwidget.imageView.shape.RadiusModel;
import com.jandy.jwidget.imageView.shape.RoundedShader;
import com.jandy.jwidget.imageView.shape.ShaderHelper;


public class RoundedImageView extends ShaderImageView {

    private RoundedShader shader;
    private RadiusModel mRadiusModel; //圆角

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        shader = new RoundedShader();
        return shader;
    }

    public final int getRadius() {
        if (shader != null) {
            return shader.getRadius();
        }
        return 0;
    }

    public final void setRadius(final int radius) {
        if (shader != null) {
            shader.setRadius(radius);
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mRadiusModel != null) {
            mRadiusModel.drawRadius(canvas, getMeasuredWidth(), getMeasuredHeight());
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRadiusModel != null) {
            mRadiusModel.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public void drawRadius(float[] radius) {
        this.mRadiusModel = new RadiusModel(radius);
    }
}
