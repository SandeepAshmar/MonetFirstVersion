package com.monet_android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.monet_android.activity.LoginScreen;
import com.monet_android.activity.MainActivity;
import com.monet_android.halper.OnClearFromRecentService;
import com.monet_android.modle.reacionIcon.Item;
import com.monet_android.qCards.LandingScreen;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import java.util.ArrayList;

import static com.monet_android.qCards.LandingScreen.arrayList;
import static com.monet_android.utils.AppConstant.CANCEL_URL;
import static com.monet_android.utils.AppConstant.HIT_CANCEL;

public class SplashScreen extends AppCompatActivity {

    private Runnable runnable;
    private Handler handler;
    private String c_url, c_id, cmp_eval, c_duration, c_length, cmp_sequence, cmp_id, cf_id, user_id, cancel, success, q_id, cmp_count, api_token, c_type, c_thumb_url;

//    private String url =
//            "monet://?cmp_count=2" +
//            "&c_url=https://www.youtube.com/watch?v=-zn71VAo6zo" +
//            "&c_id=1626&" +
//            "c_thumb_url=https://i.ytimg.com/vi/-zn71VAo6zo/2.jpg&" +
//            "c_type=1" +
//            "&cmp_eval=1-4-5-6" +
//            "&c_duration=53" +
//            "&c_length=67" +
//            "&cmp_sequence=Pre,Emotion,Post" +
//            "&cmp_id=1327" +
//            "&cf_id=34783" +
//            "&user_id=13494&" +
//            "api_token=UJb8Sg8BkGBSCFMvbxgNZA04oUL7ZuSeKEI8giJwuleHoRmLSmzqV7lZlMfQ" +
//            "&q_id=34496971" +
//            "success=https://theoremreach.com/respondent_result?status=10,user_id=9999993012&" +
//            "cancel=https://theoremreach.com/routers/monetRedirect?success=false,cmp_id=1327,user_id=9999993012";

    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    ClipData clipData = clipBoard.getPrimaryClip();
                    ClipData.Item item = clipData.getItemAt(0);
                     AppPreferences.setDirectUrl(SplashScreen.this, item.getText().toString());
                }
            });

        handler = new Handler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (runnable != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSplashScreen();
        if (!AppUtils.isConnectionAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSplashScreen() {

        runnable = new Runnable() {
            @Override
            public void run() {

                url = AppPreferences.getDirectUrl(SplashScreen.this);

                if (url != null && url.contains("cmp_id")) {

                    AppPreferences.setCmpLength(SplashScreen.this, 0);
                    AppPreferences.setCmpLengthCountFlag(SplashScreen.this, 0);
                    AppPreferences.setCmpLengthCount(SplashScreen.this, 0);
                    splitUrl();
                    AppPreferences.setDirectUrl(SplashScreen.this, "");

                } else {
                    boolean value = AppPreferences.isUserLoggedOut(SplashScreen.this);

                    Intent intent;
                    if (value == false) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                    } else {
                        intent = new Intent(getApplicationContext(), LoginScreen.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 1500);
    }

    private void splitUrl() {
        HIT_CANCEL = true;
        String[] str = url.split("&");
        String[] strElement;

        AppPreferences.setDirectHit(this, true);
        AppPreferences.setDirectHitCancel(this, true);

        int addLength = str.length;

        for (int i = 0; i < addLength; i++) {
            if (str[i].contains("c_url")) {
                c_url = str[i];
                strElement = c_url.split("c_url=");
                int strLength = strElement.length;
                c_url = strElement[(strLength - 1)];
            }
            if (str[i].contains("c_id")) {
                c_id = str[i];
                strElement = c_id.split("c_id=");
                int strLength = strElement.length;
                c_id = strElement[(strLength - 1)];
            }
            if (str[i].contains("cmp_eval")) {
                cmp_eval = str[i];
                strElement = cmp_eval.split("cmp_eval=");
                int strLength = strElement.length;
                cmp_eval = strElement[(strLength - 1)];
            }
            if (str[i].contains("c_duration")) {
                c_duration = str[i];
                strElement = c_duration.split("c_duration=");
                int strLength = strElement.length;
                c_duration = strElement[(strLength - 1)];
            }
            if (str[i].contains("c_length")) {
                c_length = str[i];
                strElement = c_length.split("c_length=");
                int strLength = strElement.length;
                c_length = String.valueOf(strElement[(strLength - 1)]);
            }
            if (str[i].contains("cmp_sequence")) {
                cmp_sequence = str[i];
                strElement = cmp_sequence.split("cmp_sequence=");
                int strLength = strElement.length;
                cmp_sequence = strElement[(strLength - 1)];
            }
            if (str[i].contains("cmp_id")) {
                cmp_id = str[i];
            }
            if (str[i].contains("user_id")) {
                user_id = str[i];
            }
            if (str[i].contains("cancel")) {
                cancel = str[i];
            }
            if (str[i].contains("success=https://")) {
                success = str[i];
            }
            if (str[i].contains("q_id")) {
                q_id = str[i];
                strElement = q_id.split("q_id=");
                int strLength = strElement.length;
                q_id = strElement[(strLength - 1)];
            }
            if (str[i].contains("cmp_count")) {
                cmp_count = str[i];
                strElement = cmp_count.split("cmp_count=");
                int strLength = strElement.length;
                cmp_count = strElement[(strLength - 1)];
            }
            if (str[i].contains("cf_id")) {
                cf_id = str[i];
                strElement = cf_id.split("cf_id=");
                int strLength = strElement.length;
                cf_id = strElement[(strLength - 1)];
            }
            if (str[i].contains("api_token")) {
                api_token = str[i];
                strElement = api_token.split("api_token=");
                int strLength = strElement.length;
                api_token = strElement[(strLength - 1)];
            }
            if (str[i].contains("c_type")) {
                c_type = str[i];
                strElement = c_type.split("c_type=");
                int strLength = strElement.length;
                c_type = strElement[(strLength - 1)];
            }
            if (str[i].contains("c_thumb_url")) {
                c_thumb_url = str[i];
                strElement = c_thumb_url.split("c_thumb_url=");
                int strLength = strElement.length;
                c_thumb_url = strElement[(strLength - 1)];
            }
        }

        str = cancel.split(",");

        for (int i = 0; i < str.length; i++) {
            if (str[i].contains("cmp_id")) {
                cmp_id = str[i];
                strElement = cmp_id.split("cmp_id=");
                int strLength = strElement.length;
                cmp_id = strElement[(strLength - 1)];
            }
            if (str[i].contains("user_id")) {
                user_id = str[i];
                strElement = user_id.split("user_id=");
                int strLength = strElement.length;
                user_id = strElement[(strLength - 1)];
            }
            if (str[i].contains("cancel")) {
                cancel = str[i];
                strElement = cancel.split("cancel=");
                int strLength = strElement.length;
                cancel = strElement[(strLength - 1)];
            }
        }

        str = success.split(",");

        for (int i = 0; i < str.length; i++) {
            if (str[i].contains("cmp_id")) {
                cmp_id = str[i];
                strElement = cmp_id.split("cmp_id=");
                int strLength = strElement.length;
                cmp_id = strElement[(strLength - 1)];
            }
            if (str[i].contains("user_id")) {
                user_id = str[i];
                strElement = user_id.split("user_id=");
                int strLength = strElement.length;
                user_id = strElement[(strLength - 1)];
            }
            if (str[i].contains("success=https://")) {
                success = str[i];
                strElement = success.split("success=");
                int strLength = strElement.length;
                success = strElement[(strLength - 1)];
            }
        }

        AppPreferences.setRedirectUserId(this, user_id);
        AppPreferences.setCamEval(this, cmp_eval);
        AppPreferences.setVideoUrl(this, c_url);
        AppPreferences.setC_Id(this, c_id);
        AppPreferences.setVideoTime(this, c_length);
        AppPreferences.setCmpId(this, cmp_id);
        AppPreferences.setCfId(this, cf_id);
        CANCEL_URL = cancel;
        AppPreferences.setSuccessUrl(this, success);
        AppPreferences.setCmpLength(this, Integer.valueOf(cmp_count));
        AppPreferences.setQ_Id(this, q_id);
        AppPreferences.setRedirectApiToken(this, api_token);
        AppPreferences.setRedirectUser(this, "yes");

        str = cmp_sequence.split(",");

        for (int i = 0; i < str.length; i++) {
            arrayList.add(str[i]);
        }

        openLandPage();
    }

    private void openLandPage() {

        startActivity(new Intent(this, LandingScreen.class));
        finish();
    }
}