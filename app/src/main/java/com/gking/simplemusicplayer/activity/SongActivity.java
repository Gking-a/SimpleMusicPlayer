package com.gking.simplemusicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SongActivity extends BaseActivity {
    MusicPlayer musicPlayer= ((MyApplicationImpl) getApplication()).mMusicPlayer;
    SongBean song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(this,false);
        setContentView(R.layout.activity_song);
        load();
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
                message.what=MyHandler.UPDATA_PROGRESS;
                handler.sendMessage(message);
            }
        }
    }
    class MyHandler extends Handler{
        public static final int SET_MAX=0;
        public static final int UPDATA_PROGRESS=1;
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            switch (msg.what){
                case SET_MAX:
                    progress.setMax(msg.arg1);
                    RecyclerView lyricView=f(R.id.song_lyric);
                    WebRequest.lyric(song.id, MyCookieJar.getLoginCookie(), new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String body=response.body().string();
                            JsonObject jsonObject= JsonParser.parseString(body).getAsJsonObject();
                            LyricBean lyricBean=new LyricBean(jsonObject);
                            MyAdapter myAdapter=new MyAdapter(lyricBean);
                        }
                    });
                case UPDATA_PROGRESS:
                    int currentPosition = ((MyApplicationImpl) getApplication()).mMusicPlayer.getCurrentPosition();
                    if(!progress.isPressed())
                        progress.setProgress(currentPosition);
            }
        }
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH>{
        List<String> content;
        LyricBean lyricBean;
        public MyAdapter(LyricBean lyricBean){
            this.lyricBean=lyricBean;
            this.content=lyricBean.getLyric();
        }
        @NonNull
        @NotNull
        @Override
        public MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new MyVH(LayoutInflater.from(getContext()).inflate(R.layout.lyric,parent));
        }
        @Override
        public void onBindViewHolder(@NonNull @NotNull MyVH holder, int position) {
            holder.Lyric.setText(content.get(position));
        }
        @Override
        public int getItemCount() {
            return 0;
        }
        class MyVH extends RecyclerView.ViewHolder{
            TextView Lyric;
            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                Lyric=itemView.findViewById(R.id.lyric);
            }
        }
    }
}