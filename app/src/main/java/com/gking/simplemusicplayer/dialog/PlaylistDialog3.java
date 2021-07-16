package com.gking.simplemusicplayer.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.MySettingsActivity;
import com.gking.simplemusicplayer.base.BaseDialog;
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

public class PlaylistDialog3 extends BaseDialog {
    PlaylistBean playlistBean;
    PlaylistFragment playlistFragment;
    public void show(PlaylistBean playlistBean) {
        this.playlistBean=playlistBean;
        super.show();
    }
    public PlaylistDialog3(@NonNull @NotNull Activity context, PlaylistFragment playlistFragment) {
        super(context);
        this.playlistFragment=playlistFragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_playlist1);
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth();// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失
        View root=getView();
        window.findViewById(R.id.dialog_playlist_close).setOnClickListener(v -> {
            dismiss();
        });
        window.findViewById(R.id.dialog_playlist_subscribe).setOnClickListener(v -> {
            WebRequest.playlist_subscribe(playlistBean.id,new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String string = response.body().string();
                    WebRequest.user_playlist(MySettingsActivity.get(account_id), MyCookieJar.getLoginCookie(),playlistFragment.getGetPlaylistCallback());
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
            });
            dismiss();
        });
    }
    @Override
    protected View loadView() {
        View view=View.inflate(getContext(),R.layout.dialog_playlist3,null);
        return view;
    }
}
