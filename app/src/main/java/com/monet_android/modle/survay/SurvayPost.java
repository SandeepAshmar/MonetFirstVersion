package com.monet_android.modle.survay;

import com.google.gson.annotations.SerializedName;

public class SurvayPost {

    @SerializedName("data")
    String data;

    @SerializedName("cf_id")
    String cf_id;

    @SerializedName("cmp_id")
    String cmp_id;

    @SerializedName("type")
    String type;

    public SurvayPost(String data, String cf_id, String cmp_id, String type) {
        this.data = data;
        this.cf_id = cf_id;
        this.cmp_id = cmp_id;
        this.type = type;
    }
}
