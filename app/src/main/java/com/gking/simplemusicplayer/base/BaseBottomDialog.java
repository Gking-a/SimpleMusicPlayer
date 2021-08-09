package com.gking.simplemusicplayer.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.interfaces.Operable;

import org.jetbrains.annotations.NotNull;

public abstract class BaseBottomDialog<T extends Activity> extends BaseDialog<T>{

    public BaseBottomDialog(@NonNull @NotNull T context) {
        super(context);
    }

    public BaseBottomDialog(Operable<? extends T> operable) {
        super(operable);
    }

    @Override
    @NonNull
    @NotNull
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setGravity(Gravity.BOTTOM);
        setContentView(getView());
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth();// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失
    }
}
