package com.gking.simplemusicplayer.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import cn.gking.gtools.database.serializable.GSerialClass;

public class RecordSongCombinationBean implements Serializable {
    public SongBean now_play;
    public LinkedList<SongBean> songs =new LinkedList<>();
    public LinkedList<SongBean> randomSongs=new LinkedList<>();
}