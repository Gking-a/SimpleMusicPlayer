package com.gking.simplemusicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;

public class SongActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
    }
}