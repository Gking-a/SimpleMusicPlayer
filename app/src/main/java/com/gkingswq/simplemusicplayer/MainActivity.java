/*
 */

package com.gkingswq.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.gkingswq.simplemusicplayer.base.BaseActivity;
import com.google.android.material.navigation.NavigationView;
import com.gkingswq.simplemusicplayer.util.GCounting;
import gtools.util.GTimer;

public class MainActivity extends BaseActivity {
    private static final String Count_openedDrawer="openedDrawer";
    public static final String TAG = "MainActivity";
    EditText search;
    RecyclerView recentSongs;
    NavigationView nav;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        load();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    GTimer timer=new GTimer();
    @Override
    public void onBackPressed() {
        if(!timer.compareBigger(1000))super.onBackPressed();
        if(drawerLayout.isOpen())drawerLayout.close();
        else drawerLayout.open();
        timer.reset();
    }
    private void load(){
        Toolbar toolbar=f(R.id.toolbar);
        setSupportActionBar(toolbar);
        search=f(R.id.searchEditText);
        recentSongs=f(R.id.recentSongs);
        nav=f(R.id.nav);
        drawerLayout=f(R.id.drawer);
        View header=nav.getHeaderView(0).
            findViewById(R.id.nav_headerLayout);
        header.setBackground(MyResources.nav_header_bg);
        load2();
    }
    private void load2(){
        
    }
    
}
