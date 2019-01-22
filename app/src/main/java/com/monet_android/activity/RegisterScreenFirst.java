package com.monet_android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.monet_android.R;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.modle.countryCode.CountryPojo;
import com.monet_android.modle.countryCode.CountryResponse;
import com.monet_android.modle.social.SocialPojo;
import com.monet_android.modle.social.SocialPost;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppConstant.SUCCESS;

public class RegisterScreenFirst extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_regSignIn;
    private LoginButton regfb;
    private Button btn_regfb, btn_google_reg, btn_regNextFirst;
    private SignInButton btn_reggoogle;
    private EditText edt_regFirstName, edt_regLastName, edt_regEmail, edt_regPass, edt_regConPass;
    private CallbackManager callbackManager;
    private AccessToken mAccessToken;
    private static final String EMAIL = "email";
    private static int RC_SIGN_IN = 1;
    private GoogleSignInOptions gso;
    public  GoogleSignInClient mGoogleSignInClient;
    private String socialId, socialEmail, socialName, email;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog pd;
    public static ArrayList<CountryResponse> countryResponse = new ArrayList<>();
    private ProgressBar regSocialProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        initView();

    }

    private void initView() {
        tv_regSignIn = findViewById(R.id.tv_regSignIn);
        regfb = findViewById(R.id.regfb);
        btn_regfb = findViewById(R.id.btn_regfb);
        btn_google_reg = findViewById(R.id.btn_google_reg);
        btn_regNextFirst = findViewById(R.id.btn_regNextFirst);
        btn_regNextFirst.setEnabled(false);
        btn_reggoogle = findViewById(R.id.btn_reggoogle);
        edt_regFirstName = findViewById(R.id.edt_regFirstName);
        edt_regLastName = findViewById(R.id.edt_regLastName);
        edt_regEmail = findViewById(R.id.edt_regEmail);
        edt_regPass = findViewById(R.id.edt_regPass);
        edt_regConPass = findViewById(R.id.edt_regConPass);
        regSocialProg = findViewById(R.id.regSocialProg);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        btn_regNextFirst.setOnClickListener(this);
        btn_google_reg.setOnClickListener(this);
        btn_regfb.setOnClickListener(this);
        btn_reggoogle.setOnClickListener(this);
        tv_regSignIn.setOnClickListener(this);
        regfb.setOnClickListener(this);

        regfb.setLoginBehavior(LoginBehavior.WEB_ONLY);
        regfb.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonColor();
        disableSelection();

    }

    private void disableSelection() {
        edt_regFirstName.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

        edt_regLastName.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

        edt_regEmail.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

        edt_regPass.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

        edt_regConPass.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regNextFirst:
                if (AppUtils.isConnectionAvailable(this)) {
                    validate();
                } else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_regSignIn:
                startActivity(new Intent(this, LoginScreen.class));
                finish();
                break;

            case R.id.regfb:
                fbLogin();
                regSocialProg.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_regfb:
                if (AppUtils.isConnectionAvailable(this)) {
                    regfb.performClick();
                } else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_google_reg:
                if (!AppUtils.isConnectionAvailable(this)) {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    regSocialProg.setVisibility(View.VISIBLE);
                    googleLogIn();
                }
                break;
        }
    }

    private void getCountryCode() {
        pd.show();
        ApiInterface apiInterface = BaseUrl.getCountryCode().create(ApiInterface.class);
        Call<CountryPojo> pojoCall = apiInterface.getCountryCode();
        pojoCall.enqueue(new Callback<CountryPojo>() {
            @Override
            public void onResponse(Call<CountryPojo> call, Response<CountryPojo> response) {
                pd.dismiss();
                if (response.body().getCode().equals(SUCCESS)) {
                    countryResponse.addAll(response.body().getResponse());
                    Bundle bundle = new Bundle();
                    bundle.putString("first_name", edt_regFirstName.getText().toString());
                    bundle.putString("last_name", edt_regLastName.getText().toString());
                    bundle.putString("user_email", edt_regEmail.getText().toString());
                    bundle.putString("user_pass", edt_regPass.getText().toString());
                    bundle.putString("con_pass", edt_regConPass.getText().toString());
                    Intent intent = new Intent(RegisterScreenFirst.this, RegisterScreenSecond.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterScreenFirst.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(RegisterScreenFirst.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void validate() {

        email = edt_regEmail.getText().toString();

        if (edt_regEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter email id", Toast.LENGTH_SHORT).show();
        } else if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Please enter correct email id", Toast.LENGTH_SHORT).show();
        } else if (edt_regFirstName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
        } else if (edt_regPass.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (edt_regConPass.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
        } else if (edt_regPass.getText().toString().equals(edt_regConPass.getText().toString())) {
            getCountryCode();
        } else {
            Toast.makeText(this, "Password and Confirm Password should be same", Toast.LENGTH_SHORT).show();
        }

    }

    private void googleLogIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void fbLogin() {
        regfb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                getUserProfile(mAccessToken);
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegisterScreenFirst.this, "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(RegisterScreenFirst.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            socialName = object.getString("name");
                            socialEmail = object.getString("email");
                            socialId = object.getString("id");
                            socialLogin();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterScreenFirst.this, "Your email-id is not registered with facebook", Toast.LENGTH_SHORT).show();
                        }
                        LoginManager.getInstance().logOut();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        regSocialProg.setVisibility(View.GONE);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            socialId = account.getId();
            socialName = account.getDisplayName();
            socialEmail = account.getEmail();
            socialLogin();
        } catch (ApiException e) {
            if(e.getMessage().contains("12501")){
                Toast.makeText(this, "Login canceled by user", Toast.LENGTH_SHORT).show();
            }else if(e.getMessage().contains("12502")){
                Toast.makeText(this, "SIGN_IN_FAILED", Toast.LENGTH_SHORT).show();
            }else if(e.getMessage().contains("12500")){
                Toast.makeText(this, "sign in failure", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Something went wrong, please try after some time", Toast.LENGTH_SHORT).show();
            }
        }
        mGoogleSignInClient.signOut();
    }

    public void buttonColor() {
        edt_regEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_regEmail.getText().toString().isEmpty() || edt_regFirstName.getText().toString().isEmpty()
                        || edt_regLastName.getText().toString().isEmpty() || edt_regPass.getText().toString().isEmpty() || edt_regConPass.getText().toString().isEmpty()) {

                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regNextFirst.setEnabled(false);

                } else {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_white_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#362163"));
                    btn_regNextFirst.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_regFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_regEmail.getText().toString().isEmpty() || edt_regFirstName.getText().toString().isEmpty()
                        || edt_regLastName.getText().toString().isEmpty() || edt_regPass.getText().toString().isEmpty() || edt_regConPass.getText().toString().isEmpty()) {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regNextFirst.setEnabled(false);
                } else {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_white_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#362163"));
                    btn_regNextFirst.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_regLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_regEmail.getText().toString().isEmpty() || edt_regFirstName.getText().toString().isEmpty()
                        || edt_regLastName.getText().toString().isEmpty() || edt_regPass.getText().toString().isEmpty() || edt_regConPass.getText().toString().isEmpty()) {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regNextFirst.setEnabled(false);
                } else {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_white_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#362163"));
                    btn_regNextFirst.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_regPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_regEmail.getText().toString().isEmpty() || edt_regFirstName.getText().toString().isEmpty()
                        || edt_regLastName.getText().toString().isEmpty() || edt_regPass.getText().toString().isEmpty() || edt_regConPass.getText().toString().isEmpty()) {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regNextFirst.setEnabled(false);
                } else {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_white_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#362163"));
                    btn_regNextFirst.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        edt_regConPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_regEmail.getText().toString().isEmpty() || edt_regFirstName.getText().toString().isEmpty()
                        || edt_regLastName.getText().toString().isEmpty() || edt_regPass.getText().toString().isEmpty() || edt_regConPass.getText().toString().isEmpty()) {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_regNextFirst.setEnabled(false);
                } else {
                    btn_regNextFirst.setBackgroundResource(R.drawable.btn_white_capsule);
                    btn_regNextFirst.setTextColor(Color.parseColor("#362163"));
                    btn_regNextFirst.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void socialLogin() {
        pd.show();
        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        SocialPost socialPost = new SocialPost(socialName, socialEmail, socialId);
        Call<SocialPojo> pojoCall = apiInterface.socialLogin(socialPost);
        pojoCall.enqueue(new Callback<SocialPojo>() {
            @Override
            public void onResponse(Call<SocialPojo> call, Response<SocialPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(RegisterScreenFirst.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        AppPreferences.setUserLoggedOut(RegisterScreenFirst.this, false);
                        AppPreferences.setEmail(RegisterScreenFirst.this, response.body().getResponse().getUser_email().toString());
                        AppPreferences.setApiToken(RegisterScreenFirst.this, response.body().getResponse().getApi_token().toString());
                        AppPreferences.setUserId(RegisterScreenFirst.this, response.body().getResponse().getUser_id().toString());

                        startActivity(new Intent(RegisterScreenFirst.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterScreenFirst.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoginManager.getInstance().logOut();
                    }
                }
            }

            @Override
            public void onFailure(Call<SocialPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(RegisterScreenFirst.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }
}
