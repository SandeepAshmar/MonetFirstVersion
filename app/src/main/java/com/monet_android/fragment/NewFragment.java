package com.monet_android.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.monet_android.adapter.NewAdapter;
import com.monet_android.halper.IOnItemClickListener;
import com.monet_android.R;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.newContent.NewPojo;
import com.monet_android.modle.newContent.NewResponse;
import com.monet_android.modle.saveFeedbackPreDetails.SaveFeedBack_Pojo;
import com.monet_android.qCards.LandingScreen;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppConstant.SUCCESS;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewFragment extends Fragment {

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private NewAdapter newAdapter;
    private String Token;
    private ArrayList<NewResponse> responseArrayList = new ArrayList<>();
    private ArrayList<NewResponse> allTimeArrayList = new ArrayList<>();
    private ImageView img_searchMain;
    private RelativeLayout search_layout;
    private String layoutVisible = "0";
    private ApiInterface apiInterface;
    private TextView tv_newNoData;
    private ProgressDialog pd;
    private EditText edt_new_search;
    private String cmp_id, c_id, os, browser, browser_version, os_version, fs_version, tr_id;

    IOnItemClickListener iOnItemClickListener = new IOnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            AppPreferences.setC_Id(getActivity(), responseArrayList.get(position).getC_id().toString());
            AppPreferences.setCmpId(getActivity(), responseArrayList.get(position).getCmp_id());
            AppPreferences.setVideoUrl(getActivity(), responseArrayList.get(position).getCmp_video_url());
            LandingScreen.arrayList.clear();
            LandingScreen.arrayList.addAll(responseArrayList.get(position).getCmp_sequence());
            AppPreferences.setCamEval(getActivity(), responseArrayList.get(position).getCmp_eval());
            AppPreferences.setVideoTime(getActivity(), responseArrayList.get(position).getC_length());
            recyclerView.setClickable(false);
            AppPreferences.setCmpLength(getActivity(), 0);
            AppPreferences.setCmpLengthCountFlag(getActivity(), 0);
            AppPreferences.setCmpLengthCount(getActivity(), 0);
            saveFeedBack();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new, container, false);

        recyclerView = view.findViewById(R.id.new_recycle);
        img_searchMain = view.findViewById(R.id.img_searchNew);
        search_layout = view.findViewById(R.id.search_newlayout);
        tv_newNoData = view.findViewById(R.id.tv_newNoData);
        edt_new_search = view.findViewById(R.id.edt_new_search);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        newAdapter = new NewAdapter(getActivity(), responseArrayList, iOnItemClickListener);
        recyclerView.setAdapter(newAdapter);

        apiInterface = BaseUrl.getClient().create(ApiInterface.class);

        browser_version = Build.MODEL;
        os_version = Build.VERSION.RELEASE;
        os = "Android";
        browser = "App";
        tr_id = "";
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            fs_version = String.valueOf(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        img_searchMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutVisible.equals("0")) {
                    search_layout.setVisibility(View.VISIBLE);
                    layoutVisible = "1";
                    AppUtils.showKeyboard(v, getContext());
                } else {
                    search_layout.setVisibility(View.GONE);
                    layoutVisible = "0";
                    responseArrayList.clear();
                    responseArrayList.addAll(allTimeArrayList);
                    newAdapter.notifyDataSetChanged();
                    AppUtils.hideKeyboard(getActivity());
                }
            }
        });

        Token = "Bearer " + AppPreferences.getApiToken(getActivity());

        edt_new_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String title = edt_new_search.getText().toString();

                if(title.length() == 0){
                    responseArrayList.clear();
                    responseArrayList.addAll(allTimeArrayList);
                    newAdapter.notifyDataSetChanged();
                }else{
                    filter(title);
                }
            }
        });

        return view;
    }

    public void setNewContent() {
        pd.show();
        Call<NewPojo> pojoCall = apiInterface.getNewContent(Token);
        pojoCall.enqueue(new Callback<NewPojo>() {
            @Override
            public void onResponse(Call<NewPojo> call, Response<NewPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    tv_newNoData.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        tv_newNoData.setVisibility(View.GONE);
                        responseArrayList.addAll(response.body().getResponse());
                        allTimeArrayList.addAll(response.body().getResponse());
                        newAdapter.notifyDataSetChanged();
                        newAdapter.notifyItemInserted(responseArrayList.size());
                    } else {
                        tv_newNoData.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewPojo> call, Throwable t) {
                pd.dismiss();
                tv_newNoData.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    void filter(String text) {
        ArrayList<NewResponse> temp = new ArrayList();
        for (NewResponse d : responseArrayList) {
            if (d.getC_title().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }

        updateList(temp);
    }

    public void updateList(ArrayList<NewResponse> list) {
        responseArrayList.clear();
        responseArrayList = list;
        newAdapter = new NewAdapter(getActivity(), responseArrayList, iOnItemClickListener);
        recyclerView.setAdapter(newAdapter);
        newAdapter.notifyDataSetChanged();
    }

    public void saveFeedBack(){
        pd.show();

        cmp_id = AppPreferences.getCmpId(getContext());
        c_id = AppPreferences.getC_Id(getContext());

        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmp_id", cmp_id);
            jsonObject.put("c_id", c_id);
            jsonObject.put("os", os);
            jsonObject.put("browser", browser);
            jsonObject.put("browser_version", browser_version);
            jsonObject.put("os_version", os_version);
            jsonObject.put("fs_version", fs_version);
            jsonObject.put("tr_id", tr_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<SaveFeedBack_Pojo> pojoCall = apiInterface.saveFeedBack(Token, jsonObject);
        pojoCall.enqueue(new Callback<SaveFeedBack_Pojo>() {
            @Override
            public void onResponse(Call<SaveFeedBack_Pojo> call, Response<SaveFeedBack_Pojo> response) {
                pd.dismiss();
                if(response.body() == null){
                    Toast.makeText(getContext(), response.raw().message(), Toast.LENGTH_SHORT).show();
                }else{
                    if(response.body().getCode().equals("200")){
                        AppPreferences.setCfId(getActivity(), response.body().getResponse().get(0).getCf_id());
                        AppPreferences.setCmpLength(getActivity(), Integer.valueOf(response.body().getResponse().get(0).getCmp_count()));
                        AppPreferences.setCmpId(getActivity(), cmp_id);
                        Intent intent = new Intent(getContext(), LandingScreen.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<SaveFeedBack_Pojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        if (AppUtils.isConnectionAvailable(getActivity())) {
            allTimeArrayList.clear();
            responseArrayList.clear();
            setNewContent();
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        super.onResume();
    }
}
