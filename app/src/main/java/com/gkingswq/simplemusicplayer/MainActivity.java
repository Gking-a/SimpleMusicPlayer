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

public class MainActivity extends AppCompatActivity {
    
    public static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        loadViews();
    }
    @Override
    protected void onStart() {
        super.onStart();
        EditText search=f(R.id.searchEditText);
        search.clearFocus();
    }
    
    public <T extends View> T f(int id) {
        return super.findViewById(id);
    }
    private void loadViews(){
        Toolbar toolbar=f(R.id.toolbar);
        setSupportActionBar(toolbar);
        EditText search=f(R.id.searchEditText);
        search.setOnFocusChangeListener(new OnFocusChangeListener(){
            InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                @Override
                public void onFocusChange(View view, boolean isFocused) {
                    if(isFocused){}
                    else{imm.hideSoftInputFromWindow(view.getWindowToken(),0);}
                }
            });
        DrawerLayout drawer=f(R.id.drawer);
        drawer.requestFocus();
    }
}
