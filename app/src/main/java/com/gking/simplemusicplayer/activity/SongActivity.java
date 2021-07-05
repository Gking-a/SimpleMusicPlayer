package com.gking.simplemusicplayer.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.LyricBean;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SongActivity extends BaseActivity {
    MusicPlayer musicPlayer;
    SongBean song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(this,false);
        setContentView(R.layout.activity_song);
        load();
        musicPlayer= ((MyApplicationImpl) getApplication()).mMusicPlayer;
        song= (SongBean) getIntent().getSerializableExtra("bean");
        musicPlayer.operateAfterPrepared(mp -> {
            timeThread=new TimeThread();
            timeThread.start();
        });
    }
    MyHandler handler=new MyHandler();
    SeekBar progress;
    TimeThread timeThread;
    private void load() {
        progress=f(R.id.song_progress);
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            MyThread thread=new MyThread();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    musicPlayer.seekTo(progress);
                    thread.setText(time2str(progress));
                    handler.post(thread);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                musicPlayer.setLockProgress(true);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlayer.setLockProgress(false);
                musicPlayer.seekTo(seekBar.getProgress());
            }
            class MyThread extends Thread{
                String text;

                public void setText(String text) {
                    this.text = text;
                }

                @Override
                public void run() {
                    TextView tv1=f(R.id.song_time_progress);
                    tv1.setText(time2str(musicPlayer.getCurrentPosition()));
                }
            }
        });
        lyricView=f(R.id.song_lyric);
        lyricView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    class TimeThread extends Thread{
        public TimeThread(){
            Message message=new Message();
            message.what=MyHandler.SET_MAX;
            message.arg1=musicPlayer.getDuration();
            handler.sendMessage(message);
        }
        @Override
        public void run() {
            while (!interrupted()){
                Message message=new Message();
                message.what=MyHandler.UPDATE_PROGRESS;
                handler.sendMessage(message);
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    RecyclerView lyricView;
    class MyHandler extends Handler{
        public static final int SET_MAX=0;
        public static final int UPDATE_PROGRESS =1;
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
                                MyAdapter myAdapter=new MyAdapter(lyricBean);
                                lyricView.setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();
                            });
                        }
                    });
                case UPDATE_PROGRESS:
                    int currentPosition = musicPlayer.getCurrentPosition();
                    if(!progress.isPressed()) {
                        progress.setProgress(currentPosition);
                    }
            }
        }
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH>{
        List<String> content;
        LyricBean lyricBean;
        public MyAdapter(LyricBean lyricBean){
            this.lyricBean=lyricBean;
            this.content=lyricBean.getLyric();
            if(lyricBean.isNolyric()){
                content.add("纯音乐，请欣赏");
            }
        }
        @NonNull
        @NotNull
        @Override
        public MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(getContext()).inflate(R.layout.lyric,parent,false);
            return new MyVH(v);
        }
        @Override
        public void onBindViewHolder(@NonNull @NotNull MyVH holder, int position) {
            holder.Lyric.setText(content.get(position));
        }
        @Override
        public int getItemCount() {
            return content.size();
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