package com.gking.simplemusicplayer.base;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;

import org.jetbrains.annotations.NotNull;

public abstract class BaseDialog<T extends Activity> extends Dialog {
    public T activity;
    View view;

    public T getActivity() {
        return activity;
    }

    public View getView() {
        return view;
    }

    public BaseDialog(@NonNull @NotNull T context) {
        super(context, R.style.MyDialog);
        this.activity = context;
        view=loadView();
    }
    protected abstract View loadView();
}
