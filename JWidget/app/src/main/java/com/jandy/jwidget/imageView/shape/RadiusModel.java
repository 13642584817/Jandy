package com.jandy.jwidget.imageView.shape;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

public class RadiusModel {

    /**
     * 圆角属性
     */
    private RectF mRadiusRectF;
    private float[] mRadius;

    public RadiusModel(float[] mRadius) {
        this.mRadius = mRadius;
    }

    /**
     * 切圆弧
     *
     * @param canvas 画布
     */
    public void drawRadius(Canvas canvas, int w, int h) {
        if (mRadius != null) {
            if (mRadiusRectF == null) {
                mRadiusRectF = new RectF();
            }
            mRadiusRectF.set(0, 0, w, h);
            Path path = new Path();
            path.addRoundRect(mRadiusRectF, mRadius, Path.Direction.CW);
            canvas.clipPath(path);
        }
    }

    /**
     * 尺寸改变重新设宽高
     *
     * @param w 宽
     * @param h 高
     */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mRadiusRectF != null) {
            mRadiusRectF.set(0, 0, w, h);
        }
    }
}
