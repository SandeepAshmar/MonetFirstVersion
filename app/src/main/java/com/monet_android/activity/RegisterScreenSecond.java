package com.monet_android.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.monet_android.R;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.register.RegisterPojo;
import com.monet_android.modle.register.RegisterPost;
import com.monet_android.modle.registerOtp.RegisterOtpPojo;
import com.monet_android.modle.registerOtp.RegisterOtpPost;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import java.util.Calendar;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.activity.RegisterScreenFirst.countryResponse;
import static com.monet_android.utils.AppConstant.REGISTER_SUCCESS;
import static com.monet_android.utils.AppConstant.SUCCESS;

public class RegisterScreenSecond extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout dob_layout, male_ll, female_ll;
    private CountryCodePicker ccp;
    private EditText edt_reg_mobile, edt_reg_city, edt_reg_state, edt_forgotPop_otp;
    private String mobileNumber, gender = "0", countryCode, dob, city, state;
    private Calendar cal, cal1, calOpen;
    private TextView tv_month, tv_day, tv_year, tv_male, tv_female, tv_terms;
    private ImageView img_male, img_female;
    private CheckBox terms_check;
    private Button btn_regSign;
    private String userFirstName, userLastName, userEmail, userPass, userConPass, countryName, dobMonth, otp;
    private ProgressDialog pd;
    private Dialog dialog;
    private Button btn_forgotPop_cancel, btn_forgotPop_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen_second);

        initView();
    }

    public void initView() {

        dob_layout = findViewById(R.id.dob_layout);
        ccp = findViewById(R.id.ccp);
        edt_reg_mobile = findViewById(R.id.edt_reg_mobile);
        tv_month = findViewById(R.id.tv_month);
        tv_day = findViewById(R.id.tv_day);
        tv_year = findViewById(R.id.tv_year);
        male_ll = findViewById(R.id.male_ll);
        female_ll = findViewById(R.id.female_ll);
        tv_male = findViewById(R.id.tv_male);
        tv_female = findViewById(R.id.tv_female);
        img_male = findViewById(R.id.img_male);
        img_female = findViewById(R.id.img_female);
        edt_reg_city = findViewById(R.id.edt_reg_city);
        tv_terms = findViewById(R.id.tv_terms);
        terms_check = findViewById(R.id.terms_check);
        btn_regSign = findViewById(R.id.btn_regSign);
        edt_reg_state = findViewById(R.id.edt_reg_state);

//        ccp.registerCarrierNumberEditText(edt_reg_mobile);

        dialog = new Dialog(this, R.style.Theme_Dialog);

        cal = Calendar.getInstance();
        cal1 = Calendar.getInstance();
        calOpen = Calendar.getInstance();
        cal.add(Calendar.YEAR, -100);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal1.add(Calendar.YEAR, -13);
        calOpen.set(1990, Calendar.JANUARY , 1);

        dob_layout.setOnClickListener(this);
        male_ll.setOnClickListener(this);
        female_ll.setOnClickListener(this);
        tv_terms.setOnClickListener(this);
        btn_regSign.setOnClickListener(this);

        terms_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonColor();
            }
        });

        buttonColor();
        checkEditText();
        generateOtp();

        Bundle bundle = this.getIntent().getExtras();
        userFirstName = bundle.getString("first_name");
        userLastName = bundle.getString("last_name");
        userEmail = bundle.getString("user_email");
        userPass = bundle.getString("user_pass");
        userConPass = bundle.getString("con_pass");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        disableSelection();

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryName = ccp.getSelectedCountryName();
                edt_reg_city.getText().clear();
                if(countryName.equalsIgnoreCase("India")) {
                    edt_reg_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                }else if(countryName.equalsIgnoreCase("United States")) {
                    edt_reg_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                }

                else {
                    edt_reg_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
                }
            }
        });

    }

    private void disableSelection() {
        edt_reg_mobile.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_reg_city.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_reg_state.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dob_layout:
                DateDialog();
                break;
            case R.id.male_ll:
                tv_male.setTextColor(Color.parseColor("#ffffff"));
                tv_female.setTextColor(Color.parseColor("#b4b3b3"));
                img_male.setImageResource(R.mipmap.male_selection);
                img_female.setImageResource(R.mipmap.female_unselect);
                gender = "Male";

                buttonColor();
                break;

            case R.id.female_ll:
                tv_male.setTextColor(Color.parseColor("#b4b3b3"));
                tv_female.setTextColor(Color.parseColor("#ffffff"));
                img_male.setImageResource(R.mipmap.male_unselect);
                img_female.setImageResource(R.mipmap.female_select);
                gender = "Female";

                buttonColor();
                break;

            case R.id.tv_terms:
                startActivity(new Intent(RegisterScreenSecond.this, TermsActivity.class));
                break;

            case R.id.terms_check:
                buttonColor();
                break;

            case R.id.btn_regSign:
                mobileNumber = edt_reg_mobile.getText().toString();
                city = edt_reg_city.getText().toString();
                countryName = ccp.getSelectedCountryName();
                dob = dobMonth + "/" +  tv_day.getText().toString() + "/" + tv_year.getText().toString();
                state = edt_reg_state.getText().toString();

                if(countryName.equalsIgnoreCase("India") || countryName.contains("United States")){
                    if (mobileNumber.length() < 10 ) {
                        Toast.makeText(this, "Please enter 10 digits mobile number", Toast.LENGTH_SHORT).show();
                    }else {
                        sendOtp();
                    }
                }else{
                        sendOtp();
                }


                break;
        }
    }

    private void generateOtp() {
        Random rnd = new Random();
        otp = String.valueOf(100000 + rnd.nextInt(999999));
    }

    private void sendOtp() {
        if (!AppUtils.isConnectionAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        } else {
            pd.show();
            ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
            RegisterOtpPost registerOtpPost = new RegisterOtpPost(userEmail, otp, mobileNumber);
            Call<RegisterOtpPojo> forgotPassPojoCall = apiInterface.sendOtpRegister(registerOtpPost);
            forgotPassPojoCall.enqueue(new Callback<RegisterOtpPojo>() {
                @Override
                public void onResponse(Call<RegisterOtpPojo> call, Response<RegisterOtpPojo> response) {
                    pd.dismiss();
                    if (response.body() == null) {
                        Toast.makeText(RegisterScreenSecond.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.body().getCode().equals(SUCCESS)) {
                            Toast.makeText(RegisterScreenSecond.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            openVerifyOtpDialog();
                        } else {
                            Toast.makeText(RegisterScreenSecond.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RegisterOtpPojo> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(RegisterScreenSecond.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                   callApi();
                } else {
                    Toast.makeText(RegisterScreenSecond.this, "Please enter valid otp", Toast.LENGTH_SHORT).show();
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

    private void buttonColor() {

        if (edt_reg_city.getText().toString().isEmpty() || edt_reg_mobile.getText().toString().isEmpty() || edt_reg_state.getText().toString().isEmpty()) {
            btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
            btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
            btn_regSign.setEnabled(false);
        } else if (!(terms_check.isChecked())) {
            btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
            btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
            btn_regSign.setEnabled(false);
        } else if (gender.equals("0") || tv_month.getText().toString().isEmpty()) {
            btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
            btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
            btn_regSign.setEnabled(false);
        } else {
            btn_regSign.setBackgroundResource(R.drawable.btn_white_capsule);
            btn_regSign.setTextColor(Color.parseColor("#362163"));
            btn_regSign.setEnabled(true);
        }
    }

    private void checkEditText() {
        edt_reg_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_reg_city.getText().toString().isEmpty() || edt_reg_mobile.getText().toString().isEmpty() || edt_reg_state.getText().toString().isEmpty()) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else if (!(terms_check.isChecked())) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else if (gender.equals("0") || tv_month.getText().toString().isEmpty()) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else {
                    buttonColor();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_reg_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_reg_city.getText().toString().isEmpty() || edt_reg_mobile.getText().toString().isEmpty() || edt_reg_state.getText().toString().isEmpty()) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else if (!(terms_check.isChecked())) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else if (gender.equals("0") || tv_month.getText().toString().isEmpty()) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else {
                    buttonColor();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_reg_state.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_reg_city.getText().toString().isEmpty() || edt_reg_mobile.getText().toString().isEmpty() || edt_reg_state.getText().toString().isEmpty()) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else if (!(terms_check.isChecked())) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else if (gender.equals("0") || tv_month.getText().toString().isEmpty()) {
                    btn_regSign.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regSign.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regSign.setEnabled(false);
                } else {
                    buttonColor();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void callApi() {
        pd.show();
        for (int j = 0; j < countryResponse.size(); j++) {
            if (countryResponse.get(j).getCountries_name().equalsIgnoreCase(countryName)) {
                countryCode = countryResponse.get(j).getCountries_id();
            }
        }

        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        RegisterPost registerPost = new RegisterPost(userFirstName, userLastName, userEmail, userPass, userConPass, gender, dob, mobileNumber, city, state, countryCode);
        final Call<RegisterPojo> registerPojoCall = apiInterface.registerUser(registerPost);
        registerPojoCall.enqueue(new Callback<RegisterPojo>() {
            @Override
            public void onResponse(Call<RegisterPojo> call, Response<RegisterPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(RegisterScreenSecond.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(REGISTER_SUCCESS)) {
                        AppPreferences.setEmail(RegisterScreenSecond.this, response.body().getResponse().getUser_email());
                        AppPreferences.setUserId(RegisterScreenSecond.this, response.body().getResponse().getUser_id());
                        AppPreferences.setApiToken(RegisterScreenSecond.this, response.body().getResponse().getApi_token());
                        AppPreferences.setUserLoggedOut(RegisterScreenSecond.this, false);
                        startActivity(new Intent(RegisterScreenSecond.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterScreenSecond.this, response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(RegisterScreenSecond.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void DateDialog() {

        DatePickerDialog dialog = new DatePickerDialog(this,
                R.style.Theme_AppCompat_Light_Dialog, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                // TODO Auto-generated method stub

                if (month == 0) {
                    tv_month.setText("January");
                    dobMonth = "01";
                } else if (month == 1) {
                    tv_month.setText("February");
                    dobMonth = "02";
                } else if (month == 2) {
                    tv_month.setText("March");
                    dobMonth = "03";
                } else if (month == 3) {
                    tv_month.setText("April");
                    dobMonth = "04";
                } else if (month == 4) {
                    tv_month.setText("May");
                    dobMonth = "05";
                } else if (month == 5) {
                    tv_month.setText("June");
                    dobMonth = "06";
                } else if (month == 6) {
                    tv_month.setText("July");
                    dobMonth = "07";
                } else if (month == 7) {
                    tv_month.setText("August");
                    dobMonth = "08";
                } else if (month == 8) {
                    tv_month.setText("September");
                    dobMonth = "09";
                } else if (month == 9) {
                    tv_month.setText("October");
                    dobMonth = "10";
                } else if (month == 10) {
                    tv_month.setText("November");
                    dobMonth = "11";
                } else if (month == 11) {
                    tv_month.setText("December");
                    dobMonth = "12";
                }

                tv_day.setText(String.valueOf(day));
                tv_year.setText(String.valueOf(year));
                buttonColor();
            }
        }, calOpen.get(Calendar.YEAR), calOpen.get(Calendar.MONTH),
                calOpen.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(cal1.getTimeInMillis());
        dialog.show();
    }
}
