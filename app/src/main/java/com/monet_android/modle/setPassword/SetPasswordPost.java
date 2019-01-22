package com.monet_android.modle.setPassword;

import com.google.gson.annotations.SerializedName;

public class SetPasswordPost {

    @SerializedName("user_email")
    String user_email;

    @SerializedName("user_password")
    String user_password;

    public SetPasswordPost(String user_email, String user_password) {
        this.user_email = user_email;
        this.user_password = user_password;
    }
}
