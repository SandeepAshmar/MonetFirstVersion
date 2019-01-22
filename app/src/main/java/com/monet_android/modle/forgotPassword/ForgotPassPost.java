package com.monet_android.modle.forgotPassword;

import com.google.gson.annotations.SerializedName;

public class ForgotPassPost {

    @SerializedName("user_email")
    String user_email;

    @SerializedName("otp")
    String otp;

    public ForgotPassPost(String user_email, String otp) {
        this.user_email = user_email;
        this.otp = otp;
    }
}
