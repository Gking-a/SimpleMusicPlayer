package com.gking.simplemusicplayer;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class NoFailureCallback implements Callback {
    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        System.err.println(call);
        e.printStackTrace();
    }
}
