package com.gking.simplemusicplayer.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.MainActivity;
import com.gking.simplemusicplayer.activity.PlaylistActivity;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.util.Util;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PlaylistFragment {
    public TabLayout playlist_tab;
    public RecyclerView playlistView1, playlistView2;
    public MyAdapter myAdapter, myAdapter2;
    public MainActivity activity;
    private final View view;

    public View getView() {
        return view;
    }

    public PlaylistFragment(MainActivity activity) {
        this.activity = activity;
        view = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_playlist, null);
        Toolbar toolbar = view.findViewById(R.id.main_search_toolbar_layout);
        activity.setSupportActionBar(toolbar);
        playlistView1 = ((RecyclerView) View.inflate(getContext(), R.layout.recycler_view, null));
        playlistView1.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistView2 = (RecyclerView) View.inflate(getContext(), R.layout.recycler_view, null);
        playlistView2.setLayoutManager(new LinearLayoutManager(getContext()));
        playlist_tab = view.findViewById(R.id.main_playlist_type);
        ViewPager viewPager = view.findViewById(R.id.main_playlist_viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter(activity));
        playlist_tab.setupWithViewPager(viewPager);
    }

    class MyViewPagerAdapter extends PagerAdapter {
        private final MainActivity activity;

        public MyViewPagerAdapter(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
            View mView = null;
            if (position == 0) {
                mView = playlistView1;
            } else if (position == 1) {
                mView = playlistView2;
            }
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
            if (position == 0) return "我创建的";
            if (position == 1) return "我收藏的";
            return null;
        }

        @Override
        public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
            return view == object;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH> {
        public List<PlaylistBean> playlists;
        MyAdapter.MyItemTouchHelperCallback callback = new MyAdapter.MyItemTouchHelperCallback();
        ItemTouchHelper itemTouchHelper;
        private MainActivity activity;

        public MyAdapter(MainActivity activity, List<PlaylistBean> playlists) {
            this.playlists = playlists;
            itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(playlistView1);
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
            View view = LayoutInflater.from(activity.getContext()).inflate(R.layout.list_small2, parent, false);
            return new MyAdapter.MyVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MyAdapter.MyVH holder, int position) {
            PlaylistBean bean = playlists.get(position);
            holder.title.setText(bean.name);
            Util.getCover(bean.coverImgUrl, bitmap -> activity.handler.post(() -> holder.icon.setImageBitmap(bitmap)));
            View.OnClickListener onClickListener = v -> {
                Intent intent = new Intent(activity.getContext(), PlaylistActivity.class);
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

    public void setAdapter(List<PlaylistBean> data1, List<PlaylistBean> data2) {
        activity.handler.post(() -> {
            myAdapter = new MyAdapter(activity, data1);
            playlistView1.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
            myAdapter2 = new MyAdapter(activity, data2);
            playlistView2.setAdapter(myAdapter2);
            myAdapter2.notifyDataSetChanged();
        });
    }

    public MainActivity getContext() {
        return activity;
    }
}
