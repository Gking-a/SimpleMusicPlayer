package com.gkingswq.simplemusicplayer;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class ListChange extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listchange);
		Toolbar tb=findViewById(R.id.toolbar);
		setSupportActionBar(tb);
		//tb.setNavigationOnClickListener(new MyListener2());
    }
}
