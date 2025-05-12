package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.dialog.SongDialog1;
import com.gking.simplemusicplayer.interfaces.SongOperable;
import com.gking.simplemusicplayer.beans.PlaylistBean;
import com.gking.simplemusicplayer.beans.SongBean;
import com.gking.simplemusicplayer.beans.SongManager;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.fragment.SearchFragment.MySongAdapter;

public class PlaylistActivity extends BaseActivity implements SongOperable<BaseActivity>{
    public Handler handler = new Handler();
    private String playlistId;
    public Callback refreshPlaylistCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) { }
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
            WebRequest.song_detail(ids,  new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {}
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String body = response.body().string();
                    System.out.println(body);
                    JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                    JsonArray songs = JsonUtil.getAsJsonArray(jsonObject, "songs");
                    for (int i = 0; i < songs.size(); i++) {
                        JsonObject song = songs.get(i).getAsJsonObject();
                        String id = song.get("id").getAsString();
                        SongBean bean = new SongBean(playlistId,song);
                        nameMap.put(JsonUtil.getAsString(song, "name"), bean);
                        music.add(bean);
                    }
                    handler.post(() -> {
                        MyAdapter myAdapter = new MyAdapter(getContext(), music, playlistId);
                        songList.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
                    });
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        setContext(this);
        setLoadControlPanel(true);
        PlaylistBean playlistBean = ((PlaylistBean) getIntent().getSerializableExtra("bean"));
        playlistId = playlistBean.id;
        load(playlistBean);
        WebRequest.playlist_detail(playlistId,  refreshPlaylistCallback);
    }

    List<SongBean> music = new LinkedList<>();
    boolean isSearching = false;
    public RecyclerView songList;

    private void load(PlaylistBean playlist) {
        songList = f(R.id.playlist_songs);
        songList.setLayoutManager(new LinearLayoutManager(getContext()));
        songList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        Button back = f(R.id.playlist_toolbar_back);
        back.setOnClickListener(v -> finish());
        TextView title = f(R.id.playlist_toolbar_title);
        title.setText(playlist.name);
        EditText search = f(R.id.playlist_toolbar_search);
        MyTextWatcher watcher = new MyTextWatcher();
        search.addTextChangedListener(watcher);
        Button menu = f(R.id.playlist_toolbar_menu);
        PopupMenu popupMenu = new PopupMenu(getContext(), menu);
        View.OnClickListener l2 = v -> popupMenu.show();
        View.OnClickListener l1 = v -> {
            menu.setBackgroundResource(R.drawable.dots);
            search.setVisibility(View.GONE);
            watcher.cancel();
            menu.setOnClickListener(l2);
        };
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.playlist, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.playlist_menu_search) {
                search.setVisibility(View.VISIBLE);
                watcher.start(search);
                menu.setOnClickListener(l1);
                menu.setBackgroundResource(R.drawable.close);
            }
            if (item.getItemId() == R.id.playlist_random) {
                SongManager.getInstance().set(playlistId, ((MySongAdapter) songList.getAdapter()).content);
                int i = new Random().nextInt(SongManager.getInstance().randomSongs.size());
                SongBean songBean = SongManager.getInstance().randomSongs.get(i);
                Intent intent = new Intent(getContext(), SongActivity.class);
                intent.putExtra("bean", songBean);
                startActivity(intent);
            }
            if(item.getItemId()==R.id.playlist_menu_download){
                new Thread(()->{
                    for (SongBean songBean:((MySongAdapter) songList.getAdapter()).content){
                        String id = songBean.id;
                        Util.downloadSong(songBean);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            return false;
        });
        menu.setOnClickListener(l2);
    }

    LinkedHashMap<String, SongBean> nameMap = new LinkedHashMap<>();

    @Override
    public void onSongDelete(String pid,SongBean songBean) {
        WebRequest.playlist_detail(pid,  new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                handler.post(()->{
                    PlaylistActivity.MyAdapter adapter = (PlaylistActivity.MyAdapter) songList.getAdapter();
                    adapter.notifyItemRemoved(songBean);
                });
            }
        });
    }
    public class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            if (!isSearching) return;
            String text = s.toString();
            LinkedList<SongBean> beans = new LinkedList<>();
            for (String name : nameMap.keySet()) {
                if (name.contains(text)) beans.add(nameMap.get(name));
            }
            MyAdapter myAdapter = new MyAdapter(getContext(), beans, playlistId);
            songList.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }

        public void cancel() {
            isSearching = false;
            MyAdapter myAdapter = new MyAdapter(getContext(), SongManager.getInstance().songs, playlistId);
            songList.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }

        public void start(EditText et) {
            isSearching = true;
            et.setText(et.getText());
        }
    }
    public class MyAdapter extends MySongAdapter {
        public MyAdapter(BaseActivity activity, List<SongBean> content, String playlistId) {
            super(activity, content, playlistId);
        }
        public void notifyItemRemoved(SongBean songBean){
            for (int i = 0; i < super.content.size(); i++) {
                if(songBean.id.equals(super.content.get(i).id)){
                    super.content.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }
        @Override
        public View.OnClickListener getOnMoreClickListener(SongBean songBean, String playlistId) {
            return v -> new SongDialog1(PlaylistActivity.this).show(playlistId, songBean);
        }
    }
}