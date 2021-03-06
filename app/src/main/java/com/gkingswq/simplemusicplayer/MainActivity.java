
package com.gkingswq.simplemusicplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gkingswq.simplemusicplayer.Interface.OnGetNameCompile;
import com.gkingswq.simplemusicplayer.base.base1.Activity1;
import com.google.android.material.navigation.NavigationView;
import libs.com.hz.android.keyboardlayout.KeyboardLayout;
//import com.rey.material.widget.Slider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GTools.GLibrary;
import GTools.GLibraryManager;
import com.gkingswq.simplemusicplayer.util.GLists;
//import GTools.GSettings;
import com.gkingswq.simplemusicplayer.util.GSong;
import com.gkingswq.simplemusicplayer.util.MyCollections;

import static com.gkingswq.simplemusicplayer.PlayingService.mp;
import static com.gkingswq.simplemusicplayer.Value.Actions.*;
import static com.gkingswq.simplemusicplayer.Value.Files._LINK;
import static com.gkingswq.simplemusicplayer.Value.Files._LOCATION;
import static com.gkingswq.simplemusicplayer.Value.Files._NAME;
import static com.gkingswq.simplemusicplayer.Value.Files._SETTINGS;
import static com.gkingswq.simplemusicplayer.Value.Flags.*;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.*;
import static com.gkingswq.simplemusicplayer.Value.Settings.*;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.fab1;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.fab2;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.fab_menu;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.t1;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.t2;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.t3;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.t4;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mA;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mB;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mC;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.seekBar;
import android.view.inputmethod.InputMethodManager;
public class MainActivity extends Activity1 {
	Map<Integer,File> lists=new HashMap<>();
	File selectedFile;
	private static RecyclerView mRecyclerView;
	DrawerLayout drawer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loadSettings();
		findViews();

		Toolbar tb=findViewById(R.id.toolbar);
		setSupportActionBar(tb);
		tb.setNavigationOnClickListener(new MyListener2());

		EditText ed=findViewById(R.id.main_searchName);
		ed.addTextChangedListener(new MyListener4());

		DrawerLayout dl=findViewById(R.id.drawer);
		this.drawer=dl;
		NavigationView nv=findViewById(R.id.nav);
		nv.setNavigationItemSelectedListener(new MyListener());

		RecyclerView rv=findViewById(R.id.main_recyclerview);
		mRecyclerView =rv;
		rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setFocusableInTouchMode(true);
		rv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

