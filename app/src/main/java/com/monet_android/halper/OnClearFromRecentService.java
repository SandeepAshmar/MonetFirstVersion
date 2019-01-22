package com.monet_android.halper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.monet_android.activity.ThankyouPage;
import com.monet_android.activity.questions.QuestionScreen;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.staging.StagingPojo;
import com.monet_android.qCards.LandingScreen;
import com.monet_android.utils.AppPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.activity.questions.QuestionScreen.token;
import static com.monet_android.qCards.LandingScreen.stagingJson;
import static com.monet_android.utils.AppConstant.CANCEL_URL;
import static com.monet_android.utils.AppConstant.CMP_ID;
import static com.monet_android.utils.AppConstant.HIT_CANCEL;
import static com.monet_android.utils.AppConstant.SUCCESS;
import static com.monet_android.utils.AppConstant.TOKEN;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        callApi();
        if(HIT_CANCEL){
            hitCancelUrl();
        }
        stopSelf();
    }

    private void hitCancelUrl(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CANCEL_URL));
        startActivity(browserIntent);
    }

    public void callApi(){

        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        Call<StagingPojo> pojoCall = apiInterface.sendStagingData(TOKEN, CMP_ID, stagingJson);

        pojoCall.enqueue(new Callback<StagingPojo>() {
            @Override
            public void onResponse(Call<StagingPojo> call, Response<StagingPojo> response) {
                if(response.body() == null){
                    Log.d("save", "body null");
                }else{
                    if(response.body().getCode().equalsIgnoreCase(SUCCESS)){
                        Log.d("save", "Successful "+response.body().getMessage());
                    }else{
                        Log.d("save", "Unsuccessful "+response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<StagingPojo> call, Throwable t) {
                Log.d("save", "Error "+t.getMessage());
            }
        });
    }
}