package com.monet_android.activity.reaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.monet_android.R;
import com.monet_android.activity.PlayVideoAndRecordScreen;
import com.monet_android.fragment.ReactionPartFour;
import com.monet_android.fragment.ReactionPartThree;
import com.monet_android.fragment.ReactionPartTwo;
import com.monet_android.fragment.ReactionPartOne;
import com.monet_android.qCards.FaceDetectInstructions;
import com.monet_android.utils.AppUtils;

import org.json.JSONException;

import static com.monet_android.qCards.LandingScreen.stagingJson;

public class Reaction_Activity extends AppCompatActivity {

    private SeekBar sb_reaction;
    private ImageView img_reactionBack;
    private TextView tv_rectionCount;
    private String layoutShow = "first_layout";
    private Button btn_reactionProceed, btn_reactionExit, btn_reactionNext;
    private RelativeLayout reaction_container;
    private LinearLayout btn_exit_layout, btn_nextLayout;
    private FrameLayout reaction_frame;
    private ReactionPartTwo reactionPartTwo;
    private ReactionPartThree reactionPartThree;
    private ReactionPartFour reactionPartFour;


    @SuppressLint({"ResourceType", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction);

        sb_reaction = findViewById(R.id.sb_reaction);
        img_reactionBack = findViewById(R.id.img_reactionBack);
//        tv_rectionCount = findViewById(R.id.tv_rectionCount);
        btn_reactionExit = findViewById(R.id.btn_reactionExit);
        btn_reactionProceed = findViewById(R.id.btn_reactionProceed);
        btn_nextLayout = findViewById(R.id.btn_nextLayout);
        btn_exit_layout = findViewById(R.id.btn_exit_layout);
        reaction_frame = findViewById(R.id.reaction_frame);
        btn_reactionNext = findViewById(R.id.btn_reactionNext);

        reactionPartTwo = new ReactionPartTwo();
        reactionPartThree = new ReactionPartThree();
        reactionPartFour = new ReactionPartFour();

        setFragment(reactionPartTwo);
        setFragmentButton();
        sb_reaction.setProgress(1);

        try {
            stagingJson.put("5", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        img_reactionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.reaction_frame, fragment);
        fragmentTransaction.commit();
    }

    private void setFragmentButton(){
        btn_reactionProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setFragment(reactionPartThree);
                    layoutShow = "second_layout";
                    btn_nextLayout.setVisibility(View.VISIBLE);
                    btn_exit_layout.setVisibility(View.GONE);
                    sb_reaction.setProgress(2);
//                    tv_rectionCount.setText("2/3");

            }
        });

        btn_reactionNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutShow.equalsIgnoreCase("second_layout")){
                    setFragment(reactionPartFour);
                    btn_reactionNext.setText("watch");
                    layoutShow = "watch";
                    sb_reaction.setProgress(3);
//                    tv_rectionCount.setText("3/3");
                }else if(layoutShow.equalsIgnoreCase("watch")){
                    if(AppUtils.isConnectionAvailable(Reaction_Activity.this)){
                        try {
                            stagingJson.put("5", "3");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        btn_reactionNext.setClickable(false);
                        startActivity(new Intent(Reaction_Activity.this, ReactionWatchVideo.class));
                        finish();
                    }else{
                        AppUtils.noInternetDialog(Reaction_Activity.this);
                    }

                }
            }
        });

        btn_reactionExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
