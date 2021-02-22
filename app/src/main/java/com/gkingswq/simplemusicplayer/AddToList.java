package com.gkingswq.simplemusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import GTools.GLibrary;
import GTools.GLibraryManager;

import static com.gkingswq.simplemusicplayer.Value.Settings.DEFAULT_LIST;

public class AddToList extends AppCompatActivity {
//    20201206日常偷懒中
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        用的receive的布局
        setContentView(R.layout.receive);
        findViewById(R.id.receiveEditText).setVisibility(View.GONE);
        Intent intent=getIntent();
        final ArrayList<String> list=intent.getStringArrayListExtra("selectedIds");
//        照搬Settings里的module1
        Spinner mSpinner=findViewById(R.id.receiveSpinner);
        String[] lns=getlistnames();
        String[] noendnames=new String[lns.length];
        for(int i=0;i<lns.length;i++){
            noendnames[i]=lns[i].substring(0,lns[i].length()-6);
        }
        ArrayAdapter madapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, noendnames);
        mSpinner.setAdapter(madapter);
        mSpinner.setSelection(-1);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override public void onItemSelected(AdapterView<?> adapter, View view, int p, long id) {
                try {
                    GLibraryManager.getLib(new File(getFilesDir(),getlistnames()[p]+".GList")).add("local",list,GLibrary.TYPE_STRINGS);
                    GLibraryManager.getLib(new File(getFilesDir(),getlistnames()[p]+".GList")).save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p1) {}
        });
    }
//    用的Settings里的方法
    private String[] getlistnames(){
        File[] all=getFilesDir().listFiles();
        if(all.length==0){return null;}
        ArrayList<String> r=new ArrayList<>();
        for(File f:all){
            if(f.getName().endsWith(".GList")){
                r.add(f.getName());
            }
        }
        return r.toArray(new String[r.size()]);
    }
}
