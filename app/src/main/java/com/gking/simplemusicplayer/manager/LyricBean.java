package com.gking.simplemusicplayer.manager;

import com.gking.simplemusicplayer.util.JsonUtil;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricBean implements Serializable {
    public boolean isNolyric() {
        return nolyric;
    }
    public boolean nolyric=false;
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
                String slyric= matcher.group(4);
                this.time.add(getTime(matcher));
                this.lyric.add(slyric);
            }
        }
        if(lyric.size()==0){
            lyric.addAll(Arrays.asList(split));
        }
        try{
            String tlrc= JsonUtil.getAsString(data,"tlyric","lyric");
            if(tlrc.trim().equals(""))return;
            String[] tsplit = tlrc.split("\n");
            for (String s:tsplit) {
                Matcher matcher=pattern.matcher(s);
                if(matcher.find()){
                    String slyric= matcher.group(4);
                    int i = time.indexOf(getTime(matcher));
                    this.lyric.set(i-1,lyric.get(i-1)+'\n'+slyric);
                }
            }
        }catch (Exception e){}
    }
    public LinkedList<Integer> time=new LinkedList<>();
    public LinkedList<String> lyric=new LinkedList<>();

    public LinkedList<String> getLyric() {
        return lyric;
    }

    private static final Pattern pattern=Pattern.compile("\\[(\\d{2}):(\\d{2})[\\.:](.*?)\\](.*)");
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
