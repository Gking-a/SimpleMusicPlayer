package com.gkingswq.simplemusicplayer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gkingswq.simplemusicplayer.base.base1.Activity1;
import com.hz.android.keyboardlayout.KeyboardLayout;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import GTools.GJson;
import com.gkingswq.simplemusicplayer.util.GLists;

import static com.gkingswq.simplemusicplayer.Value.StringPool.*;
import static com.gkingswq.simplemusicplayer.Value.Flags.FLAG_SOLO;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.playflag;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.playid;

public class Search extends Activity1 {
    String tag = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        final EditText ed=findViewById(R.id.searchName);
        final RecyclerView mRecyclerView=findViewById(R.id.searchList1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        Map<String,String> m=new HashMap<>();
        m.put("", "");
        MyAdapter ma = new MyAdapter(this, m);
        ma.notifyDataSetChanged();
        mRecyclerView.setAdapter(ma);
        final Handler handler=new Handler();
//        final ArrayList<String> names=new ArrayList<>();
//        for(String name:allnamemap.keySet()){
//            names.add(name);
//        }
        final Switch s=findViewById(R.id.search_switch1);
        ImageButton imageButton=findViewById(R.id.search_bt1);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s.isChecked()){
                    String songname=ed.getText().toString();
                    if(!(songname!=null&&songname.length()!=0&&!songname.equals(""))){ return;}
                    Checked(songname,handler,mRecyclerView);
                }
            }
        });
        ed.addTextChangedListener(new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence s,int start,int before,int count) {
                }
                @Override
                public void onTextChanged(CharSequence s,int start,int before,int count) {
                }
                @Override
                public void afterTextChanged(Editable p1) {
                    GLists.reMap();
                    String songname=ed.getText().toString();
                    if(!(songname!=null&&songname.length()!=0&&!songname.equals(""))){ return;}
                    if(s.isChecked()){
//                        Checked(songname,handler);
                    }else {
                        unChecked(songname);
                    }
                }

                private void unChecked(String songname){
                    Map<String,String> namesmap=GLists.getStaticnamemap();
                    Set<String> mkeyset=new HashSet<>();
                    for(String temp:namesmap.keySet()){
                        mkeyset.add(temp);
                    }
                    for(String uncheckname:mkeyset) {
                        if(uncheckname.indexOf(songname)<0) {
                            namesmap.remove(uncheckname);
                        }
                    }
                    MyAdapter myAdapter=new MyAdapter(Search.this,namesmap);
                    myAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(myAdapter);
                }
			});
        class MyRunnable1 implements Runnable{
            private MyAdapter myAdapter;
            public MyRunnable1(MyAdapter ma){
                this.myAdapter=ma;
            }
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(myAdapter);
            }
        }
    }
    protected class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        List<String> data;
        Context context;
        Map<String,String> data2;
        public MyAdapter(Context c,Map<String,String> map){
            this.context=c;
            data2=map;
            this.data=new ArrayList<>();
            for(String name:map.keySet()){
                data.add(name);
            }
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parents, int p2) {
            LayoutInflater inflater=LayoutInflater.from(context);
            ViewHolder vh=new ViewHolder(inflater.inflate(R.layout.searchitem1,parents,false));
            return vh;
        }
        @Override
        public void onBindViewHolder(final MyAdapter.ViewHolder vh, final int position) {
            vh.Name.setText(data.get(position));
            MyListener3 m3=new MyListener3(data2.get(data.get(position)));
            vh.Name.setOnClickListener(m3);
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder{
            TextView Name;
            public ViewHolder (View view)
            {
                super(view);
                Name=(TextView)view.findViewById(R.id.item1Name);
            }
        }
        class MyListener3 implements View.OnClickListener {
            String id;
            public MyListener3(String id){
                this.id=id;
            }
            @Override
            public void onClick(View p1) {
                Intent intent=new Intent(Search.this,PlayingService.class);
                intent.putExtra(playid,id);
                intent.putExtra(playflag,FLAG_SOLO);
                startService(intent);
            }
        }
    }
    public static String getURLkw(String kw){
        try{
            byte[] s1=kw.getBytes("UTF-8");
            String result="";
            for (byte b:s1){
                if(b>=0){
                    if(b==32){
                        result+="%20";
                        continue;
                    }
                    result+=new String(new byte[]{b});
                    continue;
                }
                result+="%"+Integer.toHexString(b).substring(6,8);
            }
            return result;
        }catch (UnsupportedEncodingException e){
            return null;
        }
    }
    private void Checked(final String songname, final Handler handler, final RecyclerView mRecyclerView){
        Thread t=new Thread(){
            @Override
            public void run() {
                final KeyboardLayout kl=findViewById(R.id.keyboradlayout1);
                String urlPath=SearchSong.replace("\\search\\",getURLkw(songname));
                try {
                    InputStream is =new URL(urlPath).openStream();
                    GJson json=new GJson(is);
                    GJson.ValueClass songClass=json.getRoot().ac.get(0).getSon("result").getSon("songs");
                    if(json.getRoot().ac.get(0).getSon("result").getSon("songs")==null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Map<String,String> m=new HashMap<>();
                                m.put("", "");
                                MyAdapter ma = new MyAdapter(Search.this, m);
                                ma.notifyDataSetChanged();
                                mRecyclerView.setAdapter(ma);
                            }
                        });
                        return;
                    }
                    Map<String ,String > map=new HashMap<>();
                    for (GJson.ValueClass song:songClass.ac){
                        String name=song.getString("name");
                        String id=song.getString("id");
                        map.put(name,id);
                    }
                    final MyAdapter myAdapter=new MyAdapter(Search.this,map);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                            mRecyclerView.setAdapter(myAdapter);
                        }
                    });
                } catch (Exception e) {
                    Log.e("e",e.toString(),e);
                }
            }
        };
        t.start();
    }
/**
*@author Gking
*@version 1
*@time 2020.6.14 08:53
*@return null*/
//private static Map<String,String> midmap=GLists;
//    class MyAdapter extends ArrayAdapter<String> {
}
