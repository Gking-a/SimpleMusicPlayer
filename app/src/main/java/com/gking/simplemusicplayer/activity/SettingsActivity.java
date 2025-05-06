/*
 */

package com.gking.simplemusicplayer.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.util.MyCookies;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import cn.gking.gtools.GDataBase;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.*;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.output;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        RecyclerView recyclerView=findViewById(R.id.activity_settings_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Item> data= new ArrayList<>();
        data.add(new ItemEdit("选中的歌词的颜色",window_color, Integer.toHexString(getWindowColor())));
        data.add(new ItemButton("开启悬浮窗权限",null,"开启"){
            @Override
            public void execute(Object o) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
        });
        data.add(new ItemEdit("下载音乐文件夹（需要自己给读写权限）",local_download,getString(local_download)));
        data.add(new ItemSwitch("自动开启悬浮歌词",zdkqxfgc,Boolean.parseBoolean(get(zdkqxfgc))));
        data.add(new ItemEdit("悬浮窗歌词文字大小",xfcgcwzdx,get(xfcgcwzdx)));
        data.add(new ItemButton("申请权限",null,"申请"){
            @Override
            public void execute(Object o) {
                RequestPermissions(SettingsActivity.this, Manifest.permission_group.STORAGE);
            }
        });
        MyAdapter myAdapter = new MyAdapter(this, data);
        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }
    public static abstract class Item<V>{
        public static final int TYPE_SWITCH=0;
        public static final int TYPE_EDIT=1;
        public static final int TYPE_BUTTON=2;
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

        public Item(int type, String text, String sign, V value) {
            this.type = type;
            this.text = text;
            this.sign = sign;
            this.value = value;
        }

        private Item(int type, String text, String sign) {
            this.type = type;
            this.text = text;
            this.sign = sign;
        }
        public abstract void execute(Object o);
    }
    public static class ItemEdit extends Item<String>{
        public ItemEdit(String text, String sign,String v) {
            super(TYPE_EDIT,text, sign);
            setValue(v);
        }
        @Override
        public void execute(Object o){
            SettingsActivity.library.add(sign,o.toString().trim());
        }
    }
    public static class ItemButton extends Item<String>{
        public ItemButton(String text, String sign,String v) {
            super(TYPE_BUTTON,text, sign);
            setValue(v);
        }
        public void execute(Object o){}
    }
    public static class ItemSwitch extends Item<Boolean>{
        public ItemSwitch(String text, String sign, Boolean value) {
            super(TYPE_SWITCH,text,sign, value);
        }
        @Override
        public void execute(Object o) {
            SettingsActivity.set(sign, o);
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
            else if(viewType==Item.TYPE_BUTTON)view=inflater.inflate(R.layout.item_button,parent,false);
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
                        itemEdit.execute(s);
                    }
                });
            }
            if(item instanceof ItemButton) {
                ItemButton itemButton = (ItemButton) item;
                holder.button.setText(itemButton.getValue());
                holder.button.setOnClickListener(v -> itemButton.execute(null));
            }
            if (item instanceof ItemSwitch) {
                ItemSwitch itemSwitch = (ItemSwitch) item;
                Boolean value = itemSwitch.getValue();
                holder.switchMaterial.setChecked(value);
                holder.switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        itemSwitch.execute(isChecked);
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
            Button button;
            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                textView=itemView.findViewById(R.id.item_title);
                editText=itemView.findViewById(R.id.item_edittext);
                switchMaterial=itemView.findViewById(R.id.item_switch);
                button=itemView.findViewById(R.id.item_button);
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            library.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static final int getWindowColor(){
        try {
            BigInteger bigInteger=new BigInteger(get(window_color),16);
            return bigInteger.intValue();
        }catch (Exception e){
            e.printStackTrace();
            return 0xffff0000;
        }
    }
    public static final String DEFAULT_LIST ="defaultlist";
    public static final String LOCKEDNOTIFICATIONSHOW="lockednotificationshow";
    public static final String DEFAULT_WINDOW_SHOW="defaultwindow";
    public static final File SettingsFile =new File("/data/user/0/com.gking.simplemusicplayer/files/Settings");
    private static int ver;
    public static GDataBase library;
    public static String get(String key){
        return library.getString(key);
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
        library.add(key,v);
        try {
            library.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static{
        File file = new File("/data/user/0/com.gking.simplemusicplayer/files");
        if(!file.exists())file.mkdirs();
        if (!SettingsFile.exists()) {
            try {
                SettingsFile.createNewFile();

            } catch (IOException e) {
            }
        }
        library = new GDataBase(SettingsFile);
        if(library.getString(auto_next)==null) {
//				lib.create(true);
//                library.connect();
            library.add(auto_next, true);
        }
        if(library.getString(play_mode)==null) {
            library.add(play_mode, SettingsActivity.Params.PLAY_MODE.RANDOM);
        }
        if(library.getString(window_color)==null) {
            library.add(window_color, Integer.toHexString(0xFF00ff00));
        }
        if(library.getString("ver")==null) {
            library.add("ver", "1.2");
        }
//                library.add(account_phone,"18263610381");
//                library.add(account_pw,"gking1980");
        if(library.getString(xfcgcwzdx)==null) {
            library.add(xfcgcwzdx, "16");
        }
        File music = new File(file, "music");
        if(!music.exists()) {
            music.mkdirs();
        }
        if(library.getString(local_download)==null) {
            library.add(local_download, music.getAbsolutePath());
        }
        library.save();
            //GFileUtil.CopyFile("/sdcard/SETTINGS",_SETTINGS);
    }
    public static final class Params{
        public static final String account_name = "account_name";
        public static final String account_id = "account_id";
        public static final String account_pw = "account_pw";
        public static final String account_phone = "account_phone";
        public static final String auto_next="auto_next";
        public static final String play_mode="play_mode";
        public static final String window_color="window_color";
        public static final String local_download="local_download";
        public static final String login_fetch_cookies ="login_fetch_cookies";
        public static final String __csrf ="__csrf",MUSIC_A_T="MUSIC_A_T",MUSIC_R_T="MUSIC_R_T",NMTID="NMTID",MUSIC_U="MUSIC_U";
        //悬浮歌词文字大小
        public static final String xfcgcwzdx="xfcgcwzdx";
        //自动开启悬浮歌词
        public static final String zdkqxfgc="zdkqxfgc";
        public static final String cookie="cookie";
        public static final class PLAY_MODE{
            public static final String NONE="0";
            public static final String LOOP="1";
            public static final String RANDOM="2";
            public static final String ORDER="3";
        }
    }
    /**
     * 动态申请权限
     * @param context    上下文
     * @param permission 要申请的一个权限，列如写的权限：Manifest.permission.WRITE_EXTERNAL_STORAGE
     * @return  是否有当前权限
     */

    private boolean RequestPermissions(@NonNull Context context, @NonNull String permission) {
        if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            output("requestMyPermissions",": 【 " + permission + " 】没有授权，申请权限");
            this.requestPermissions(new String[]{permission}, 100);
            return false;
        } else {
            output("requestMyPermissions",": 【 " + permission + " 】有权限");
            return true;
        }

//————————————————
//    版权声明：本文为CSDN博主「仟易柴君」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/ERP_LXKUN_JAK/article/details/108265128
    }
    public static boolean loadCookie(){
        if(library.getString(__csrf)!=null&&!library.getString(__csrf).trim().equals("")){
            MyCookies.__csrf=library.getString(__csrf);
            MyCookies.MUSIC_U=library.getString(MUSIC_U);
            MyCookies.NMTID=library.getString(NMTID);
            MyCookies.MUSIC_A_T=library.getString(MUSIC_A_T);
            MyCookies.MUSIC_R_T=library.getString(MUSIC_R_T);
            MyCookies.init=true;
        }
        return false;
    }
}
