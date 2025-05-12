package com.gking.simplemusicplayer.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.MainActivity;
import com.gking.simplemusicplayer.activity.PlaylistActivity;
import com.gking.simplemusicplayer.activity.SongActivity;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.base.BaseViewPagerFragment;
import com.gking.simplemusicplayer.dialog.PlaylistDialog3;
import com.gking.simplemusicplayer.dialog.SongDialog2;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.beans.PlaylistBean;
import com.gking.simplemusicplayer.beans.SongBean;
import com.gking.simplemusicplayer.beans.SongManager;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hz.android.keyboardlayout.KeyboardLayout;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchFragment extends BaseViewPagerFragment<MainActivity> {
    static final String[] title = new String[]{"歌曲", "歌单"};
    static final int num = title.length;
    static String[] keywords = new String[num];
    static String keyword;
    List<RecyclerView> recyclerViews;
    public SearchFragment(MainActivity activity) {
        super(activity);
    }

    @Override
    protected View loadView() {
        MainActivity activity=getContext();
        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_search, null);
        EditText search_et = view.findViewById(R.id.fragment_search_search);
        ConstraintLayout constraintLayout = view.findViewById(R.id.fragment_search_show);
        TabLayout tabLayout = view.findViewById(R.id.fragment_search_tab);
        ViewPager viewPager = view.findViewById(R.id.fragment_search_viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new MyOnTabSelectedListener());
        KeyboardLayout keyboardLayout = view.findViewById(R.id.fragment_search_keyboard);
        keyboardLayout.setKeyboardLayoutListener(new KeyboardLayout.KeyboardLayoutListener() {
            boolean last = false;
            @Override
            public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {
                if (isActive == last) return;
                last = isActive;
                if (isActive) {
                    constraintLayout.setVisibility(View.GONE);
                } else {
                    SearchFragment.keyword = search_et.getText().toString();
                    constraintLayout.setVisibility(View.VISIBLE);
                    tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
                }
            }
        });
        recyclerViews = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            RecyclerView recyclerView = ((RecyclerView) View.inflate(activity, R.layout.recycler_view, null));
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerViews.add(recyclerView);
        }
        return view;
    }

    class MyViewPagerAdapter extends PagerAdapter {
        @NonNull
        @NotNull
        @Override
        public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
            View mView = recyclerViews.get(position);
            container.addView(mView);
            return mView;
        }

        @Override
        public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
            container.removeView(((View) object));
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return title[position];
            if (position == 1) return title[position];
            return null;
        }

        @Override
        public int getCount() {
            return num;
        }

        @Override
        public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
            return view == object;
        }
    }
    static class MyPlaylistAdapter extends RecyclerView.Adapter<MyPlaylistAdapter.MyVH> {
        List<PlaylistBean> playlists;
        MainActivity activity;
        public MyPlaylistAdapter(MainActivity activity, List<PlaylistBean> playlists) {
            this.activity = activity;
            this.playlists = playlists;
        }
        @NonNull
        @NotNull
        @Override
        public MyPlaylistAdapter.MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.playlist_horizontal, parent, false);
            return new MyPlaylistAdapter.MyVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MyPlaylistAdapter.MyVH holder, int position) {
            PlaylistBean bean = playlists.get(position);
            holder.title.setText(bean.name);
            Util.getCover(bean.coverImgUrl, bitmap -> activity.handler.post(() -> holder.icon.setImageBitmap(bitmap)));
            View.OnClickListener onClickListener = v -> {
                Intent intent = new Intent(activity, PlaylistActivity.class);
                intent.putExtra("bean", bean);
                activity.startActivity(intent);
            };
            holder.icon.setOnClickListener(onClickListener);
            holder.title.setOnClickListener(onClickListener);
            holder.more.setOnClickListener(getOnMoreClickListener(bean));
        }
        public View.OnClickListener getOnMoreClickListener(PlaylistBean bean){
            return null;
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
    public static class MySongAdapter extends RecyclerView.Adapter<MySongAdapter.MyVH> {
        public List<SongBean> content;
        BaseActivity activity;
        String playlistId;
        public void setData(List<SongBean> data){
            content=data;
        }
        public MySongAdapter(BaseActivity activity, List<SongBean> content, String playlistId) {
            this.activity = activity;
            this.content = content;
            this.playlistId=playlistId;
            SongManager.getInstance().set(playlistId,content);
            SongManager.getInstance().setPointer(SongManager.getInstance().songs);
            SongManager.getInstance().randomSort();
        }
        @NonNull
        @NotNull
        @Override
        public MySongAdapter.MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.song_horizontal, parent, false);
            return new MySongAdapter.MyVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MySongAdapter.MyVH myVH, int position) {
            String id = content.get(position).id;
            SongBean song = content.get(position);
            myVH.Name.setText(song.name);
            Util.getCover(song.coverUrl, bitmap -> {
                activity.handler.post(() -> myVH.Cover.setImageBitmap(bitmap));
            });
            View.OnClickListener onClickListener = v -> {
                SongManager.getInstance().set(playlistId,content);
                ((MyApplicationImpl) activity.getApplication()).getMusicPlayer().start(song, null);
                Intent intent = new Intent(activity, SongActivity.class);
                intent.putExtra("bean", song);
                activity.startActivity(intent);
//                myHandler.post(() -> ((MyApplicationImpl) getApplication()).setControlInfo(id,JsonUtil.getAsString(song,"name"),au,myHandler));
            };
            myVH.Author.setText(song.author);
            myVH.Root.setOnClickListener(onClickListener);
            myVH.Name.setOnClickListener(onClickListener);
            myVH.Author.setOnClickListener(onClickListener);
            myVH.Cover.setOnClickListener(onClickListener);
            myVH.More.setOnClickListener(getOnMoreClickListener(song,playlistId));
        }
        public View.OnClickListener getOnMoreClickListener(SongBean songBean,String playlistId){
            return null;
        }
        @Override
        public int getItemCount() {
            return content.size();
        }

        class MyVH extends RecyclerView.ViewHolder {
            View Root;
            TextView Name, Author;
            MaterialButton More;
            RoundedImageView Cover;

            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                Name = itemView.findViewById(R.id.song_name);
                Author = itemView.findViewById(R.id.song_author);
                More = itemView.findViewById(R.id.song_more);
                Cover = itemView.findViewById(R.id.song_cover);
                Root = itemView.findViewById(R.id.song_item_layout);
            }
        }
    }
    class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        private Callback songCallBack = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                JsonArray songs = JsonUtil.getAsJsonArray(jsonObject, "result", "songs");
                List<SongBean> music = new ArrayList<>();
                for (int i = 0; i < songs.size(); i++) {
                    JsonObject song = songs.get(i).getAsJsonObject();
                    SongBean bean = new SongBean(null,song);
                    SongManager.getInstance().addSong(bean);
                    music.add(bean);
                }
                MainActivity activity=getContext();
                activity.handler.post(() -> {
                    RecyclerView recyclerView = recyclerViews.get(0);
                    MySongAdapter mySongAdapter = new MySongAdapter(activity, music,"s"+keyword){
                        @Override
                        public View.OnClickListener getOnMoreClickListener(SongBean songBean, String playlistId) {
                            return v -> new SongDialog2(getContext()).show(songBean);
                        }
                    };
                    recyclerView.setAdapter(mySongAdapter);
                    mySongAdapter.notifyDataSetChanged();
                });
            }
        };
        private Callback playlistCallBack = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                System.out.println(body);
                JsonArray jsonArray = JsonUtil.getAsJsonArray(JsonParser.parseString(body).getAsJsonObject(), "result", "playlists");
                List<PlaylistBean> playlistBeans = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    playlistBeans.add(new PlaylistBean(jsonArray.get(i).getAsJsonObject()));
                }
                MainActivity activity=getContext();
                activity.handler.post(() -> {
                    RecyclerView recyclerView = recyclerViews.get(1);
                    MyPlaylistAdapter myPlaylistAdapter = new MyPlaylistAdapter(activity, playlistBeans){
                        @Override
                        public View.OnClickListener getOnMoreClickListener(PlaylistBean bean) {
                            return v -> new PlaylistDialog3(getContext(),getContext().getPlaylistCallback).show(bean);
                        }
                    };
                    recyclerView.setAdapter(myPlaylistAdapter);
                    myPlaylistAdapter.notifyDataSetChanged();
                });
            }
        };

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            searchImpl(tab);
        }

        private void searchImpl(TabLayout.Tab tab) {
            int position = tab.getPosition();
            if (keyword == null || keyword.trim().equals("")) return;
            if (keyword.equals(keywords[position])) return;
            keywords[position] = keyword;
            if (position == 0) {
                WebRequest.cloudsearch(keyword, 1,  songCallBack);
            }
            if (position == 1) {
                WebRequest.cloudsearch(keyword, 1000,  playlistCallBack);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            searchImpl(tab);
        }
    }
}
