/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.monet_android.visionCamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;

import com.google.android.gms.vision.CameraSource;
import com.monet_android.R;
import com.monet_android.activity.FaceTrackerScreen;
import com.monet_android.activity.PlayVideoAndRecordScreen;

import java.util.HashSet;
import java.util.Set;


public class GraphicOverlay extends View {
    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    private int mFacing = CameraSource.CAMERA_FACING_BACK;
    private Set<Graphic> mGraphics = new HashSet<>();
    public boolean detecting = false;
    public boolean detecting1 = false;
    public boolean detecting2 = false;
    public boolean detecting3 = false;
    public boolean detectComplete = false;
    public Handler handler = new Handler();
    public Runnable runnable;
    public int i = 1;

    public static abstract class Graphic {
        private GraphicOverlay mOverlay;

        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }

        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         * <ol>
         * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of
         * the supplied value from the preview scale to the view scale.</li>
         * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.</li>
         * </ol>
         *
         * @param canvas drawing canvas
         */
        public abstract void draw(Canvas canvas);

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateX(float x) {
            if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateY(float y) {
            return scaleY(y);
        }

        public void postInvalidate() {
            mOverlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();
    }

    public void add(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
            detecting = true;
            setView();
//            changeImage();
        }
        postInvalidate();
    }

    /**
     * Removes a graphic from the overlay.
     */
    public void remove(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
            detecting = false;
            setView();
//            changeImage();img_detect

        }
        postInvalidate();
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }

    /**
     * Draws the overlay with its associated graphic objects.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }
        }
    }

    private void setView() {

        if(detectComplete){
            FaceTrackerScreen.tv_detecting.setTextColor(Color.GREEN);
            FaceTrackerScreen.tv_detecting.setText("Finish");
        }else{
            if(detecting){
                if(detecting3){
                    FaceTrackerScreen.tv_detecting.setTextColor(Color.GREEN);
                    FaceTrackerScreen.tv_detecting.setText("Finish");
                    detectComplete = true;
                }else{
                    FaceTrackerScreen.tv_detecting.setTextColor(Color.GREEN);
                    FaceTrackerScreen.tv_detecting.setText("Detecting");
                }

            }else{
                if(detecting3){
                    FaceTrackerScreen.tv_detecting.setTextColor(Color.GREEN);
                    FaceTrackerScreen.tv_detecting.setText("Finish");
                    detectComplete = true;
                }else{
                    FaceTrackerScreen.tv_detecting.setTextColor(Color.RED);
                    FaceTrackerScreen.tv_detecting.setText("Not Detecting");
                }
            }
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                if (detecting) {
                    if (detecting1 != true) {
                        detectFirst();
                    }else if(detecting2 !=true){
                        detectSecond();
                    }else if(detecting3 !=true){
                        detectThird();
                    }
                } else {
                    detecting1 = false;
                    detecting2 = false;
                    detecting3 = false;
                }
            }
        };
        handler.postDelayed(runnable, 1000);


    }

    private void detectFirst() {
        if (detecting) {
            FaceTrackerScreen.v_cOne.postDelayed(new Runnable() {
                @Override
                public void run() {
                   FaceTrackerScreen.v_cOne.setBackgroundResource(R.drawable.ic_green_back);
                   detecting1 = true;
                   detecting2 = false;
                   detecting3 = false;
                }
            }, 1000);
        }
    }

    private void detectSecond() {
        if (detecting) {
            FaceTrackerScreen.v_cTwo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FaceTrackerScreen.v_cTwo.setBackgroundResource(R.drawable.ic_green_back);
                    detecting1 = true;
                    detecting2 = true;
                    detecting3 = false;
                }
            }, 1000);
        }

    }

    private void detectThird() {
        if (detecting) {
            FaceTrackerScreen.v_cThree.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FaceTrackerScreen.v_cThree.setBackgroundResource(R.drawable.ic_green_back);
                    FaceTrackerScreen.btn_feceDetct.setEnabled(true);
                    FaceTrackerScreen.btn_feceDetct.setBackgroundResource(R.drawable.btn_pro_activate);
                    detecting1 = true;
                    detecting2 = true;
                    detecting3 = true;
                }
            }, 1000);
        }
    }
}
