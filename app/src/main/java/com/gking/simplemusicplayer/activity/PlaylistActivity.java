package com.gking.simplemusicplayer.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.manager.SongManager;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import gtools.managers.GHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.impl.MyApplicationImpl.l;

public class PlaylistActivity extends BaseActivity {
    MyHandler myHandler = new MyHandler();
    Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        setContext(this);
        setLoadControlPanel(true);
        SongManager.getInstance().clear();
        String id = getIntent().getStringExtra("id");
        JsonObject playlist = (JsonObject) GHolder.standardInstance.get(id);
        load(playlist);
        WebRequest.playlist_detail(JsonUtil.getAsString(playlist, "id"), MyCookieJar.getLoginCookie(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                JsonObject object = JsonParser.parseString(body).getAsJsonObject();
                JsonArray trackIds = JsonUtil.getAsJsonArray(object, "playlist", "trackIds");
                List<String> ids = new ArrayList<>();
                for (int i = 0; i < trackIds.size(); i++) {
                    String id = JsonUtil.getAsString(trackIds.get(i).getAsJsonObject(), "id");
                    ids.add(id);
                }
                l(ids.size());
                WebRequest.song_detail(ids, MyCookieJar.getLoginCookie(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String body = response.body().string();
                        GHolder<String, JsonObject> holder = ((MyApplicationImpl) getApplication()).getSongInfo();
                        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                        JsonArray songs = JsonUtil.getAsJsonArray(jsonObject, "songs");
                        for (int i = 0; i < songs.size(); i++) {
                            JsonObject song = songs.get(i).getAsJsonObject();
                            String id = song.get("id").getAsString();
                            SongBean bean= new SongBean(song);
                            SongManager.getInstance().addSong(bean);
                            nameMap.put(JsonUtil.getAsString(song,"name"),bean);
                            music.add(bean);
                            if (!holder.getIds().contains(id)) {
                                holder.add(id, song);
                            }
                        }
                        Message message = new Message();
                        message.what = MyHandler.UPDATE_UI;
                        message.obj=music;
                        myHandler.sendMessage(message);
                    }
                });
            }
        });
    }
    List<SongBean> music=new LinkedList<>();
    boolean isSearching=false;
    RecyclerView songList;
    private void load(JsonObject playlist) {
        songList=f(R.id.songs);
        songList.setLayoutManager(new LinearLayoutManager(getContext()));
        songList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        Button back=f(R.id.playlist_toolbar_back);
        back.setOnClickListener(v->finish());
        TextView title=f(R.id.playlist_toolbar_title);
        title.setText(playlist.get("name").getAsString());
        EditText search=f(R.id.playlist_toolbar_search);
        MyTextWatcher watcher=new MyTextWatcher();
        search.addTextChangedListener(watcher);
        Button menu=f(R.id.playlist_toolbar_menu);
        PopupMenu popupMenu=new PopupMenu(getContext(),menu);
        View.OnClickListener l2=v-> popupMenu.show();
        View.OnClickListener l1= v -> {
            menu.setBackgroundResource(R.drawable.dots);
            search.setVisibility(View.GONE);
            watcher.cancel();
            menu.setOnClickListener(l2);
        };
        MenuInflater inflater=popupMenu.getMenuInflater();
        inflater.inflate(R.menu.playlist,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.playlist_menu_search){
                search.setVisibility(View.VISIBLE);
                watcher.start(search);
                menu.setOnClickListener(l1);
                menu.setBackgroundResource(R.drawable.close);
            }
            return false;
        });
        menu.setOnClickListener(l2);
    }

    class MyHandler extends Handler {
        public static final int UPDATE_UI = 0;
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    RecyclerView recyclerView = f(R.id.songs);
                    MyAdapter adapter=new MyAdapter(getContext(), (List<SongBean>) msg.obj);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH> {
        List<SongBean> content;
        Context context;
        public MyAdapter(Context context, List<SongBean> content) {
            this.content = content;
            this.context = context;
            SongManager.getInstance().setPointer(SongManager.getInstance().songs);
        }
        @NonNull
        @NotNull
        @Override
        public MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.song_item, parent,false);
            return new MyVH(v);
        }
        @Override
        public void onBindViewHolder(@NonNull @NotNull MyVH myVH, int position) {
            String id = content.get(position).id;
            JsonObject json= ((MyApplicationImpl) getApplication()).getSongInfo().get(id);
            SongBean song=content.get(position);
            myVH.Name.setText(song.name);
            Util.getCover(song.coverUrl, bitmap -> {
                handler.post(()->myVH.Cover.setImageBitmap(bitmap));
            });
            View.OnClickListener onClickListener= v -> {
                ((MyApplicationImpl) getApplication()).getMusicPlayer().start(song,null);
                Intent intent = new Intent(getContext(), SongActivity.class);
                intent.putExtra("bean",song);
                startActivity(intent);
//                myHandler.post(() -> ((MyApplicationImpl) getApplication()).setControlInfo(id,JsonUtil.getAsString(song,"name"),au,myHandler));
            };
            myVH.Author.setText(song.author);
            myVH.Root.setOnClickListener(onClickListener);
            myVH.Name.setOnClickListener(onClickListener);
            myVH.Author.setOnClickListener(onClickListener);
            myVH.Cover.setOnClickListener(onClickListener);
            myVH.More.setOnClickListener(v->{
                String[] items=new String[]{"分享","查看统计"};
                BottomMenu.show(items)
                        .setOnIconChangeCallBack(new OnIconChangeCallBack(true) {
                            @Override
                            public int getIcon(BottomMenu bottomMenu, int index, String s) {
                                switch (index){
                                    case 0:return android.R.mipmap.sym_def_app_icon;
                                    case 1:return android.R.mipmap.sym_def_app_icon;
                                }
                                return 0;
                            }})
                        .setOnMenuItemClickListener((bottomMenu, text, index) -> {
                            switch (index){
                                case 0:
                                    ClipboardManager cm= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    cm.setPrimaryClip(ClipData.newPlainText("Label","https://music.163.com/#/song?id="+id));
                                    makeToast("链接已经复制到剪切板");
                                case 1:
                                    makeToast("尚未开始研发");
                            }
                            return false;
                        });
            });
        }
        @Override
        public int getItemCount() {
            return content.size();
        }

        class MyVH extends RecyclerView.ViewHolder {
            View Root;
            TextView Name,Author;
            MaterialButton More;
            RoundedImageView Cover;
            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                Name = itemView.findViewById(R.id.song_name);
                Author=itemView.findViewById(R.id.song_author);
                More=itemView.findViewById(R.id.song_more);
                Cover=itemView.findViewById(R.id.song_cover);
                Root = itemView.findViewById(R.id.song_item_layout);
            }
        }
    }
    LinkedHashMap<String, SongBean> nameMap=new LinkedHashMap<>();
    public class MyTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            if(!isSearching)return;
            String text=s.toString();
            LinkedList<SongBean> beans=new LinkedList<>();
            for(String name:nameMap.keySet()){
                if(name.contains(text))beans.add(nameMap.get(name));
            }
            MyAdapter myAdapter=new MyAdapter(getContext(),beans);
            songList.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }
        public void cancel(){
            isSearching=false;
            MyAdapter myAdapter=new MyAdapter(getContext(),SongManager.getInstance().songs);
            songList.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }
        public void start(EditText et){
            isSearching=true;
            et.setText(et.getText());
        }
    }
}