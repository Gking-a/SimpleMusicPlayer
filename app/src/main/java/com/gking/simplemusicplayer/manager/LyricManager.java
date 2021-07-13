package com.gking.simplemusicplayer.manager;

import java.util.LinkedList;

public class LyricManager {
    LyricBean lyricBean;
    //min -1，表示还没到第一句歌词;max=lyric.size 表示最后一段歌词已经放完
    public int getPosition(int msec){
        if(lyricBean==null)return -1;
        if(lyricBean.nolyric)return -1;
        LinkedList<Integer> time = lyricBean.time;
        for (int i = 0; i < time.size()-1; i++) {
            if(i==time.size()-1)break;
            int last=time.get(i);
            int next=time.get(i+1);
            if(last<=msec&&msec<=next)return i-1;
        }
        return time.size()-1;
    }
    public String getLyric(int msec){
        if(lyricBean==null)return null;
        if(lyricBean.nolyric)return null;
        int p=getPosition(msec);
        if(p<0)return null;
        if (p>=lyricBean.lyric.size())return null;
        return lyricBean.lyric.get(p);
    }

    interface OnLyricChangedListener {
        public void onLyricChanged(long millsec,String lyric);
    }
    public LyricBean getLyricBean() {
        return lyricBean;
    }

    public void setLyricBean(LyricBean lyricBean) {
        this.lyricBean = lyricBean;
    }

    public static LyricManager Instance=new LyricManager();
    private LyricManager(){}
    public static LyricManager getInstance(LyricBean lyricBean) {
        Instance.lyricBean=lyricBean;
        return Instance;
    }
}
