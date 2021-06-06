/*
 */

package com.gkingswq.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    
    public static final String TAG = "MainActivity";
    EditText search;
    RecyclerView recentSongs;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        load();
    }
    @Override
    protected void onStart() {
        super.onStart();
        search.clearFocus();
        imm.hideSoftInputFromWindow(search.getWindowToken(),0);
    }
    
    public <T extends View> T f(int id) {
        return super.findViewById(id);
    }
    private void load(){
        Toolbar toolbar=f(R.id.toolbar);
        setSupportActionBar(toolbar);
        search=f(R.id.searchEditText);
        DrawerLayout drawer=f(R.id.drawer);
        drawer.requestFocus();
        recentSongs=f(R.id.recentSongs);
        imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }
}
