package com.gking.simplemusicplayer.dialog;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.ChoosePlaylistActivity;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.base.BaseBottomDialog;
import com.gking.simplemusicplayer.beans.SongBean;
import com.gking.simplemusicplayer.util.Util;

import org.jetbrains.annotations.NotNull;

public class SongDialog2 extends BaseBottomDialog<BaseActivity> {
    public RecyclerView view;
    public SongDialog2(@NonNull @NotNull BaseActivity context) {
        super(context);
    }
    SongBean songBean;
    public void show( SongBean bean){
        this.songBean=bean;
        show();
    }
    @NotNull
    @Override
    protected View loadView() {
        View view=View.inflate(getContext(), R.layout.dialog_song1,null);
        view.findViewById(R.id.dialog_song_download).setOnClickListener(v -> {
            Toast.makeText(getContext(),"下载中",Toast.LENGTH_SHORT).show();
            new Thread(()-> Util.downloadSong(songBean)).start();
            dismiss();
        });
        view.findViewById(R.id.dialog_song_close).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.dialog_song_delete).setVisibility(View.GONE);
        view.findViewById(R.id.dialog_song_add).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getActivity(), ChoosePlaylistActivity.class);
            intent.putExtra("song",songBean);
            getActivity().startActivityForResult(intent,ChoosePlaylistActivity.RequestCode);
        });
        return view;
    }
}
