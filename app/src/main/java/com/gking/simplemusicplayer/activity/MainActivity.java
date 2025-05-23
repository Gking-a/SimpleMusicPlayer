/*
 */

package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gking.simplemusicplayer.NoFailureCallback;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.beans.SongManager;
import com.gking.simplemusicplayer.fragment.PlaylistFragment;
import com.gking.simplemusicplayer.fragment.RecommendFragment;
import com.gking.simplemusicplayer.fragment.SearchFragment;
import com.gking.simplemusicplayer.beans.LoginBean;
import com.gking.simplemusicplayer.beans.PlaylistBean;
import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import cn.gking.gtools.util.GTimer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.*;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.application;

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    public Handler handler=new Handler();
    NavigationView nav;
    DrawerLayout drawerLayout;
    String[] title=new String[]{"歌单","搜索","推荐"};
    private final List<View> fragments=new LinkedList<>();
    private PlaylistFragment playlistFragment;
    private RecommendFragment recommendFragment;
    public Callback getPlaylistCallback;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContext(this);
        setContentView(R.layout.activity_main);
        setLoadControlPanel(true);
        loadBaseSettings();
        loadView();
        loadUserSettings();
    }
    private void loadView() {
        playlistFragment=new PlaylistFragment(this,getPlaylistCallback);
        fragments.add(playlistFragment.getView());
        SearchFragment searchFragment=new SearchFragment(this);
        fragments.add(searchFragment.getView());
        recommendFragment = new RecommendFragment(this);
        fragments.add(recommendFragment.getView());
        drawerLayout = f(R.id.main_drawer);
        nav = f(R.id.nav);
        nav.setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_login)
                startActivityForResult(new Intent(getContext(), LoginCellphoneActivity.class), LoginCellphoneActivity.RequestCode);
            if(item.getItemId()==R.id.nav_settings)
                startActivity(new Intent(getContext(),SettingsActivity.class));
            if(item.getItemId()==R.id.nav_qr_login)
                startActivityForResult(new Intent(getContext(),QRLoginActivity.class),QRLoginActivity.RequestCode);
            if(item.getItemId()==R.id.lllllllllaunch){
                lllllllaunch();
//                recommendFragment.update();
//                WebRequest.user_playlist(loginBean.id, Cookies.getLoginCookie(), getPlaylistCallback);
            }
            if(item.getItemId()==R.id.nav_logout) {
                WebRequest.logout();
                SettingsActivity.library.remove(cookie, (byte) 0);
                SettingsActivity.library.remove("csrf", (byte) 0);
            }
            return true;
        });
        TabLayout tabLayout = f(R.id.main_tab);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        ViewPager viewPager = f(R.id.main_viewpager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new MyViewPagerAdapter());
    }

    private void lllllllaunch() {
        SettingsActivity.loadCookie();
        WebRequest.user_account(new NoFailureCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                JsonObject asJsonObject = JsonParser.parseString(string).getAsJsonObject();
                JsonObject account = asJsonObject.getAsJsonObject("account");
                String id = account.get("id").getAsString();
                String nickName=asJsonObject.getAsJsonObject("profile").get("nickname").getAsString();
                if(!Objects.equals(SettingsActivity.get(account_id), id)) {
                    SettingsActivity.set(account_id, id);
                }
                application.userID =id;
                application.nickname=nickName;
                {
                    recommendFragment.update();
                    WebRequest.user_playlist(id,  getPlaylistCallback);
                }
                response.close();
            }
        });
    }

    //高耦合度的ViewPager适配器
    class MyViewPagerAdapter extends PagerAdapter {
        @NonNull
        @NotNull
        @Override
        public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
            View mView = fragments.get(position);
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
            return title[position];
        }
        @Override
        public int getCount() {
            return title.length;
        }
        @Override
        public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
            return view == object;
        }
    }
    private void loadUserSettings() {
        if (false&&SettingsActivity.get(account_phone) != null && SettingsActivity.get(account_pw) != null) {
            Intent i = new Intent(this, LoginCellphoneActivity.class);
            i.putExtra("ph", SettingsActivity.get(account_phone));
            i.putExtra("pw", SettingsActivity.get(account_pw));
            startActivityForResult(i, LoginCellphoneActivity.RequestCode);
        }
        if(SettingsActivity.getBoolean(auto_login)&&SettingsActivity.get(__csrf)!=null){
            lllllllaunch();
//            Intent intent = new Intent(this, QRLoginActivity.class);
//            startActivityForResult(intent,QRLoginActivity.RequestCode);
        }else{
        }
        if(SettingsActivity.getBoolean(record_playlist)){
            MusicPlayer.preventplayonprepareforonce=false;
            SongManager.getInstance().songs=SettingsActivity.recordBean.songs;
            SongManager.getInstance().randomSongs=SettingsActivity.recordBean.randomSongs;
            SongManager.getInstance().setPointer(SettingsActivity.recordBean.songs);
            SongManager.getInstance().setPointer(SettingsActivity.recordBean.randomSongs);
            getMyApplication().mMusicPlayer.start(SettingsActivity.recordBean.now_play, false);
        }
    }
    private void loadBaseSettings() {
        getPlaylistCallback= new NoFailureCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                Log.i("getPlaylist",body);
                JsonArray jsonArray = JsonParser.parseString(body).getAsJsonObject().getAsJsonArray("playlist");
                List<PlaylistBean> playlistBeans = new ArrayList<>();
                List<PlaylistBean> playlistBeans2 = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject playlist = jsonArray.get(i).getAsJsonObject();
                    String uid = playlist.getAsJsonObject("creator").get("userId").getAsString();
                    if (uid.equals(SettingsActivity.get(account_id))) {
                        PlaylistBean playlistBean = new PlaylistBean(playlist);
                        playlistBeans.add(playlistBean);
                    } else {
                        PlaylistBean playlistBean = new PlaylistBean(playlist);
                        playlistBeans2.add(playlistBean);
                    }
                }
                playlistFragment.setAdapter(playlistBeans, playlistBeans2);
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    GTimer timer = new GTimer();
    @Override
    public void onBackPressed() {
        if (!timer.compareBigger(1000)) super.onBackPressed();
        if (drawerLayout.isOpen()) drawerLayout.close();
        else drawerLayout.open();
        timer.reset();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginCellphoneActivity.RequestCode) {
            if (data.getBooleanExtra("success", false)) {
                MenuItem login = nav.getMenu().findItem(R.id.nav_login);
                LoginBean loginBean = (LoginBean) data.getSerializableExtra("loginBean");
                login.setTitle(loginBean.name);
                makeToast("登录成功");
                SettingsActivity.set(account_phone, loginBean.ph);
                SettingsActivity.set(account_pw, loginBean.pw);
                SettingsActivity.set(account_id, loginBean.id);
                SettingsActivity.set(account_name, loginBean.name);
                {
                    recommendFragment.update();
                    WebRequest.user_playlist(loginBean.id,  getPlaylistCallback);
                }
            }
        }
        if(requestCode == QRLoginActivity.RequestCode){
            if (data != null && data.getBooleanExtra("success", false)) {
                SettingsActivity.loadCookie();
            }
        }
    }
}
