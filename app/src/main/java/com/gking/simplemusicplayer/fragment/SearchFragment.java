package com.gking.simplemusicplayer.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.gking.simplemusicplayer.base.BaseViewPagerFragment;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.manager.SongManager;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hz.android.keyboardlayout.KeyboardLayout;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;
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
        View view = LayoutInflater.from(activity).inflate(R.layout.activity_main_search, null);
        EditText search_et = view.findViewById(R.id.main_search_search);
        ConstraintLayout constraintLayout = view.findViewById(R.id.main_search_show);
        TabLayout tabLayout = view.findViewById(R.id.main_search_tab);
        ViewPager viewPager = view.findViewById(R.id.main_search_viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new MyOnTabSelectedListener());
        KeyboardLayout keyboardLayout = view.findViewById(R.id.main_search_keyboard);
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
        }

        @Override
        public int getItemCount() {
            return playlists.size();
        }

        class MyVH extends RecyclerView.ViewHolder {

            public final TextView title;
            public final ImageView icon;

            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.list_small_icon);
                title = itemView.findViewById(R.id.list_small_title);
            }
        }
    }
    static class MySongAdapter extends RecyclerView.Adapter<MySongAdapter.MyVH> {
        List<SongBean> content;
        MainActivity activity;
        String playlistId;
        public MySongAdapter(MainActivity activity, List<SongBean> content,String playlistId) {
            this.activity = activity;
            this.content = content;
            this.playlistId=playlistId;
            SongManager.getInstance().setPointer(SongManager.getInstance().songs);
            SongManager.getInstance().randomSort();
        }

        @NonNull
        @NotNull
        @Override
        public MySongAdapter.MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.song_item, parent, false);
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
            myVH.More.setOnClickListener(v -> {
                String[] items = new String[]{"分享", "查看统计"};
                BottomMenu.show(items)
                        .setOnIconChangeCallBack(new OnIconChangeCallBack(true) {
                            @Override
                            public int getIcon(BottomMenu bottomMenu, int index, String s) {
                                switch (index) {
                                    case 0:
                                        return android.R.mipmap.sym_def_app_icon;
                                    case 1:
                                        return android.R.mipmap.sym_def_app_icon;
                                }
                                return 0;
                            }
                        })
                        .setOnMenuItemClickListener((bottomMenu, text, index) -> {
                            switch (index) {
                                case 0:
                                    ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    cm.setPrimaryClip(ClipData.newPlainText("Label", "https://music.163.com/#/song?id=" + id));
                                    activity.makeToast("链接已经复制到剪切板");
                                case 1:
                                    activity.makeToast("尚未开始研发");
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
                    SongBean bean = new SongBean(song);
                    SongManager.getInstance().addSong(bean);
                    music.add(bean);
                }
                MainActivity activity=getContext();
                activity.handler.post(() -> {
                    RecyclerView recyclerView = recyclerViews.get(0);
                    MySongAdapter mySongAdapter = new MySongAdapter(activity, music,"s"+keyword);
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
                    MyPlaylistAdapter myPlaylistAdapter = new MyPlaylistAdapter(activity, playlistBeans);
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
                WebRequest.cloudsearch(keyword, 1, MyCookieJar.getLoginCookie(), songCallBack);
            }
            if (position == 1) {
                WebRequest.cloudsearch(keyword, 1000, MyCookieJar.getLoginCookie(), playlistCallBack);
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
