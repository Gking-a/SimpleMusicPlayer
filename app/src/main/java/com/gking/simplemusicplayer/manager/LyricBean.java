package com.gking.simplemusicplayer.manager;

import com.gking.simplemusicplayer.util.JsonUtil;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricBean {
    public LyricBean(JsonObject data){
        String lrc= JsonUtil.getAsString(data,"lrc","lyric");
        String[] split = lrc.split("\n");
        for (String s:split) {
            Matcher matcher=pattern.matcher(s);
            if(matcher.find()){
                int min=Integer.parseInt(matcher.group(1));
                int sec= Integer.parseInt(matcher.group(2));
                int mil= Integer.parseInt(matcher.group(3));
                String lyric= matcher.group(4);
                long time=min*60*1000+sec*1000+mil;
                this.time.add(time);
                this.lyric.add(lyric);
            }
        }
        lrc=null;
    }
    LinkedList<Long> time=new LinkedList<>();
    LinkedList<String> lyric=new LinkedList<>();

    public LinkedList<String> getLyric() {
        return lyric;
    }

    private Pattern pattern=Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{3})\\](.*)}");
}
