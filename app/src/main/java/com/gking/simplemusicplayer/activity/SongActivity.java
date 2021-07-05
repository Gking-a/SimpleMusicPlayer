package com.gking.simplemusicplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.LyricBean;
import com.gking.simplemusicplayer.manager.LyricManager;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SongActivity extends BaseActivity {
    MusicPlayer musicPlayer;
    SongBean song;
    private MyOnSeekBarChangeListener onSeekBarChangeListener=new MyOnSeekBarChangeListener();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(this,false);
        setContentView(R.layout.activity_song);
        load();
        musicPlayer= ((MyApplicationImpl) getApplication()).mMusicPlayer;
        song= (SongBean) getIntent().getSerializableExtra("bean");
        musicPlayer.operateAfterPrepared(mp -> {
            Message message=new Message();
            message.what=MyHandler.SET_MAX;
            message.arg1=musicPlayer.getDuration();
            handler.sendMessage(message);
        });
    }
    MyHandler handler=new MyHandler(new WeakReference<>(SongActivity.this));
    SeekBar progress;
    TimeThread timeThread;
    private void load() {
        progress=f(R.id.song_progress);
        lyricView=f(R.id.song_lyric);
        lyricView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    class TimeThread extends Thread{

        @Override
        public void run() {
            while (!interrupted()){
                Message message=new Message();
                message.what=MyHandler.UPDATE_PROGRESS;
                handler.sendMessage(message);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    RecyclerView lyricView;
    class MyHandler extends Handler{
        public MyHandler(WeakReference<SongActivity> activityWeakReference) {
        }
        public void sendMessage(int what,Object o,int arg1,int arg2){
            Message message=new Message();
            message.what=what;
            message.obj=o;
            message.arg1=arg1;
            message.arg2=arg2;
            sendMessage(message);
        }
        public static final int SET_MAX=0;
        public static final int UPDATE_PROGRESS =1;
        public static final int SHOW_LYRIC=2;
//        public void assign(){
//            progress = activity.progress;
//            song = activity.song;
//            handler = activity.handler;
//            lyricView = activity.lyricView;
//            musicPlayer = activity.musicPlayer;
//        }
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            switch (msg.what){
                case SET_MAX:
                    progress.setMax(msg.arg1);
                    TextView tv=f(R.id.song_time_duration);
                    tv.setText(time2str(msg.arg1));
                    WebRequest.lyric(song.id, MyCookieJar.getLoginCookie(), new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String body=response.body().string();
                            JsonObject jsonObject= JsonParser.parseString(body).getAsJsonObject();
                            JsonElement noLyric=jsonObject.get("nolyric");
                            LyricBean lyricBean;
                            if(noLyric!=null){
                                lyricBean=new LyricBean();
                            }else {
                                lyricBean=new LyricBean(jsonObject);
                            }
                            handler.post(()->{
                                myAdapter = new MyAdapter(lyricBean);
                                lyricView.setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();
                                onSeekBarChangeListener.lyricManager=LyricManager.getInstance(lyricBean);
                                timeThread=new TimeThread();
                                timeThread.start();
                                progress.setOnSeekBarChangeListener(onSeekBarChangeListener);
                            });
                        }
                    });
                case UPDATE_PROGRESS:
                    int currentPosition = musicPlayer.getCurrentPosition();
                    if(!progress.isPressed()) {
                        progress.setProgress(currentPosition);
                    }
                case SHOW_LYRIC:
                    if(myAdapter==null){
                        System.out.println("NULL!!!!!!");
                        return;
                    }
                    TextView time_progress = f(R.id.song_time_progress);
                    time_progress.setText(time2str(progress.getProgress()));
                    myAdapter.showLyric();
            }
        }
    }
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        final MyThread thread = new MyThread();
        LyricManager lyricManager;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(lyricManager==null)return;
            int position = lyricManager.getPosition(progress);
            handler.sendMessage(MyHandler.SHOW_LYRIC,null,position,position);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            musicPlayer.setLockProgress(true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            musicPlayer.setLockProgress(false);
            musicPlayer.seekTo(seekBar.getProgress());
            thread.setText(time2str(seekBar.getProgress()));
            handler.post(thread);
        }

        class MyThread extends Thread {
            String text;

            public void setText(String text) {
                this.text = text;
            }

            @Override
            public void run() {
                TextView tv1 = f(R.id.song_time_progress);
                tv1.setText(time2str(musicPlayer.getCurrentPosition()));
            }
        }
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH>{
        List<String> content;
        LyricBean lyricBean;
        Map<Integer,TextView> views=new HashMap<>();
        TextView last=null;
        int position=0;
        public MyAdapter(LyricBean lyricBean){
            this.lyricBean=lyricBean;
            this.content=lyricBean.getLyric();
            if(lyricBean.isNolyric()){
                content.add("纯音乐，请欣赏");
            }
        }
        @NonNull @NotNull
        @Override
        public MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(getContext()).inflate(R.layout.lyric,parent,false);
            return new MyVH(v);
        }
        @Override
        public void onBindViewHolder(@NonNull @NotNull MyVH holder, int position) {
            holder.Lyric.setText(content.get(position));
            views.put(position, holder.Lyric);
        }
        @Override
        public int getItemCount() {
            return content.size();
        }
        public void showLyric(){
            LyricManager manager=onSeekBarChangeListener.lyricManager;
            if (manager == null)return;
            int position=manager.getPosition(musicPlayer.getCurrentPosition());
            if(position<0)return;
            if(this.position==position)return;
            this.position=position;
            if(last!=null){
                last.setTextColor(0xFF000000);
            }
            last=views.get(position);
            if(last!=null)
                views.get(position).setTextColor(0xFF00FF00);
            lyricView.scrollToPosition(position);
        }
        class MyVH extends RecyclerView.ViewHolder{
            TextView Lyric;
            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                Lyric=itemView.findViewById(R.id.lyric);
            }
        }
    }
    public static String time2str(int msec){
        int sec=msec/1000;
        return sec/60+":"+sec%60;
    }
}