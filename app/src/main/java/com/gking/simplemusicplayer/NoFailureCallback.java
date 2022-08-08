package com.gking.simplemusicplayer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;

public abstract class NoFailureCallback implements Callback {
    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        System.err.println(call);
        e.printStackTrace();
    }
}
