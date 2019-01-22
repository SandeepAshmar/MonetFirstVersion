package com.monet_android.qCards;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.monet_android.R;
import com.monet_android.activity.ThankyouPage;
import com.monet_android.activity.TermsActivity;
import com.monet_android.activity.questions.QuestionQCard;
import com.monet_android.activity.reaction.Reaction_Activity;
import com.monet_android.adapter.LandAdapter;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.halper.OnClearFromRecentService;
import com.monet_android.modle.getCampDetails.GetCampDetails_Pojo;
import com.monet_android.modle.getCampDetails.GetCampDetails_Response;
import com.monet_android.utils.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppConstant.CMP_ID;
import static com.monet_android.utils.AppConstant.SUCCESS;
import static com.monet_android.utils.AppConstant.TOKEN;

public class LandingScreen extends AppCompatActivity implements View.OnClickListener {

    private String cmp_id, token;
    private CheckBox land_chack;
    private TextView tv_land_watch;
    private Button btn_landExit, btn_landProceed;
    private TextView tv_landTerms;
    private RecyclerView mRecycler;
    private LandAdapter mAdapter;
    private ArrayList<GetCampDetails_Response> detailsResponses = new ArrayList<>();
    public static ArrayList<String> arrayList = new ArrayList<String>();
    public static JSONObject stagingJson = new JSONObject();
    private ProgressDialog pd;
    private OnClearFromRecentService onClearFromRecentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        land_chack = findViewById(R.id.land_chack);
        btn_landExit = findViewById(R.id.btn_landExit);
        btn_landProceed = findViewById(R.id.btn_landProceed);
        tv_landTerms = findViewById(R.id.tv_landTerms);
        tv_land_watch = findViewById(R.id.tv_land_watch);
        mRecycler = findViewById(R.id.recyler_land);
        onClearFromRecentService = new OnClearFromRecentService();

        pd = new ProgressDialog(this);
        pd.setMessage("Loading....");
        pd.setCanceledOnTouchOutside(false);

        land_chack.setOnClickListener(this);
        btn_landExit.setOnClickListener(this);
        btn_landProceed.setOnClickListener(this);
        tv_landTerms.setOnClickListener(this);

        if (land_chack.isChecked()) {
            btn_landProceed.setBackgroundResource(R.drawable.btn_pro_activate);
            btn_landProceed.setEnabled(true);
        } else {
            btn_landProceed.setBackgroundResource(R.drawable.btn_pro_gray);
            btn_landProceed.setEnabled(false);
        }

        if(AppPreferences.isRedirectUser(this).equalsIgnoreCase("no")){
            token = "Bearer " + AppPreferences.getApiToken(this);
            cmp_id = AppPreferences.getCmpId(this);
        }else{
            token = "Bearer " + AppPreferences.getRedirectApiToken(this);
            cmp_id = AppPreferences.getCmpId(this);
        }

        mAdapter = new LandAdapter(this, detailsResponses);
        mRecycler.setAdapter(mAdapter);

        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        try {
            stagingJson.put("1", "1");
            stagingJson.put("2", "0");
            stagingJson.put("3", "0");
            stagingJson.put("4", "0");
            stagingJson.put("5", "0");
            stagingJson.put("6", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getCampDetails();
        OnClearFromRecentService onClearFromRecentService = new OnClearFromRecentService();

        TOKEN = token;
        CMP_ID = cmp_id;
    }

    public void getCampDetails() {
        pd.show();
        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        Call<GetCampDetails_Pojo> pojoCall = apiInterface.getCampDetails(token, cmp_id);
        pojoCall.enqueue(new Callback<GetCampDetails_Pojo>() {
            @Override
            public void onResponse(Call<GetCampDetails_Pojo> call, Response<GetCampDetails_Pojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(LandingScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {

                        if (response.body().getResponse().size() <= 1) {
                            mRecycler.setLayoutManager(new GridLayoutManager(LandingScreen.this, 1));
                            tv_land_watch.setText("watch " + response.body().getResponse().size() + " short clip");
                        } else {
                            mRecycler.setLayoutManager(new GridLayoutManager(LandingScreen.this, 2));
                            tv_land_watch.setText("watch " + response.body().getResponse().size() + " short clips");
                        }
                        detailsResponses.addAll(response.body().getResponse());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(LandingScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetCampDetails_Pojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(LandingScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.land_chack:
                if (land_chack.isChecked()) {
                    btn_landProceed.setBackgroundResource(R.drawable.btn_pro_activate);
                    btn_landProceed.setEnabled(true);
                } else {
                    btn_landProceed.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_landProceed.setEnabled(false);
                }
                break;

            case R.id.btn_landExit:
                try {
                    stagingJson.put("1", "3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                btn_landExit.setClickable(false);
                onBackPressed();
                break;

            case R.id.btn_landProceed:
                try {
                    stagingJson.put("1", "2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                btn_landProceed.setClickable(false);
                setScreen();
                break;

            case R.id.tv_landTerms:
                startActivity(new Intent(this, TermsActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClearFromRecentService.onTaskRemoved(new Intent(LandingScreen.this, OnClearFromRecentService.class));
        finish();
        super.onBackPressed();
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
}
