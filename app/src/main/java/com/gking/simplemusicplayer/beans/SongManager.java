package com.gking.simplemusicplayer.beans;

import android.os.Build;
import android.util.Log;

import com.gking.simplemusicplayer.activity.SettingsActivity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SongManager {
    public static String playlistId=null;
    public static final SongManager songManager=new SongManager();
    public static final SongManager getInstance(){
        return songManager;
    }

    public LinkedList<SongBean> songs =new LinkedList<>();
    public LinkedList<SongBean> randomSongs=new LinkedList<>();
    public Set<String> unreachableSong=new HashSet<>();
    public Set<String> reachableSong=new HashSet<>();
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
            SettingsActivity.recordBean.songs=songs;
            SettingsActivity.recordBean.randomSongs=randomSongs;
    }
    public SongBean nextValidSong(String playmode,SongBean musicBean){
        if(!variedList())return null;
        if (playmode.equals(SettingsActivity.Params.PLAY_MODE.LOOP)){return varifiedBean(musicBean);}
        else if (playmode.equals(SettingsActivity.Params.PLAY_MODE.RANDOM)){return varifiedBean(SongManager.getInstance().getRandomSong0(musicBean.id).next);}
        else if (playmode.equals(SettingsActivity.Params.PLAY_MODE.ORDER)){return varifiedBean(SongManager.getInstance().getNextOrderSong(musicBean.id));}
        else if (playmode.equals(SettingsActivity.Params.PLAY_MODE.NONE))return null;
        return null;
    }
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
        if(list==null)return;
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
    public void markReachable(String id,boolean reachable){
        if(reachable)reachableSong.add(id);
        else unreachableSong.add(id);
    }

    /**
     * Warning! this method doesnot send request,It just check local results
     * use with WebRequest check_music
     * @param id
     * @return
     */
    public boolean varifyReachable(String id){
        if(id==null||unreachableSong.contains(id))return false;
        else return true;
    }

    /**
     *
     * @param bean
     * @return null if unreachable
     */
    public SongBean varifiedBean(SongBean bean){
        if(bean==null)return null;
        if(varifyReachable(bean.id))return bean;
        else return null;
    }
    public boolean variedList(){
        if(songs==null||randomSongs==null)return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for (SongBean song : songs) {
                if(!unreachableSong.contains(song))return true;
            }
        }
        throw new RuntimeException("Not support Stream");
    }
}
