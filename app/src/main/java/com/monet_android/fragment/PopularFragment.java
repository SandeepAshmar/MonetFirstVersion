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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.monet_android.adapter.PopularAdapter;
import com.monet_android.R;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.halper.IOnItemClickListener;
import com.monet_android.modle.popularContent.PopularPojo;
import com.monet_android.modle.popularContent.PopularResponse;
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
public class PopularFragment extends Fragment {


    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private String Token;
    private ImageView img_searchPopular;
    private RelativeLayout search_layout;
    private String layoutVisible = "0";
    private ApiInterface apiInterface;
    private TextView tv_popularNoData;
    private ProgressDialog pd;
    private EditText edt_popular_search;
    private ArrayList<PopularResponse> responseArrayList = new ArrayList<>();
    private ArrayList<PopularResponse> allTimeArrayList = new ArrayList<>();
    private PopularAdapter popAdapter;
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
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        recyclerView = view.findViewById(R.id.popular_recycle);
        img_searchPopular = view.findViewById(R.id.img_searchPopular);
        search_layout = view.findViewById(R.id.search_popularlayout);
        tv_popularNoData = view.findViewById(R.id.tv_popularNoData);
        edt_popular_search = view.findViewById(R.id.edt_popular_search);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        popAdapter = new PopularAdapter(getActivity(), responseArrayList, iOnItemClickListener);
        recyclerView.setAdapter(popAdapter);

        apiInterface = BaseUrl.getClient().create(ApiInterface.class);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

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

        img_searchPopular.setOnClickListener(new View.OnClickListener() {
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
                    popAdapter.notifyDataSetChanged();
                    AppUtils.hideKeyboard(getActivity());
                }
            }
        });

        Token = "Bearer " + AppPreferences.getApiToken(getActivity());

        edt_popular_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String title = edt_popular_search.getText().toString();

                if(title.length() == 0){
                    responseArrayList.clear();
                    responseArrayList.addAll(allTimeArrayList);
                    popAdapter.notifyDataSetChanged();
                }else{
                    filter(title);
                }
            }
        });

        return view;
    }

    public void setPopContent() {
        pd.show();
        Call<PopularPojo> pojoCall = apiInterface.getPopularContent(Token);
        pojoCall.enqueue(new Callback<PopularPojo>() {
            @Override
            public void onResponse(Call<PopularPojo> call, Response<PopularPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    tv_popularNoData.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        tv_popularNoData.setVisibility(View.GONE);
                        responseArrayList.addAll(response.body().getResponse());
                        allTimeArrayList.addAll(response.body().getResponse());
                        popAdapter.notifyDataSetChanged();
                        popAdapter.notifyItemInserted(responseArrayList.size());
                    } else {
                        tv_popularNoData.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PopularPojo> call, Throwable t) {
                pd.dismiss();
                tv_popularNoData.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    void filter(String text) {
        ArrayList<PopularResponse> temp = new ArrayList();
        for (PopularResponse d : responseArrayList) {
            if (d.getC_title().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }

        updateList(temp);
    }

    public void updateList(ArrayList<PopularResponse> list) {
        responseArrayList.clear();
        responseArrayList = list;
        popAdapter = new PopularAdapter(getActivity(), responseArrayList, iOnItemClickListener);
        recyclerView.setAdapter(popAdapter);
        popAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        if (AppUtils.isConnectionAvailable(getActivity())) {
            allTimeArrayList.clear();
            responseArrayList.clear();
            setPopContent();
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        super.onResume();
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

}
