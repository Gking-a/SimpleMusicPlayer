package com.gking.simplemusicplayer.manager;

import java.util.LinkedList;
import java.util.Random;

public class SongManager {
    public static final SongManager songManager=new SongManager();
    public static final SongManager getInstance(){
        return songManager;
    }
    public void addSong(SongBean musicBean){
        songs.add(musicBean);
        randomSongs.add(musicBean);
    }
    public void clear(){
        songs=null;
        randomSongs=null;
        System.gc();
        songs =new LinkedList<>();
        randomSongs=new LinkedList<>();
    }
    public LinkedList<SongBean> songs =new LinkedList<>();
    public LinkedList<SongBean> randomSongs=new LinkedList<>();
    public void randomSort(){
        Random random=new Random();
        for (int i = 0; i < randomSongs.size(); i++) {
            int p=Math.abs(random.nextInt())% randomSongs.size();
            SongBean temp= randomSongs.get(i);
            randomSongs.set(i, randomSongs.get(p));
            randomSongs.set(p,temp);
        }
        setPointer(randomSongs);
    }
    public void setPointer(LinkedList<SongBean> list){
        for (int i = 0; i < list.size(); i++) {
            SongBean bean=list.get(i);
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