package com.gking.simplemusicplayer.base;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;

import org.jetbrains.annotations.NotNull;

public abstract class BaseDialog extends Dialog {
    public Activity activity;
    View view;

    public Activity getActivity() {
        return activity;
    }

    public View getView() {
        return view;
    }

    public BaseDialog(@NonNull @NotNull Activity context) {
        super(context, R.style.MyDialog);
        this.activity = context;
        view=loadView();
    }
    protected abstract View loadView();
}
