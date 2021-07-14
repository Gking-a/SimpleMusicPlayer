/*
 */

package com.gking.simplemusicplayer.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.LoginBean;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.manager.SongManager;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.JsonUtil;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Collections;
import java.util.List;

import gtools.util.GTimer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.MySettingsActivity.Params.*;
public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    MyHandler handler=new MyHandler();
    RecyclerView recentSongs;
    NavigationView nav;
    DrawerLayout drawerLayout;
    RecyclerView playlistView1,playlistView2;
    private MyAdapter myAdapter,myAdapter2;
    private View playlist,search;
    private TabLayout playlist_tab;
    private SearchFragment searchFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContext(this);
        setContentView(R.layout.activity_main);
        setLoadControlPanel(true);
        loadSystemSettings();
        loadView();
        loadUserSettings();
//        debug();
    }
    private void loadView() {
        drawerLayout=f(R.id.main_drawer);
        nav=f(R.id.nav);
        nav.setNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.login)startActivityForResult(new Intent(getContext(), LoginCellphoneActivity.class), LoginCellphoneActivity.RequestCode);
            return true;
        });
        TabLayout tabLayout=f(R.id.main_tab);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        ViewPager viewPager=f(R.id.main_viewpager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new MyViewPagerAdapter());
        loadPlaylistView();
        loadSearchView();
    }
    //高耦合度的ViewPager适配器
    class MyViewPagerAdapter extends PagerAdapter{
        @NonNull
        @NotNull
        @Override
        public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
            View mView=null;
            if(position==0) {
                mView = playlist;
            }else if(position==1){
                mView=search;
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
            if(position==0)return "歌单";
            if(position==1)return "搜索";
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
            return view==object;
        }
    }
    private View loadSearchView(){
        searchFragment=new SearchFragment(this);
        search= LayoutInflater.from(getContext()).inflate(R.layout.activity_main_search,null);
        EditText search_et=search.findViewById(R.id.main_search_search);
        ConstraintLayout constraintLayout=search.findViewById(R.id.main_search_show);
        TabLayout tabLayout=search.findViewById(R.id.main_search_tab);
        ViewPager viewPager=search.findViewById(R.id.main_search_viewpager);
        viewPager.setAdapter(searchFragment.new MyViewPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(searchFragment.new MyOnTabSelectedListener());
        KeyboardLayout keyboardLayout=search.findViewById(R.id.main_search_keyboard);
        keyboardLayout.setKeyboardLayoutListener(new KeyboardLayout.KeyboardLayoutListener() {
            boolean last=false;
            @Override
            public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {
                if(isActive==last)return;
                last=isActive;
                System.out.println("====================="+isActive);
                if(isActive) {
                    constraintLayout.setVisibility(View.GONE);
                } else {
                    SearchFragment.keyword=search_et.getText().toString();
                    constraintLayout.setVisibility(View.VISIBLE);
                    tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
                }
            }
        });
        return search;
    }
    private View loadPlaylistView() {
        playlist = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_playlist,null);
        Toolbar toolbar= playlist.findViewById(R.id.main_search_toolbar_layout);
        setSupportActionBar(toolbar);
        playlistView1 = ((RecyclerView) View.inflate(getContext(), R.layout.recycler_view, null));
        playlistView1.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistView2=(RecyclerView)View.inflate(getContext(),R.layout.recycler_view,null);
        playlistView2.setLayoutManager(new LinearLayoutManager(getContext()));
        playlist_tab = playlist.findViewById(R.id.main_playlist_type);
        ViewPager viewPager= playlist.findViewById(R.id.main_playlist_viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter2());
        playlist_tab.addTab(playlist_tab.newTab());
        playlist_tab.addTab(playlist_tab.newTab());
        playlist_tab.setupWithViewPager(viewPager);
        MenuItem login=nav.getMenu().findItem(R.id.login);
        if(MySettingsActivity.get(account_name)!=null){
            login.setTitle(MySettingsActivity.get(account_name));
        }else login.setTitle("登录");
        return playlist;
    }
    private void loadUserSettings() {
        if(MySettingsActivity.get(account_phone)!=null&&MySettingsActivity.get(account_pw)!=null){
            Intent i=new Intent(this, LoginCellphoneActivity.class);
            i.putExtra("ph", MySettingsActivity.get(account_phone));
            i.putExtra("pw", MySettingsActivity.get(account_pw));
            startActivityForResult(i, LoginCellphoneActivity.RequestCode);
        }
    }
    private void loadSystemSettings() {
        if(!ifOps())
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if(item.getItemId()==R.id.main_save_playlist_position){
            if (playlist_tab.getSelectedTabPosition()==0&&myAdapter != null&&myAdapter.playlists.size()>0) {
                WebRequest.playlist_order_update1(myAdapter.playlists, MyCookieJar.getLoginCookie(), null);
            }
            if (playlist_tab.getSelectedTabPosition()==1&&myAdapter2 != null&&myAdapter2.playlists.size()>0) {
                WebRequest.playlist_order_update1(myAdapter2.playlists, MyCookieJar.getLoginCookie(), null);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    GTimer timer=new GTimer();
    @Override
    public void onBackPressed() {
        if(!timer.compareBigger(1000))super.onBackPressed();
        if(drawerLayout.isOpen())drawerLayout.close();
        else drawerLayout.open();
        timer.reset();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode== LoginCellphoneActivity.RequestCode){
            if(data.getBooleanExtra("success",false)){
                MenuItem login=nav.getMenu().findItem(R.id.login);
                LoginBean loginBean = LoginCellphoneActivity.loginBean;
                login.setTitle(loginBean.name);
                MySettingsActivity.set(account_phone,loginBean.ph);
                MySettingsActivity.set(account_pw,loginBean.pw);
                MySettingsActivity.set(account_id, loginBean.id);
                MySettingsActivity.set(account_name, loginBean.name);
                {
                    WebRequest.user_playlist(loginBean.id, MyCookieJar.getLoginCookie(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            FW.w(e);
                            System.out.println(e);
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String body=response.body().string();
                            JsonArray jsonArray= JsonParser.parseString(body).getAsJsonObject().getAsJsonArray("playlist");
                            List<PlaylistBean> playlistBeans=new ArrayList<>();
                            List<PlaylistBean> playlistBeans2=new ArrayList<>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject playlist=jsonArray.get(i).getAsJsonObject();
                                String uid=playlist.getAsJsonObject("creator").get("userId").getAsString();
                                if(uid.equals(MySettingsActivity.get(account_id))){
                                    PlaylistBean playlistBean = new PlaylistBean(playlist);
                                    playlistBeans.add(playlistBean);
                                }else{
                                    PlaylistBean playlistBean = new PlaylistBean(playlist);
                                    playlistBeans2.add(playlistBean);
                                }
                            }
                            Message message=new Message();
                            message.what=MyHandler.UPDATE_COVER;
                            message.arg1=0;
                            message.obj=playlistBeans;
                            handler.sendMessage(message);
                            message=new Message();
                            message.what=MyHandler.UPDATE_COVER;
                            message.obj=playlistBeans2;
                            message.arg1=1;
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        }
    }
    class MyHandler extends Handler{
        public static final int UPDATE_COVER=0;
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            switch (msg.what){
                case UPDATE_COVER:
                    List<PlaylistBean> beans= ((List<PlaylistBean>) msg.obj);
                    if(msg.arg1==0) {
                        myAdapter = new MyAdapter(beans);
                        playlistView1.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
                    }
                    if(msg.arg1==1) {
                        myAdapter2 = new MyAdapter(beans);
                        playlistView2.setAdapter(myAdapter2);
                        myAdapter2.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH>{
        List<PlaylistBean> playlists;
        MyItemTouchHelperCallback callback=new MyItemTouchHelperCallback();
        ItemTouchHelper itemTouchHelper;
        public MyAdapter(List<PlaylistBean> playlists) {
            this.playlists = playlists;
            itemTouchHelper=new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(playlistView1);
        }
        class MyItemTouchHelperCallback extends ItemTouchHelper.Callback{
            @Override
            public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                int flag=ItemTouchHelper.UP|ItemTouchHelper.DOWN;
                return makeMovementFlags(flag,0);
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
                int start=source.getAdapterPosition();
                int end=target.getAdapterPosition();
                Collections.swap(playlists,start,end);
                notifyItemMoved(start,end);
                return true;
            }
            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }
        @NonNull
        @NotNull
        @Override
        public MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.list_small2,parent,false);
            return new MyVH(view);
        }
        @Override
        public void onBindViewHolder(@NonNull @NotNull MyVH holder, int position) {
            PlaylistBean bean=playlists.get(position);
            holder.title.setText(bean.name);
            Util.getCover(bean.coverImgUrl,bitmap -> handler.post(()->holder.icon.setImageBitmap(bitmap)));
            View.OnClickListener onClickListener= v -> {
                Intent intent=new Intent(getContext(), PlaylistActivity.class);
                intent.putExtra("bean",bean);
                startActivity(intent);
            };
            holder.icon.setOnClickListener(onClickListener);
            holder.title.setOnClickListener(onClickListener);
        }
        @Override
        public int getItemCount() {
            return playlists.size();
        }

        class MyVH extends RecyclerView.ViewHolder{

            public final TextView title;
            public final ImageView icon;

            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.list_small_icon);
                title = itemView.findViewById(R.id.list_small_title);
            }
        }
    }
    class MyViewPagerAdapter2 extends PagerAdapter{
        @Override
        public int getCount() {
            return 2;
        }
        public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
            View mView=null;
            if(position==0) {
                mView = playlistView1;
            }else if(position==1){
                mView=playlistView2;
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
            if(position==0)return "我创建的";
            if(position==1)return  "我收藏的";
            return null;
        }
        @Override
        public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
            return view==object;
        }
    }
    static class SearchFragment{
        static final String[] title=new String[]{"歌曲","歌单"};
        static final int num=title.length;
        static String[] keywords=new String[num];
        static String keyword;
        List<RecyclerView> recyclerViews=new ArrayList<>();
        MainActivity activity;
        public SearchFragment(MainActivity activity) {
            this.activity = activity;
            for (int i = 0; i < num; i++) {
                RecyclerView recyclerView= ((RecyclerView) View.inflate(activity, R.layout.recycler_view, null));
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                recyclerViews.add(recyclerView);
            }
        }
        class MyViewPagerAdapter extends PagerAdapter{
            @NonNull
            @NotNull
            @Override
            public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
                View mView=recyclerViews.get(position);
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
                if(position==0)return title[position];
                if(position==1)return title[position];
                return null;
            }

            @Override
            public int getCount() {
                return num;
            }
            @Override
            public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
                return view==object;
            }
        }
        static class MyPlaylistAdapter extends RecyclerView.Adapter<MyPlaylistAdapter.MyVH>{
            List<PlaylistBean> playlists;
            MainActivity activity;
            public MyPlaylistAdapter(MainActivity activity, List<PlaylistBean> playlists) {
                this.activity=activity;
                this.playlists = playlists;
            }
            @NonNull
            @NotNull
            @Override
            public MyPlaylistAdapter.MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(activity).inflate(R.layout.list_small2,parent,false);
                return new MyPlaylistAdapter.MyVH(view);
            }
            @Override
            public void onBindViewHolder(@NonNull @NotNull MyPlaylistAdapter.MyVH holder, int position) {
                PlaylistBean bean=playlists.get(position);
                holder.title.setText(bean.name);
                Util.getCover(bean.coverImgUrl,bitmap -> activity.handler.post(()->holder.icon.setImageBitmap(bitmap)));
                View.OnClickListener onClickListener= v -> {
                    Intent intent=new Intent(activity, PlaylistActivity.class);
                    intent.putExtra("bean",bean);
                    activity.startActivity(intent);
                };
                holder.icon.setOnClickListener(onClickListener);
                holder.title.setOnClickListener(onClickListener);
            }
            @Override
            public int getItemCount() {
                return playlists.size();
            }

            class MyVH extends RecyclerView.ViewHolder{

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
            public MySongAdapter(MainActivity activity, List<SongBean> content) {
                this.activity = activity;
                this.content=content;
                SongManager.getInstance().setPointer(SongManager.getInstance().songs);
                SongManager.getInstance().randomSort();
            }
            @NonNull
            @NotNull
            @Override
            public MySongAdapter.MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(activity).inflate(R.layout.song_item, parent,false);
                return new MySongAdapter.MyVH(v);
            }
            @Override
            public void onBindViewHolder(@NonNull @NotNull MySongAdapter.MyVH myVH, int position) {
                String id = content.get(position).id;
                SongBean song=content.get(position);
                myVH.Name.setText(song.name);
                Util.getCover(song.coverUrl, bitmap -> {
                    activity.handler.post(()->myVH.Cover.setImageBitmap(bitmap));
                });
                View.OnClickListener onClickListener= v -> {
                    ((MyApplicationImpl) activity.getApplication()).getMusicPlayer().start(song,null);
                    Intent intent = new Intent(activity, SongActivity.class);
                    intent.putExtra("bean",song);
                    activity.startActivity(intent);
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
                                        ClipboardManager cm= (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                                        cm.setPrimaryClip(ClipData.newPlainText("Label","https://music.163.com/#/song?id="+id));
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
        class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener{

            private Callback songCallBack;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (keyword == null) return;
                if (keyword.equals(keywords[position])) return;
                keywords[position] = keyword;
                if (position == 0) {
                    songCallBack = new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String body = response.body().string();
                            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                            JsonArray songs = JsonUtil.getAsJsonArray(jsonObject, "songs");
                            List<SongBean> music = new ArrayList<>();
                            for (int i = 0; i < songs.size(); i++) {
                                JsonObject song = songs.get(i).getAsJsonObject();
                                SongBean bean = new SongBean(song);
                                SongManager.getInstance().addSong(bean);
                                music.add(bean);
                            }
                            activity.handler.post(() -> {
                                RecyclerView recyclerView = recyclerViews.get(0);
                                MySongAdapter mySongAdapter = new MySongAdapter(activity, music);
                                recyclerView.setAdapter(mySongAdapter);
                                mySongAdapter.notifyDataSetChanged();
                            });
                        }
                    };
                    WebRequest.cloudsearch(keyword, 1, MyCookieJar.getLoginCookie(), songCallBack);
                }
                if (position == 1) {
                    WebRequest.cloudsearch(keyword, 1000, MyCookieJar.getLoginCookie(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String body=response.body().string();
                            System.out.println(body);
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(keyword != null&& !keyword.equals("")) {
                    int position = tab.getPosition();
                    if (!keyword.equals(keywords[position])) {
                        if (position==0)WebRequest.cloudsearch(keyword, 1, MyCookieJar.getLoginCookie(), songCallBack);
                    }
                }
            }
        };
    }
}
