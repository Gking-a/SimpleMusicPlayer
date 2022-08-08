package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.dialog.PlaylistCreateDialog;
import com.gking.simplemusicplayer.dialog.PlaylistDialog1;
import com.gking.simplemusicplayer.util.Cookies;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.account_id;

public class ChoosePlaylistActivity extends BaseActivity {
    public static final int RequestCode=1001;
    SongBean songBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_playlist);
        if (getIntent() != null&&getIntent().getSerializableExtra("song")!=null) {
            songBean= ((SongBean) getIntent().getSerializableExtra("song"));
        }
        loadView();
        setResult(RequestCode,new Intent());
        WebRequest.user_playlist(SettingsActivity.get(account_id), Cookies.getLoginCookie(),getGetPlaylistCallback());
    }
    Toolbar.OnMenuItemClickListener onMenuItemClickListener=new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.main_save_playlist_position) {
                WebRequest.playlist_order_update1(myAdapter.playlists, Cookies.getLoginCookie(), null);
            }else if(itemId==R.id.main_playlist_create){
                PlaylistCreateDialog playlistCreateDialog=new PlaylistCreateDialog(getContext());
                playlistCreateDialog.show();
                playlistCreateDialog.setSimpleInterface(arg -> {
                    WebRequest.user_playlist(SettingsActivity.get(account_id), Cookies.getLoginCookie(),getGetPlaylistCallback());
                });
            }
            return false;
        }
    };
    Callback getPlaylistCallback;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    public Callback getGetPlaylistCallback() {
        return getPlaylistCallback;
    }
    private void loadView() {
        Toolbar toolbar = findViewById(R.id.activity_choose_playlist_toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        recyclerView = findViewById(R.id.activity_choose_playlist_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getPlaylistCallback= new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                JsonArray jsonArray = JsonParser.parseString(body).getAsJsonObject().getAsJsonArray("playlist");
                List<PlaylistBean> playlistBeans = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject playlist = jsonArray.get(i).getAsJsonObject();
                    String uid = playlist.getAsJsonObject("creator").get("userId").getAsString();
                    if (uid.equals(SettingsActivity.get(account_id))) {
                        PlaylistBean playlistBean = new PlaylistBean(playlist);
                        playlistBeans.add(playlistBean);
                    }
                }
                getContext().handler.post(()->{
                    myAdapter = new MyAdapter(ChoosePlaylistActivity.this, playlistBeans);
                    recyclerView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                });
            }
        };
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH> {
        public List<PlaylistBean> playlists;
        MyItemTouchHelperCallback callback = new MyItemTouchHelperCallback();
        ItemTouchHelper itemTouchHelper;
        private BaseActivity activity;
        int res_layout=R.layout.playlist_horizontal;
        public MyAdapter(MainActivity activity, List<PlaylistBean> playlists,int res_layout){
            this(activity,playlists);
            this.res_layout=res_layout;
        }
        public MyAdapter(BaseActivity activity, List<PlaylistBean> playlists) {
            this.playlists = playlists;
            itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            this.activity = activity;
        }

        class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
            @Override
            public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                int flag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                return makeMovementFlags(flag, 0);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder source, @NonNull @NotNull RecyclerView.ViewHolder target) {
                int start = source.getAdapterPosition();
                int end = target.getAdapterPosition();
                Collections.swap(playlists, start, end);
                notifyItemMoved(start, end);
                return true;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }

        @NonNull
        @NotNull
        @Override
        public MyAdapter.MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity.getContext()).inflate(R.layout.playlist_horizontal, parent, false);
            return new MyAdapter.MyVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MyAdapter.MyVH holder, int position) {
            PlaylistBean playlistBean = playlists.get(position);
            holder.title.setText(playlistBean.name);
            Util.getCover(playlistBean.coverImgUrl, bitmap -> activity.handler.post(() -> holder.icon.setImageBitmap(bitmap)));
            View.OnClickListener onClickListener = v -> {
                Intent intent = new Intent(activity.getContext(), PlaylistActivity.class);
                intent.putExtra("playlistBean", playlistBean);
                intent.putExtra("success",true);
                intent.putExtra("songBean",songBean);
                setResult(RequestCode,intent);
                finish();
            };
            holder.icon.setOnClickListener(onClickListener);
            holder.title.setOnClickListener(onClickListener);
            holder.more.setOnClickListener(getOnMoreClickListener(playlistBean));
        }
        public View.OnClickListener getOnMoreClickListener(PlaylistBean bean){
            return v -> {
                PlaylistDialog1 playlistDialog1 = new PlaylistDialog1(getContext(),getPlaylistCallback);
                playlistDialog1.show(bean);
            };
        }
        @Override
        public int getItemCount() {
            return playlists.size();
        }

        class MyVH extends RecyclerView.ViewHolder {

            public final TextView title;
            public final ImageView icon;
            private final ImageButton more;

            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.list_small_icon);
                title = itemView.findViewById(R.id.list_small_title);
                more = itemView.findViewById(R.id.playlist_more);
            }
        }
    }
}