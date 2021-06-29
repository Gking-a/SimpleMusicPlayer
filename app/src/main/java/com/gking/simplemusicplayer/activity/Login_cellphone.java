package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import gtools.managers.GHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Login_cellphone extends BaseActivity {
    public static final String TAG="login_cellphone";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_cellphone);
        setContext(this);
        EditText phone=f(R.id.loginPhone);
        EditText password=f(R.id.loginPassword);
        phone.setText("18263610381");
        password.setText("gking1980");
        Button button=f(R.id.loginLogin);
        button.setOnClickListener(v -> {
            String ph=phone.getText().toString(),
                    pw=password.getText().toString();
            if(!(ph==null||pw==null||ph.equals("")||pw.equals("")||ph.length()!=11)){
                WebRequest.cellphone(ph, pw, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        makeToast("登陆失败");
                        FW.w(e);
                        System.out.println(e);
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String body=response.body().string();
                        System.out.println(body);
                        System.out.println(MyCookieJar.getLoginCookie());
                        JsonObject jsonObject=JsonParser.parseString(body).getAsJsonObject();
                        String code=jsonObject.get("code").getAsString();
                        if(code.equals("200")){
                            makeToast("登录成功");
                            finish();
                            GHolder.standardInstance.add(TAG,JsonParser.parseString(body).getAsJsonObject());
                        }else {
                            makeToast("登录失败 "+jsonObject.get("msg").getAsString());
                        }
                    }
                });
            }else {
                makeToast("账号密码格式错误");
            }
        });
    }
}
