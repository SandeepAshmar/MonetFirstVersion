
package com.monet_android.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.monet_android.R;
import com.monet_android.activity.questions.QuestionQCard;
import com.monet_android.activity.questions.QuestionScreen;
import com.monet_android.activity.reaction.Reaction_Activity;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.halper.OnClearFromRecentService;
import com.monet_android.modle.getCampignFlow.GetCampFlowPojo;
import com.monet_android.modle.staging.StagingPojo;
import com.monet_android.qCards.FaceDetectInstructions;
import com.monet_android.utils.AppPreferences;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.qCards.LandingScreen.arrayList;
import static com.monet_android.qCards.LandingScreen.stagingJson;
import static com.monet_android.utils.AppConstant.HIT_CANCEL;
import static com.monet_android.utils.AppConstant.SUCCESS;

public class ThankyouPage extends AppCompatActivity {


    private Button btn_cam_proceed;
    private TextView progress_text, tv_cam_first, tv_proceedText;
    private Handler handler = new Handler();
    private int pStatus = 0;
    private int setMinValue = 0;
    private int setMaxValue = 0;
    private DonutProgress donutProgress;
    private ProgressDialog pd;
    private String token;
    private OnClearFromRecentService onClearFromRecentService;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_first_camp_screen);

        donutProgress = findViewById(R.id.donut_progress_lodging);
        progress_text = findViewById(R.id.progress_text);
        tv_cam_first = findViewById(R.id.tv_cam_first);
        tv_proceedText = findViewById(R.id.tv_proceedText);
        btn_cam_proceed = findViewById(R.id.btn_cam_proceed);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading....");

        onClearFromRecentService = new OnClearFromRecentService();
        setDetails();

        btn_cam_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_cam_proceed.getText().equals("finish")) {
                    AppPreferences.setDirectHitCancel(ThankyouPage.this, false);
                    try {
                        stagingJson.put("6", "2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    setMaxValue = 0;
                    setMinValue = 0;
                    pStatus = 0;

                    HIT_CANCEL = false;
                    onClearFromRecentService.onTaskRemoved(new Intent(ThankyouPage.this, OnClearFromRecentService.class));

                    boolean value = AppPreferences.isUserLoggedOut(ThankyouPage.this);
                    if (value == false) {
                        startActivity(new Intent(ThankyouPage.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(ThankyouPage.this, LoginScreen.class));
                        finish();
                    }
                    AppPreferences.setRedirectApiToken(ThankyouPage.this, "");
                    if (AppPreferences.getSuccessUrl(ThankyouPage.this).contains("https://")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppPreferences.getSuccessUrl(ThankyouPage.this)));
                        startActivity(browserIntent);
                        AppPreferences.setSuccessUrl(ThankyouPage.this, "");
                    }
                } else {
                    setScreen();
                }
            }
        });
    }

    private void setDetails() {

        if (setMaxValue == 0) {
            setMaxValue = AppPreferences.getCmpLength(this);
        }
        setMinValue = AppPreferences.getCmpLengthCountFlag(this);

        if (setMaxValue == 1) {
            AppPreferences.setCmpLength(this, 0);
            AppPreferences.setCmpLengthCountFlag(this, 0);
            AppPreferences.setCmpLengthCount(this, 0);
        } else {
            if (setMinValue == 1) {
                tv_cam_first.setText(R.string.complete_first);
                tv_proceedText.setText(R.string.proceed_first);
                tv_proceedText.setTextSize(16);
                tv_cam_first.setTextSize(16);
                btn_cam_proceed.setText("proceed");
            } else if (setMinValue == 2) {
                tv_cam_first.setText(R.string.complete_secound);
                tv_proceedText.setText(R.string.proceed_secound);
                tv_proceedText.setTextSize(16);
                tv_cam_first.setTextSize(16);
                btn_cam_proceed.setText("proceed");
            } else if (setMinValue == 3) {
                tv_cam_first.setText(R.string.complete_third);
                tv_proceedText.setText(R.string.proceed_third);
                tv_proceedText.setTextSize(16);
                tv_cam_first.setTextSize(16);
                btn_cam_proceed.setText("proceed");
            }
        }

        donutProgress.setText("");
        donutProgress.setMax(setMaxValue);

        runSeekBar();

        if (setMinValue == setMaxValue) {
            tv_cam_first.setText(R.string.complete_congrats);
            tv_proceedText.setText(R.string.proceed_forth);
            tv_proceedText.setTextSize(16);
            tv_cam_first.setTextSize(16);
            btn_cam_proceed.setText("finish");
        }
    }

    public void runSeekBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus < setMinValue) {
                    pStatus += 1;
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            donutProgress.setDonut_progress("" + pStatus);
                            progress_text.setText(pStatus + "/" + setMaxValue);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void setScreen() {
        int count = Integer.valueOf(AppPreferences.getCmpLength(this));
        int i = AppPreferences.getCmpLengthCount(this);

        if (count == 1) {
            setDetails();
            runSeekBar();
            AppPreferences.setCmpLength(this, 0);
            AppPreferences.setCmpLengthCountFlag(this, 0);
            AppPreferences.setCmpLengthCount(this, 0);
        } else {

            if (arrayList.size() > i) {
                if (arrayList.get(i).equalsIgnoreCase("Pre")) {
                    AppPreferences.setQuestionType(this, "pre");
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Post")) {
                    AppPreferences.setQuestionType(this, "post");
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Emotion")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, FaceDetectInstructions.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Reaction")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, Reaction_Activity.class));
                    finish();
                }
            } else {

                int flag = AppPreferences.getCmpLengthCountFlag(this);
                AppPreferences.setCmpLengthCountFlag(this, flag + 1);
                getData();
            }

        }

    }

    private void getData() {
        pd.show();
        String cmp_id = AppPreferences.getCmpId(this);
        if (AppPreferences.isRedirectUser(this).equalsIgnoreCase("no")) {
            token = "Bearer " + AppPreferences.getApiToken(this);
        } else {
            token = "Bearer " + AppPreferences.getRedirectApiToken(this);
        }
        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        Call<GetCampFlowPojo> pojoCall = apiInterface.getCampFlow(token, cmp_id);
        pojoCall.enqueue(new Callback<GetCampFlowPojo>() {
            @Override
            public void onResponse(Call<GetCampFlowPojo> call, Response<GetCampFlowPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(ThankyouPage.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        AppPreferences.setCmpLength(ThankyouPage.this, setMaxValue);
                        AppPreferences.setCmpLengthCountFlag(ThankyouPage.this, setMinValue);
                        arrayList.clear();
                        arrayList.addAll(response.body().getResponse().getCmp_sequence());
                        AppPreferences.setCamEval(ThankyouPage.this, response.body().getResponse().getCmp_eval());

                        if (arrayList.get(0).equalsIgnoreCase("Pre")) {
                            AppPreferences.setQuestionType(ThankyouPage.this, "pre");
                            startActivity(new Intent(ThankyouPage.this, QuestionScreen.class));
                            finish();
                        } else if (arrayList.get(0).equalsIgnoreCase("Post")) {
                            AppPreferences.setQuestionType(ThankyouPage.this, "post");
                            startActivity(new Intent(ThankyouPage.this, QuestionScreen.class));
                            finish();
                        } else if (arrayList.get(0).equalsIgnoreCase("Emotion")) {
                            startActivity(new Intent(ThankyouPage.this, FaceDetectInstructions.class));
                            finish();
                        } else if (arrayList.get(0).equalsIgnoreCase("Reaction")) {
                            startActivity(new Intent(ThankyouPage.this, Reaction_Activity.class));
                            finish();
                        }

                    } else {
                        Toast.makeText(ThankyouPage.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetCampFlowPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(ThankyouPage.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
