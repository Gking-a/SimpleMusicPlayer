package com.gkingswq.simplemusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import GTools.GLibrary;
import GTools.GLibraryManager;
import com.gkingswq.simplemusicplayer.util.JSON;
import GTools.util.GFileUtil;

import static com.gkingswq.simplemusicplayer.Value.Files._LINK;
import static com.gkingswq.simplemusicplayer.Value.Files._LOCATION;
import static com.gkingswq.simplemusicplayer.Value.Files._NAME;

public class Receive extends AppCompatActivity {
	static File selectedFile;
	private static final Pattern userPattern = Pattern.compile("userid=[\\d]+");
	private static final Pattern Pattern1 =Pattern.compile("/(.*?)\\?id=([\\d]+)");
	private static final Pattern Pattern2=Pattern.compile("/([a-z]+)/([\\d]+)/");
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive);
        spiada();
        findViewById(R.id.receiveButtonN).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1) {
                Intent i=new Intent(Receive.this, CreateNewList.class);
                startActivity(i);
            }
        });
        Intent ti=getIntent();
        String action=ti.getAction();
        String type=ti.getType();
        String path = "";
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            path = ti.getStringExtra(Intent.EXTRA_TEXT);
        }
        EditText editText=findViewById(R.id.receiveEditText);
        editText.setText(path);
        Matcher m = Pattern1.matcher(path);
        Matcher m2 = Pattern2.matcher(path);
        String prefix = "%LOCAL%",id = null;
        if(m.find()){
            prefix=getPrefix(m.group(1));
            id = m.group(2);
        }else if(m2.find()){
            prefix=m2.group(1);
            id = m2.group(2);
        }
        final String finalid=id;
        switch (prefix){
            case "playlist" :{
                ArrayList<String> ids=JSON.getSongsfromList(id,true);
                File willfile=new File(getFilesDir(), ids.get(0)+".GList");
                ids.remove(0);
                try {
                    if(!willfile.exists()){willfile.createNewFile();}
                    GLibrary lib=new GLibrary(willfile);
                    GLibraryManager.add(lib);
                    lib.connect();
                    for (String s:ids) {
                        lib.add("163id",s,GLibrary.TYPE_STRINGS);
                    }
                    lib.save();
                    finish();
                } catch (IOException e) {}
            }
            break;
            case "song":{
                Button b=findViewById(R.id.receiveButton);
                b.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View p1) {
                        if (selectedFile != null) {
                            try {
                                FileWriter fw = new FileWriter(selectedFile, true);
                                fw.write("\n[Gking:id]*" + finalid);
                                fw.flush();
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        finish();
                    }
                });
            }
            break;
            case "%LOCAL%":
                Button b=findViewById(R.id.receiveButton);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText=findViewById(R.id.receiveEditText);
                        String path=editText.getText().toString();
                        File f=new File(path);
                        if(!f.exists()){
                            Toast.makeText(getApplicationContext(), "No file exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String name=f.getName();
                        name=name.substring(0,name.lastIndexOf("."));
                        if (selectedFile == null) {
                            Toast.makeText(getApplicationContext(), "No List exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            GLibrary lib= GLibraryManager.getLib(_LINK);
                            String guid="@"+ new Object().hashCode();
                            for (String key:lib.getStringMap().keySet()){
                                if (lib.get(key).equals(path)){
                                    guid=key;
                                }
                            }
                            lib= GLibraryManager.getLib(selectedFile);
                            lib.add("local",guid,GLibrary.TYPE_STRINGS);
                            lib.save();
                            lib= GLibraryManager.getLib(_LINK);
//                            正儿八经的写个注释
//                            这里依旧添加path是为了做查找
//                            当重复时候通过这个判断
                            lib.add(guid,path,GLibrary.TYPE_STRING);
                            lib.save();
                            lib= GLibraryManager.getLib(_NAME);
                            lib.add(guid,name,GLibrary.TYPE_STRING);
                            lib.save();
                            File songFile=new File(_LOCATION,guid);
                            songFile.createNewFile();
                            GFileUtil.CopyFile(f,songFile);
                            finish();
                        }catch (IOException e){
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG);
                        }
                    }
                    public boolean saveLib(GLibrary lib) {
                        try {
                            lib.save();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            return true;
                        }
                        return false;
                    }
                });
        }
	}
	private static String getPrefix(String prefix){
		while (prefix.contains("/")){
			prefix = prefix.substring(prefix.indexOf("/") + 1);
		}
		return prefix;
	}
	private void spiada() {
		Spinner sp=findViewById(R.id.receiveSpinner);
		ArrayAdapter ad=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Re());
		ad.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		sp.setAdapter(ad);
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int po, long id) {
					File f=new File(getFilesDir(), Re()[po] + ".GList");
					selectedFile=f;
				}
				@Override
				public void onNothingSelected(AdapterView<?> pa) {
					selectedFile = null;
				}
			});
	}
	@Override
	protected void onResume() {
		super.onResume();
		spiada();

	}
	private String[] Re() {
		File files=getFilesDir();
		ArrayList<File> lists=new ArrayList<File>();

		ArrayList<String> names=new ArrayList<String>();
		for (File f:files.listFiles()) {
			if (f.getName().endsWith(".GList")) {
				lists.add(f);

				names.add(f.getName().substring(0, f.getName().indexOf(".GList")));
			}
		}
		final String s2[]=names.toArray(new String[lists.size()]);
		return s2;
	}
}
