package com.gking.simplemusicplayer.dialog;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.base.BaseBottomDialog;
import com.gking.simplemusicplayer.fragment.PlaylistFragment;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.util.WebRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.account_id;

public class PlaylistDialog2 extends BaseBottomDialog {
    PlaylistBean playlistBean;
    PlaylistFragment playlistFragment;
    public void show(PlaylistBean playlistBean) {
        this.playlistBean=playlistBean;
        super.show();
    }
    public PlaylistDialog2(@NonNull @NotNull Activity context, PlaylistFragment playlistFragment) {
        super(context);
        this.playlistFragment=playlistFragment;
    }
    @NotNull
    @Override
    protected View loadView() {
        View view = View.inflate(getContext(), R.layout.dialog_playlist2, null);
        view.findViewById(R.id.dialog_playlist_close).setOnClickListener(v -> {
            dismiss();
        });
        view.findViewById(R.id.dialog_playlist_unsubscribe).setOnClickListener(v -> {
            WebRequest.playlist_unsubscribe(playlistBean.id,new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String string = response.body().string();
                    WebRequest.user_playlist(SettingsActivity.get(account_id), MyCookieJar.getLoginCookie(),playlistFragment.getGetPlaylistCallback());
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
