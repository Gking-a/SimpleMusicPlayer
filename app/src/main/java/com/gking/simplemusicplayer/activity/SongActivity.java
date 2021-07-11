package com.gking.simplemusicplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SongActivity extends BaseActivity {
    MusicPlayer musicPlayer;
    SongBean song=null;
    private MyOnSeekBarChangeListener onSeekBarChangeListener=new MyOnSeekBarChangeListener();
    private MyAdapter myAdapter;
    private MusicPlayer.OnSongBeanChangeListener onSongBeanChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(this,false);
        setContentView(R.layout.activity_song);
        handler=new MyHandler(new WeakReference<>(this));
        load();
        timeThread=new TimeThread();
        timeThread.start();
        onSongBeanChangeListener = (musicPlayer, songBean) -> {
            song = songBean;
            musicPlayer.operateAfterPrepared(mp -> {
                Message message = new Message();
                message.what = MyHandler.SET_MAX;
                message.arg1 = musicPlayer.getDuration();
                handler.sendMessage(message);
            });
        };
        musicPlayer.addOnSongBeanChangeListener(onSongBeanChangeListener);
    }
    MyHandler handler;
    SeekBar progress;
    TimeThread timeThread;
    private void load() {
        musicPlayer=((MyApplicationImpl) getApplication()).mMusicPlayer;
        System.out.println(musicPlayer==null);
        progress=f(R.id.song_progress);
        lyricView=f(R.id.song_lyric);
        lyricView.setLayoutManager(new LinearLayoutManager(getContext()));
        f(R.id.song_next).setOnClickListener(v -> musicPlayer.next(null));
        f(R.id.song_last).setOnClickListener(v -> musicPlayer.last(null));
        f(R.id.song_pause).setOnClickListener(v -> musicPlayer.pause());
        progress.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }
    class TimeThread extends Thread{
        private int position=0;
        @Override
        public void run() {
            while (!interrupted()){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                if (song != null) {
                    Message message=handler.obtainMessage(MyHandler.UPDATE_PROGRESS);
                    handler.sendMessage(message);
                    {
                        LyricManager manager=onSeekBarChangeListener.lyricManager;
                        if (manager == null)continue;
                        int position=manager.getPosition(musicPlayer.getCurrentPosition());
                        if(position<0)continue;
                        if(this.position==position)continue;
                        this.position=position;
                        System.out.println("show msg"+position);
                        handler.sendMessage(handler.obtainMessage(MyHandler.SHOW_LYRIC,position,position));
                    }
                }
            }
            System.out.println("death");
        }
    }

    RecyclerView lyricView;
    static class MyHandler extends Handler{
        WeakReference<SongActivity> activityWeakReference;
        public MyHandler(WeakReference<SongActivity> activityWeakReference) {
            this.activityWeakReference=activityWeakReference;
        }
        public static final int SET_MAX=0;
        public static final int UPDATE_PROGRESS =1;
        public static final int SHOW_LYRIC=20;
        SeekBar progress;
        SongBean song;
        MyHandler handler;
        RecyclerView lyricView;
        MusicPlayer musicPlayer;
        public void assign(){
            System.out.println("assign");
            if(activityWeakReference.get()==null)return;
            SongActivity activity=activityWeakReference.get();
            progress = activity.progress;
            song = activity.song;
            handler = activity.handler;
            lyricView = activity.lyricView;
            musicPlayer = ((MyApplicationImpl) activity.getApplication()).mMusicPlayer;
        }
        public <T extends View> T f(int id){
            SongActivity activity=activityWeakReference.get();
            if (activity == null)return null;
            return activity.f(id);
        }
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            SongActivity activity=activityWeakReference.get();
            if (activity == null)return;
            switch (msg.what){
                case SET_MAX:
                    assign();
                    ((TextView) activity.f(R.id.song_toolbar_title)).setText(song.name);
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
                                activity.myAdapter = activity.new MyAdapter(lyricBean);
                                lyricView.setAdapter(activity.myAdapter);
                                activity.myAdapter.notifyDataSetChanged();
                                activity.onSeekBarChangeListener.lyricManager=LyricManager.getInstance(lyricBean);
                            });
                        }
                    });
                case UPDATE_PROGRESS: {
                    if (musicPlayer == null) {
                        System.out.println("player is null!!!");
                        return;
                    }
                    int currentPosition = musicPlayer.getCurrentPosition();
                    if (!progress.isPressed()) {
                        progress.setProgress(currentPosition);
                    }
                    break;
                }
                case SHOW_LYRIC:
                    if(activity.myAdapter==null){
                        System.out.println("NULL!!!!!!");
                        return;
                    }
                    TextView time_progress = f(R.id.song_time_progress);
                    time_progress.setText(time2str(progress.getProgress()));
                    activity.myAdapter.showLyric(msg.arg1);
            }
        }
    }
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        final MyThread thread = new MyThread();
        LyricManager lyricManager;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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

        @Override
        public void onViewAttachedToWindow(@NonNull @NotNull MyVH holder) {
            LyricManager manager=onSeekBarChangeListener.lyricManager;
            if (manager == null) return;
            int position=manager.getPosition(musicPlayer.getCurrentPosition());
            if(holder.getLayoutPosition()==position){
                last=holder.Lyric;
                if(last!=null)
                    last.setTextColor(0xFF00FF00);
            }
        }
        public void showLyric(int position){
            System.out.println("show"+position);
            if(last!=null){
                last.setTextColor(0xFF000000);
            }
            MyVH vh = (MyVH) lyricView.findViewHolderForAdapterPosition(position);
            if(vh!=null) {
                last=vh.Lyric;
                last.setTextColor(0xFF00FF00);
            }
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
    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            timeThread.interrupt();
            myAdapter=null;
            progress.setOnSeekBarChangeListener(null);
            musicPlayer.removeOnSongBeanChangeListener(onSongBeanChangeListener);
            onSongBeanChangeListener=null;
            System.gc();
        }
    }
}