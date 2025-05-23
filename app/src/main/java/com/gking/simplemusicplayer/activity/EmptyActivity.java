package com.gking.simplemusicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;

public class EmptyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        TextView textView=f(R.id.activity_empty_tv);
        int res = getIntent().getIntExtra("text", 0);
        textView.setText(res);
        if(res==0)
            textView.setText(getIntent().getStringExtra("text_str"));
    }
}