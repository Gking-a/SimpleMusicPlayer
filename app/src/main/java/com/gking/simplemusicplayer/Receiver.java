/*
 */

package com.gking.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import com.gking.simplemusicplayer.util.CopyWeb;
import org.json.JSONException;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.UnCrypt;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Receiver extends Activity {
    public static final String TAG="Receiver";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent ti=getIntent();
        String action=ti.getAction();
        String type=ti.getType();
        String uid = "";
//        if (Intent.ACTION_SEND.equals(action) && type != null) {
//            uid = ti.getStringExtra(Intent.EXTRA_TEXT);
//        }
        uid = ti.getStringExtra(Intent.EXTRA_TEXT);
        //http://music.163.com/playlist/6606033030/1607939711/?userid=1607939711 
        Pattern idPattern1=Pattern.compile("userid=(.*?)");
        Matcher matcher=idPattern1.matcher(uid);
        if(matcher.find()){
            final String Uid=matcher.group(1);
            OkHttpClient okHttpClient=new OkHttpClient();
            Request request=new Request.Builder()
                    .url(UnCrypt.PlayListURL+uid)
                    .get()
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.i(TAG,response.body().string());
                    try {
                        JSONObject json=new JSONObject(response.body().string());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e(TAG,e.toString());
                    finish();
                }
            });

        }
        finish();
    }
    
}
