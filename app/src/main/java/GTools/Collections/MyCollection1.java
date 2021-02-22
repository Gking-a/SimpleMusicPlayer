package GTools.Collections;

import java.util.ArrayList;

public class MyCollection1{
    public static final int wait=0;
    public static final int finish=1;
    public static final int no=-1;

    public String getId() {
        return id;
    }

    private String id;

    public MyCollection1(String id) {
        this.id = id;
    }

    public int flag=0;

    public ArrayList<Integer> getTime() {
        return time;
    }

    public void setTime(ArrayList<Integer> time) {
        this.time = time;
    }

    public ArrayList<String> getLyric() {
        return lyric;
    }

    public void setLyric(ArrayList<String> lyric) {
        this.lyric = lyric;
    }

    private ArrayList<Integer> time;
    private ArrayList<String> lyric;
}

