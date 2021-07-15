package com.gking.simplemusicplayer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.SimpleInterface;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.util.WebRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PlaylistCreateDialog extends Dialog {
    Context context;
    public PlaylistCreateDialog(@NonNull Context context) {
        super(context,R.style.MyDialog);
        this.context=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_playlist_create);
        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失
        Button cancel = findViewById(R.id.playlist_create_cencel);
        cancel.setOnClickListener(v -> {
            dismiss();
        });
        Button sure=findViewById(R.id.playlist_create_sure);
        sure.setOnClickListener(v -> {
            EditText editText=findViewById(R.id.playlist_create_name);
            String name=editText.getText().toString();
            if(name==null||name.trim().equals("")){
                Toast.makeText(context,"请输入名字",Toast.LENGTH_SHORT).show();
                return;
            }
            CheckBox c1=findViewById(R.id.playlist_create_privacy);
            int pr=0;
            if(c1.isChecked())pr=10;
            CheckBox c2=findViewById(R.id.playlist_create_type);
            String type="NORMAL";
            if(c2.isChecked())type="VIDEO";
            WebRequest.playlist_create(name, pr, type, MyCookieJar.getLoginCookie(), new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println(response.body().string());
                    Toast.makeText(context,"成功",Toast.LENGTH_SHORT).show();
                    dismiss();
                    if(simpleInterface!=null)simpleInterface.method(null);
                }
            });
        });
    }
    SimpleInterface simpleInterface;

    public void setSimpleInterface(SimpleInterface simpleInterface) {
        this.simpleInterface = simpleInterface;
    }
}
