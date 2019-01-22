package com.monet_android.modle.reaction;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class ReactionPost {

    @SerializedName("reactions")
    String reactions;

    @SerializedName("cf_id")
    String cf_id;

    @SerializedName("user_id")
    String user_id;

    @SerializedName("camp_id")
    String camp_id;

    public ReactionPost(String reactions, String cf_id, String user_id, String camp_id) {
        this.cf_id = cf_id;
        this.user_id = user_id;
        this.camp_id = camp_id;
        this.reactions = reactions;
    }
}
