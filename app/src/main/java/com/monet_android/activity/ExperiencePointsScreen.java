package com.monet_android.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.monet_android.R;
import com.monet_android.adapter.LastWatchVideoAdapter;
import com.monet_android.adapter.LeaderBoardAdapter;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.lastWatchVideoList.LastWatchVideoPojo;
import com.monet_android.modle.leaderboard.LeaderBoardLeaders;
import com.monet_android.modle.leaderboard.LeaderBoardPojo;
import com.monet_android.modle.xpcp.XpCpPojo;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppConstant.IMG_BASE_URL;
import static com.monet_android.utils.AppConstant.SUCCESS;

public class ExperiencePointsScreen extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_exp_tab, tv_leadUserRank, tv_leadUserPoints;
    private Button btn_xpcp, btn_leaderboard;
    private CircleImageView img_leadUserImage;
    private ImageView img_experience_back;
    private RecyclerView rv_leaderBoard, rv_lastVideo;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager lastVideoWatchLayout;
    private PieChart pieChart;
    private LeaderBoardAdapter leaderBoardAdapter;
    private LastWatchVideoAdapter lastWatchVideoAdapter;
    private ArrayList<LeaderBoardLeaders> boardLeaders = new ArrayList<>();
    private ApiInterface apiInterface;
    private String token;
    private ProgressDialog pd;
    private LinearLayout ll_userLeader;
    private NestedScrollView nsv_leaderBoard;
    private RecyclerView recyclerView;
    private boolean xp = true, xpDataFatch = false;
    private ArrayList<String> xpCpList = new ArrayList<>();
    private String xp_gain, xp1, rp_id, r_cash_point, total_points_earned, last_update, title, level, max_xp, cp_gain, points, rp_u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_points_screen);

        tv_exp_tab = findViewById(R.id.tv_exp_tab);
        rv_leaderBoard = findViewById(R.id.rv_leaderBoard);
        rv_lastVideo = findViewById(R.id.rv_lastVideo);
        btn_leaderboard = findViewById(R.id.btn_leaderboard);
        btn_xpcp = findViewById(R.id.btn_xpcp);
        tv_leadUserRank = findViewById(R.id.tv_leadUserRank);
        tv_leadUserPoints = findViewById(R.id.tv_leadUserPoints);
        img_leadUserImage = findViewById(R.id.img_leadUserImage);
        ll_userLeader = findViewById(R.id.ll_userLeader);
        img_experience_back = findViewById(R.id.img_experience_back);
        nsv_leaderBoard = findViewById(R.id.nsv_leaderBoard);
        pieChart = findViewById(R.id.piechart);

        btn_leaderboard.setOnClickListener(this);
        btn_xpcp.setOnClickListener(this);
        img_experience_back.setOnClickListener(this);

        apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading....");

        token = "Bearer " + AppPreferences.getApiToken(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_leaderBoard.setLayoutManager(linearLayoutManager);
        lastVideoWatchLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_lastVideo.setLayoutManager(lastVideoWatchLayout);

        getXpCp();
        getLastWachVideo();
    }

    private void getXpCp() {
        pd.show();
        Call<XpCpPojo> pojoCall = apiInterface.getXpCp(token);
        pojoCall.enqueue(new Callback<XpCpPojo>() {
            @Override
            public void onResponse(Call<XpCpPojo> call, Response<XpCpPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    xpDataFatch = false;
                    Toast.makeText(ExperiencePointsScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        xp_gain = response.body().getResponse().getXp_gain();
                        xp1 = response.body().getResponse().getXp();
                        rp_id = response.body().getResponse().getRp_id();
                        r_cash_point = response.body().getResponse().getR_cash_point();
                        total_points_earned = response.body().getResponse().getTotal_points_earned();
                        last_update = response.body().getResponse().getLast_update();
                        title = response.body().getResponse().getTitle();
                        level = response.body().getResponse().getLevel();
                        max_xp = response.body().getResponse().getMax_xp();
                        cp_gain = response.body().getResponse().getCp_gain();
                        points = response.body().getResponse().getPoints();
                        rp_u_id = response.body().getResponse().getRp_u_id();
                        setPieChart();
                        xpDataFatch = true;
                    } else {
                        xpDataFatch = false;
                        Toast.makeText(ExperiencePointsScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<XpCpPojo> call, Throwable t) {
                pd.dismiss();
                xpDataFatch = false;
                Toast.makeText(ExperiencePointsScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPieChart() {

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.clear();

        PieDataSet dataSet = new PieDataSet(yValues, "CP Result");
        if (xp) {
            yValues.add(new PieEntry(Float.parseFloat(points), "CP"));
            yValues.add(new PieEntry(Float.parseFloat(cp_gain), "CP gain"));
            yValues.add(new PieEntry(Float.parseFloat(r_cash_point), "max cp"));
            dataSet = new PieDataSet(yValues, "CP Result");
        } else {
            yValues.add(new PieEntry(Float.parseFloat(xp1), "XP"));
        yValues.add(new PieEntry(Float.parseFloat(xp_gain),"XP gain"));
            yValues.add(new PieEntry(Float.parseFloat(max_xp), "max xp"));
            dataSet = new PieDataSet(yValues, "XP Result");
        }

        dataSet.setSliceSpace(1f);
        dataSet.setSelectionShift(1f);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(57, 102, 170));
        colors.add(Color.rgb(136, 51, 133));
        colors.add(Color.rgb(26, 148, 66));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        int colorWhite = Color.parseColor("#ffffff");
        pieChart.setEntryLabelColor(colorWhite);
        pieChart.setData(data);
        pieChart.setData(data);
    }

    private void getLastWachVideo(){
        pd.show();
        Call<LastWatchVideoPojo> pojoCall = apiInterface.getLastVideoWatchList(token);
        pojoCall.enqueue(new Callback<LastWatchVideoPojo>() {
            @Override
            public void onResponse(Call<LastWatchVideoPojo> call, Response<LastWatchVideoPojo> response) {
                pd.dismiss();
                if(response.body() == null){
                    Toast.makeText(ExperiencePointsScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                }else{
                    if(response.body().getCode().equals(SUCCESS)){

                        lastWatchVideoAdapter = new LastWatchVideoAdapter(ExperiencePointsScreen.this, response.body().getResponse());
                        rv_lastVideo.setAdapter(lastWatchVideoAdapter);
                    }
                }

            }

            @Override
            public void onFailure(Call<LastWatchVideoPojo> call, Throwable t) {

            }
        });
     }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_leaderboard:
                if (AppUtils.isConnectionAvailable(this)) {
                    boardLeaders.clear();
                    getLeaderBoard();
                    tv_exp_tab.setText("Leaderboard");
                } else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                pieChart.setVisibility(View.GONE);
                rv_lastVideo.setVisibility(View.GONE);
                break;
            case R.id.img_experience_back:
                onBackPressed();
                break;
            case R.id.btn_xpcp:
                nsv_leaderBoard.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                rv_lastVideo.setVisibility(View.VISIBLE);

                if(xpDataFatch){
                    if (xp) {
                        btn_xpcp.setText("CP Points");
                        tv_exp_tab.setText("CP Points");
                        xp = false;
                    } else {
                        btn_xpcp.setText("XP Points");
                        tv_exp_tab.setText("XP Points");
                        xp = true;
                    }
                    setPieChart();
                }else{
                    getXpCp();
                }
                break;
        }
    }

    private void getLeaderBoard() {
        pd.show();
        Call<LeaderBoardPojo> pojoCall = apiInterface.getLeaderBoard(token);
        pojoCall.enqueue(new Callback<LeaderBoardPojo>() {
            @Override
            public void onResponse(Call<LeaderBoardPojo> call, Response<LeaderBoardPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(ExperiencePointsScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        boardLeaders.addAll(response.body().getResponse().getLeaders());
                        leaderBoardAdapter = new LeaderBoardAdapter(ExperiencePointsScreen.this, boardLeaders);
                        rv_leaderBoard.setAdapter(leaderBoardAdapter);
                        nsv_leaderBoard.setVisibility(View.VISIBLE);
                        if (response.body().getResponse().getUser().getPoints() != null) {
                            int point = 0;
                            point = Integer.parseInt(response.body().getResponse().getUser().getPoints());
                            if (point > 99) {
                                tv_leadUserRank.setTextSize(10);
                            } else {
                                tv_leadUserRank.setTextSize(14);
                            }
                        }

                        if(response.body().getResponse().getUser().getPoints() == null){
                            tv_leadUserPoints.setText("0 xp");
                        }else {
                            tv_leadUserPoints.setText(response.body().getResponse().getUser().getPoints()+" xp");
                        }

                        tv_leadUserRank.setText(response.body().getResponse().getUser().getUser_rank());
                        if (response.body().getResponse().getUser().getImage_id() == null || response.body().getResponse().getUser().getImage_id().isEmpty()) {
                            img_leadUserImage.setBackgroundResource(R.drawable.ic_user);
                        } else {
                            Glide.with(ExperiencePointsScreen.this).load(IMG_BASE_URL + response.body().getResponse().getUser().getImage_id())
                                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(img_leadUserImage);
                        }

                    } else {
                        Toast.makeText(ExperiencePointsScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LeaderBoardPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(ExperiencePointsScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
