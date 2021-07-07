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
        String lrc= JsonUtil.getAsString(data,"lrc","lyric");
        String[] split = lrc.split("\n");
        time.add(0);
        for (String s:split) {
            Matcher matcher=pattern.matcher(s);
            if(matcher.find()){
                int time=0;
                time+=Integer.parseInt(matcher.group(1))*60*1000;
                time+= ((int) (Double.parseDouble(matcher.group(2)) * 1000));
                String lyric= matcher.group(3);
                this.time.add(time);
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

    private Pattern pattern=Pattern.compile("\\[(\\d{2}):(.*?)\\](.*)");
}
