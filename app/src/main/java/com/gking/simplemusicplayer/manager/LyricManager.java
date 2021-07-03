package com.gking.simplemusicplayer.manager;

import gtools.util.GTimer;

public class LyricManager {
    LyricBean lyricBean;
    Thread runThread;
    OnLyricChangedListener listener;
    public void start(LyricBean lyricBean, OnLyricChangedListener listener){
        this.lyricBean=lyricBean;
        this.listener=listener;
        runThread=new Thread(()->{
            try{
                GTimer gTimer=new GTimer();
                while(!runThread.isInterrupted()){

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        runThread.start();
    }

    public void reset(){
        runThread.interrupt();
        runThread.stop();
        runThread=null;
        listener=null;
        lyricBean=null;
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
    public static LyricManager getInstance() {
        return Instance;
    }
}
