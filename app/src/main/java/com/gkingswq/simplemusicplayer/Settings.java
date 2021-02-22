package com.gkingswq.simplemusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Switch;

import GTools.GLibrary;
//import GTools.GSettings;
import static com.gkingswq.simplemusicplayer.Value.Files._SETTINGS;
import static com.gkingswq.simplemusicplayer.Value.Flags.*;
import static com.gkingswq.simplemusicplayer.Value.Settings.*;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import GTools.GLibraryManager;
import GTools.util.GFileUtil;

public class Settings extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
        Toolbar tb=findViewById(R.id.toolbar);
		setSupportActionBar(tb);
		tb.setNavigationOnClickListener(new OnClickListener(){@Override public void onClick(View p1) {finish();}});
		module1();
		module2();
		module3();
		module4();
		module5();
		module6();
		module7();
	}
	final GLibrary lib=GLibraryManager.getLib(_SETTINGS);
	private void module1(){
		SettingsItem<Spinner> item=new SettingsItem<Spinner>(R.id.settingsitem1,"默认歌单");
		Spinner mSpinner=item.getExecution();
		String[] lns=getlistnames();
		String[] noendnames=new String[lns.length];
		for(int i=0;i<lns.length;i++){
			noendnames[i]=lns[i].substring(0,lns[i].length()-6);
		}
		ArrayAdapter madapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, noendnames);
		mSpinner.setAdapter(madapter);
		mSpinner.setSelection(-1);
		String name=lib.get(DEFAULT_LIST);
		if(lns.length==0){return;}
		if(name==null){mSpinner.setSelection(0);}
		else if(!new File(getFilesDir(),name).exists()){mSpinner.setSelection(0);}
		else{
			for(int i=0;i<lns.length;i++){
				if(name.equals(lns[i])){
					mSpinner.setSelection(i);
					break;
				}
			}
		}
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override public void onItemSelected(AdapterView<?> adapter, View view, int p, long id) {
				lib.add(DEFAULT_LIST,getlistnames()[p],GLibrary.TYPE_STRING);
			}
			@Override public void onNothingSelected(AdapterView<?> p1) {}
		});
	}
	private void module2(){
		SettingsItem<Spinner> item=new SettingsItem<Spinner>(R.id.settingsitem2,"默认播放模式");
		Spinner mSpinner=item.getExecution();
		int mflag=Integer.valueOf(lib.get(playflag));
		ArrayAdapter madapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,new String[]{"random","solo","loop"});
		mSpinner.setAdapter(madapter);
		switch(mflag){
			case FLAG_RANDOM:mSpinner.setSelection(0);break;
			case FLAG_SOLO:mSpinner.setSelection(1);break;
			case FLAG_LOOP:mSpinner.setSelection(2);break;}
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override public void onItemSelected(AdapterView<?> adapter, View view, int p, long id) {
				lib.add(playflag,p,GLibrary.TYPE_STRING);
			}
			@Override public void onNothingSelected(AdapterView<?> p1) {
			}
		});
	}
	private void module3(){
		Button bt=findViewById(R.id.settingsButton1);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				File nameFile=new File(getFilesDir(), "Name");
				if(nameFile.exists()){
					nameFile.delete();
				}
			}
		});
	}
	private void module4(){
    	Button bt1=findViewById(R.id.settingsButton2);
    	Button bt2=findViewById(R.id.settingsButton3);
    	bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				startActivityForResult(intent,0);
			}
		});
    	bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				File pf=new File("/storage/emulated/0/Android/data/com.gkingswq.simplemusicplayer/lists/");
				try{if(!pf.exists()){pf.mkdirs(); } }catch (Exception e){ }
				for(File f:getFilesDir().listFiles()){
					if(f.getName().endsWith(".GList")){
						try{
							File f2=new File(pf,f.getName());
							if(f2.exists()){
								f2.delete();
							}
							f2.createNewFile();
							BufferedInputStream r=new BufferedInputStream(new FileInputStream(f));
							BufferedOutputStream w=new BufferedOutputStream(new FileOutputStream(f2));
							int i;
							while ((i=r.read())!=-1){
								w.write(i);
							}
							r.close();
							w.flush();w.close();
							GLibraryManager.add(new GLibrary(f2));
						}catch (Exception e){
						}
					}
				}
			}
		});
	}
	private void module5(){
        SettingsItem<Switch> item=new SettingsItem<Switch>(R.id.settingsitem3,"锁屏通知");
		Switch mSwitch=item.getExecution();
        mSwitch.setChecked(Boolean.parseBoolean(lib.get(LOCKEDNOTIFICATIONSHOW)));
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Window win=getWindow();
                    win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                    lib.add(LOCKEDNOTIFICATIONSHOW,true,GLibrary.TYPE_STRING);
                }else{
                    lib.add(LOCKEDNOTIFICATIONSHOW,false,GLibrary.TYPE_STRING);
                }
            }
        });
    }
	private void module6(){
		SettingsItem<Switch> item=new SettingsItem<Switch>(R.id.settingsitem4,"默认开启桌面歌词");
		Switch mSwitch=item.getExecution();
		mSwitch.setChecked(Boolean.parseBoolean(lib.get(DEFAULT_WINDOW_SHOW)));
		mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1, boolean isCheked) {
					lib.add(DEFAULT_WINDOW_SHOW,isCheked,GLibrary.TYPE_STRING);
				}
			});
	}
	private void module7(){
		SettingsItem<EditText> item=new SettingsItem<EditText>(R.id.settingsitem5,"歌词颜色");
		EditText ed=item.getExecution();
		ed.setText(lib.get(WINDOW_COLOR));
		ed.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
				}
				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
				}
				@Override
				public void afterTextChanged(Editable ed) {
					String text=ed.toString();
					lib.add(WINDOW_COLOR,text,GLibrary.TYPE_STRING);
				}
			});
	}
	private String[] getlistnames(){
		File[] all=getFilesDir().listFiles();
		if(all.length==0){return null;}
		ArrayList<String> r=new ArrayList<>();
		for(File f:all){
			if(f.getName().endsWith(".GList")){
				r.add(f.getName().substring(0,f.getName().length()));
			}
		}
		return r.toArray(new String[r.size()]);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			GLibraryManager.getLib(_SETTINGS).save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String path = uri.getPath().toString();
                File f = new File(path);
                if (f.exists()) {
                    try {
                        File f2 = new File(getFilesDir(), f.getName());
                        if (!f2.exists()) {
                            f2.createNewFile();
                        }
						GFileUtil.CopyFile(f,f2);
                    } catch (Exception e) {
                    }
                }
            }
        } else if (requestCode == 1) {

        }
    }
	private class SettingsItem<T extends View>{
		private T execution;
		public SettingsItem(int res,String text){
			LinearLayout layout=findViewById(res);
			TextView tv=layout.findViewById(R.id.settingsItemTextView);
			tv.setText(text);
			execution=layout.findViewById(R.id.settingsItemExecution);
		}
		public T getExecution() {
			return execution;
		}
	}
}
