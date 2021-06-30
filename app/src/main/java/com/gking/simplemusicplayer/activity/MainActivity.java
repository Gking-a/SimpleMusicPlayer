/*
 */

package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.MyResources;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.impl.RecyclerViewAdapter;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gtools.managers.GHolder;
import gtools.util.GTimer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    Map<String,GHolder> playlists=new LinkedHashMap<>();
    MyHandler handler=new MyHandler();
    EditText search;
    RecyclerView recentSongs;
    NavigationView nav;
    DrawerLayout drawerLayout;
    LinearLayout playlistView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContext(this);
        setContentView(R.layout.main);
        load();
//        debug();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private void load(){
        Toolbar toolbar=f(R.id.toolbar);
        setSupportActionBar(toolbar);
        search=f(R.id.searchEditText);
        recentSongs=f(R.id.recentSongs);
        playlistView=f(R.id.songLists);
        nav=f(R.id.nav);
        drawerLayout=f(R.id.drawer);
        MenuItem login=nav.getMenu().findItem(R.id.login);
        if(MySettings.get("account_name")!=null){
           login.setTitle(MySettings.get("account_name"));
           Intent i=new Intent(this,Login_cellphone.class);
           i.putExtra("ph",MySettings.get("account_phone"));
           i.putExtra("pw",MySettings.get("account_pw"));
           startActivityForResult(i,Login_cellphone.RequestCode);
        }else login.setTitle("登录");
        nav.setNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.login)startActivityForResult(new Intent(getContext(), Login_cellphone.class),Login_cellphone.RequestCode);
            return true;
        });
        load2();
    }
    private void load2(){
    }
    GTimer timer=new GTimer();
    @Override
    public void onBackPressed() {
        if(!timer.compareBigger(1000))super.onBackPressed();
        if(drawerLayout.isOpen())drawerLayout.close();
        else drawerLayout.open();
        timer.reset();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==Login_cellphone.RequestCode){
            if(data.getBooleanExtra("refresh",false)){
                JsonObject object= (JsonObject) GHolder.standardInstance.get(Login_cellphone.RequestCode);
                String name=object.getAsJsonObject("profile").get("nickname").getAsString();
                MenuItem login=nav.getMenu().findItem(R.id.login);
                login.setTitle(name);
                MySettings.set("account_phone",data.getStringExtra("phone"));
                MySettings.set("account_pw",data.getStringExtra("pw"));
                MySettings.set("account_id",object.getAsJsonObject("account").get("id").getAsString());
                MySettings.set("account_name",name);
                {
                    WebRequest.user_playlist(object.getAsJsonObject("account").get("id").getAsString(), MyCookieJar.getLoginCookie(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            FW.w(e);
                            System.out.println(e);
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String body=response.body().string();
                            System.out.println(body);
                            JsonArray jsonArray= JsonParser.parseString(body).getAsJsonObject().getAsJsonArray("playlist");
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject playlist=jsonArray.get(i).getAsJsonObject();
                                String uid=playlist.getAsJsonObject("creator").get("userId").getAsString();
                                if(uid.equals(MySettings.get("account_id"))){
                                    String id=playlist.get("id").getAsString();
                                    GHolder holder=new GHolder();
                                    Bitmap cover= BitmapFactory.decodeStream(new URL(
                                            playlist.get("coverImgUrl").getAsString()+"?param=50y50"
                                    ).openStream());
                                    holder.add("cover",cover);
                                    holder.add("name",playlist.get("name").getAsString());
                                    holder.add("json",playlist);
                                    holder.add("id",id);
                                    GHolder.standardInstance.add(id,playlist);
                                    playlists.put(id,holder);
                                }
                            }
                            Message message=new Message();
                            message.what=MyHandler.UPDATE_COVER;
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        }
    }
    class MyHandler extends Handler{
        public static final int UPDATE_COVER=0;
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            switch (msg.what){
                case UPDATE_COVER:
                    for(String id:playlists.keySet()){
                        GHolder holder=playlists.get(id);
                        View view= LayoutInflater.from(getContext()).inflate(R.layout.list_small,null);
                        ImageView iv=view.findViewById(R.id.list_small_icon);
                        TextView tv=view.findViewById(R.id.list_small_title);
                        iv.setImageBitmap((Bitmap) holder.get("cover"));
                        tv.setText((String) holder.get("name"));
                        View layout=view.findViewById(R.id.list_small_layout);
                        layout.setOnClickListener(v -> {
                            Intent intent=new Intent(getContext(),Playlist.class);
                            intent.putExtra("id",(String)holder.get("id"));
                            startActivity(intent);
                        });
                        playlistView.addView(view);
                    }
                    break;
            }
        }
    }
}
