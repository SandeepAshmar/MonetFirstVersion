package com.monet_android.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.monet_android.R;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.updateProfile.UpdateProfilePojo;
import com.monet_android.modle.updateProfile.UpdateProfilePost;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppUtils.hideKeyboard;

public class SettingsScreen extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_eye, img_setting_back;
    private EditText edt_curent_pass_set, edt_new_pass_set, edt_veri_pass_set;
    private String visible = "0", layoutVisible = "0";
    private TextView tv_logout, tv_changePass;
    private Button btn_change_pass, btn_logout, btn_logout_cancel;
    private String oldPass, newPass, token, userId;
    private LinearLayout logout_layout, setting_layout;
    private ProgressDialog pd;
    private TextInputLayout til_curPass, til_newPass, til_veriPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        img_eye = findViewById(R.id.img_eye);
        img_eye.setEnabled(false);
        img_setting_back = findViewById(R.id.img_setting_back);
        edt_curent_pass_set = findViewById(R.id.edt_curent_pass_set);
        edt_new_pass_set = findViewById(R.id.edt_new_pass_set);
        edt_veri_pass_set = findViewById(R.id.edt_veri_pass_set);
        tv_logout = findViewById(R.id.tv_logout);
        btn_change_pass = findViewById(R.id.btn_change_pass);
        setting_layout = findViewById(R.id.setting_layout);
        logout_layout = findViewById(R.id.logout_layout);
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout_cancel = findViewById(R.id.btn_logout_cancel);
        tv_changePass = findViewById(R.id.tv_changePass);
        til_veriPass = findViewById(R.id.til_veriPass);
        til_newPass = findViewById(R.id.til_newPass);
        til_curPass = findViewById(R.id.til_curPass);

        til_curPass.setEnabled(false);
        til_newPass.setEnabled(false);
        til_veriPass.setEnabled(false);

        img_eye.setOnClickListener(this);
        img_setting_back.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_logout_cancel.setOnClickListener(this);
        btn_change_pass.setOnClickListener(this);
        tv_changePass.setOnClickListener(this);

        token = "Bearer " + AppPreferences.getApiToken(this);
        userId = AppPreferences.getUserId(this);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        eyeBtnVisible();
        disableSelection();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_eye:
                setText();
                break;
            case R.id.img_setting_back:
                onBackPressed();
                break;
            case R.id.tv_logout:
                visibleLayout();
                break;
            case R.id.btn_change_pass:
                validate();
                break;
            case R.id.btn_logout:
                AppPreferences.clearAllPreferences(this);
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(SettingsScreen.this, LoginScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;
            case R.id.btn_logout_cancel:
                visibleLayout();
                break;

            case R.id.tv_changePass:
                til_curPass.setEnabled(true);
                til_newPass.setEnabled(true);
                til_veriPass.setEnabled(true);
                til_curPass.requestFocus();
                break;
        }
    }

    private void disableSelection() {
        edt_curent_pass_set.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        edt_new_pass_set.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        edt_veri_pass_set.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
    }

    private void validate() {

        if (edt_new_pass_set.getText().toString().equals(edt_veri_pass_set.getText().toString())) {
            if (AppUtils.isConnectionAvailable(this)) {
                apiCall();
            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "New password and Confirm password should be same", Toast.LENGTH_SHORT).show();
        }

    }

    private void apiCall() {
        newPass = edt_new_pass_set.getText().toString();
        oldPass = edt_curent_pass_set.getText().toString();

        pd.show();
        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        UpdateProfilePost updateProfilePost = new UpdateProfilePost(oldPass, newPass);
        Call<UpdateProfilePojo> postCall = apiInterface.profileUpdate(token, userId, updateProfilePost);
        postCall.enqueue(new Callback<UpdateProfilePojo>() {
            @Override
            public void onResponse(Call<UpdateProfilePojo> call, Response<UpdateProfilePojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(SettingsScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals("200")) {
                        hideKeyboard(SettingsScreen.this);
                        edt_curent_pass_set.setText("");
                        edt_new_pass_set.setText("");
                        edt_veri_pass_set.setText("");
                        Toast.makeText(SettingsScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateProfilePojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(SettingsScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void visibleLayout() {
        if (layoutVisible.equals("0")) {
            logout_layout.setVisibility(View.VISIBLE);
            edt_new_pass_set.setEnabled(false);
            edt_curent_pass_set.setEnabled(false);
            edt_veri_pass_set.setEnabled(false);
            img_eye.setEnabled(false);
            tv_logout.setEnabled(false);
            layoutVisible = "1";
        } else {
            logout_layout.setVisibility(View.GONE);
            edt_new_pass_set.setEnabled(true);
            edt_curent_pass_set.setEnabled(true);
            edt_veri_pass_set.setEnabled(true);
            img_eye.setEnabled(true);
            tv_logout.setEnabled(true);
            layoutVisible = "0";
        }
    }

    public void eyeBtnVisible() {
        edt_new_pass_set.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_curent_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else if (edt_new_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else if (edt_veri_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_activate);
                    btn_change_pass.setEnabled(true);
                }

                if (edt_curent_pass_set.getText().toString() != null && !edt_curent_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else if (edt_new_pass_set.getText().toString() != null && !edt_new_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else if (edt_veri_pass_set.getText().toString() != null && !edt_veri_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else {
                    img_eye.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_curent_pass_set.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_curent_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else if (edt_new_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else if (edt_veri_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_activate);
                    btn_change_pass.setEnabled(true);
                }

                if (edt_curent_pass_set.getText().toString() != null && !edt_curent_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else if (edt_new_pass_set.getText().toString() != null && !edt_new_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else if (edt_veri_pass_set.getText().toString() != null && !edt_veri_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else {
                    img_eye.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_veri_pass_set.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_curent_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else if (edt_new_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else if (edt_veri_pass_set.getText().toString().isEmpty()) {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_change_pass.setEnabled(false);
                } else {
                    btn_change_pass.setBackgroundResource(R.drawable.btn_pro_activate);
                    btn_change_pass.setEnabled(true);
                }

                if (edt_curent_pass_set.getText().toString() != null && !edt_curent_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else if (edt_new_pass_set.getText().toString() != null && !edt_new_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else if (edt_veri_pass_set.getText().toString() != null && !edt_veri_pass_set.getText().toString().isEmpty()) {
                    img_eye.setEnabled(true);
                } else {
                    img_eye.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setText() {
        if (visible.equals("0")) {
            edt_curent_pass_set.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edt_new_pass_set.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edt_veri_pass_set.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            visible = "1";
            img_eye.setImageResource(R.mipmap.eye_green);
        } else {
            edt_curent_pass_set.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            edt_new_pass_set.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            edt_veri_pass_set.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            visible = "0";
            img_eye.setImageResource(R.mipmap.eye_gray);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
