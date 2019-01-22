package com.monet_android.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.fido.fido2.api.common.RequestOptions;
import com.monet_android.R;
import com.monet_android.activity.questions.QuestionScreen;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.radarChart.RadarMyPojo;
import com.monet_android.modle.radarChart.RadarResponse;
import com.monet_android.utils.AppPreferences;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.R.drawable.ic_camp;
import static com.monet_android.R.drawable.ic_leader_image;
import static com.monet_android.R.drawable.ic_more;
import static com.monet_android.utils.AppConstant.IMG_BASE_URL;
import static com.monet_android.utils.AppConstant.SUCCESS;

public class EmotionMore extends AppCompatActivity {

    public static final int NB_QUALITIES1 = 7;
    public static final float MAX1 = 10, MIN1 = 0f;
    private RadarChart chartHallOfFame;
    private Button lastVideo, hallOfFame;
    private ApiInterface apiInterface;
    private String token;
    private ImageView img_emotion_back;
    private ProgressDialog pd;
    ArrayList<RadarEntry> videoOthers = new ArrayList<>();
    ArrayList<String> videoOthersImages = new ArrayList<>();
    ArrayList<RadarEntry> hallOthers = new ArrayList<>();
    ArrayList<String> hallOthersImages = new ArrayList<>();
    ArrayList<RadarEntry> videoYou = new ArrayList<>();
    ArrayList<String> videoYouImages = new ArrayList<>();
    ArrayList<RadarEntry> hallYou = new ArrayList<>();
    ArrayList<String> hallYouImages = new ArrayList<>();
    private Dialog dialog;
    private String radarType = "video", imageType = "";
    ArrayList<IRadarDataSet> sets = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_more);

        initView();
    }

    @SuppressLint("NewApi")
    private void initView() {

        chartHallOfFame = findViewById(R.id.chartHallOfFame);
        lastVideo = findViewById(R.id.btnLastVideo);
        hallOfFame = findViewById(R.id.btnHallOfFame);
        img_emotion_back = findViewById(R.id.img_emotion_back);

        apiInterface = BaseUrl.getClient().create(ApiInterface.class);

        token = "Bearer " + AppPreferences.getApiToken(this);

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading....");

        chartHallOfFame.getDescription().setEnabled(false);
        chartHallOfFame.setWebLineWidth(1f);
        chartHallOfFame.setWebColor(Color.BLACK);
        chartHallOfFame.setWebLineWidth(1f);
        chartHallOfFame.setWebColorInner(Color.BLACK);
        chartHallOfFame.setWebAlpha(100);

        lastVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radarType = "video";
                setDataHallOfFame();
                viewHallOfFame();
            }
        });

        hallOfFame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radarType = "all";
                setDataHallOfFame();
                viewHallOfFame();
            }
        });

        img_emotion_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        chartHallOfFame.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (h.getDataSetIndex() == 1) {
                    imageType = "you";
                    showImageDialog(h.getX());
                } else {
                    imageType = "other";
                    showImageDialog(h.getX());
                }

            }

            @Override
            public void onNothingSelected() {
            }
        });

        getLastVideoWatch();
        getHallOfFame();
        setDataHallOfFame();
    }

    private void getLastVideoWatch() {
        pd.show();
        Call<RadarMyPojo> pojoCall = apiInterface.getLastVideoChart(token);
        pojoCall.enqueue(new Callback<RadarMyPojo>() {
            @Override
            public void onResponse(Call<RadarMyPojo> call, Response<RadarMyPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(EmotionMore.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        setLastVideoData(response);
                        setDataHallOfFame();
                    } else {
                        Toast.makeText(EmotionMore.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<RadarMyPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(EmotionMore.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setLastVideoData(Response<RadarMyPojo> response) {
        for (int i = 0; i < NB_QUALITIES1; i++) {

            float other = 0;
            float user = 0;
            if (i == 0) {
                other = Float.parseFloat(response.body().getResponse().getHappy().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getHappy().getValue().getCurrent());
                if (response.body().getResponse().getHappy().getImage_path().getAll().isEmpty() || response.body().getResponse().getHappy().getImage_path().getAll() == null) {
                    videoOthersImages.add(0, "");
                } else {
                    videoOthersImages.add(0, IMG_BASE_URL + response.body().getResponse().getHappy().getImage_path().getAll());
                }

                if (response.body().getResponse().getHappy().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getHappy().getImage_path().getCurrent() == null) {
                    videoYouImages.add(0, "");
                } else {
                    videoYouImages.add(0, IMG_BASE_URL + response.body().getResponse().getHappy().getImage_path().getCurrent());
                }


            }
            if (i == 1) {
                other = Float.parseFloat(response.body().getResponse().getSuprised().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getSuprised().getValue().getCurrent());
                if (response.body().getResponse().getSuprised().getImage_path().getAll().isEmpty() || response.body().getResponse().getSuprised().getImage_path().getAll() == null) {
                    videoOthersImages.add(1, "");
                } else {
                    videoOthersImages.add(1, IMG_BASE_URL + response.body().getResponse().getSuprised().getImage_path().getAll());
                }

                if (response.body().getResponse().getSuprised().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getSuprised().getImage_path().getCurrent() == null) {
                    videoYouImages.add(1, "");
                } else {
                    videoYouImages.add(1, IMG_BASE_URL + response.body().getResponse().getSuprised().getImage_path().getCurrent());
                }
            }
            if (i == 2) {
                other = Float.parseFloat(response.body().getResponse().getScared().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getScared().getValue().getCurrent());
                if (response.body().getResponse().getScared().getImage_path().getAll().isEmpty() || response.body().getResponse().getScared().getImage_path().getAll() == null) {
                    videoOthersImages.add(2, "");
                } else {
                    videoOthersImages.add(2, IMG_BASE_URL + response.body().getResponse().getScared().getImage_path().getAll());
                }

                if (response.body().getResponse().getScared().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getScared().getImage_path().getCurrent() == null) {
                    videoYouImages.add(2, "");
                } else {
                    videoYouImages.add(2, IMG_BASE_URL + response.body().getResponse().getScared().getImage_path().getCurrent());
                }
            }
            if (i == 3) {
                other = Float.parseFloat(response.body().getResponse().getDisgusted().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getDisgusted().getValue().getCurrent());
                if (response.body().getResponse().getDisgusted().getImage_path().getAll().isEmpty() || response.body().getResponse().getDisgusted().getImage_path().getAll() == null) {
                    videoOthersImages.add(3, "");
                } else {
                    videoOthersImages.add(3, IMG_BASE_URL + response.body().getResponse().getDisgusted().getImage_path().getAll());
                }

                if (response.body().getResponse().getDisgusted().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getDisgusted().getImage_path().getCurrent() == null) {
                    videoYouImages.add(3, "");
                } else {
                    videoYouImages.add(3, IMG_BASE_URL + response.body().getResponse().getDisgusted().getImage_path().getCurrent());
                }
            }
            if (i == 4) {
                other = Float.parseFloat(response.body().getResponse().getAngry().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getAngry().getValue().getCurrent());
                if (response.body().getResponse().getAngry().getImage_path().getAll().isEmpty() || response.body().getResponse().getAngry().getImage_path().getAll() == null) {
                    videoOthersImages.add(4, "");
                } else {
                    videoOthersImages.add(4, IMG_BASE_URL + response.body().getResponse().getAngry().getImage_path().getAll());
                }

                if (response.body().getResponse().getAngry().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getAngry().getImage_path().getCurrent() == null) {
                    videoYouImages.add(4, "");
                } else {
                    videoYouImages.add(4, IMG_BASE_URL + response.body().getResponse().getAngry().getImage_path().getCurrent());
                }
            }
            if (i == 5) {
                other = Float.parseFloat(response.body().getResponse().getSad().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getSad().getValue().getCurrent());
                if (response.body().getResponse().getSad().getImage_path().getAll().isEmpty() || response.body().getResponse().getSad().getImage_path().getAll() == null) {
                    videoOthersImages.add(5, "");
                } else {
                    videoOthersImages.add(5, IMG_BASE_URL + response.body().getResponse().getSad().getImage_path().getAll());
                }

                if (response.body().getResponse().getSad().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getSad().getImage_path().getCurrent() == null) {
                    videoYouImages.add(5, "");
                } else {
                    videoYouImages.add(5, IMG_BASE_URL + response.body().getResponse().getSad().getImage_path().getCurrent());
                }
            }
            if (i == 6) {
                other = Float.parseFloat(response.body().getResponse().getNeutral().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getNeutral().getValue().getCurrent());
                if (response.body().getResponse().getNeutral().getImage_path().getAll().isEmpty() || response.body().getResponse().getNeutral().getImage_path().getAll() == null) {
                    videoOthersImages.add(6, "");
                } else {
                    videoOthersImages.add(6, IMG_BASE_URL + response.body().getResponse().getNeutral().getImage_path().getAll());
                }

                if (response.body().getResponse().getNeutral().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getNeutral().getImage_path().getCurrent() == null) {
                    videoYouImages.add(6, "");
                } else {
                    videoYouImages.add(6, IMG_BASE_URL + response.body().getResponse().getNeutral().getImage_path().getCurrent());
                }
            }

            videoOthers.add(new RadarEntry(other));
            videoYou.add(new RadarEntry(user));
        }
        viewHallOfFame();
    }

    private void getHallOfFame() {
        pd.show();
        Call<RadarMyPojo> pojoCall = apiInterface.getHallOfFame(token);
        pojoCall.enqueue(new Callback<RadarMyPojo>() {
            @Override
            public void onResponse(Call<RadarMyPojo> call, Response<RadarMyPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(EmotionMore.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        setHallFameData(response);
                    } else {
                        Toast.makeText(EmotionMore.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<RadarMyPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(EmotionMore.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setHallFameData(Response<RadarMyPojo> response) {

        for (int i = 0; i < NB_QUALITIES1; i++) {

            float other = 0;
            float user = 0;
            if (i == 0) {
                other = Float.parseFloat(response.body().getResponse().getHappy().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getHappy().getValue().getCurrent());
                if (response.body().getResponse().getHappy().getImage_path().getAll().isEmpty() || response.body().getResponse().getHappy().getImage_path().getAll() == null) {
                    hallOthersImages.add(0, "");
                } else {
                    hallOthersImages.add(0, IMG_BASE_URL + response.body().getResponse().getHappy().getImage_path().getAll());
                }

                if (response.body().getResponse().getHappy().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getHappy().getImage_path().getCurrent() == null) {
                    hallYouImages.add(0, "");
                } else {
                    hallYouImages.add(0, IMG_BASE_URL + response.body().getResponse().getHappy().getImage_path().getCurrent());
                }
            }
            if (i == 1) {
                other = Float.parseFloat(response.body().getResponse().getSuprised().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getSuprised().getValue().getCurrent());
                if (response.body().getResponse().getSuprised().getImage_path().getAll().isEmpty() || response.body().getResponse().getSuprised().getImage_path().getAll() == null) {
                    hallOthersImages.add(1, "");
                } else {
                    hallOthersImages.add(1, IMG_BASE_URL + response.body().getResponse().getSuprised().getImage_path().getAll());
                }

                if (response.body().getResponse().getSuprised().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getSuprised().getImage_path().getCurrent() == null) {
                    hallYouImages.add(1, "");
                } else {
                    hallYouImages.add(1, IMG_BASE_URL + response.body().getResponse().getSuprised().getImage_path().getCurrent());
                }
            }
            if (i == 2) {
                other = Float.parseFloat(response.body().getResponse().getScared().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getScared().getValue().getCurrent());
                if (response.body().getResponse().getScared().getImage_path().getAll().isEmpty() || response.body().getResponse().getScared().getImage_path().getAll() == null) {
                    hallOthersImages.add(2, "");
                } else {
                    hallOthersImages.add(2, IMG_BASE_URL + response.body().getResponse().getScared().getImage_path().getAll());
                }

                if (response.body().getResponse().getScared().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getScared().getImage_path().getCurrent() == null) {
                    hallYouImages.add(2, "");
                } else {
                    hallYouImages.add(2, IMG_BASE_URL + response.body().getResponse().getScared().getImage_path().getCurrent());
                }
            }
            if (i == 3) {
                other = Float.parseFloat(response.body().getResponse().getDisgusted().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getDisgusted().getValue().getCurrent());
                if (response.body().getResponse().getDisgusted().getImage_path().getAll().isEmpty() || response.body().getResponse().getDisgusted().getImage_path().getAll() == null) {
                    hallOthersImages.add(3, "");
                } else {
                    hallOthersImages.add(3, IMG_BASE_URL + response.body().getResponse().getDisgusted().getImage_path().getAll());
                }

                if (response.body().getResponse().getDisgusted().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getDisgusted().getImage_path().getCurrent() == null) {
                    hallYouImages.add(3, "");
                } else {
                    hallYouImages.add(3, IMG_BASE_URL + response.body().getResponse().getDisgusted().getImage_path().getCurrent());
                }
            }
            if (i == 4) {
                other = Float.parseFloat(response.body().getResponse().getAngry().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getAngry().getValue().getCurrent());
                if (response.body().getResponse().getAngry().getImage_path().getAll().isEmpty() || response.body().getResponse().getAngry().getImage_path().getAll() == null) {
                    hallOthersImages.add(4, "");
                } else {
                    hallOthersImages.add(4, IMG_BASE_URL + response.body().getResponse().getAngry().getImage_path().getAll());
                }

                if (response.body().getResponse().getAngry().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getAngry().getImage_path().getCurrent() == null) {
                    hallYouImages.add(4, "");
                } else {
                    hallYouImages.add(4, IMG_BASE_URL + response.body().getResponse().getAngry().getImage_path().getCurrent());
                }
            }
            if (i == 5) {
                other = Float.parseFloat(response.body().getResponse().getSad().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getSad().getValue().getCurrent());
                if (response.body().getResponse().getSad().getImage_path().getAll().isEmpty() || response.body().getResponse().getSad().getImage_path().getAll() == null) {
                    hallOthersImages.add(5, "");
                } else {
                    hallOthersImages.add(5, IMG_BASE_URL + response.body().getResponse().getSad().getImage_path().getAll());
                }

                if (response.body().getResponse().getSad().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getSad().getImage_path().getCurrent() == null) {
                    hallYouImages.add(5, "");
                } else {
                    hallYouImages.add(5, IMG_BASE_URL + response.body().getResponse().getSad().getImage_path().getCurrent());
                }
            }
            if (i == 6) {
                other = Float.parseFloat(response.body().getResponse().getNeutral().getValue().getAll());
                user = Float.parseFloat(response.body().getResponse().getNeutral().getValue().getCurrent());
                if (response.body().getResponse().getNeutral().getImage_path().getAll().isEmpty() || response.body().getResponse().getNeutral().getImage_path().getAll() == null) {
                    hallOthersImages.add(6, "");
                } else {
                    hallOthersImages.add(6, IMG_BASE_URL + response.body().getResponse().getNeutral().getImage_path().getAll());
                }

                if (response.body().getResponse().getNeutral().getImage_path().getCurrent().isEmpty() || response.body().getResponse().getNeutral().getImage_path().getCurrent() == null) {
                    hallYouImages.add(6, "");
                } else {
                    hallYouImages.add(6, IMG_BASE_URL + response.body().getResponse().getNeutral().getImage_path().getCurrent());
                }
            }

            hallOthers.add(new RadarEntry(other));
            hallYou.add(new RadarEntry(user));
        }
        viewHallOfFame();

    }

    private void setDataHallOfFame() {

        RadarDataSet set1 = null;
        RadarDataSet set2 = null;

        if (radarType.equals("video")) {
            set1 = new RadarDataSet(videoOthers, "Others");
            set2 = new RadarDataSet(videoYou, "You");
        } else {
            set1 = new RadarDataSet(hallOthers, "Others");
            set2 = new RadarDataSet(hallYou, "You");
        }


        set1.setColor(Color.rgb(255, 51, 51));
        set1.setFillColor(Color.rgb(255, 153, 153));
        set1.setDrawFilled(true);
        set1.setFillAlpha(200);
        set1.setLineWidth(2f);
        set1.setDrawHighlightIndicators(false);
        set1.setDrawHighlightCircleEnabled(true);


        set2.setColor(Color.rgb(38, 40, 79));
        set2.setFillColor(Color.rgb(215, 215, 229));
        set2.setDrawFilled(true);
        set2.setFillAlpha(200);
        set2.setLineWidth(2f);
        set2.setDrawHighlightIndicators(false);
        set2.setDrawHighlightCircleEnabled(true);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(20f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.BLACK);

        chartHallOfFame.setData(data);
        chartHallOfFame.invalidate();

    }

    private void viewHallOfFame() {

        //animate the chart
        chartHallOfFame.animateXY(2400, 2400, Easing.EasingOption.EaseInOutCirc, Easing.EasingOption.EaseInOutCirc);

        //we define axis
        XAxis xAxis = chartHallOfFame.getXAxis();
        xAxis.setTextSize(14f);
        xAxis.setYOffset(0);
        xAxis.setXOffset(0);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private String[] qualities = new String[]{"happy", "suprised", "scared", "disgusted", "angry", "sad", "neutral"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return qualities[(int) value % qualities.length];
            }
        });

        xAxis.setTextColor(Color.BLACK);
        YAxis yAxis = chartHallOfFame.getYAxis();
        yAxis.setLabelCount(NB_QUALITIES1, true);
        yAxis.setTextSize(12f);
        yAxis.setAxisMinimum(MIN1);
        yAxis.setAxisMaximum(MAX1);   //we define min and max for axis
        yAxis.setDrawLabels(false);

        //configure legend for radar chart
        Legend l = chartHallOfFame.getLegend();
        l.setTextSize(16f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setXEntrySpace(10f);
        l.setYEntrySpace(10f);
        l.setTextColor(Color.BLACK);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoOthers.clear();
        hallOthers.clear();
        videoYou.clear();
        hallYou.clear();
    }

    @SuppressLint("NewApi")
    public void showImageDialog(float value) {
        dialog.setContentView(R.layout.dialog_image);

        CircleImageView img_radar = dialog.findViewById(R.id.img_radar);

        img_radar.postInvalidate();
        /*If radar type is true then the values for hall of fame else last video watch*/
        if (radarType.equals("all")) {
            /*if image type is true the images will show for you else images show for others*/
            if (imageType.equals("you")) {
                if (value == 0.0) {
                    if (hallYouImages.get(0).isEmpty() || hallYouImages.get(0) == null) {
                        img_radar.setImageResource(R.drawable.ic_happy);
                    } else {
                        Glide.with(this).load(hallYouImages.get(0))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 1.0) {
                    if (hallYouImages.get(1).isEmpty() || hallYouImages.get(1) == null) {
                        img_radar.setImageResource(R.drawable.ic_surprised);
                    } else {
                        Glide.with(this).load(hallYouImages.get(1))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 2.0) {
                    if (hallYouImages.get(2).isEmpty() || hallYouImages.get(2) == null) {
                        img_radar.setImageResource(R.drawable.ic_scared);
                    } else {
                        Glide.with(this).load(hallYouImages.get(2))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 3.0) {
                    if (hallYouImages.get(3).isEmpty() || hallYouImages.get(3) == null) {
                        img_radar.setImageResource(R.drawable.ic_disgusted);
                    } else {
                        Glide.with(this).load(hallYouImages.get(3))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 4.0) {
                    if (hallYouImages.get(4).isEmpty() || hallYouImages.get(4) == null) {
                        img_radar.setImageResource(R.drawable.ic_angry);
                    } else {
                        Glide.with(this).load(hallYouImages.get(4))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 5.0) {
                    if (hallYouImages.get(5).isEmpty() || hallYouImages.get(5) == null) {
                        img_radar.setImageResource(R.drawable.ic_sad);
                    } else {
                        Glide.with(this).load(hallYouImages.get(5))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 6.0) {
                    if (hallYouImages.get(6).isEmpty() || hallYouImages.get(6) == null) {
                        img_radar.setImageResource(R.drawable.ic_neutral);
                    } else {
                        Glide.with(this).load(hallYouImages.get(6))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                }
            } else {
                if (value == 0.0) {
                    if (hallOthersImages.get(0).isEmpty() || hallOthersImages.get(0) == null) {
                        img_radar.setImageResource(R.drawable.ic_happy);
                    } else {
                        Glide.with(this).load(hallOthersImages.get(0))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 1.0) {
                    if (hallOthersImages.get(1).isEmpty() || hallOthersImages.get(1) == null) {
                        img_radar.setImageResource(R.drawable.ic_surprised);
                    } else {
                        Glide.with(this).load(hallOthersImages.get(1))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 2.0) {
                    if (hallOthersImages.get(2).isEmpty() || hallOthersImages.get(2) == null) {
                        img_radar.setImageResource(R.drawable.ic_scared);
                    } else {
                        Glide.with(this).load(hallOthersImages.get(2))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 3.0) {
                    if (hallOthersImages.get(3).isEmpty() || hallOthersImages.get(3) == null) {
                        img_radar.setImageResource(R.drawable.ic_disgusted);
                    } else {
                        Glide.with(this).load(hallOthersImages.get(3))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 4.0) {
                    if (hallOthersImages.get(4).isEmpty() || hallOthersImages.get(4) == null) {
                        img_radar.setImageResource(R.drawable.ic_angry);
                    } else {
                        Glide.with(this).load(hallOthersImages.get(4))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 5.0) {
                    if (hallOthersImages.get(5).isEmpty() || hallOthersImages.get(5) == null) {
                        img_radar.setImageResource(R.drawable.ic_sad);
                    } else {
                        Glide.with(this).load(hallOthersImages.get(5))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 6.0) {
                    if (hallOthersImages.get(6).isEmpty() || hallOthersImages.get(6) == null) {
                        img_radar.setImageResource(R.drawable.ic_neutral);
                    } else {
                        Glide.with(this).load(hallOthersImages.get(6))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                }
            }
        } else {
            if (imageType.equals("you")) {
                if (value == 0.0) {
                    if (videoYouImages.get(0).isEmpty() || videoYouImages.get(0) == null) {
                        img_radar.setImageResource(R.drawable.ic_happy);
                    } else {
                        Glide.with(this).load(videoYouImages.get(0))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 1.0) {
                    if (videoYouImages.get(1).isEmpty() || videoYouImages.get(1) == null) {
                        img_radar.setImageResource(R.drawable.ic_surprised);
                    } else {
                        Glide.with(this).load(videoYouImages.get(1))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 2.0) {
                    if (videoYouImages.get(2).isEmpty() || videoYouImages.get(2) == null) {
                        img_radar.setImageResource(R.drawable.ic_scared);
                    } else {
                        Glide.with(this).load(videoYouImages.get(2))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 3.0) {
                    if (videoYouImages.get(3).isEmpty() || videoYouImages.get(3) == null) {
                        img_radar.setImageResource(R.drawable.ic_disgusted);
                    } else {
                        Glide.with(this).load(videoYouImages.get(3))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 4.0) {
                    if (videoYouImages.get(4).isEmpty() || videoYouImages.get(4) == null) {
                        img_radar.setImageResource(R.drawable.ic_angry);
                    } else {
                        Glide.with(this).load(videoYouImages.get(4))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 5.0) {
                    if (videoYouImages.get(5).isEmpty() || videoYouImages.get(5) == null) {
                        img_radar.setImageResource(R.drawable.ic_sad);
                    } else {
                        Glide.with(this).load(videoYouImages.get(5))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 6.0) {
                    if (videoYouImages.get(6).isEmpty() || videoYouImages.get(6) == null) {
                        img_radar.setImageResource(R.drawable.ic_neutral);
                    } else {
                        Glide.with(this).load(videoYouImages.get(6))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                }
            } else {
                if (value == 0.0) {
                    if (videoOthersImages.get(0).isEmpty() || videoOthersImages.get(0) == null) {
                        img_radar.setImageResource(R.drawable.ic_happy);
                    } else {
                        Glide.with(this).load(videoOthersImages.get(0))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 1.0) {
                    if (videoOthersImages.get(1).isEmpty() || videoOthersImages.get(1) == null) {
                        img_radar.setImageResource(R.drawable.ic_surprised);
                    } else {
                        Glide.with(this).load(videoOthersImages.get(1))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 2.0) {
                    if (videoOthersImages.get(2).isEmpty() || videoOthersImages.get(2) == null) {
                        img_radar.setImageResource(R.drawable.ic_scared);
                    } else {
                        Glide.with(this).load(videoOthersImages.get(2))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 3.0) {
                    if (videoOthersImages.get(3).isEmpty() || videoOthersImages.get(3) == null) {
                        img_radar.setImageResource(R.drawable.ic_disgusted);
                    } else {
                        Glide.with(this).load(videoOthersImages.get(3))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 4.0) {
                    if (videoOthersImages.get(4).isEmpty() || videoOthersImages.get(4) == null) {
                        img_radar.setImageResource(R.drawable.ic_angry);
                    } else {
                        Glide.with(this).load(videoOthersImages.get(4))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 5.0) {
                    if (videoOthersImages.get(5).isEmpty() || videoOthersImages.get(5) == null) {
                        img_radar.setImageResource(R.drawable.ic_sad);
                    } else {
                        Glide.with(this).load(videoOthersImages.get(5))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                } else if (value == 6.0) {
                    if (videoOthersImages.get(6).isEmpty() || videoOthersImages.get(6) == null) {
                        img_radar.setImageResource(R.drawable.ic_neutral);
                    } else {
                        Glide.with(this).load(videoOthersImages.get(6))
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(img_radar);
                    }
                }
            }
        }
        dialog.show();
    }
}