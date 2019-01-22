package com.monet_android.modle.getProfile;

import com.google.gson.annotations.SerializedName;

public class GetProfilePost {

    @SerializedName("user_id")
    String userId;

    public GetProfilePost(String userId) {
        this.userId = userId;
    }
}
