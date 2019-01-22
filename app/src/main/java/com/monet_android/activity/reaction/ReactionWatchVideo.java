package com.monet_android.activity.reaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.monet_android.R;
import com.monet_android.activity.ThankyouPage;
import com.monet_android.activity.questions.QuestionQCard;
import com.monet_android.adapter.IconsAdapter;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.halper.IOnItemClickListener;
import com.monet_android.modle.reacionIcon.Item;
import com.monet_android.modle.reaction.ReactionPost;
import com.monet_android.modle.reaction.ReactionResponse;
import com.monet_android.qCards.FaceDetectInstructions;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.qCards.LandingScreen.arrayList;
import static com.monet_android.qCards.LandingScreen.stagingJson;
import static com.monet_android.utils.AppConstant.SUCCESS;
import static com.monet_android.utils.AppConstant.YOUTUBE_KEY;

public class ReactionWatchVideo extends YouTubeBaseActivity implements YouTubePlayer.PlayerStateChangeListener {

    private YouTubePlayerView youtube_viewReaction;
    private String video_id = "", token, cf_id, user_id, cmp_id, coment;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    boolean doubleBackToExitPressedOnce = false;
    private RecyclerView icons_recycler;
    public static ArrayList<Item> iconList = new ArrayList<>();
    private IconsAdapter iconsAdapter;
    private GridLayoutManager gridLayoutManager;
    private String dialog_name;
    private Dialog dialog;
    private YouTubePlayer player;
    private JSONObject reactionMainObject = new JSONObject();
    private ProgressDialog pd;
    private int videoStopTime = 0;
    private TextView tv_dialogName;
    private EditText edt_dialogcoment;
    private ImageView img_dialog;
    private Button btn_dialog;
    boolean intrupt = false;

    JSONArray jsonArray1 = new JSONArray();
    JSONArray jsonArray2 = new JSONArray();
    JSONArray jsonArray3 = new JSONArray();
    JSONArray jsonArray4 = new JSONArray();
    JSONArray jsonArray5 = new JSONArray();
    JSONArray jsonArray6 = new JSONArray();
    JSONArray jsonArray7 = new JSONArray();
    JSONArray jsonArray8 = new JSONArray();
    JSONArray jsonArray9 = new JSONArray();
    JSONObject jsonObject;


    IOnItemClickListener iOnItemClickListener = new IOnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            dialog_name = iconList.get(position).getReactionName();
            videoStopTime = player.getCurrentTimeMillis();
            showDialog();
            setIcon(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_watch_video);

