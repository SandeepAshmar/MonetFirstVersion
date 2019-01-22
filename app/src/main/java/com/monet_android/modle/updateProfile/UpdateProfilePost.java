package com.monet_android.modle.updateProfile;

import com.google.gson.annotations.SerializedName;

public class UpdateProfilePost {

    @SerializedName("user_gender")
    String user_gender;

    @SerializedName("user_phone")
    String user_phone;

    @SerializedName("user_email")
    String user_email;

    @SerializedName("user_city")
    String user_city;

    @SerializedName("user_state")
    String user_state;

    @SerializedName("image_url")
    String image_url;

    @SerializedName("user_country")
    String user_country;

    @SerializedName("user_dob")
    String user_dob;

    @SerializedName("user_fname")
    String user_fname;

    @SerializedName("User_Old_Password")
    String user_old_password;

    @SerializedName("New_Password")
    String new_password;

    //this is for profile update
    public UpdateProfilePost(String user_gender, String user_phone, String user_email, String user_city, String user_state, String image_url, String user_country, String user_dob, String user_fname) {
        this.user_gender = user_gender;
        this.user_phone = user_phone;
        this.user_email = user_email;
        this.user_city = user_city;
        this.user_state = user_state;
        this.image_url = image_url;
        this.user_country = user_country;
        this.user_dob = user_dob;
        this.user_fname = user_fname;
    }

    //this is for update password
    public UpdateProfilePost(String user_old_password, String new_password) {
        this.user_old_password = user_old_password;
        this.new_password = new_password;
    }
}
