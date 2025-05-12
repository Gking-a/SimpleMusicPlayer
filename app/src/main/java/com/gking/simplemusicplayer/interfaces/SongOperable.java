package com.gking.simplemusicplayer.interfaces;

import android.content.Context;

import com.gking.simplemusicplayer.beans.SongBean;

public interface SongOperable<T extends Context> extends Operable<T>{
    void onSongDelete(String pid,SongBean songBean);
}