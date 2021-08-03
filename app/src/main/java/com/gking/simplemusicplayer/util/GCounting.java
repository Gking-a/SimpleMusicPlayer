/*
 *@time 2020.6.11 13:11
 */

package com.gking.simplemusicplayer.util;

import cn.gking.gtools.managers.GHolder;

public class GCounting {
    private static GHolder<String,Integer> h=new GHolder<>();
    public static void set(String name,Integer value){
        h.add(name,value);
    }
    public static void remove(String name){
        h.remove(name);
    }
    public static void changeBoolean(String name){
        int v=h.get(name);
        if(v==0)h.add(name,1);else h.add(name,0);
    }
    public static boolean getBoolean(String name){
        int v=h.get(name);
        if(v==0)return true;else return false;
    }
}
