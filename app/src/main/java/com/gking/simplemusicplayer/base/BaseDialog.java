package com.gking.simplemusicplayer.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.interfaces.Operable;

import org.jetbrains.annotations.NotNull;

public abstract class BaseDialog<T extends Activity> extends Dialog {
    public T activity;
    View view;
    public BaseDialog(Operable<?extends T> operable) {
        this(operable.getContext());
        this.operable = operable;
    }

    public Operable<? extends T> getOperable() {
        return operable;
    }

    Operable<? extends T> operable;
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
    @NotNull
    @NonNull
    protected abstract View loadView();
}
