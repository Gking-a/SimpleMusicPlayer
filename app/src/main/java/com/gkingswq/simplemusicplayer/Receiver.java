/*
 */

package com.gkingswq.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import gtools.SingleThreadTask;
import org.json.JSONObject;
import com.gkingswq.simplemusicplayer.util.CopyWeb;
import org.json.JSONException;
import com.gkingswq.simplemusicplayer.util.FW;

public class Receiver extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent ti=getIntent();
        String action=ti.getAction();
        String type=ti.getType();
        String path = "";
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            path = ti.getStringExtra(Intent.EXTRA_TEXT);
        }
        
        //http://music.163.com/playlist/6606033030/1607939711/?userid=1607939711 
        Pattern idPattern1=Pattern.compile("/playlist/(.*?)/");
        Matcher matcher=idPattern1.matcher(path);
        if(matcher.find()){
            final String playlistId=matcher.group();
            Thread t=new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonRoot=new JSONObject(
                                CopyWeb.doGet("https://api.imjad.cn/cloudmusic/?type=playlist&id=" + playlistId));
                            FW.w(jsonRoot.toString());
                        } catch (JSONException e) {}}
                });
            t.start();
            
        }
        
    }
    
}
