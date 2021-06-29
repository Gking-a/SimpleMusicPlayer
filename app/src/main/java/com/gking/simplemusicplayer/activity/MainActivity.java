/*
 */

package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gtools.managers.GHolder;
import gtools.util.GTimer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";
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
        debug();
    }
    private void debug() {
        startActivity(new Intent(getContext(),Login_cellphone.class));
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
//        List<GHolder<Object,String>> items=new ArrayList<>();
        MenuItem login=nav.getMenu().findItem(R.id.login);
        if(MySettings.get("account_name")!=null){
           login.setTitle(MySettings.get("account_name"));
           Intent i=new Intent();
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
                        }
                    });
                }


            }
        }
    }
}
