package com.gking.simplemusicplayer.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.ChoosePlaylistActivity;
import com.gking.simplemusicplayer.activity.PlaylistActivity;
import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.base.BaseBottomDialog;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.interfaces.SongOperable;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SongDialog1 extends BaseBottomDialog<Activity>{
    public SongDialog1(@NonNull @NotNull SongOperable<? extends BaseActivity> context) {
        super(context);
    }
    String pid;
    SongBean songBean;
    public void show(String pid, SongBean bean){
        this.pid=pid;
        this.songBean=bean;
        show();
    }
    @NotNull
    @Override
    protected View loadView() {
        View view=View.inflate(getContext(), R.layout.dialog_song1,null);
        view.findViewById(R.id.dialog_song_download).setOnClickListener(v -> {
            Toast.makeText(getContext(),"下载",Toast.LENGTH_LONG).show();
            new Thread(()-> Util.downloadSong(songBean.id)).start();
            dismiss();
        });
        view.findViewById(R.id.dialog_song_close).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.dialog_song_delete).setOnClickListener(v -> {
            if (pid == null||songBean==null||songBean.id==null) {
                dismiss();
                return;
            }
            WebRequest.playlist_tracks_delete(pid, new String[]{songBean.id}, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                }
            });
            dismiss();
        });
        view.findViewById(R.id.dialog_song_add).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getActivity(), ChoosePlaylistActivity.class);
            intent.putExtra("song",songBean);
            getActivity().startActivityForResult(intent,ChoosePlaylistActivity.RequestCode);
        });
        return view;
    }
}
