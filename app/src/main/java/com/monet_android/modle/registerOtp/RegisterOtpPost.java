package com.monet_android.modle.registerOtp;

import com.google.gson.annotations.SerializedName;

public class RegisterOtpPost {

    @SerializedName("user_email")
    String user_email;

    @SerializedName("otp")
    String otp;

    @SerializedName("user_phone")
    String user_phone;

    public RegisterOtpPost(String user_email, String otp, String user_phone) {
        this.user_email = user_email;
        this.otp = otp;
        this.user_phone = user_phone;
    }
}
