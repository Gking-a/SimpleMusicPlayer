package com.gking.simplemusicplayer.interfaces;

import android.content.Context;

public interface Operable<T extends Context> {
    T getContext();
}
