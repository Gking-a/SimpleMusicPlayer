/*
 */

package com.gking.simplemusicplayer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.MyResources;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

import gtools.GLibrary;
import gtools.managers.GHolder;
import gtools.util.GTimer;

import static com.gking.simplemusicplayer.impl.MyApplicationImpl.CoverImg;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.Playlists;
public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";
    EditText search;
    RecyclerView recentSongs;
    NavigationView nav;
    DrawerLayout drawerLayout;
    LinearLayout playlistView;
    GHolder<String,Bitmap> pictures;
    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContext(this);
        setContentView(R.layout.main);
        load();
    }
    @Override
    protected void onStart() {
        super.onStart();
        reloadOnStart();
        File[] files=Playlists.listFiles();
        if(!(files ==null)){
            for (File a:files) {
                GLibrary gLibrary = new GLibrary(a,true);
                String name=gLibrary.get("name");
                String coverImgId=gLibrary.get("coverImgId");
                Bitmap bitmap= pictures.get(coverImgId);
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
    private void reloadOnStart() {
        if(pictures.getIds().size()!=CoverImg.list().length)
            super.loadPictures();
    }

    private void load(){
        Toolbar toolbar=f(R.id.toolbar);
        setSupportActionBar(toolbar);
        search=f(R.id.searchEditText);
        recentSongs=f(R.id.recentSongs);
        nav=f(R.id.nav);
        drawerLayout=f(R.id.drawer);
        LinearLayout header=nav.getHeaderView(0).
            findViewById(R.id.nav_headerLayout);
        header.setBackground(MyResources.nav_header_bg);
        TextView textView=new TextView(this);
        if(MySettings.get(MySettings.login)!=null){
            textView.setText(MySettings.get(MySettings.login));
        }else textView.setText("登录");
        header.addView(textView);
        header.setClickable(true);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),Login_cellphone.class);
                startActivity(intent);
            }
        });
        playlistView=f(R.id.songLists);

        load2();
    }
    private void load2(){
        pictures=(GHolder<String,Bitmap>) GHolder.standardInstance.get("PlaylistPictures");
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
