/*
 */

package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.LoginBean;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gtools.managers.GHolder;
import gtools.util.GTimer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.MySettingsActivity.Params.*;
public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    Map<String,GHolder> playlists=new LinkedHashMap<>();
    MyHandler handler=new MyHandler();
    EditText search;
    RecyclerView recentSongs;
    NavigationView nav;
    DrawerLayout drawerLayout;
    RecyclerView playlistView;
    private MyAdapter myAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContext(this);
        setContentView(R.layout.activity_main);
        setLoadControlPanel(true);
        load();
//        debug();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if(item.getItemId()==R.id.main_save_playlist_position){
            if (myAdapter != null) {
                WebRequest.playlist_order_update1(myAdapter.playlists, MyCookieJar.getLoginCookie(), null);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    private void load(){
        Toolbar toolbar=f(R.id.playlist_toolbar);
        setSupportActionBar(toolbar);
        search=f(R.id.searchEditText);
        playlistView=f(R.id.main_playlist);
        playlistView.setLayoutManager(new LinearLayoutManager(getContext()));
        nav=f(R.id.nav);
        drawerLayout=f(R.id.drawer);
        MenuItem login=nav.getMenu().findItem(R.id.login);
        if(MySettingsActivity.get(account_name)!=null){
            login.setTitle(MySettingsActivity.get(account_name));
            Intent i=new Intent(this, LoginCellphoneActivity.class);
            i.putExtra("ph", MySettingsActivity.get(account_phone));
            i.putExtra("pw", MySettingsActivity.get(account_pw));
            startActivityForResult(i, LoginCellphoneActivity.RequestCode);
        }else login.setTitle("登录");
        nav.setNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.login)startActivityForResult(new Intent(getContext(), LoginCellphoneActivity.class), LoginCellphoneActivity.RequestCode);
            return true;
        });
        load2();
    }
    private void load2(){
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
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject playlist=jsonArray.get(i).getAsJsonObject();
                                String uid=playlist.getAsJsonObject("creator").get("userId").getAsString();
                                if(uid.equals(MySettingsActivity.get(account_id))){
                                    PlaylistBean playlistBean = new PlaylistBean(playlist);
                                    playlistBeans.add(playlistBean);
                                }
                            }
                            Message message=new Message();
                            message.what=MyHandler.UPDATE_COVER;
                            message.obj=playlistBeans;
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
                    myAdapter = new MyAdapter(beans);
                    playlistView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
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
            itemTouchHelper.attachToRecyclerView(playlistView);
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
}
