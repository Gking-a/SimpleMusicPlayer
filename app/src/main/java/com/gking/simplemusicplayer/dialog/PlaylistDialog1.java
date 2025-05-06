package com.gking.simplemusicplayer.dialog;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.base.BaseBottomDialog;
import com.gking.simplemusicplayer.util.MyCookies;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.util.WebRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.account_id;

public class PlaylistDialog1 extends BaseBottomDialog {

    public PlaylistDialog1(@NonNull @NotNull Activity context,Callback getPlaylistCallback) {
        super(context);
        this.getPlaylistCallback=getPlaylistCallback;
    }
    PlaylistBean playlistBean;
    Callback getPlaylistCallback;
    public void show(PlaylistBean playlistBean) {
        this.playlistBean=playlistBean;
        super.show();
    }
    @NotNull
    @Override
    protected View loadView() {
        View view=View.inflate(getActivity(), R.layout.dialog_playlist1,null);
        view.findViewById(R.id.dialog_playlist_close).setOnClickListener(v -> {
            dismiss();
        });
        view.findViewById(R.id.dialog_playlist_delete).setOnClickListener(v -> {
            WebRequest.playlist_delete(playlistBean.id,new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String string = response.body().string();
                    System.out.println(string);
                    WebRequest.user_playlist(SettingsActivity.get(account_id), getPlaylistCallback);
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
            });
            dismiss();
        });
        return view;
    }
}
