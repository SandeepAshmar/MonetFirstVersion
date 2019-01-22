package com.monet_android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.monet_android.R;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.setPassword.SetPasswordPojo;
import com.monet_android.modle.setPassword.SetPasswordPost;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppConstant.SUCCESS;

public class SetPasswordScreen extends AppCompatActivity {

    private Button btn_setPass_submit;
    private EditText edt_setPass_newPass, edt_setPass_conPass;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password_screen);

        btn_setPass_submit = findViewById(R.id.btn_setPass_submit);
        btn_setPass_submit.setEnabled(false);
        edt_setPass_newPass = findViewById(R.id.edt_setPass_newPass);
        edt_setPass_conPass = findViewById(R.id.edt_setPass_conPass);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading....");

        edt_setPass_newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edt_setPass_newPass.getText().toString().length() == 0 || edt_setPass_conPass.getText().toString().length() == 0){
                    btn_setPass_submit.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_setPass_submit.setEnabled(false);
                }else{
                    btn_setPass_submit.setBackgroundResource(R.drawable.btn_forgot_submit);
                    btn_setPass_submit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_setPass_conPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edt_setPass_newPass.getText().toString().length() == 0 || edt_setPass_conPass.getText().toString().length() == 0){
                    btn_setPass_submit.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_setPass_submit.setEnabled(false);
                }else{
                    btn_setPass_submit.setBackgroundResource(R.drawable.btn_forgot_submit);
                    btn_setPass_submit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_setPass_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_setPass_conPass.getText().toString().equals(edt_setPass_newPass.getText().toString())){
                    if(AppUtils.isConnectionAvailable(SetPasswordScreen.this)){
                        apiCall();
                    }else{
                        Toast.makeText(SetPasswordScreen.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(SetPasswordScreen.this, "New password and confirm password should be same", Toast.LENGTH_SHORT).show();
                }
            }
        });

        disableSelection();
    }

    private void disableSelection() {
        edt_setPass_newPass.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_setPass_conPass.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

    private void apiCall(){
        pd.show();

        String email = AppPreferences.getEmail(this);
        String pass = edt_setPass_newPass.getText().toString();
        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        SetPasswordPost setPasswordPost = new SetPasswordPost(email, pass);
        Call<SetPasswordPojo> passwordPojoCall = apiInterface.setPassword(email, pass);
        passwordPojoCall.enqueue(new Callback<SetPasswordPojo>() {
            @Override
            public void onResponse(Call<SetPasswordPojo> call, Response<SetPasswordPojo> response) {
                pd.dismiss();
                if(response.body() == null) {
                    Toast.makeText(SetPasswordScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                }else{
                    if(response.body().getCode().equals(SUCCESS)){
                        startActivity(new Intent(SetPasswordScreen.this, LoginScreen.class));
                        finish();
                        Toast.makeText(SetPasswordScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SetPasswordScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SetPasswordPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(SetPasswordScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
