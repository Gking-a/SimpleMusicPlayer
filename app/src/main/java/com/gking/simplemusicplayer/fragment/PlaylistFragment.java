package com.gking.simplemusicplayer.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.gking.simplemusicplayer.activity.MySettingsActivity;
import com.gking.simplemusicplayer.activity.PlaylistActivity;
import com.gking.simplemusicplayer.base.BaseViewPagerFragment;
import com.gking.simplemusicplayer.dialog.PlaylistCreateDialog;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.popup.PlaylistPopupWindow;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import okhttp3.Callback;

import static com.gking.simplemusicplayer.activity.MySettingsActivity.Params.account_id;

public class PlaylistFragment extends BaseViewPagerFragment<MainActivity> {
    public TabLayout playlist_tab;
    public RecyclerView playlistView1, playlistView2;
    public MyAdapter myAdapter, myAdapter2;
    Toolbar.OnMenuItemClickListener onMenuItemClickListener=new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.main_save_playlist_position) {
                if (playlist_tab.getSelectedTabPosition() == 0 && myAdapter != null && myAdapter.playlists.size() > 0) {
                    WebRequest.playlist_order_update1(myAdapter.playlists, MyCookieJar.getLoginCookie(), null);
                }
                if (playlist_tab.getSelectedTabPosition() == 1 && myAdapter2 != null && myAdapter2.playlists.size() > 0) {
                    WebRequest.playlist_order_update1(myAdapter2.playlists, MyCookieJar.getLoginCookie(), null);
                }
            }else if(itemId==R.id.main_playlist_create){
                PlaylistCreateDialog playlistCreateDialog=new PlaylistCreateDialog(getContext());
                playlistCreateDialog.show();
                playlistCreateDialog.setSimpleInterface(arg -> {
                    WebRequest.user_playlist(MySettingsActivity.get(account_id),MyCookieJar.getLoginCookie(),getGetPlaylistCallback());
                });
            }
            return false;
        }
    };
    public PlaylistPopupWindow popupWindow;

    public Callback getGetPlaylistCallback() {
        return getPlaylistCallback;
    }
    Callback getPlaylistCallback;
    public PlaylistFragment(MainActivity activity, Callback getPlaylistCallback) {
        super(activity);
        this.getPlaylistCallback=getPlaylistCallback;
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
            View view = LayoutInflater.from(activity.getContext()).inflate(R.layout.playlist_horizontal, parent, false);
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
            holder.more.setOnClickListener(v -> {
                popupWindow.showAtBottom(activity.f(R.id.main_viewpager),bean);
            });
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

    public void setAdapter(List<PlaylistBean> data1, List<PlaylistBean> data2) {
        MainActivity activity=getContext();
        activity.handler.post(() -> {
            myAdapter = new MyAdapter(activity, data1);
            playlistView1.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
            myAdapter2 = new MyAdapter(activity, data2);
            playlistView2.setAdapter(myAdapter2);
            myAdapter2.notifyDataSetChanged();
        });
    }
    @Override
    protected View loadView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_playlist, null);
        Toolbar toolbar = view.findViewById(R.id.main_playlist_toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
//        activity.setSupportActionBar(toolbar);
        playlistView1 = ((RecyclerView) View.inflate(getContext(), R.layout.recycler_view, null));
        playlistView1.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistView2 = (RecyclerView) View.inflate(getContext(), R.layout.recycler_view, null);
        playlistView2.setLayoutManager(new LinearLayoutManager(getContext()));
        playlist_tab = view.findViewById(R.id.main_playlist_type);
        ViewPager viewPager = view.findViewById(R.id.main_playlist_viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter(getContext()));
        playlist_tab.setupWithViewPager(viewPager);
        popupWindow = new PlaylistPopupWindow(getContext(),this);
        return view;
    }
}