package com.gking.simplemusicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gking.simplemusicplayer.R;
import com.google.gson.JsonObject;

import gtools.managers.GHolder;

public class Playlist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        String id = getIntent().getStringExtra("id");
        JsonObject playlist= (JsonObject) GHolder.standardInstance.get(id);
    }
}