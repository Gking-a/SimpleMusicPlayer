package com.gking.simplemusicplayer.dialog;

import android.view.View;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.base.BaseActivity;
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

public class PlaylistDialog3 extends BaseBottomDialog<BaseActivity> {
    PlaylistBean playlistBean;

    public PlaylistDialog3(@NonNull @NotNull BaseActivity context,Callback getPlaylistCallback) {
        super(context);
        this.getPlaylistCallback=getPlaylistCallback;
    }
    Callback getPlaylistCallback;
    public void show(PlaylistBean playlistBean) {
        this.playlistBean=playlistBean;
        super.show();
    }
    @NotNull
    @Override
    protected View loadView() {
        View view=View.inflate(getContext(),R.layout.dialog_playlist3,null);
        view.findViewById(R.id.dialog_playlist_close).setOnClickListener(v -> {
            dismiss();
        });
        view.findViewById(R.id.dialog_playlist_subscribe).setOnClickListener(v -> {
            WebRequest.playlist_subscribe(playlistBean.id,new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String string = response.body().string();
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
