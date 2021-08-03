/*
 */

package com.gking.simplemusicplayer.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.gking.gtools.GLibrary;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.*;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        RecyclerView recyclerView=findViewById(R.id.activity_settings_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Item> data= new ArrayList<>();
        data.add(new ItemEdit("选中的歌词的颜色",window_color,SettingsActivity.get(window_color)));
        MyAdapter myAdapter = new MyAdapter(this, data);
        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }
    public static class Item<V>{
        public static final int TYPE_SWITCH=0;
        public static final int TYPE_EDIT=1;
        public int type;
        public String text;
        public String sign;
        public V getValue() {
            return value;
        }
        public void setValue(V value) {
            this.value = value;
        }
        public V value;
        public Item(String text, String sign,V value) {
            this.text = text;
            this.sign = sign;
            setValue(value);
        }
        private Item(int type, String text, String sign) {
            this.type = type;
            this.text = text;
            this.sign = sign;
        }
    }
    public static class ItemEdit extends Item<String>{
        public ItemEdit(String text, String sign,String v) {
            super(TYPE_EDIT,text, sign);
            setValue(v);
        }
    }
    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH>{
        List<Item> data;
        Context context;
        public MyAdapter(Context context,List<Item> data) {
            this.data = data;
            this.context = context;
        }
        @NonNull
        @NotNull
        @Override
        public MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view=null;
            LayoutInflater inflater = LayoutInflater.from(context);
            if(viewType==Item.TYPE_SWITCH)view= inflater.inflate(R.layout.item_switch,parent,false);
            else if(viewType==Item.TYPE_EDIT)view=inflater.inflate(R.layout.item_edit,parent,false);
            return new MyVH(view);
        }
        @Override
        public void onBindViewHolder(@NonNull @NotNull MyVH holder, int position) {
            Item item=data.get(position);
            holder.textView.setText(item.text);
            if(item instanceof ItemEdit){
                ItemEdit itemEdit=(ItemEdit)item;
                holder.editText.setText(itemEdit.value);
                holder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count){}
                    @Override
                    public void afterTextChanged(Editable s) {
                        SettingsActivity.library.add(item.sign,s.toString().trim(),GLibrary.TYPE_STRING);
                    }
                });
            }
        }
        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            return data.get(position).type;
        }

        class MyVH extends RecyclerView.ViewHolder{
            TextView textView;
            EditText editText;
            SwitchMaterial switchMaterial;
            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                textView=itemView.findViewById(R.id.item_title);
                editText=itemView.findViewById(R.id.item_edittext);
                switchMaterial=itemView.findViewById(R.id.item_switch);
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            library.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static final String DEFAULT_LIST ="defaultlist";
    public static final String LOCKEDNOTIFICATIONSHOW="lockednotificationshow";
    public static final String WINDOW_COLOR="windowcolor";
    public static final String DEFAULT_WINDOW_SHOW="defaultwindow";
    public static final File SettingsFile =new File("/data/user/0/com.gking.simplemusicplayer/files/Settings");
    private static int ver;
    public static GLibrary library;
    public static String get(String key){
        return library.get(key);
    }
    public static boolean getBoolean(String symbol){
        return Boolean.parseBoolean(get(symbol));
    }
    public static int getInt(String symbol){
        return Integer.parseInt(get(symbol));
    }
    public static long getLong(String symbol){
        return Long.parseLong(get(symbol));
    }
    public static String getString(String symbol){
        return get(symbol);
    }
    public static void set(String key,Object v){
        library.add(key,v,GLibrary.TYPE_STRING);
        try {
            library.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static{
        if (!SettingsFile.exists()) {
            try {
                SettingsFile.createNewFile();
                library = new GLibrary(SettingsFile.getName(), SettingsFile);
//				lib.create(true);
                library.connect();
                library.add(auto_next, true, GLibrary.TYPE_STRING);
                library.add(play_mode, SettingsActivity.Params.PLAY_MODE.RANDOM, GLibrary.TYPE_STRING);
                library.add(window_color, 0xffFF0000, GLibrary.TYPE_STRING);
                library.add("ver", 1, GLibrary.TYPE_STRING);
                library.save();
                //GFileUtil.CopyFile("/sdcard/SETTINGS",_SETTINGS);
            } catch (IOException e) {
            }
        }
        library = new GLibrary(SettingsFile, true);
    }
    public static final class Params{
        public static final String account_name = "account_name";
        public static final String account_id = "account_id";
        public static final String account_pw = "account_pw";
        public static final String account_phone = "account_phone";
        public static final String auto_next="auto_next";
        public static final String play_mode="play_mode";
        public static final String window_color="window_color";
        public static final class PLAY_MODE{
            public static final String NONE="0";
            public static final String LOOP="1";
            public static final String RANDOM="2";
            public static final String ORDER="3";
        }
    }
}
