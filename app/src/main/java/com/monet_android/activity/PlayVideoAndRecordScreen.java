package com.monet_android.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.monet_android.R;
import com.monet_android.activity.questions.QuestionQCard;
import com.monet_android.activity.reaction.Reaction_Activity;
import com.monet_android.qCards.FaceDetectInstructions;
import com.monet_android.utils.AppPreferences;
import com.pedro.encoder.input.video.Camera1ApiManager;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.json.JSONException;

import static com.monet_android.qCards.LandingScreen.arrayList;
import static com.monet_android.qCards.LandingScreen.stagingJson;
import static com.monet_android.utils.AppConstant.RTMP_PASSWORD;
import static com.monet_android.utils.AppConstant.RTMP_URL;
import static com.monet_android.utils.AppConstant.RTMP_USERNAME;
import static com.monet_android.utils.AppConstant.YOUTUBE_KEY;

public class PlayVideoAndRecordScreen extends YouTubeBaseActivity implements YouTubePlayer.PlayerStateChangeListener, ConnectCheckerRtmp {

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private YouTubePlayer player;
    private String video_id = "";
    private SurfaceView cameraView;
    public static ImageView camera_icon, img_detect;
    private RelativeLayout video_layout;
    private RtmpCamera1 rtmpCamera1;
    int flag = 0, videoTime = 0;
    private boolean connectionStatus;
    private Runnable runnable;
    private Runnable runnable1;
    private Handler handler;
    private Handler handler1;
    private boolean count = true, detecting = false;
    private String bitrate = "150";
    boolean doubleBackToExitPressedOnce = false;
    boolean faceDetect = false;
    boolean intrupt = false;
    private int detectedTime = 0, minVisionTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_and_record_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cameraView = findViewById(R.id.surfaceView);
        youTubePlayerView = findViewById(R.id.youtube_view);
        camera_icon = findViewById(R.id.camera_icon);

        handler = new Handler();
        handler1 = new Handler();
        img_detect = findViewById(R.id.img_detect);
        video_layout = findViewById(R.id.videoLayout);
        video_id = AppPreferences.getVideoUrl(this);
        video_id = video_id.replace("https://www.youtube.com/watch?v=", "");