        initView();

    }

    public void initView() {
        icons_recycler = findViewById(R.id.icons_recycler);
        youtube_viewReaction = findViewById(R.id.youtube_viewReaction);
        dialog = new Dialog(ReactionWatchVideo.this, R.style.Theme_Dialog);
        video_id = AppPreferences.getVideoUrl(this);
        if (video_id.isEmpty()) {
            Toast.makeText(this, "No video url found", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (AppUtils.isConnectionAvailable(this)) {
                playVideo();
                setReactionIcons();
            } else {
                AppUtils.noInternetDialog(this);
            }
        }
        if (AppPreferences.isRedirectUser(this).equalsIgnoreCase("no")) {
            token = "Bearer " + AppPreferences.getApiToken(this);
        } else {
            token = "Bearer " + AppPreferences.getRedirectApiToken(this);
        }
        user_id = AppPreferences.getUserId(this);
        cf_id = AppPreferences.getCfId(this);
        cmp_id = AppPreferences.getCmpId(this);

        pd = new ProgressDialog(this);
        pd.setMessage("Please wait.....");
        pd.setCanceledOnTouchOutside(false);

        try {
            stagingJson.put("5", "4");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playVideo() {
        video_id = video_id.replace("https://www.youtube.com/watch?v=", "");
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player = youTubePlayer;
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                player.loadVideo(video_id);
//                youTubePlayer.loadVideo(video_id);
                youTubePlayer.setPlayerStateChangeListener(ReactionWatchVideo.this);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
                Toast.makeText(ReactionWatchVideo.this, result.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        youtube_viewReaction.initialize(YOUTUBE_KEY, onInitializedListener);
    }

    private void setReactionIcons() {

        String cmpList = AppPreferences.getCamEval(this);

        if (cmpList.contains("a") || cmpList.contains("1")) {
            iconList.add(new Item("Like"));
        }
        if (cmpList.contains("b") || cmpList.contains("2")) {
            iconList.add(new Item("Love"));
        }
        if (cmpList.contains("c") || cmpList.contains("3")) {
            iconList.add(new Item("Want"));
        }
        if (cmpList.contains("d") || cmpList.contains("4")) {
            iconList.add(new Item("Memorable"));
        }
        if (cmpList.contains("e") || cmpList.contains("5")) {
            iconList.add(new Item("Dislike"));
        }
        if (cmpList.contains("f") || cmpList.contains("6")) {
            iconList.add(new Item("Accurate"));
        }
        if (cmpList.contains("g") || cmpList.contains("7")) {
            iconList.add(new Item("Misleading"));
        }
        if (cmpList.contains("h") || cmpList.contains("8")) {
            iconList.add(new Item("Engaging"));
        }
        if (cmpList.contains("i") || cmpList.contains("9")) {
            iconList.add(new Item("Boring"));
        }

        gridLayoutManager = new GridLayoutManager(this, 5);
        icons_recycler.setLayoutManager(gridLayoutManager);
        iconsAdapter = new IconsAdapter(iconList, this, iOnItemClickListener);
        icons_recycler.setAdapter(iconsAdapter);
    }

    @Override
    public void onLoading() {
        icons_recycler.setClickable(false);
    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
        icons_recycler.setClickable(true);
    }

    @Override
    public void onVideoEnded() {
        try {
            stagingJson.put("5", "5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        iconList.clear();
        dialog.dismiss();
        submitReactionPart();
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 0);
    }

    @SuppressLint("NewApi")
    public void showDialog() {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("_") &&
                            !Character.toString(source.charAt(i)).equals("-") &&
                            !Character.toString(source.charAt(i)).equals(" ")) {
                        return "";
                    }
                }
                return null;
            }
        };

        pauseVideo();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        tv_dialogName = dialog.findViewById(R.id.tv_dialogName);
        edt_dialogcoment = dialog.findViewById(R.id.edt_dialogComment);
        img_dialog = dialog.findViewById(R.id.img_dialog);
        btn_dialog = dialog.findViewById(R.id.btn_dialog);

        edt_dialogcoment.setFilters(new InputFilter[]{filter});

        tv_dialogName.setText(dialog_name);

        setHint(edt_dialogcoment);
        dialog.show();

        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_dialogcoment.getText().toString().isEmpty()) {
                    Toast.makeText(ReactionWatchVideo.this, "Please enter your review", Toast.LENGTH_SHORT).show();
                } else {
                    coment = edt_dialogcoment.getText().toString();
                    dialog.dismiss();
                    playVideoAgain();
                    try {
                        setReactionJson();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void setIcon( int position) {

        if (iconList.get(position).getReactionName().equalsIgnoreCase("like")) {
            img_dialog.setImageResource(R.drawable.ic_like);
        }
        if (iconList.get(position).getReactionName().equalsIgnoreCase("love")) {
            img_dialog.setImageResource(R.drawable.ic_love);
        }
        if (iconList.get(position).getReactionName().equalsIgnoreCase("Want")) {
            img_dialog.setImageResource(R.drawable.ic_want);
        }
        if (iconList.get(position).getReactionName().equalsIgnoreCase("memorable")) {
            img_dialog.setImageResource(R.drawable.ic_memorable);
        }
        if (iconList.get(position).getReactionName().equalsIgnoreCase("dislike")) {
            img_dialog.setImageResource(R.drawable.ic_dislike);
        }
        if (iconList.get(position).getReactionName().equalsIgnoreCase("accurate")) {
            img_dialog.setImageResource(R.drawable.ic_helpful);
        }
        if (iconList.get(position).getReactionName().equalsIgnoreCase("misleading")) {
            img_dialog.setImageResource(R.drawable.ic_not_helpful);
        }
        if (iconList.get(position).getReactionName().equalsIgnoreCase("engaging")) {
            img_dialog.setImageResource(R.drawable.ic_engaging);
        }
        if (iconList.get(position).getReactionName().equalsIgnoreCase("boring")) {
            img_dialog.setImageResource(R.drawable.ic_bore);
        }
    }

    private void setHint(EditText editText) {

        if (dialog_name.equalsIgnoreCase("like")) {
            editText.setHint("You like this part.... Why?");
        } else if (dialog_name.equalsIgnoreCase("love")) {
            editText.setHint("You love this part.... Why?");
        } else if (dialog_name.equalsIgnoreCase("Want to watch / buy")) {
            editText.setHint("You want this product..... Why?");
        } else if (dialog_name.equalsIgnoreCase("memorable")) {
            editText.setHint("This part was memorable to you.... Why?");
        } else if (dialog_name.equalsIgnoreCase("dislike")) {
            editText.setHint("You did not like this part.... Why?");
        } else if (dialog_name.equalsIgnoreCase("accurate")) {
            editText.setHint("This part helps you make a decision about whether or not you might like this product.... Why?");
        } else if (dialog_name.equalsIgnoreCase("misleading")) {
            editText.setHint("This part is misleading... Why?");
        } else if (dialog_name.equalsIgnoreCase("engaging")) {
            editText.setHint("This part specially captures your attention.... Why?");
        } else if (dialog_name.equalsIgnoreCase("boring")) {
            editText.setHint("This part is not interesting.... Why?");
        }

    }

    private void pauseVideo() {
        player.pause();
    }

    private void playVideoAgain() {
        player.play();
    }

    private void setReactionJson() throws JSONException {

        videoStopTime = (videoStopTime / 1000) % 60;

        if (dialog_name.equalsIgnoreCase("Love")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray1.put(jsonObject);
            reactionMainObject.put(dialog_name, jsonArray1);
        } else if (dialog_name.equalsIgnoreCase("Like")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray2.put(jsonObject);
            reactionMainObject.put(dialog_name, jsonArray2);
        } else if (dialog_name.equalsIgnoreCase("Want to watch / buy")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray3.put(jsonObject);
            reactionMainObject.put("Want", jsonArray3);
        } else if (dialog_name.equalsIgnoreCase("Memorable")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray4.put(jsonObject);
            reactionMainObject.put(dialog_name, jsonArray4);
        } else if (dialog_name.equalsIgnoreCase("Dislike")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray5.put(jsonObject);
            reactionMainObject.put(dialog_name, jsonArray5);
        } else if (dialog_name.equalsIgnoreCase("Accurate")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray6.put(jsonObject);
            reactionMainObject.put(dialog_name, jsonArray6);
        } else if (dialog_name.equalsIgnoreCase("Misleading")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray7.put(jsonObject);
            reactionMainObject.put(dialog_name, jsonArray7);
        } else if (dialog_name.equalsIgnoreCase("engaging")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray8.put(jsonObject);
            reactionMainObject.put(dialog_name, jsonArray8);
        } else if (dialog_name.equalsIgnoreCase("Boring")) {
            jsonObject = new JSONObject();
            jsonObject.put("time", videoStopTime);
            jsonObject.put("coment", coment);
            jsonArray9.put(jsonObject);
            reactionMainObject.put(dialog_name, jsonArray9);
        }

    }

    private void submitReactionPart() {
        if (reactionMainObject.length() == 0) {
            setScreen();
        } else {
            pd.show();
            ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
            ReactionPost reactionPost = new ReactionPost("" + reactionMainObject, cf_id, user_id, cmp_id);
            Log.d("send json object", "json" + reactionMainObject);
            Call<ReactionResponse> responseCall = apiInterface.submitReactionPart(token, reactionPost);
            responseCall.enqueue(new Callback<ReactionResponse>() {
                @Override
                public void onResponse(Call<ReactionResponse> call, Response<ReactionResponse> response) {
                    pd.dismiss();
                    if (response.body() == null) {
                        Toast.makeText(ReactionWatchVideo.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                        player.release();
                        playVideo();
                    } else {
                        if (response.body().getCode().equals(SUCCESS)) {
                            setScreen();
                        } else {
                            Toast.makeText(ReactionWatchVideo.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ReactionResponse> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(ReactionWatchVideo.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setScreen() {
        int count = Integer.valueOf(AppPreferences.getCmpLength(this));
        int i = AppPreferences.getCmpLengthCount(this);

        if (count == 1) {
            if (arrayList.size() > i) {
                if (arrayList.get(i).equalsIgnoreCase("Pre")) {
                    AppPreferences.setQuestionType(this, "pre");
                    try {
                        stagingJson.put("2", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Post")) {
                    AppPreferences.setQuestionType(this, "post");
                    try {
                        stagingJson.put("3", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Emotion")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("4", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, FaceDetectInstructions.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Reaction")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("5", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, Reaction_Activity.class));
                    finish();
                }
            } else {
                int flag = AppPreferences.getCmpLengthCountFlag(this);
                AppPreferences.setCmpLengthCountFlag(this, flag + 1);
                try {
                    stagingJson.put("6", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, ThankyouPage.class));
                finish();
            }
        } else {
            if (arrayList.size() > i) {
                if (arrayList.get(i).equalsIgnoreCase("Pre")) {
                    AppPreferences.setQuestionType(this, "pre");
                    try {
                        stagingJson.put("2", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Post")) {
                    AppPreferences.setQuestionType(this, "post");
                    try {
                        stagingJson.put("3", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Emotion")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("4", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, FaceDetectInstructions.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Reaction")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("5", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, Reaction_Activity.class));
                    finish();
                }
            } else {
                int flag = AppPreferences.getCmpLengthCountFlag(this);
                AppPreferences.setCmpLengthCountFlag(this, flag + 1);
                try {
                    stagingJson.put("6", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, ThankyouPage.class));
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (intrupt) {
            player.play();
        }
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        player.pause();
        intrupt = true;
    }
}
