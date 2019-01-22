package com.monet_android.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.monet_android.R;
import com.monet_android.activity.EmotionMore;
import com.monet_android.activity.ExperiencePointsScreen;
import com.monet_android.activity.ProfileScreen;
import com.monet_android.activity.SettingsScreen;
import com.monet_android.activity.TellAFriendScreen;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.countryCode.CountryPojo;
import com.monet_android.modle.countryCode.CountryResponse;
import com.monet_android.utils.AppPreferences;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppConstant.SUCCESS;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment implements View.OnClickListener {

    LinearLayout account_layout, setting_layout, friend_layout, points_layout, emotion_layout;
    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_more, container, false);

        account_layout = view.findViewById(R.id.account_layout);
        setting_layout = view.findViewById(R.id.setting_layout);
        friend_layout = view.findViewById(R.id.friend_layout);
        points_layout = view.findViewById(R.id.points_layout);
        emotion_layout = view.findViewById(R.id.emotion_layout);

        account_layout.setOnClickListener(this);
        setting_layout.setOnClickListener(this);
        friend_layout.setOnClickListener(this);
        points_layout.setOnClickListener(this);
        emotion_layout.setOnClickListener(this);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading....");
        pd.setCanceledOnTouchOutside(false);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.account_layout:
                startActivity(new Intent(getContext(), ProfileScreen.class));
                break;
            case R.id.setting_layout:
                startActivity(new Intent(getContext(), SettingsScreen.class));
                break;
            case R.id.friend_layout:
                startActivity(new Intent(getContext(), TellAFriendScreen.class));
                break;
            case R.id.points_layout:
                startActivity(new Intent(getContext(), ExperiencePointsScreen.class));
                break;
            case R.id.emotion_layout:
                startActivity(new Intent(getContext(), EmotionMore.class));
                break;
        }
    }
}
