/*
 */

package com.gking.simplemusicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.util.GFile;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

import gtools.GLibrary;
import gtools.managers.GHolder;
import gtools.model.ModelManager;
import gtools.util.GTimer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.*;
public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";
    EditText search;
    RecyclerView recentSongs;
    NavigationView nav;
    DrawerLayout drawerLayout;
    LinearLayout playlistView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        load();
    }
    @Override
    protected void onStart() {
        super.onStart();
        File[] files=Playlists.listFiles();
        if(!(files ==null)){
            for (File a:files) {
                GLibrary gLibrary = new GLibrary(a,true);
                String name=gLibrary.get("name");
                String coverImgId=gLibrary.get("coverImgId");
                Bitmap bitmap= BitmapFactory.decodeFile(new File(CoverImg,coverImgId).getAbsolutePath());
                LayoutInflater inflater=LayoutInflater.from(this);
                View layout=inflater.inflate(R.layout.list_small,null);
                ImageView imageView=layout.findViewById(R.id.list_small_icon);
                imageView.setBackground(new BitmapDrawable(getResources(),bitmap));
                TextView textView=layout.findViewById(R.id.list_small_title);
                textView.setText(name);
                playlistView.addView(layout);
            }
        }

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
        playlistView=f(R.id.songLists);

        load2();
    }
    private void load2(){


    }
    
}
