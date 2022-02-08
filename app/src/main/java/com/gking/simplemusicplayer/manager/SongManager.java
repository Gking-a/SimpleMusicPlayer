package com.gking.simplemusicplayer.manager;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SongManager {
    public static String playlistId=null;
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
    public void set(String playlistId, List<SongBean> list){
            clear();
            for (SongBean s :list) {
                addSong(s);
            }
            SongManager.getInstance().setPointer(SongManager.getInstance().songs);
            SongManager.getInstance().randomSort();
    }
    public LinkedList<SongBean> songs =new LinkedList<>();
    public LinkedList<SongBean> randomSongs=new LinkedList<>();
    public SongBean getLastOrderSong(String id){
        if(id.equals(songs.getFirst().id))return songs.getLast();
        for (int i = 0; i < songs.size(); i++) {
            if(id.equals(songs.get(i).id)) {
                return songs.get(i-1);
            }
        }
        return null;
    }
    public SongBean getNextOrderSong(String id){
        if(id.equals(songs.getLast().id)){
            return songs.getFirst();
        }
        for (int i = 0; i < songs.size(); i++) {
            if(id.equals(songs.get(i).id)) {
                return songs.get(i + 1);
            }
        }
        return null;
    }
    public SongBean getRandomSong0(String id){
        for (SongBean s :
                randomSongs) {
            Log.e("randomSongs",s.name);
        }
        for (SongBean s : randomSongs) {
            if (s.id.equals(id))return s;
        }
        return null;
    }
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