        try {
            stagingJson.put("4", "4");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        videoTime = Integer.parseInt(AppPreferences.getVideoTime(PlayVideoAndRecordScreen.this));

        minVisionTime = (videoTime * 70) / 100;

        if (video_id.isEmpty()) {
            Toast.makeText(this, "No video url found", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            playVideo();
        }

        camera_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_layout.setVisibility(View.VISIBLE);
                camera_icon.setVisibility(View.GONE);
                hideView();
            }
        });

        rtmpCamera1 = new RtmpCamera1(cameraView, this);
        rtmpCamera1.startPreview();
    }

    /*You tube video start*/
    public void playVideo() {

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player = youTubePlayer;
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                player.loadVideo(video_id);
                youTubePlayer.setPlayerStateChangeListener(PlayVideoAndRecordScreen.this);
                if (!rtmpCamera1.isStreaming()) {
                    rtmpCamera1.setAuthorization(RTMP_USERNAME, RTMP_PASSWORD);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
                Toast.makeText(PlayVideoAndRecordScreen.this, result.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        youTubePlayerView.initialize(YOUTUBE_KEY, onInitializedListener);
    }

    @Override
    public void onLoading() {
        Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaded(String s) {
        video_layout.setVisibility(View.VISIBLE);
        hideView();

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
        recordingStart();
    }

    @Override
    public void onVideoEnded() {
        detecting = false;
        try {
            stagingJson.put("4", "5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rtmpCamera1.stopStream();
        rtmpCamera1.stopPreview();
        rtmpCamera1.disableFaceDetection();
        if (detectedTime >= minVisionTime) {
            setScreen();
        } else {
            if (faceDetect) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setCancelable(false);
                builder1.setMessage("Sorry you are not in the frame");
                builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        detectedTime = 0;
                        player.play();
                        startActivity(new Intent(PlayVideoAndRecordScreen.this, PlayVideoAndRecordScreen.class));
                        finish();
                    }
                });
                builder1.show();
            } else {
                setScreen();
            }

        }
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Toast.makeText(this, "" + errorReason, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(PlayVideoAndRecordScreen.this, MainActivity.class));
        finish();
    }

    private void hideView() {

        video_layout.postDelayed(new Runnable() {
            @Override
            public void run() {

                video_layout.setVisibility(View.INVISIBLE);
                camera_icon.setVisibility(View.VISIBLE);

            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 0);
    }

    @Override
    public void onConnectionSuccessRtmp() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(PlayVideoAndRecordScreen.this, "Connection success", Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    @Override
    public void onConnectionFailedRtmp(final String s) {
//        runOnUiThread(new Runnable() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void run() {
//                rtmpCamera1.stopStream();
//                rtmpCamera1.stopPreview();
//                AlertDialog.Builder builder = new AlertDialog.Builder(PlayVideoAndRecordScreen.this);
//                builder.setMessage("Your internet connection is slow, Please try again");
//                builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        startActivity(new Intent(PlayVideoAndRecordScreen.this, FaceDetectInstructions.class));
//                        finish();
//                    }
//                });
//                builder.show();
//            }
//        });
    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(PlayVideoAndRecordScreen.this, "Disconnected", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(PlayVideoAndRecordScreen.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(PlayVideoAndRecordScreen.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recordingStart() {


        if (rtmpCamera1.isRecording() || prepareEncoders()) {
            String rtmpUrl = RTMP_URL + AppPreferences.getCfId(this);
            Log.d("test", "rtmp "+rtmpUrl);
            rtmpCamera1.startStream(rtmpUrl);
            if (flag == 0) {
                try {
                    rtmpCamera1.switchCamera();
                } catch (final CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                flag++;
            }
            try {
                rtmpCamera1.enableFaceDetection(new Camera1ApiManager.FaceDetectorCallback() {
                    @Override
                    public void onGetFaces(Camera.Face[] faces) {

                        checkNetworkType();
                        changeImage(faces.length);
                    }
                });
            } catch (Exception e) {
                Log.d("MainActivity", "Exception found in face detection..." + e.getMessage());
            }

        } else {
            Toast.makeText(this, "Error preparing stream, This device can not do it...", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean prepareEncoders() {
        int width = 320;
        int height = 240;
        return rtmpCamera1.prepareVideo(width, height, Integer.parseInt("30"),
                Integer.parseInt(bitrate) * 1024,  // bitrate
                false, CameraHelper.getCameraOrientation(this));
    }

    private void changeImage(int i) {
        faceDetect = true;
        if (i == 0) {
            img_detect.setImageResource(R.drawable.ic_red_back);
            count = true;
            detecting = true;
            notDetectDialog();
        } else {
            img_detect.setImageResource(R.drawable.ic_green_back);
            if (count) {
                countPercentage();
                detecting = false;
            }
        }
    }

    private void countPercentage() {
        count = false;
        runnable = new Runnable() {
            @Override
            public void run() {
                detectedTime = detectedTime + 1;
                count = true;
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void notDetectDialog() {
        if (detecting) {
            runnable1 = new Runnable() {
                @Override
                public void run() {
                    if(detecting){
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlayVideoAndRecordScreen.this);
                        builder.setMessage("It seems you are not in front of the camera from last 5 seconds, Please align yourself and play video again.");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
            };
            handler.postDelayed(runnable1, 5000);
        } else {
            handler1.removeCallbacks(runnable1);
        }
    }

    private void checkNetworkType() {

        connectionStatus = isConnectedFast(getApplicationContext());
        if (!connectionStatus) {
            bitrate = "150";
            // connection is slow
            Log.d("Connection Status", "POOR CONNECTION");

        } else {
            // connection is fast
            Log.d("Connection Status", "GOOD CONNECTION");
            bitrate = "200";

        }

    }

    private boolean isConnectedFast(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype(), context));
    }

    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    private boolean isConnectionFast(int type, int subType, Context context) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;

        } else if (type == ConnectivityManager.TYPE_MOBILE) {

            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps

                // Above API level 7, make sure to set android:targetSdkVersion to appropriate level to use these

                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (intrupt) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlayVideoAndRecordScreen.this);
            builder.setMessage("Due to interruptions, you are out of the frame. So please watch the campaign again to get rewards!!");
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(PlayVideoAndRecordScreen.this, FaceDetectInstructions.class));
                    finish();
                }
            }).show();
        }
    }

    private void setScreen() {
        rtmpCamera1.stopPreview();
        int count = AppPreferences.getCmpLength(this);
        int i = AppPreferences.getCmpLengthCount(this);

        if (count == 1) {
            if (arrayList.size() > i) {
                if (arrayList.get(i).equalsIgnoreCase("Pre")) {
                    AppPreferences.setQuestionType(this, "pre");
                    try {
                        stagingJson.put("2", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Post")) {
                    AppPreferences.setQuestionType(this, "post");
                    try {
                        stagingJson.put("3", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Emotion")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("4", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, FaceDetectInstructions.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Reaction")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("5", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, Reaction_Activity.class));
                    finish();
                }
            } else {
                int flag = AppPreferences.getCmpLengthCountFlag(this);
                AppPreferences.setCmpLengthCountFlag(this, flag + 1);
                try {
                    stagingJson.put("6", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, ThankyouPage.class));
                finish();
            }
        } else {
            if (arrayList.size() > i) {
                if (arrayList.get(i).equalsIgnoreCase("Pre")) {
                    AppPreferences.setQuestionType(this, "pre");
                    try {
                        stagingJson.put("2", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Post")) {
                    AppPreferences.setQuestionType(this, "post");
                    try {
                        stagingJson.put("3", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Emotion")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("4", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, FaceDetectInstructions.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Reaction")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("5", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, Reaction_Activity.class));
                    finish();
                }
            } else {
                int flag = AppPreferences.getCmpLengthCountFlag(this);
                AppPreferences.setCmpLengthCountFlag(this, flag + 1);
                try {
                    stagingJson.put("6", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, ThankyouPage.class));
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }



    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        intrupt = true;
    }
}