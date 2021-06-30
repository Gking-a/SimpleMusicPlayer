package com.gking.simplemusicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gtools.managers.GHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Playlist extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        String id = getIntent().getStringExtra("id");
        JsonObject playlist=(JsonObject) GHolder.standardInstance.get(id);
        WebRequest.playlist_detail(JsonUtil.getAsString(playlist, "id"), MyCookieJar.getLoginCookie(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body=response.body().string();
                JsonObject object= JsonParser.parseString(body).getAsJsonObject();
                JsonArray trackIds = JsonUtil.getAsJsonArray(object, "playlist", "trackIds");
                List<String> ids=new ArrayList<>();
                for (int i = 0; i < trackIds.size(); i++) {
                    String id=JsonUtil.getAsString(trackIds.get(i).getAsJsonObject(),"id");
                    ids.add(id);
                }
                WebRequest.song_detail(ids, MyCookieJar.getLoginCookie(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String body=response.body().string();
                        JsonObject jsonObject=JsonParser.parseString(body).getAsJsonObject();
                        JsonArray songs = JsonUtil.getAsJsonArray(jsonObject, "songs");
                        for (int i = 0; i < songs.size(); i++) {
                            String name = songs.get(i).getAsJsonObject().get("name").getAsString();

                        }
                    }
                });
            }
        });
    }
    class MyHandler extends Handler{
        public static final int UPDATE_COVER=0;
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            switch (msg.what){
                case UPDATE_COVER:
                    break;
            }
        }
    }
}