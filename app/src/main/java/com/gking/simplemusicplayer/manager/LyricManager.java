package com.gking.simplemusicplayer.manager;

import java.util.LinkedList;

public class LyricManager {
    LyricBean lyricBean;
    public int getPosition(int msec){
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
    interface OnLyricChangedListener {
        public void onLyricChanged(long millsec,String lyric);
    }
    public LyricBean getLyricBean() {
        return lyricBean;
    }

    public void setLyricBean(LyricBean lyricBean) {
        this.lyricBean = lyricBean;
    }

    public static final LyricManager Instance=new LyricManager();
    private LyricManager(){}
    public static LyricManager getInstance(LyricBean lyricBean) {
        Instance.lyricBean=lyricBean;
        return Instance;
    }
}
