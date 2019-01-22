package com.monet_android.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.monet_android.R;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.forgotPassword.ForgotPassPojo;
import com.monet_android.modle.forgotPassword.ForgotPassPost;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppConstant.SUCCESS;

public class ForgotPassScreen extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_signup;
    private Button cancel, submit;
    private EditText edt_email, edt_forgotPop_otp;
    private String otp, email;
    private Dialog dialog;
    private ProgressDialog pd;
    private Button btn_forgotPop_cancel, btn_forgotPop_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_screen);

        tv_signup = findViewById(R.id.tv_forgot_signup);
        cancel = findViewById(R.id.btn_forgot_cancel);
        submit = findViewById(R.id.btn_forgot_submit);
        submit.setEnabled(false);
        edt_email = findViewById(R.id.edt_forgot_email);

        dialog = new Dialog(this, R.style.Theme_Dialog);

        tv_signup.setOnClickListener(this);
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);

        edt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_email.getText().toString().isEmpty()) {
                    submit.setBackgroundResource(R.drawable.btn_gray_capsule);
                    submit.setEnabled(false);
                } else {
                    submit.setBackgroundResource(R.drawable.btn_forgot_submit);
                    submit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("sending otp...");

        disableSelection();
    }

    private void disableSelection() {
        edt_email.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });
    }

    private void generateOtp() {
        Random rnd = new Random();
        otp = String.valueOf(100000 + rnd.nextInt(900000));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgot_signup:
                startActivity(new Intent(ForgotPassScreen.this, RegisterScreenFirst.class));
                finish();
                break;
            case R.id.btn_forgot_cancel:
                onBackPressed();
                break;
            case R.id.btn_forgot_submit:

                if (Patterns.EMAIL_ADDRESS.matcher(edt_email.getText().toString()).matches()) {
                    generateOtp();
                    callApi();
                } else {
                    Toast.makeText(ForgotPassScreen.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void callApi() {
        if (!AppUtils.isConnectionAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        } else if (edt_email.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter email id", Toast.LENGTH_SHORT).show();
        } else {
            email = edt_email.getText().toString();
            pd.show();
            ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
            ForgotPassPost forgotPassPost = new ForgotPassPost(email, otp);
            Call<ForgotPassPojo> forgotPassPojoCall = apiInterface.sendOtp(forgotPassPost);
            forgotPassPojoCall.enqueue(new Callback<ForgotPassPojo>() {
                @Override
                public void onResponse(Call<ForgotPassPojo> call, Response<ForgotPassPojo> response) {
                    pd.dismiss();
                    if (response.body() == null) {
                        Toast.makeText(ForgotPassScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.body().getCode().equals(SUCCESS)) {
                            Toast.makeText(ForgotPassScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            openVerifyOtpDialog();
                        } else {
                            Toast.makeText(ForgotPassScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ForgotPassPojo> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(ForgotPassScreen.this, "Email id not registered", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openVerifyOtpDialog() {
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.forgot_pass_dialog);

        edt_forgotPop_otp = dialog.findViewById(R.id.edt_forgotPop_otp);
        btn_forgotPop_cancel = dialog.findViewById(R.id.btn_forgotPop_cancel);
        btn_forgotPop_submit = dialog.findViewById(R.id.btn_forgotPop_submit);

        edt_forgotPop_otp.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_forgotPop_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_forgotPop_otp.length() == 6) {
                    btn_forgotPop_submit.setBackgroundResource(R.drawable.btn_pro_activate);
                } else {
                    btn_forgotPop_submit.setBackgroundResource(R.drawable.btn_pro_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_forgotPop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_forgotPop_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_forgotPop_otp.getText().toString().equals(otp)) {

                    AppPreferences.setEmail(ForgotPassScreen.this, email);
                    startActivity(new Intent(ForgotPassScreen.this, SetPasswordScreen.class));
                    finish();

                } else {
                    Toast.makeText(ForgotPassScreen.this, "Please enter valid otp", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_forgotPop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
