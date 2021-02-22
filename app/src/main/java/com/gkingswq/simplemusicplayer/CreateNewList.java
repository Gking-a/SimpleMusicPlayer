package com.gkingswq.simplemusicplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import GTools.GLibrary;
import GTools.GLibraryManager;

public class CreateNewList extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlistadd);
        final EditText ed=findViewById(R.id.playlistaddEditText);
        Button b = findViewById(R.id.playlistaddButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=ed.getText().toString();
                if(name!=null&&name.length()!=0&&!name.equals("")){
                    File file=new File(getFilesDir(),name+".GList");
                    if(file.exists())
                        file.delete();
                    try {
                        file.createNewFile();
                        GLibraryManager.add(new GLibrary(file,true));
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(CreateNewList.this,e.toString(),Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });
    }
}
