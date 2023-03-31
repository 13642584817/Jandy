package com.jandy.jwidget.imageView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

public class AlphaImageView extends androidx.appcompat.widget.AppCompatImageView {
    public AlphaImageView(Context context) {
        super(context);
    }

    public AlphaImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.7f);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setAlpha(1.0f);
                break;
        }
        return super.onTouchEvent(event);
    }
}
