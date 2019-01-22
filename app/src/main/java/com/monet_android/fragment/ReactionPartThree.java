package com.monet_android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.monet_android.R;
import com.monet_android.utils.AppPreferences;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import static com.monet_android.utils.AppConstant.REACTION_A;
import static com.monet_android.utils.AppConstant.REACTION_B;
import static com.monet_android.utils.AppConstant.REACTION_C;
import static com.monet_android.utils.AppConstant.REACTION_D;
import static com.monet_android.utils.AppConstant.REACTION_E;
import static com.monet_android.utils.AppConstant.REACTION_F;
import static com.monet_android.utils.AppConstant.REACTION_G;
import static com.monet_android.utils.AppConstant.REACTION_H;
import static com.monet_android.utils.AppConstant.REACTION_I;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReactionPartThree extends Fragment {

    private View view;
    private String camEval;
    private ImageView  img_reactionIconA, img_reactionIconB, img_reactionIconC, img_reactionIconD, img_reactionIconE, img_reactionIconF, img_reactionIconG, img_reactionIconH, img_reactionIconI;
    private TextView tv_reactionNameA, tv_reactionNameB, tv_reactionNameC, tv_reactionNameD, tv_reactionNameE, tv_reactionNameF, tv_reactionNameG, tv_reactionNameH, tv_reactionNameI;
    private LinearLayout ll_reactionA, ll_reactionB, ll_reactionC, ll_reactionD, ll_reactionE, ll_reactionF, ll_reactionG, ll_reactionH, ll_reactionI;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reaction_part_three, container, false);

        img_reactionIconA = view.findViewById(R.id.img_reactionIconA);
        img_reactionIconB = view.findViewById(R.id.img_reactionIconB);
        img_reactionIconC = view.findViewById(R.id.img_reactionIconC);
        img_reactionIconD = view.findViewById(R.id.img_reactionIconD);
        img_reactionIconE = view.findViewById(R.id.img_reactionIconE);
        img_reactionIconF = view.findViewById(R.id.img_reactionIconF);
        img_reactionIconG = view.findViewById(R.id.img_reactionIconG);
        img_reactionIconH = view.findViewById(R.id.img_reactionIconH);
        img_reactionIconI = view.findViewById(R.id.img_reactionIconI);
        tv_reactionNameA = view.findViewById(R.id.tv_reactionNameA);
        tv_reactionNameB = view.findViewById(R.id.tv_reactionNameB);
        tv_reactionNameC = view.findViewById(R.id.tv_reactionNameC);
        tv_reactionNameD = view.findViewById(R.id.tv_reactionNameD);
        tv_reactionNameE = view.findViewById(R.id.tv_reactionNameE);
        tv_reactionNameF = view.findViewById(R.id.tv_reactionNameF);
        tv_reactionNameG = view.findViewById(R.id.tv_reactionNameG);
        tv_reactionNameH = view.findViewById(R.id.tv_reactionNameH);
        tv_reactionNameI = view.findViewById(R.id.tv_reactionNameI);
        ll_reactionA = view.findViewById(R.id.ll_reactionA);
        ll_reactionB = view.findViewById(R.id.ll_reactionB);
        ll_reactionC = view.findViewById(R.id.ll_reactionC);
        ll_reactionD = view.findViewById(R.id.ll_reactionD);
        ll_reactionE = view.findViewById(R.id.ll_reactionE);
        ll_reactionF = view.findViewById(R.id.ll_reactionF);
        ll_reactionG = view.findViewById(R.id.ll_reactionG);
        ll_reactionH = view.findViewById(R.id.ll_reactionH);
        ll_reactionI = view.findViewById(R.id.ll_reactionI);

        camEval = AppPreferences.getCamEval(getActivity());
        camEval = camEval.replaceAll("[0-9()]", "");
        AppPreferences.setCamEval(getContext(),camEval);

        setReactionIcons();

        return view;
    }

    public void setReactionIcons(){

        if(camEval.contains("a")){
            ll_reactionA.setVisibility(View.VISIBLE);
            img_reactionIconA.setImageResource(R.drawable.ic_like);
            tv_reactionNameA.setText(R.string.reaction_A);
        }
        if(camEval.contains("b")){
            ll_reactionB.setVisibility(View.VISIBLE);
            img_reactionIconB.setImageResource(R.drawable.ic_love);
            tv_reactionNameB.setText(R.string.reaction_B);
        }
        if(camEval.contains("c")){
            ll_reactionC.setVisibility(View.VISIBLE);
            img_reactionIconC.setImageResource(R.drawable.ic_want);
            tv_reactionNameC.setText(R.string.reaction_C);
        }
        if(camEval.contains("d")){
            ll_reactionD.setVisibility(View.VISIBLE);
            img_reactionIconD.setImageResource(R.drawable.ic_memorable);
            tv_reactionNameD.setText(R.string.reaction_D);
        }
        if(camEval.contains("e")){
            ll_reactionE.setVisibility(View.VISIBLE);
            img_reactionIconE.setImageResource(R.drawable.ic_dislike);
            tv_reactionNameE.setText(R.string.reaction_E);
        }
        if(camEval.contains("f")){
            ll_reactionF.setVisibility(View.VISIBLE);
            img_reactionIconF.setImageResource(R.drawable.ic_helpful);
            tv_reactionNameF.setText(R.string.reaction_F);
        }
        if(camEval.contains("g")){
            ll_reactionG.setVisibility(View.VISIBLE);
            img_reactionIconG.setImageResource(R.drawable.ic_not_helpful);
            tv_reactionNameG.setText(R.string.reaction_G);
        }
        if(camEval.contains("h")){
            ll_reactionH.setVisibility(View.VISIBLE);
            img_reactionIconH.setImageResource(R.drawable.ic_engaging);
            tv_reactionNameH.setText(R.string.reaction_H);
        }
        if(camEval.contains("i")){
            ll_reactionI.setVisibility(View.VISIBLE);
            img_reactionIconI.setImageResource(R.drawable.ic_bore);
            tv_reactionNameI.setText(R.string.reaction_I);
        }


    }

}
