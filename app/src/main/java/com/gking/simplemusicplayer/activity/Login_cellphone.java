package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
    public static final int RequestCode=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_cellphone);
        setContext(this);
        EditText phone=f(R.id.loginPhone);
        EditText password=f(R.id.loginPassword);
        Intent intent=getIntent();
        String dph=intent.getStringExtra("ph");
        String dpw=intent.getStringExtra("pw");
        if(dph!=null&&dpw!=null){
            WebRequest.login_cellphone(dph,dpw,new MyCallBack(dph,dpw));
        }
        Button button=f(R.id.loginLogin);
        setResult(RequestCode,new Intent());
        button.setOnClickListener(v -> {
            String ph=phone.getText().toString(),
                    pw=password.getText().toString();
            if(!(ph==null||pw==null||ph.equals("")||pw.equals("")||ph.length()!=11)){
                WebRequest.login_cellphone(ph, pw, new MyCallBack(ph,pw));
            }else {
                makeToast("账号密码格式错误");
            }
        });
    }
    class MyCallBack implements Callback{
        String ph,pw;
        public MyCallBack(String ph, String pw) {
            this.ph = ph;
            this.pw = pw;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            makeToast("登陆失败");
            FW.w(e);
            System.out.println(e);
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String body=response.body().string();
            JsonObject jsonObject=JsonParser.parseString(body).getAsJsonObject();
            String code=jsonObject.get("code").getAsString();
            if(code.equals("200")){
                makeToast("登录成功");
                Intent result=new Intent();
                result.putExtra("refresh",true);
                result.putExtra("phone",ph);
                result.putExtra("pw",pw);
                setResult(RequestCode,result);
                GHolder.standardInstance.add(RequestCode,JsonParser.parseString(body).getAsJsonObject());
                finish();
            }else {
                makeToast("登录失败 "+jsonObject.get("msg").getAsString());
            }
        }
    }
}
