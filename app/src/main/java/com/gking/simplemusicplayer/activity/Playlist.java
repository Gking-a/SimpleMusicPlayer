package com.gking.simplemusicplayer.activity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gtools.managers.GHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Playlist extends BaseActivity {
    MyHandler myHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        setContext(this);
        RecyclerView recyclerView=f(R.id.songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String id = getIntent().getStringExtra("id");
        JsonObject playlist = (JsonObject) GHolder.standardInstance.get(id);
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
                            if (!holder.getIds().contains(id)) {
                                holder.add(id, song);
                                Bitmap cover= BitmapFactory.decodeStream(new URL(JsonUtil.getAsString(playlist,"al","picUrl")+"?param=50y50").openStream());
                                ((MyApplicationImpl) getApplication()).getSongCover().add(id,cover);
                            }
                        }
                        Message message = new Message();
                        message.what = MyHandler.UPDATE_UI;
                        message.obj=ids;
                        myHandler.sendMessage(message);
                    }
                });
            }
        });
    }

    class MyHandler extends Handler {
        public static final int UPDATE_UI = 0;
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    RecyclerView recyclerView = f(R.id.songs);
                    MyAdapter adapter=new MyAdapter(getContext(), (List<String>) msg.obj);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH> {
        List<String> content;
        Context context;

        public MyAdapter(Context context, List<String> content) {
            this.content = content;
            this.context = context;
        }
        @NonNull
        @NotNull
        @Override
        public MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.song_item, null);
            return new MyVH(v);
        }
        @Override
        public void onBindViewHolder(@NonNull @NotNull MyVH myVH, int position) {
            String id = content.get(position);
            JsonObject song= ((MyApplicationImpl) getApplication()).getSongInfo().get(id);
            myVH.Layout.setOnClickListener(v-> ((MyApplicationImpl) getApplication()).getMusicPlayer().start(id,null));
            myVH.Name.setText(JsonUtil.getAsString(song,"name"));
            myVH.Cover.setImageBitmap(((MyApplicationImpl) getApplication()).getSongCover().get(id));
            String au;
            {
                StringBuilder sb=new StringBuilder();
                JsonArray ar = JsonUtil.getAsJsonArray(song, "ar");
                for (int i = 0; i < ar.size(); i++) {
                    sb.append(ar.get(i).getAsJsonObject().get("name").getAsString()).append("/");
                }
                au=sb.substring(0,sb.length()-1);
            }
            myVH.Author.setText(au);
            myVH.More.setOnClickListener(v->{
                MyFunction fun = new MyFunction();
                fun.moreOnClick();
            });
        }
        @Override
        public int getItemCount() {
            return content.size();
        }

        class MyVH extends RecyclerView.ViewHolder {
            ConstraintLayout Layout;
            TextView Name,Author;
            MaterialButton More;
            RoundedImageView Cover;
            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                Name = itemView.findViewById(R.id.song_name);
                Author=itemView.findViewById(R.id.song_author);
                More=itemView.findViewById(R.id.song_more);
                Cover=itemView.findViewById(R.id.song_cover);
                Layout = itemView.findViewById(R.id.song_item_layout);
            }
        }
    }
}