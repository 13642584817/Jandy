package com.jandy.jwidget.textview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 滚动TextView
 */
public class MarqueeTextView extends AppCompatTextView {
    public MarqueeTextView(Context context) {
        super(context);
        initView(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.setMarqueeRepeatLimit(-1);
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setSingleLine();
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
