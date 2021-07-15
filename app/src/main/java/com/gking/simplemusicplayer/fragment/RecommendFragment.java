package com.gking.simplemusicplayer.fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.Async;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.MainActivity;
import com.gking.simplemusicplayer.base.BaseViewPagerFragment;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendFragment extends BaseViewPagerFragment<MainActivity> {

    private RecyclerView songsView;
    private RecyclerView playlistsView;

    public RecommendFragment(MainActivity activity) {
        super(activity);
    }
    @Async
    public void update() {
        WebRequest.recommend_songs(MyCookieJar.getLoginCookie(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                JsonArray jsonArray=JsonUtil.getAsJsonArray(JsonParser.parseString(body).getAsJsonObject(),"data","dailySongs");
                ArrayList<SongBean> songBeans = new ArrayList<>(10);
                for (int i = 0; i < 10; i++) {
                    songBeans.add(new SongBean(jsonArray.get(i).getAsJsonObject()));
                }
                Handler handler = getContext().handler;
                handler.post(() -> {
                    SearchFragment.MySongAdapter mySongAdapter=new SearchFragment.MySongAdapter(getContext(),songBeans);
                    songsView.setAdapter(mySongAdapter);
                    mySongAdapter.notifyDataSetChanged();
                });
            }
        });
        WebRequest.recommend_resource(MyCookieJar.getLoginCookie(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                JsonArray jsonArray = JsonUtil.getAsJsonArray(JsonParser.parseString(body).getAsJsonObject(), "recommend");
                ArrayList<PlaylistBean> playlistBeans = new ArrayList<>(9);
                for (int i = 0; i < jsonArray.size(); i++) {
                    playlistBeans.add(new PlaylistBean(jsonArray.get(i).getAsJsonObject()));
                }
                Handler handler = getContext().handler;
                handler.post(() -> {
                   SearchFragment.MyPlaylistAdapter myPlaylistAdapter=new SearchFragment.MyPlaylistAdapter(getContext(),playlistBeans);
                   playlistsView.setAdapter(myPlaylistAdapter);
                   myPlaylistAdapter.notifyDataSetChanged();
                });
            }
        });
    }
    @Override
    protected View loadView() {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.activity_main_recommend,null);
        songsView = view.findViewById(R.id.main_recommend_songs);
        songsView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        playlistsView = view.findViewById(R.id.main_recommend_playlists);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        playlistsView.setLayoutManager(gridLayoutManager);
        return view;
    }
}
