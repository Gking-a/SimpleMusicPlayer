package com.gking.simplemusicplayer.popup;

import android.app.Activity;
import android.view.View;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.MySettingsActivity;
import com.gking.simplemusicplayer.base.BaseBottomPopupWindow;
import com.gking.simplemusicplayer.fragment.PlaylistFragment;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.gking.simplemusicplayer.util.WebRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.MySettingsActivity.Params.account_id;

public class PlaylistPopupWindow extends BaseBottomPopupWindow {
    PlaylistFragment playlistFragment;
    public PlaylistPopupWindow(Activity context,PlaylistFragment playlistFragment) {
        super(context, R.layout.playlist_popup_window);
        this.playlistFragment=playlistFragment;
        View root=getView();
        root.findViewById(R.id.playlist_popup_window_delete).setOnClickListener(v -> {
            WebRequest.playlist_delete(playlistBean.id,new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String string = response.body().string();
                    System.out.println(string);
                    WebRequest.user_playlist(MySettingsActivity.get(account_id), MyCookieJar.getLoginCookie(),playlistFragment.getGetPlaylistCallback());
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
            });
            dismiss();
        });
        root.findViewById(R.id.playlist_popup_window_close).setOnClickListener(v -> dismiss());
    }
    public PlaylistBean playlistBean;
    public void showAtBottom(View parent, PlaylistBean bean) {
        super.showAtBottom(parent);
        this.playlistBean=bean;
    }
}
