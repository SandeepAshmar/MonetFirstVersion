package com.monet_android.connection;

import android.content.Context;

import com.monet_android.utils.AppConstant;
import com.monet_android.utils.AppPreferences;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.monet_android.utils.AppConstant.P_BASE_URL;
import static com.monet_android.utils.AppConstant.S_BASE_URL;

public class BaseUrl {

    public static final String BASE_URL = AppConstant.IS_STAGING ? S_BASE_URL : P_BASE_URL;

    public static String countryCodeUrl = "https://dev.monetrewards.com/Diy/public/api/";

    private static Retrofit retrofit = null;
    private static Retrofit retrofit1 = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getCountryCode() {
        if (retrofit1 == null) {
            retrofit1 = new Retrofit.Builder()
                    .baseUrl(countryCodeUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit1;
    }
}
