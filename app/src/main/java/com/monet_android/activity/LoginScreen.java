package com.monet_android.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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
import com.monet_android.modle.login.LoginPojo;
import com.monet_android.modle.login.LoginPost;
import com.monet_android.modle.social.SocialPojo;
import com.monet_android.modle.social.SocialPost;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.utils.AppConstant.SUCCESS;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    private SignInButton btn_google;
    private Button btn_fb, btn_google_login, btn_sign_in;
    private LoginButton fb;
    private TextView tv_forgotPass, tv_signUp;
    private EditText email, password;
    private static int RC_SIGN_IN = 1;
    private String socialId, socialEmail, socialName, Email = "", Password = "";
    private CallbackManager callbackManager;
    private AccessToken mAccessToken;
    private static final String EMAIL = "email";
    private ProgressDialog pd;
    boolean doubleBackToExitPressedOnce = false;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar socialProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        initView();
    }

    private void initView() {
        btn_google = findViewById(R.id.btn_google);
        btn_fb = findViewById(R.id.btn_fb);
        fb = findViewById(R.id.fb);
        tv_forgotPass = findViewById(R.id.tv_forgotPass);
        tv_signUp = findViewById(R.id.tv_signUp);
        btn_google_login = findViewById(R.id.btn_google_login);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_in.setEnabled(false);
        email = findViewById(R.id.edt_loginEmail);
        password = findViewById(R.id.edt_loginPass);
        socialProgress = findViewById(R.id.socialProgress);

        fb.setLoginBehavior(LoginBehavior.WEB_ONLY);

        btn_fb.setOnClickListener(this);
        btn_google_login.setOnClickListener(this);
        btn_sign_in.setOnClickListener(this);
        fb.setOnClickListener(this);
        tv_signUp.setOnClickListener(this);
        tv_forgotPass.setOnClickListener(this);

        fb.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading...");

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        getKeyHash();
        signInBtnColor();
        disableSelection();
    }

    private void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.monet_android",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", "KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void disableSelection() {
        email.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

        password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
            case R.id.btn_google_login:
                if (!AppUtils.isConnectionAvailable(this)) {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    signIn();
                }
                break;

            case R.id.btn_fb:
                if (!AppUtils.isConnectionAvailable(this)) {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    fb.performClick();
                }
                break;
            case R.id.fb:
                fbLogin();
                break;

            case R.id.btn_sign_in:
                validate();
                break;

            case R.id.tv_signUp:
                startActivity(new Intent(LoginScreen.this, RegisterScreenFirst.class));
                finish();
                break;

            case R.id.tv_forgotPass:
                startActivity(new Intent(LoginScreen.this, ForgotPassScreen.class));
                break;
        }

    }

    private void fbLogin() {
        socialProgress.setVisibility(View.VISIBLE);
        fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                getUserProfile(mAccessToken);
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginScreen.this, "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginScreen.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
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
                            socialId = object.getString("id");
                            socialName = object.getString("name");
                            socialEmail = object.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LoginManager.getInstance().logOut();
                        socialLogin();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void validate() {

        if (AppUtils.isConnectionAvailable(this)) {

            Email = email.getText().toString();
            Password = password.getText().toString();

            if (Email.isEmpty()) {
                Toast.makeText(this, "Please enter email id", Toast.LENGTH_SHORT).show();
            } else if (Password.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            } else {
                callLoginApi();
            }

        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void callLoginApi() {
        AppUtils.hideKeyboard(this);
        pd.show();

        ApiInterface apiInterface = BaseUrl.getClient().create(ApiInterface.class);
        LoginPost loginPost = new LoginPost(Email, Password);
        Call<LoginPojo> pojoCall = apiInterface.loginUser(loginPost);
        pojoCall.enqueue(new Callback<LoginPojo>() {
            @Override
            public void onResponse(Call<LoginPojo> call, Response<LoginPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(LoginScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        AppPreferences.setUserLoggedOut(LoginScreen.this, false);
                        AppPreferences.setApiToken(LoginScreen.this, response.body().getResponse().getApi_token());
                        AppPreferences.setEmail(LoginScreen.this, response.body().getResponse().getUser_email());
                        AppPreferences.setUserId(LoginScreen.this, response.body().getResponse().getUser_id());
                        startActivity(new Intent(LoginScreen.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<LoginPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(LoginScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInBtnColor() {
        email.addTextChangedListener(new TextWatcher() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.getText().toString().isEmpty()) {
                    btn_sign_in.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_sign_in.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_sign_in.setEnabled(false);
                } else if (password.getText().toString().isEmpty()) {
                    btn_sign_in.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_sign_in.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_sign_in.setEnabled(false);
                } else {
                    btn_sign_in.setBackgroundResource(R.drawable.btn_white_capsule);
                    btn_sign_in.setTextColor(Color.parseColor("#362163"));
                    btn_sign_in.setEnabled(true);
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.getText().toString().isEmpty()) {
                    btn_sign_in.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_sign_in.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_sign_in.setEnabled(false);
                } else if (password.getText().toString().isEmpty()) {
                    btn_sign_in.setBackgroundResource(R.drawable.btn_gray_capsule);
                    btn_sign_in.setTextColor(Color.parseColor("#b7b7b7"));
                    btn_sign_in.setEnabled(false);
                } else {
                    btn_sign_in.setBackgroundResource(R.drawable.btn_white_capsule);
                    btn_sign_in.setTextColor(Color.parseColor("#362163"));
                    btn_sign_in.setEnabled(true);
                }
            }

            @SuppressLint("ResourceAsColor")
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
                    Toast.makeText(LoginScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        AppPreferences.setUserLoggedOut(LoginScreen.this, false);
                        AppPreferences.setEmail(LoginScreen.this, response.body().getResponse().getUser_email());
                        AppPreferences.setApiToken(LoginScreen.this, response.body().getResponse().getApi_token());
                        AppPreferences.setUserId(LoginScreen.this, response.body().getResponse().getUser_id());

                        if (response.body().getEmail()) {
                            startActivity(new Intent(LoginScreen.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginScreen.this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginScreen.this, ProfileScreen.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("login", "login");
                            intent.putExtras(mBundle);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoginManager.getInstance().logOut();
                    }
                }
            }

            @Override
            public void onFailure(Call<SocialPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(LoginScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
            }
        });
    }

    private void signIn() {
        socialProgress.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialProgress.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        if (!AppUtils.isConnectionAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
        super.onResume();
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
}
