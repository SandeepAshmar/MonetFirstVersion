package com.monet_android.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.monet_android.R;
import com.monet_android.fragment.CampaignFragment;
import com.monet_android.fragment.MoreFragment;
import com.monet_android.fragment.NewFragment;
import com.monet_android.fragment.PopularFragment;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;
    private CampaignFragment campaignFragment;
    private MoreFragment moreFragment;
    private NewFragment newFragment;
    private PopularFragment popularFragment;
    private FrameLayout main_frame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        main_frame = findViewById(R.id.main_frame);

        Menu menu = bottomNavigationView.getMenu();

        menu.add(Menu.NONE, R.id.navigation_campaigns, Menu.NONE, "Campaigns")
                .setIcon(R.drawable.ic_camp);
        menu.add(Menu.NONE, R.id.navigation_new, Menu.NONE, "New")
                .setIcon(R.drawable.ic_new);
        menu.add(Menu.NONE, R.id.navigation_popular, Menu.NONE, "Popular")
                .setIcon(R.drawable.ic_bottom_popular);
        menu.add(Menu.NONE, R.id.navigation_more, Menu.NONE, "Menu")
                .setIcon(R.drawable.ic_more);

        campaignFragment = new CampaignFragment();
        moreFragment = new MoreFragment();
        newFragment = new NewFragment();
        popularFragment = new PopularFragment();

        setFragment(campaignFragment);
        disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AppUtils.isConnectionAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_campaigns:
                setFragment(campaignFragment);
                return true;
            case R.id.navigation_new:
                setFragment(newFragment);
                return true;
            case R.id.navigation_popular:
                setFragment(popularFragment);
                return true;
            case R.id.navigation_more:
                setFragment(moreFragment);
                return true;
            default:
                return false;
        }
    }
}
