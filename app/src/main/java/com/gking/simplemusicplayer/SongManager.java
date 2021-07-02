package com.gking.simplemusicplayer;

import android.graphics.Bitmap;

import com.gking.simplemusicplayer.impl.MusicPlayer;

import java.util.LinkedList;
import java.util.Random;

public class SongManager {
    public static final SongManager songManager=new SongManager();
    public static final SongManager getInstance(){
        return songManager;
    }
    public void addSong(MusicPlayer.MusicBean musicBean){
        songs.add(musicBean);
        randomSongs.add(musicBean);
    }
    public LinkedList<MusicPlayer.MusicBean> songs =new LinkedList<>();
    public LinkedList<MusicPlayer.MusicBean> randomSongs=new LinkedList<>();
    public void randomSort(){
        Random random=new Random();
        for (int i = 0; i < randomSongs.size(); i++) {
            int p=Math.abs(random.nextInt())% randomSongs.size();
            MusicPlayer.MusicBean temp= randomSongs.get(i);
            randomSongs.set(i, randomSongs.get(p));
            randomSongs.set(p,temp);
        }
        setPointer(randomSongs);
    }
    public void setPointer(LinkedList<MusicPlayer.MusicBean> list){
        for (int i = 0; i < list.size(); i++) {
            MusicPlayer.MusicBean bean=list.get(i);
            if(i==0){
                bean.last=list.getLast();
                bean.next=list.get((i+1)%list.size());
                continue;
            }
            bean.last=list.get(i-1);
            bean.next=list.get((i+1)%list.size());
        }
    }

}
