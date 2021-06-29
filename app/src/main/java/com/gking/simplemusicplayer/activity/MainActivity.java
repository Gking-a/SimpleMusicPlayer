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
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.MyResources;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.RecyclerViewAdapter;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import gtools.managers.GHolder;
import gtools.util.GTimer;
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
        if(MySettings.get(MySettings.login)!=null){
           login.setTitle(MySettings.get(MySettings.login));
        }else login.setTitle("登录");
        nav.setNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.login)startActivity(new Intent(getContext(), Login_cellphone.class));
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
}
