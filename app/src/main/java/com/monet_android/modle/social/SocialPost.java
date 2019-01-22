package com.monet_android.modle.social;

import com.google.gson.annotations.SerializedName;

public class SocialPost {

    @SerializedName("user_fname")
    String user_fname;

    @SerializedName("user_email")
    String user_email;

    @SerializedName("social_id")
    String social_id;

    public SocialPost(String user_fname, String user_email, String social_id) {
        this.user_fname = user_fname;
        this.user_email = user_email;
        this.social_id = social_id;
    }
}
