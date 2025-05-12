package com.gking.simplemusicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.gking.simplemusicplayer.NoFailureCallback;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.util.MyCookies;
import com.gking.simplemusicplayer.util.URLs;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gking.gtools.database.GDataBase;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.*;

public class QRLoginActivity extends AppCompatActivity {
    public boolean success=false;
    public boolean failure=false;
    public static final int RequestCode=1010;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlogin);
        ImageView iv = findViewById(R.id.qrlogin_image);

        WebRequest.login_qr_key(new NoFailureCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                System.out.println(string);
                String unikey = JsonParser.parseString(string).getAsJsonObject().get("unikey").getAsString();
                MyCookies.qrkey=unikey;
                {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("codekey",unikey);
                    String url= URLs.login_qr_create;
                    HttpUrl.Builder httpBuilder=HttpUrl.parse(url).newBuilder();
                    httpBuilder.addQueryParameter("codekey",unikey);
                    HttpUrl build = httpBuilder.build();
                    url=build.toString();
                    Bitmap qrcode = qrcode(url);
                    System.out.println(url);
                    runOnUiThread(()-> iv.setImageBitmap(qrcode));
                }
                response.close();
                while(!(success||failure)){
                    WebRequest.login_qr_check(unikey,callback);
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    Callback callback=new NoFailureCallback() {
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String string = response.body().string();
            JsonObject asJsonObject = JsonParser.parseString(string).getAsJsonObject();
            System.out.println(asJsonObject);
            String code = asJsonObject.get("code").getAsString();
            if(code.equals("800")){
                failure=true;
                Intent intent=new Intent();
                intent.putExtra("success",false);
                setResult(RequestCode,intent);
                finish();
                response.close();
            }else if(code.equals("803")){
                success=true;
                try{
                    List<String> headers = response.headers("set-cookie");
                    List<GDataBase> cookieDBs=new ArrayList<>(headers.size());
                    outerloop:for(String str:headers){
                        GDataBase cditem=new GDataBase();
                        String[] cookieitem = str.split(";");
                        for (String s : cookieitem) {
                            if(s.startsWith(" ")){
                                s=s.substring(1);
                            }
                            String[] kv = s.split("=");
                            if(kv.length<=1){
                                continue outerloop;
                            }
                            String storeK=null;
                            switch (kv[0]){
                                case __csrf:storeK=__csrf;break;
                                case MUSIC_A_T:storeK=MUSIC_A_T;break;
                                case MUSIC_U:storeK=MUSIC_U;break ;
                                case MUSIC_R_T:storeK=MUSIC_R_T;break ;
                                case NMTID:storeK=NMTID;break ;
                            }
                            if(storeK!=null){
                                SettingsActivity.set(storeK,kv[1]);
                            }
                            cditem.add(kv[0],kv[1]);
                        }
                        cookieDBs.add(cditem);
                    }
                    SettingsActivity.library.replaceDBs(SettingsActivity.Params.login_fetch_cookies,cookieDBs);
                    SettingsActivity.library.save();
                }catch (Exception e){
                    e.printStackTrace();
                }
                response.close();
                Intent intent=new Intent();
                intent.putExtra("success",true);
                setResult(RequestCode,intent);
                finish();
            }
            response.close();
        }
    };
    public Bitmap qrcode(String content){
        int width = 400;
        int height = 400;
        //HashMap设置二维码参数
        Map map = new HashMap();
        //   设置容错率 L>M>Q>H  等级越高扫描时间越长,准确率越高
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //设置字符集
        map.put(EncodeHintType.CHARACTER_SET,"utf-8");
        //设置外边距
        map.put(EncodeHintType.MARGIN,1);
        //利用编码器，生成二维码
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = null;
        try {
            bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, width, height,map);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return  bitmap;
    }
}