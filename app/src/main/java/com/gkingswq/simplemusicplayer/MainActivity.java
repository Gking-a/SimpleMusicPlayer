/*
 */

package com.gkingswq.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    
    public static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
}
