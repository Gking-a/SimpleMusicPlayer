package com.gking.simplemusicplayer.view;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptTouchLinearLayout extends LinearLayout {
    public InterceptTouchLinearLayout(Context context) {
        super(context);
    }
    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return true;
    }
}
