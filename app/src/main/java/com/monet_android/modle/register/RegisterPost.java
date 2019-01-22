package com.monet_android.modle.register;

import com.google.gson.annotations.SerializedName;

public class RegisterPost {

    @SerializedName("user_fname")
    String user_fname;

    @SerializedName("user_lname")
    String user_lname;

    @SerializedName("user_email")
    String user_email;

    @SerializedName("user_password")
    String user_password;

    @SerializedName("confirm_password")
    String confirm_password;

    @SerializedName("user_gender")
    String user_gender;

    @SerializedName("user_dob")
    String user_dob;

    @SerializedName("user_mobile")
    String user_mobile;

    @SerializedName("user_city")
    String user_city;

    @SerializedName("user_states")
    String user_state;

    @SerializedName("user_country")
    String user_country;

    public RegisterPost(String user_fname, String user_lname, String user_email, String user_password, String confirm_password, String user_gender, String user_dob, String user_mobile, String user_city, String user_state, String user_country) {
        this.user_fname = user_fname;
        this.user_lname = user_lname;
        this.user_email = user_email;
        this.user_password = user_password;
        this.confirm_password = confirm_password;
        this.user_gender = user_gender;
        this.user_dob = user_dob;
        this.user_mobile = user_mobile;
        this.user_city = user_city;
        this.user_state = user_state;
        this.user_country = user_country;
    }
}
