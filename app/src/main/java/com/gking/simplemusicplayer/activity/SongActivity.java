package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.dialog.SongDialog1;
import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.interfaces.SongOperable;
import com.gking.simplemusicplayer.manager.LyricBean;
import com.gking.simplemusicplayer.manager.LyricManager;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.service.BackgroundService;
import com.gking.simplemusicplayer.util.ControlableThread;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongActivity extends BaseActivity implements SongOperable<BaseActivity>{
    MusicPlayer musicPlayer;
    SongBean song = null;
    private MyOnSeekBarChangeListener onSeekBarChangeListener = new MyOnSeekBarChangeListener();
    private MyAdapter myAdapter;
    private MusicPlayer.OnSongBeanChangeListener onSongBeanChangeListener;
    Handler handler;
    SeekBar progress;
    TimeRunnable timeRunnable;
    ControlableThread controlableThread;
    private SeekBar volume;
    private Button mode_view;
    private Runnable loadLyric;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(this, false);
        setContentView(R.layout.activity_song);
        handler = new Handler();
        musicPlayer = ((MyApplicationImpl) getApplication()).mMusicPlayer;
        load();
        timeRunnable = new TimeRunnable();
        controlableThread=new ControlableThread(timeRunnable);
        controlableThread.setSuspend(true);
        controlableThread.start();
        onSongBeanChangeListener = new MusicPlayer.OnSongBeanChangeListener() {
            boolean p=false;
            @Override
            public void onSongBeanChange(MusicPlayer musicPlayer, SongBean songBean) {
                song = songBean;
                handler.post(()->{
                    ((TextView) f(R.id.song_toolbar_title)).setText(song.name);
                    progress.setMax(musicPlayer.getDuration());
                    ((TextView) f(R.id.song_toolbar_author)).setText(song.author);
                });
            }
            @Override
            public void onPrepared(MusicPlayer musicPlayer) {
                p=true;
                handler.post(()->{
                    TextView tv = f(R.id.song_time_duration);
                    tv.setText(time2str(musicPlayer.getDuration()));
                    progress.setMax(musicPlayer.getDuration());
                });
                controlableThread.setSuspend(!p);
            }
            @Override
            public void onFinish(MusicPlayer musicPlayer) {
                p=false;
                controlableThread.setSuspend(!p);
            }
            @Override
            public void onLyricLoaded(MusicPlayer musicPlayer, LyricBean lyricBean) {
                loadLyric = () -> {
                    myAdapter = new MyAdapter(lyricBean);
                    lyricView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                };
                handler.post(loadLyric);
            }
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    myAdapter.showLyric();
                }
            };
            @Override
            public void onLyricChange(MusicPlayer musicPlayer, int position, String lyric) {
                if(myAdapter!=null) {
                    handler.post(r1);
                }

            }
        };
        musicPlayer.addOnSongBeanChangeListener(onSongBeanChangeListener);
        musicPlayer.notify(song,onSongBeanChangeListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        volume.setProgress(((AudioManager) getSystemService(AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC));
        if (myAdapter != null) {
            myAdapter.showLyric();
        }
    }
    Runnable updata_progress = new Runnable() {
        @Override
        public void run() {
            int currentPosition = musicPlayer.getCurrentPosition();
            if (!progress.isPressed()){
                progress.setProgress(currentPosition);
            }
        }
    };
//    Runnable set_lyric = () -> {
//        if(myAdapter!=null)
//            myAdapter.showLyric(LyricManager.Instance.getPosition(musicPlayer.getCurrentPosition()));
//    };
    Runnable changeModeView = () -> changeModeView();
    private void load() {
        musicPlayer = ((MyApplicationImpl) getApplication()).mMusicPlayer;
        System.out.println(musicPlayer == null);
        progress = f(R.id.song_progress);
        lyricView = f(R.id.song_lyric);
        lyricView.setLayoutManager(new LinearLayoutManager(getContext()));
        mode_view = f(R.id.song_mode);
        changeModeView();
        mode_view.setOnClickListener(v -> changeMode());
        f(R.id.song_next).setOnClickListener(v -> musicPlayer.next(null));
        f(R.id.song_last).setOnClickListener(v -> musicPlayer.last(null));
        f(R.id.song_pause).setOnClickListener(v -> musicPlayer.pause());
//        f(R.id.song_window).setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), BackgroundService.class);
//            intent.putExtra(BackgroundService.Type.Type, BackgroundService.Type.Window);
//            startService(intent);
//        });
        f(R.id.song_toolbar_back).setOnClickListener(v -> finish());
        f(R.id.song_more).setOnClickListener(v -> {new SongDialog1(this).show(song.pid,song);
        });
        progress.setOnSeekBarChangeListener(onSeekBarChangeListener);
        volume = f(R.id.song_sound_seekbar);
        volume.setMax(((AudioManager) getSystemService(AUDIO_SERVICE)).getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volume.setProgress(((AudioManager) getSystemService(AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC));
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    AudioManager audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    @Override
    public void onSongDelete(String pid, SongBean songBean) {}
    class TimeRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (song != null) {
                handler.post(updata_progress);
                handler.post(changeModeView);
            }
        }
    }
    RecyclerView lyricView;
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        final MyThread thread = new MyThread();
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            thread.text = time2str(progress);
            handler.post(thread);
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
                tv1.setText(text);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            AudioManager audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            AudioManager audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVH> {
        List<String> content;
        LyricBean lyricBean;
        Map<Integer, TextView> views = new HashMap<>();
        TextView last = null;
        public MyAdapter(LyricBean lyricBean) {
            this.lyricBean = lyricBean;
            this.content = lyricBean.getLyric();
            if (lyricBean.isNolyric()) {
                content.add("纯音乐，请欣赏");
            }
        }
        @NonNull
        @NotNull
        @Override
        public MyVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.lyric, parent, false);
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
            int position = musicPlayer.getLyricPosition();
            if (holder.getLayoutPosition() == position) {
                last = holder.Lyric;
                if (last != null) {
                    int color = 0xFFff0000;
                    last.setTextColor(color);
                }
            }else {
                holder.Lyric.setTextColor(0xff000000);
            }
        }
        public void showLyric(){
            showLyric(musicPlayer.getLyricPosition());
        }
        public void showLyric(int position) {
            if (last != null) {
                last.setTextColor(0xFF000000);
            }
            MyVH vh = (MyVH) lyricView.findViewHolderForAdapterPosition(position);
            if (vh != null) {
                last = vh.Lyric;
                last.setTextColor(0xFFff0000);
            }
            lyricView.scrollToPosition(position);
        }
        class MyVH extends RecyclerView.ViewHolder {
            TextView Lyric;

            public MyVH(@NonNull @NotNull View itemView) {
                super(itemView);
                Lyric = itemView.findViewById(R.id.lyric);
            }
        }
    }
    public static String time2str(int msec) {
        int sec = msec / 1000;
        String s = sec / 60 + ":";
        int i = sec % 60;
        String s2 = "0";
        if (i < 10) s2 += i;
        else s2 = i + "";
        return s + s2;
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            controlableThread.interrupt();
            myAdapter = null;
            progress.setOnSeekBarChangeListener(null);
            musicPlayer.removeOnSongBeanChangeListener(onSongBeanChangeListener);
            onSongBeanChangeListener = null;
            System.gc();
        }
    }
    public void changeMode() {
        String mode = SettingsActivity.get(SettingsActivity.Params.play_mode);
        if (mode.equals(SettingsActivity.Params.PLAY_MODE.ORDER))
            SettingsActivity.set(SettingsActivity.Params.play_mode, 0);
        else SettingsActivity.set(SettingsActivity.Params.play_mode, Integer.parseInt(mode) + 1);
        changeModeView();
    }
    public void changeModeView() {
        String mode = SettingsActivity.get(SettingsActivity.Params.play_mode);
        int bitmap_res = R.drawable.close;
        if (mode.equals(SettingsActivity.Params.PLAY_MODE.NONE)) bitmap_res = R.drawable.close;
        if (mode.equals(SettingsActivity.Params.PLAY_MODE.LOOP)) bitmap_res = R.drawable.loop;
        if (mode.equals(SettingsActivity.Params.PLAY_MODE.RANDOM)) bitmap_res = R.drawable.random;
        if (mode.equals(SettingsActivity.Params.PLAY_MODE.ORDER)) bitmap_res = R.drawable.order;
        mode_view.setBackgroundResource(bitmap_res);
    }
}
