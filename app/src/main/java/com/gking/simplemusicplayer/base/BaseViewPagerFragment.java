package com.gking.simplemusicplayer.base;

import android.view.View;

public abstract class BaseViewPagerFragment<T extends BaseActivity>{
    T activity;

    public View getView() {
        return view;
    }

    public T getContext() {
        return activity;
    }

    View view;
    public BaseViewPagerFragment(T activity) {
        this.activity = activity;
        view=loadView();
    }
    private BaseViewPagerFragment(){}
    protected abstract View loadView();
}
