package com.gking.simplemusicplayer.beans;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class LoginBean implements Serializable {
    public String id,name,ph,pw;

    public LoginBean(JsonObject json,String ph, String pw) {
        this.ph = ph;
        this.pw = pw;
        id= json.getAsJsonObject("account").get("id").getAsString();
        name=json.getAsJsonObject("profile").get("nickname").getAsString();
    }
}
