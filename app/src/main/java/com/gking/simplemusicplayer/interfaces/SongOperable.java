package com.gking.simplemusicplayer.interfaces;

import android.app.Activity;
import android.content.Context;

import com.gking.simplemusicplayer.manager.SongBean;

public interface SongOperable<T extends Context> extends Operable<T>{
    void onSongDelete(String pid,SongBean songBean);
}