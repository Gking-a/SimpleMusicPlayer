package com.gkingswq.simplemusicplayer.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import android.os.Looper;
import android.util.Log;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gkingswq.simplemusicplayer.Interface.OnGetNameCompile;


import GTools.Collections.MyCollection1;
import GTools.GJson;
import GTools.GLibrary;
import GTools.GLibraryManager;

import static com.gkingswq.simplemusicplayer.util.GSong.staticmap;
import static com.gkingswq.simplemusicplayer.Value.Files._NAME;
import static com.gkingswq.simplemusicplayer.Value.StringPool.*;
/**
*@author Gking*/
public class JSON {
	public JSON(){
	}
	/**
	*@time 2020.6.23 0:3
	*@version 1
	*@return names*/
	static GLibrary namesLib= GLibraryManager.getLib(_NAME);
    protected void getName(final String id,final String existName, final OnGetNameCompile listener){
		if (existName != null) {
			listener.onCompile(id,existName);
			return;
		}
		try {
		    Thread mGetter=new Thread(new Runnable(){
		        @Override
                public void run() {
		            try{
		                Looper.prepare();
		                URL u=new URL(Detail+id);
		                BufferedReader br=new BufferedReader(new InputStreamReader(u.openStream()));
		                String info=br.readLine();
		                br.close();
		                info=info.substring(info.indexOf("name\":\"")+7,info.indexOf("\","));
		                staticmap.put(id,info);
		                namesLib.add(id,info,GLibrary.TYPE_STRING);
		                if(listener!=null){
		                    listener.onCompile(id,info);
		                }
		            }
		            catch(Exception e){}
		        }
		    });
		    mGetter.start();
		}
		catch(Exception e4){
		}
//		catch (FileNotFoundException e) {}
//		catch(IOException e2){new Fw(e2.toString());}
//		catch(InterruptedException e3){}
	}
	/**
	*@time 2020.6.21 19:22
	*@version 1.1
	*@return id in arraylist*/
	public static ArrayList<String> getSongsfromList(final String id, final boolean addName){
		final ArrayList<String> result = new ArrayList<>();
		Thread t=new Thread(){
			@Override
			public void run() {
				try {
					InputStream is=new URL(PLAYLIST_PREFIX+id).openStream();
					GJson json = new GJson(is);
					GJson.ValueClass root=json.getRoot();
					ArrayList<GJson.ValueClass> songs=root.getOnlySon().getSon("playlist").getSon("trackIds").ac;
					if(addName){
						result.add(root.getOnlySon().getSon("playlist").getString("name"));
					}
					for(GJson.ValueClass value:songs){
						result.add(value.getString("id"));
					}
				}catch (Exception e){
				}
			}
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {}
		finally{
			return result;
		}
    }
	public static ArrayList<String> getSongsfromList(String id){
		return getSongsfromList(id, false);
	}
	/**
	*@time 2020.6.22 19:24
	*@version 1
	*@return String list name*/
	private static String getListName(String allinfo){
		String allinf=allinfo;
		String result;
		result=allinfo.substring(allinf.indexOf("\"status\":")+19);
		result=result.substring(0,result.indexOf("\",\"id"));
		//new Fw(result);
		if(result.indexOf("\"name\":")>=0){
			result=result.substring(result.indexOf("name")+7);
		}
		return result;
	}
	/**
	*@version 1
	*@time 2020.6.12 12:15
	*@return lyric*/
	final static Pattern p=Pattern.compile("\\[(\\d{2}):([\\d\\.]+)]");
    public static MyCollection1 getlyric(final MyCollection1 mc){
        try{
            Thread t=new Thread(){
                public void run(){
                    ArrayList<Integer> time = new ArrayList<>();
                    String line;
                    String l2,l3;
                    try {
                    	if(mc.getId().contains("@")){
                    		mc.flag=MyCollection1.no;
                    		return;}
                        String path=Lyric_a.replace("\\search\\",mc.getId());
                        URL url=new URL(path);
                        InputStream ui=url.openStream();
                        BufferedReader br=new BufferedReader(new InputStreamReader(ui));
                        line=br.readLine();
                        if(line.indexOf("\"lyric\":\"\"")>=0||line.indexOf("[")<0){
                            mc.flag=MyCollection1.no;
                            return;
                        }
                        ArrayList<String> lyric=new ArrayList<String>();
                        l3=line.substring(0);
                        Matcher matcher=p.matcher(l3);
                        while (matcher.find()){
//                        	Log.i("time",matcher.group(0));
                            l3=l3.substring(l3.indexOf(matcher.group(0))+ matcher.group(0).length());
                            int m;
                            Double s;
                            m = Integer.valueOf(matcher.group(1));
                            s = Double.valueOf(matcher.group(2));
                            int mtime= (int) (m*60*1000+s*1000);
                            time.add(mtime);
                            String mlyric;
                            if(l3.contains("\\n")){
                                mlyric=l3.substring(0,l3.indexOf("\\n"));
                            }else {
                                mlyric=l3.substring(0,l3.indexOf("\""));
                            }
                            lyric.add(mlyric);
                        }
                        mc.setLyric(lyric);
                        mc.setTime(time);
                        mc.flag=MyCollection1.finish;
                        System.gc();
                    } catch (Exception e) {
                        Log.e("e",e.toString(),e);
                    }
                }
            };
            t.start();
            while (mc.flag==0){}
            if(mc.flag==MyCollection1.finish){
            	return mc;
			}else if (mc.flag == MyCollection1.no) {
				return null;
			}
        } catch (Exception e) {
			Log.e("e",e.toString(),e);
			return null;
        }
        return mc;
    }
    public static String decode(String unicode){
        String result = "";
        int index=-1;
        if(unicode.indexOf("\\u")<0){
            return unicode;
        }
        while ((index=unicode.indexOf("\\u"))>=0){
            unicode=unicode.substring(index+2);
            char c= (char) Integer.parseInt(unicode.substring(0,4),16);
            result+=c;
        }
        return result;
    }
}
