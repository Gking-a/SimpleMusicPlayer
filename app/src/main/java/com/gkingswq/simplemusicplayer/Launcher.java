/*
 */

package com.gkingswq.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class Launcher extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        loadResources();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void loadResources(){
        MyResources.nav_header_bg=new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.nav_header_bg));
    }
}
