package com.monet_android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.monet_android.R;
import com.monet_android.activity.questions.QuestionQCard;
import com.monet_android.halper.OnClearFromRecentService;
import com.monet_android.visionCamera.CameraSourcePreview;
import com.monet_android.visionCamera.FaceGraphic;
import com.monet_android.visionCamera.GraphicOverlay;

import java.io.IOException;


public class FaceTrackerScreen extends Activity {

//    implements SurfaceHolder.Callback, Camera.FaceDetectionListener

    public static TextView tv_detecting;
    public static ImageView v_cOne, v_cTwo, v_cThree;
    public static Button btn_feceDetct;
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    public boolean detecting = false;
    private OnClearFromRecentService onClearFromRecentService;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_tracker_screen);

        tv_detecting = findViewById(R.id.tv_detecting);
        v_cOne = findViewById(R.id.v_cOne);
        v_cTwo = findViewById(R.id.v_cTwo);
        v_cThree = findViewById(R.id.v_cThree);
        btn_feceDetct = findViewById(R.id.btn_feceDetct);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        btn_feceDetct.setEnabled(false);
        onClearFromRecentService = new OnClearFromRecentService();

        btn_feceDetct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_feceDetct.setClickable(false);
                mPreview.stop();
                mPreview.release();
                startActivity(new Intent(FaceTrackerScreen.this, PlayVideoAndRecordScreen.class));
                finish();
            }
        });


    }

    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    private void startCameraSource() {

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);

        }
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
            detecting = false;
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    @Override
    protected void onResume() {
        createCameraSource();
        startCameraSource();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClearFromRecentService.onTaskRemoved(new Intent(FaceTrackerScreen.this, OnClearFromRecentService.class));
    }
}