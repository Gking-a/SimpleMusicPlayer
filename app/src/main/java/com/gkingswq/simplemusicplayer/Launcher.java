/*
 */

package com.gkingswq.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

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
    private void loadResources(){}
}