		KeyboardLayout kl=findViewById(R.id.keyboradlayout);
		kl.setKeyboardLayoutListener(new KeyboardLayout.KeyboardLayoutListener() {
			@Override
			public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {
				if(!isActive){
					mRecyclerView.requestFocus();
				}
			}
		});
		navigationViewUpdate();
	}
    @Override
    protected void onStart() {
	    super.onStart();
	    File[] files=getFilesDir().listFiles();
	    if(files==null)
	        return;
        NavigationView nv=findViewById(R.id.nav);
        nv.getMenu().removeGroup(1);
        for (int i=0;i < files.length;i++) {
            String name=files[i].getName();
            if(name.indexOf(".GList")<0){continue;}
            name = name.substring(0, name.length() - 6);
            nv.getMenu().add(1, i, i, name);
            lists.put(i, files[i]);
        }
    }
    /**
	*@time 2020.6.20 19:32
	*@version 1
	*@return null
	*@extends MainActivity.List.setAdapter*/
	Map<Integer,String> ids;
	Map<String,String> names;
	private void navigationViewUpdate() {
		if(selectedFile==null){ return;}
		ids = new HashMap<>();
        GLibrary lib= GLibraryManager.getLib(selectedFile.getName(),selectedFile);
		List<String> arrayList=lib.getStrings("163id");
		if(arrayList==null){
		    arrayList=new ArrayList<>();
        }
		List<String> linkids=lib.getStrings("local");
		if(linkids!=null){
			arrayList.addAll(linkids);
        }
		for(int i=0;i<arrayList.size();i++){
            ids.put(i, arrayList.get(i));
        }
		fab_menu.setVisibility(View.GONE);
		if(selectedFile.equals(_LINK)){
			String[] idarray=GLibraryManager.getLib(_LINK).getAllSymbols();
			for(int i=0;i<idarray.length;i++){
				ids.put(i, idarray[i]);
			}
			Log.i("t1",t1.getY()+"");
			fab_menu.setVisibility(View.VISIBLE);
		}
		MyAdapter madapter = new MyAdapter(this, ids);
		mRecyclerView.setAdapter(madapter);
        madapter.notifyDataSetChanged();
		drawer.closeDrawer(Gravity.LEFT);
	}
	int mFLAG=FLAG_SOLO;
	boolean isSearching;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		if(id==R.id.mode){
			if(mFLAG==FLAG_SOLO){
				mFLAG=FLAG_RANDOM;
			}else if(mFLAG==FLAG_RANDOM){
				mFLAG=FLAG_SOLO;
			}
			changeModeTitle();
		}
		return true;
	}
	MenuItem menuItem;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar,menu);
        MenuItem menuItem=menu.findItem(R.id.mode);
        this.menuItem=menuItem;
		changeModeTitle();
		return true;
	}
	@Override
	protected void onDestroy() {
		String path=getCacheDir().getAbsolutePath();
		getCacheDir().delete();
		new File(path).mkdirs();
		
		super.onDestroy();
	}
	/**
	 *@time 2020.6.19.?.?
	 *@version 1
	 *@return null*/
	class MyListener implements NavigationView.OnNavigationItemSelectedListener {
		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
            InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(findViewById(R.id.main_searchName).getWindowToken(),0);
			if (item.getItemId() == R.id.entrysettings) {
				Intent intent = new Intent(MainActivity.this, Settings.class);
				startActivity(intent);
			} else if(item.getItemId()==R.id.entrysearch){
                Intent intent=new Intent(MainActivity.this,Search.class);
                startActivity(intent);
            }else if(item.getItemId()==R.id.entryadd){
                Intent intent=new Intent(MainActivity.this,Receive.class);
                startActivity(intent);
            }else if(item.getItemId()==R.id.entrylocation){
			    selectedFile=GLibraryManager.getLib(_LINK).getLibrary();
                EditText ed=findViewById(R.id.main_searchName);
                ed.setText(null);
            }else if(item.getItemId()==R.id.entryexit){
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			else {
				selectedFile=lists.get(item.getItemId());
				EditText ed=findViewById(R.id.main_searchName);
				ed.setText(null);
//				refreshrv();
			}
			return true;
		}
	}
	void t(Object o) {
		Toast.makeText(this, o.toString(), Toast.LENGTH_SHORT).show();
	}
	class MyListener2 implements OnClickListener {
		@Override
		public void onClick(View p1) {
			drawer.openDrawer(Gravity.LEFT);
		}
	}
	class MyListener3 implements OnClickListener {
		String id;
		public MyListener3(String id){
			this.id=id;
		}
		@Override
		public void onClick(View p1) {
			Intent intent=new Intent(MainActivity.this,PlayingService.class);
			//intent=new Intent(MainActivity.this,PlayingService.class);
			if(mFLAG==Value.Flags.FLAG_SOLO||isSearching){
				intent.putExtra(playid,id);
                intent.putExtra(playflag,FLAG_SOLO);
                startService(intent);
                return;
			}else{
			    if(ids.size()==0){return;}
			    ArrayList<String> mids=new ArrayList<>();
			    mids.addAll(ids.values());
			    mids=MyCollections.randomsort(mids);
//			    Collections.sort(mids);
			    intent.putExtra(playids,mids);
			    intent.putExtra(randomindex,0);
			}
			intent.putExtra(playflag,mFLAG);
			startService(intent);
		}
	}
	ArrayList<String> selectedIds=new ArrayList<>();
	protected class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
		List<String> data;
		Context context;
		public MyAdapter(Context c,Map<?,? extends String> ids){
			this.context=c;
			this.data=new ArrayList<>();
            data.addAll(ids.values());
		}
		public MyAdapter(Context c, Collection<String> collection){
			this.context=c;
			this.data=new ArrayList<>();
            data.addAll(collection);
		}
		@Override
		public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parents, int p2) {
			LayoutInflater inflater=LayoutInflater.from(context);
            return new ViewHolder(inflater.inflate(R.layout.item,parents,false));
		}
		@Override
		public void onBindViewHolder(final MyAdapter.ViewHolder vh, final int posi) {
			OnGetNameCompile mlistener=new OnGetNameCompile(){
                @Override
                public void onCompile(String id, String name) {
                    vh.Name.setText(name);
                    if(posi==data.size()-1){
                        try {
                            GLibraryManager.getLib(_NAME).save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
			};
			GSong.getName(data.get(posi),mlistener);
			vh.Position.setText(String.valueOf(posi+1));
			MyListener3 m3=new MyListener3(data.get(posi));
			vh.Name.setOnClickListener(m3);
			vh.Position.setOnClickListener(m3);
			if(selectedFile.equals(_LINK)){
				final TextView Position=vh.Position;
				Position.setText("");
				Position.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(Position.getText().equals("")){
							Position.setText("◆");
							String selectedId=data.get(posi);
							selectedIds.add(selectedId);
						}else {
							Position.setText("");
							for (int i = 0; i < selectedIds.size(); i++) {
								if(selectedIds.get(i).equals(data.get(posi))){
									selectedIds.remove(i);
									break;
								}
							}
						}
					}
				});
			}
		}
		@Override
		public int getItemCount() {
			return data.size();
		}
		class ViewHolder extends RecyclerView.ViewHolder{
			TextView Position;
			TextView Name;
			public ViewHolder (View view)
			{
				super(view);
				Name=(TextView)view.findViewById(R.id.itemTextView);
				Position=view.findViewById(R.id.itemPosition);
			}
		}
		public void eachmove(int from,int to){
			Collections.swap(data,from,to);
			notifyItemRangeChanged(from,to);
		}
		public void remove(){

		}
	}
	class MyListener4 implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            EditText ed=findViewById(R.id.main_searchName);
            String searchstr=ed.getText().toString();
            if(!(searchstr!=null&&searchstr.length()!=0&&!searchstr.equals(""))){
                isSearching=false;
                changeModeTitle();
                navigationViewUpdate();
                return;}
            isSearching=true;
			menuItem.setTitle("Search");
			ArrayList<String> arrayList= new ArrayList<>();
			for(String id:ids.values()){
				arrayList.add(id);
			}
			if(arrayList==null||arrayList.size()==0){
				return;
			}
			Map<String,String> names=GLists.getName(arrayList,searchstr);
			MyAdapter madapter = new MyAdapter(MainActivity.this, names.keySet());
			madapter.notifyDataSetChanged();
			mRecyclerView.setAdapter(madapter);
        }
    }
	/*class MyItemTouchHelper extends ItemTouchHelper{}
	class MyCallback extends ItemTouchHelper.Callback {
		private MyAdapter madapter;
		public MyCallback(MyAdapter m){
			this.madapter=m;
		}
		@Override
		public int getMovementFlags(RecyclerView p1, RecyclerView.ViewHolder p2) {
			int v=ItemTouchHelper.UP|ItemTouchHelper.DOWN;
			int h=0;
			return makeMovementFlags(v,h);
		}

		@Override
		public boolean onMove(RecyclerView p1, RecyclerView.ViewHolder p2, RecyclerView.ViewHolder p3) {
			madapter.eachmove(p2.getAdapterPosition(),p3.getAdapterPosition());
		}

		@Override
		public void onSwiped(RecyclerView.ViewHolder p1, int p2) {
		}
	}
	}*/
	public void changeModeTitle(){
        if(mFLAG==FLAG_RANDOM){
            menuItem.setTitle("随机");
        }else if(mFLAG==FLAG_SOLO){
            menuItem.setTitle("单独");
        }
    }
    public static class MyRunnable1 implements Runnable{
		public void setS(String s) {
			this.s=s;
		}
		private TextView t;
	    private String s;
	    public MyRunnable1(TextView t){
	        this.t=t;
        }
		public MyRunnable1(TextView t,String s){
			this(t);
			this.s=s;
		}
        MyRunnable1(String s){
	        t1.setText(s);
	        t2.setText(s);
	        t3.setText(s);
	        t4.setText(s);
        }
        @Override
        public void run() {
	        if(t!=null)
	            t.setText(s);
        }
    }
    public static class MyRunnable2 implements Runnable{
	    public MyRunnable2(int i){
            this();
	        setI(i);
	        seekBar.setProgress(i);
//            slider.setValue(i,true);
        }
		int i;
		public void setI(int i) {
			this.i = i;
		}
		public int getI() {
			return i;
		}
		@Override
		public void run() {
			if(seekBar!=null)
				seekBar.setProgress(i);
//		    if(slider!=null)
//		        slider.setValue(i,false);
		}
		public MyRunnable2(){
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					if(fromUser){
						seekBar.setProgress(progress);
					    mp.seekTo(progress*1000);
					}
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) { }
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) { }
			});
