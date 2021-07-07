package com.gking.simplemusicplayer.manager;

import com.gking.simplemusicplayer.util.JsonUtil;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricBean {
    public boolean isNolyric() {
        return nolyric;
    }

    boolean nolyric=false;
    public LyricBean(){
        nolyric=true;
    }
    public LyricBean(JsonObject data){
        System.out.println(data);
        String lrc= JsonUtil.getAsString(data,"lrc","lyric");
        String[] split = lrc.split("\n");
        time.add(0);
        for (String s:split) {
            Matcher matcher=pattern.matcher(s);
            if(matcher.find()){
                String lyric= matcher.group(4);
                this.time.add(getTime(matcher));
                this.lyric.add(lyric);
                System.out.println(lyric);
            }
        }
        lrc=null;
    }
    LinkedList<Integer> time=new LinkedList<>();
    LinkedList<String> lyric=new LinkedList<>();

    public LinkedList<String> getLyric() {
        return lyric;
    }

    private Pattern pattern=Pattern.compile("\\[(\\d{2}):(\\d{2})[\\.:](.*?)\\](.*)");
    private int getTime(Matcher matcher){
        int time=0;
        time+=Integer.parseInt(matcher.group(1))*60*1000;
        time+= ((int) (Double.parseDouble(matcher.group(2)) * 1000));
        String temp= matcher.group(3);
        if(temp.length()==1)time+=Integer.parseInt(temp,10)*100;
        if(temp.length()==2)time+=Integer.parseInt(temp,10)*10;
        if(temp.length()==3)time+=Integer.parseInt(temp,10);
        return time;
    }
}
