package com.gking.simplemusicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.manager.LoginBean;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginCellphoneActivity extends BaseActivity {
    public static final String TAG="login_cellphone";
    public static final int RequestCode=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_cellphone);
        setContext(this);
        Intent result=new Intent();
        setResult(RequestCode,result);
        EditText phone=f(R.id.loginPhone);
        EditText password=f(R.id.loginPassword);
        Intent intent=getIntent();
        String dph=intent.getStringExtra("ph");
        String dpw=intent.getStringExtra("pw");
        if(dph!=null&&dpw!=null){
            makeToast("登录中");
            WebRequest.login_cellphone(dph,dpw,new MyCallBack(dph,dpw));
        }
        Button button=f(R.id.loginLogin);
        button.setOnClickListener(v -> {
            RadioButton radioButton=f(R.id.agree_deal);
            if(!radioButton.isChecked()){
                makeToast("请先同意用户协议和隐私政策");
                return;
            }
            String ph=phone.getText().toString(),
                    pw=password.getText().toString();
            if(!(ph==null||pw==null||ph.equals("")||pw.equals("")||ph.length()!=11)){
                makeToast("登录中");
                WebRequest.login_cellphone(ph, pw, new MyCallBack(ph,pw));
            }else {
                makeToast("账号密码格式错误");
            }
        });
        Intent i1=new Intent(this,EmptyActivity.class);
        i1.putExtra("text",R.string.user_agreement);
        f(R.id.user_agreement_tv).setOnClickListener(v -> startActivity(i1));
        Intent i2=new Intent(this,EmptyActivity.class);
        i2.putExtra("text",R.string.privacy_policy);
        f(R.id.privacy_policy_tv).setOnClickListener(v -> startActivity(i2));
    }
    class MyCallBack implements Callback{
        String ph,pw;
        public MyCallBack(String ph, String pw) {
            this.ph = ph;
            this.pw = pw;
        }
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            makeToast("登录失败");
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String body=response.body().string();
            System.out.println(body);
            JsonObject jsonObject=JsonParser.parseString(body).getAsJsonObject();
            String code=jsonObject.get("code").getAsString();
            if(code.equals("200")){
                LoginBean loginBean=new LoginBean(jsonObject,ph,pw);
                Intent intent=new Intent();
                intent.putExtra("success",true);
                intent.putExtra("loginBean",loginBean);
                setResult(RequestCode,intent);
                finish();
            }else {
                String msg=jsonObject.get("msg").getAsString()!=null?jsonObject.get("msg").getAsString():jsonObject.get("message").getAsString();
                makeToast("登录失败 "+msg);
            }
        }
    }
}
