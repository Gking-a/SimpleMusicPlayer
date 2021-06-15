/*
 */

package com.gking.simplemusicplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import com.gking.simplemusicplayer.util.CopyWeb;
import org.json.JSONException;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.UnCrypt;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Receiver extends Activity {
    public static final String TAG="Receiver";
    public static final File CoverImg=new File("/data/user/0/com.gkingswq.simplemusicplayer/files/CoverImg/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CoverImg.exists())CoverImg.mkdirs();
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
            final String Uid=uid=matcher.group(1);
            OkHttpClient okHttpClient=new OkHttpClient();
            Request request=new Request.Builder()
                    .url(UnCrypt.PlayListURL+uid)
                    .get()
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.i(TAG,response.body().string());
                    JsonParser jsonParser=new JsonParser();
                    JsonArray array=jsonParser.parse(response.body().string()).getAsJsonObject().get("playlist").getAsJsonArray();
                    Iterator<JsonElement> iterator=array.iterator();
                    while (iterator.hasNext()){
                        JsonObject jsonObject=iterator.next().getAsJsonObject();
                        String coverImgUrl=jsonObject.get("coverImgUrl").getAsString();
                        String coverImgId=jsonObject.get("coverImgId").getAsString();
                        String id=jsonObject.get("id").getAsString();
                        String userId=jsonObject.get("userId").getAsString();
                        if(Uid.equals(userId)){
                            Bitmap image= BitmapFactory.decodeStream(new URL(coverImgUrl).openStream());
                            File coverImgFile=new File(CoverImg,coverImgId);
                            image.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(coverImgFile));

                        }
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