//			slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
//				@Override
//				public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
//					if(fromUser){
//					    mp.seekTo(newValue*1000);
//					}
//				}
//			});
		}
	}
	long lasttime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			long now=System.currentTimeMillis();
			lasttime =now- lasttime;
			drawer.closeDrawer(Gravity.LEFT);
			if(lasttime >1000){
				lasttime =now;
				t("again to exit");
				return true;
			}
			finish();
		}else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamVolume(AudioManager.STREAM_MUSIC)+1,AudioManager.FLAG_SHOW_UI);
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamVolume(AudioManager.STREAM_MUSIC)-1, AudioManager.FLAG_SHOW_UI);
        }
		return false;
	}
    private void loadSettings() {
	    String[] permissions=new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
	    //requestPermission(this,permissions,0);
	    requestAlertWindowPermission();
        GLibrary lib= GLibraryManager.getLib( _SETTINGS);
        String name=lib.get(DEFAULT_LIST);
        String s=lib.get(playflag);
        mFLAG=Integer.parseInt(lib.get(playflag));
        if(name==null){return;}
        File mfile=new File(getFilesDir(),name);
        if(mfile.exists()){
            selectedFile=mfile;
        }
//        selectedFile=_LINK;
    }
    private void findViews(){
        t1=findViewById(R.id.time_now);
        t2 = findViewById(R.id.time_total);
        t3 =findViewById(R.id.main_SongName);
        t4 = findViewById(R.id.main_lyric);
        seekBar = findViewById(R.id.seekbar);
        mA = findViewById(R.id.mA);
        mB = findViewById(R.id.mB);
        mC=findViewById(R.id.mC);
        fab_menu =findViewById(R.id.main_fab_menu);
        fab1=findViewById(R.id.main_fab_add);
        fab2=findViewById(R.id.main_fab_remove);
        mB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendBroadcast(new Intent(ACTION_PAUSE));
			}
		});
        fab1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(selectedIds==null)
					return;
				Intent i=new Intent(MainActivity.this,AddToList.class);
				i.putExtra("selectedIds",selectedIds);
			}
		});
        fab2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (String selectedID:selectedIds) {
					GLibraryManager.getLib(_LINK).remove(selectedID,GLibrary.TYPE_STRING);
					GLibraryManager.getLib(_NAME).remove(selectedID,GLibrary.TYPE_STRING);
					new File(_LOCATION,selectedID).delete();
					for (GLibrary lib: GLibraryManager.getAllLibs()) {
						List<String> list=lib.getStrings("local");
						if (list==null){
							break;
						}
						list.remove(selectedID);
						try {
							lib.add("local",list,GLibrary.TYPE_STRINGS);
							lib.save();
						} catch (IOException e) { e.printStackTrace(); }
					}
				}

				navigationViewUpdate();
			}
		});
//       fab.setY(getWindowManager().getDefaultDisplay().getHeight()-300);
//		final float[] t1Y = {0};
//		final float[] fabH = {0};
//		t1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//			@Override
//			public void onGlobalLayout() {
//				t1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//				t1Y[0] =t1.getY();
//				if(t1Y[0]!=0&& fabH[0]!=0){
//					fab.setY(t1Y[0]-10-fabH[0]);
//				}
//			}   });
//		fab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//			@Override
//			public void onGlobalLayout() {
//				fab.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//				fabH[0] =fab.getHeight();
//				if(t1Y[0]!=0&& fabH[0]!=0){
//					fab.setY(t1Y[0]-10-fabH[0]);
//				}
//			}   });
    }
}
