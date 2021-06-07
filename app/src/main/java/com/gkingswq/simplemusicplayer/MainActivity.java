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

public class MainActivity extends BaseActivity {
    
    public static final String TAG = "MainActivity";
    EditText search;
    RecyclerView recentSongs;
    NavigationView nav;
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
    
    private void load(){
        Toolbar toolbar=f(R.id.toolbar);
        setSupportActionBar(toolbar);
        search=f(R.id.searchEditText);
        recentSongs=f(R.id.recentSongs);
        nav=f(R.id.nav);
        View header=nav.getHeaderView(0).
            findViewById(R.id.nav_headerLayout);
        header.setBackground(MyResources.nav_header_bg);
    }
}
