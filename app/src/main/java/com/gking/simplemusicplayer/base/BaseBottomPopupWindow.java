package com.gking.simplemusicplayer.base;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class BaseBottomPopupWindow extends PopupWindow {
    public View getView() {
        return view;
    }

    private final View view;
    Activity context;

    public Activity getContext() {
        return context;
    }
    public BaseBottomPopupWindow(Activity context, int layout){
        this.context=context;
        view = View.inflate(context, layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
    }
    public void showAtBottom(View parent) {
        super.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }
}
