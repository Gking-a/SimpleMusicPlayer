/*
 */

package com.gkingswq.simplemusicplayer.base;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public <T extends View> T f(int id) {
        return super.findViewById(id);
    }
}
