package com.monet_android.qCards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.monet_android.R;
import com.monet_android.activity.FaceTrackerScreen;
import com.monet_android.activity.ProfileScreen;
import com.monet_android.activity.questions.QuestionQCard;
import com.monet_android.halper.OnClearFromRecentService;

import org.json.JSONException;

import static com.monet_android.activity.ProfileScreen.LOAD_FROM_GALLERY;
import static com.monet_android.qCards.LandingScreen.stagingJson;
import static com.monet_android.utils.AppUtils.openUtilityDialog;

public class FaceDetectInstructions extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout faceIns_firstll, faceIns_firstBtnll, faceIns_allowCamera, faceIns_lookScreen, faceIns_goodLight;
    private Button btn_faceIns_exit, btn_faceIns_proceed, btn_faceIns_allow, btn_faceIns_look, btn_faceIns_light;
    private TextView tv_faceIns_count;
    private ImageView img_landSecBack;
    private SeekBar sb_faceIns;
    private RelativeLayout mainLayout;
    boolean menuOut = false;
    private OnClearFromRecentService onClearFromRecentService;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect_instructions);

        btn_faceIns_exit = findViewById(R.id.btn_faceIns_exit);
        btn_faceIns_proceed = findViewById(R.id.btn_faceIns_proceed);
        btn_faceIns_allow = findViewById(R.id.btn_faceIns_allow);
        btn_faceIns_look = findViewById(R.id.btn_faceIns_look);
        btn_faceIns_light = findViewById(R.id.btn_faceIns_light);
        faceIns_firstll = findViewById(R.id.faceIns_firstll);
        faceIns_firstBtnll = findViewById(R.id.faceIns_firstBtnll);
        faceIns_allowCamera = findViewById(R.id.faceIns_allowCamera);
        faceIns_lookScreen = findViewById(R.id.faceIns_lookScreen);
        faceIns_goodLight = findViewById(R.id.faceIns_goodLight);
//        tv_faceIns_count = findViewById(R.id.tv_faceIns_count);
        img_landSecBack = findViewById(R.id.img_landSecBack);
        sb_faceIns = findViewById(R.id.sb_faceIns);
        mainLayout = findViewById(R.id.main_layout);

        onClearFromRecentService = new OnClearFromRecentService();


        btn_faceIns_exit.setOnClickListener(this);
        btn_faceIns_proceed.setOnClickListener(this);
        btn_faceIns_look.setOnClickListener(this);
        btn_faceIns_light.setOnClickListener(this);
        img_landSecBack.setOnClickListener(this);
        btn_faceIns_allow.setOnClickListener(this);

        try {
            stagingJson.put("4", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        sb_faceIns.setMax(4);
//        sb_faceIns.setMin(1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_faceIns_exit:
                onBackPressed();
                break;

            case R.id.btn_faceIns_proceed:
                faceIns_firstll.setVisibility(View.GONE);
                faceIns_firstBtnll.setVisibility(View.GONE);
                faceIns_allowCamera.setVisibility(View.VISIBLE);
                sb_faceIns.setProgress(2);

                if (checkCameraPermission()) {
                    Log.e("Tag", "Camera and External storage Permission's are allowed");
                } else {
                    requestCameraPermission();
                }
                break;

            case R.id.btn_faceIns_allow:
                faceIns_allowCamera.setVisibility(View.GONE);
                faceIns_lookScreen.setVisibility(View.VISIBLE);
                sb_faceIns.setProgress(3);
                break;

            case R.id.btn_faceIns_look:
                faceIns_lookScreen.setVisibility(View.GONE);
                faceIns_goodLight.setVisibility(View.VISIBLE);
                sb_faceIns.setProgress(4);
                break;

            case R.id.btn_faceIns_light:
                btn_faceIns_light.setClickable(false);
                try {
                    stagingJson.put("4", "3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, FaceTrackerScreen.class));
                finish();
                break;

            case R.id.img_landSecBack:
                onBackPressed();
                break;
        }
    }

    private boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.CAMERA)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme);
            alertDialog.setMessage("You Have To Give Permission From Your Device Setting To go in Setting Please Click on Settings Button");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 1012);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme);
            alertDialog.setMessage("You Have To Give Permission From Your Device Setting To go in Setting Please Click on Settings Button");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        }else{

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClearFromRecentService.onTaskRemoved(new Intent(FaceDetectInstructions.this, OnClearFromRecentService.class));
    }
}
