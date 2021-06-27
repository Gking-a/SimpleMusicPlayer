package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Login_cellphone extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_cellphone);
        setContext(this);
        Button button=f(R.id.loginLogin);
        button.setOnClickListener(v -> {
            EditText phone=f(R.id.loginPhone);
            EditText password=f(R.id.loginPassword);
            String ph=phone.getText().toString(),
                    pw=password.getText().toString();
            if(!(ph==null||pw==null||ph.equals("")||pw.equals("")||ph.length()!=11)){
                WebRequest.cellphone(ph, pw, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(getContext(),"登陆失败",Toast.LENGTH_SHORT);
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String body=response.body().string();
                        JsonObject jsonObject=JsonParser.parseString(body).getAsJsonObject();
                        String code=jsonObject.get("code").getAsString(),
                                msg=jsonObject.get("msg").getAsString();
                        if(code.equals("200")){
//                                startActivity(new Intent(getContext(),));
                        }
                    }
                });
            }else {
                Toast.makeText(getContext(),"请输入正确的账号密码",Toast.LENGTH_SHORT);
            }
        });
    }
}
