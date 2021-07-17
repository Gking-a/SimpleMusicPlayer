package com.gking.simplemusicplayer.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.ChoosePlaylistActivity;
import com.gking.simplemusicplayer.activity.PlaylistActivity;
import com.gking.simplemusicplayer.base.BaseBottomDialog;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.WebRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SongDialog1 extends BaseBottomDialog<PlaylistActivity>{

    public SongDialog1(@NonNull @NotNull PlaylistActivity context) {
        super(context);
    }
    String pid,sid;
    public void show(String pid, SongBean bean){
        this.pid=pid;
        this.sid=bean.id;
        show();
    }
    @Override
    protected View loadView() {
        View view=View.inflate(getContext(), R.layout.dialog_song1,null);
        view.findViewById(R.id.dialog_song_close).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.dialog_song_delete).setOnClickListener(v -> WebRequest.playlist_tracks_delete(pid, new String[]{sid}, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println(response.body().string());
                WebRequest.playlist_detail(pid, MyCookieJar.getLoginCookie(),getActivity().refreshPlaylistCallback);
            }
        }));
        view.findViewById(R.id.dialog_song_add).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getActivity(), ChoosePlaylistActivity.class);
            getActivity().startActivityForResult(intent,ChoosePlaylistActivity.RequestCode);
        });
        return view;
    }
}
