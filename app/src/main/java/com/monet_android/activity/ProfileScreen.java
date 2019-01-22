package com.monet_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.monet_android.R;
import com.monet_android.activity.reaction.ReactionWatchVideo;
import com.monet_android.adapter.PlaceAutocompleteAdapter;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.halper.PlaceInfo;
import com.monet_android.modle.countryCode.CountryPojo;
import com.monet_android.modle.countryCode.CountryResponse;
import com.monet_android.modle.getProfile.GetProfilePojo;
import com.monet_android.modle.getProfile.GetProfileResponse;
import com.monet_android.modle.updateProfile.UpdateProfilePojo;
import com.monet_android.modle.updateProfile.UpdateProfilePost;
import com.monet_android.utils.AppPreferences;
import com.monet_android.utils.AppUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.activity.RegisterScreenFirst.countryResponse;
import static com.monet_android.utils.AppConstant.IMG_BASE_URL;
import static com.monet_android.utils.AppConstant.SUCCESS;

public class ProfileScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private CircleImageView img_profile;
    private EditText edt_pro_username, edt_pro_dob, edt_pro_city, edt_pro_state, edt_pro_country, edt_pro_mobile, edt_pro_email;
    public Button btn_pro_update;
    private Spinner sp_pro_gender;
    private ImageView img_back;
    public static final int PICK_FROM_CAMERA = 1001, LOAD_FROM_GALLERY = 1002;
    private String finalPath = "", gender, mobile, email, city, state, country, country_code, dob, username;
    private Uri mImageCaptureUri;
    private String mCurrentPhotoPath, userId;
    private AlertDialog.Builder dialog;
    private ApiInterface apiInterface;
    private ProgressDialog pd;
    private String image, token;
    private DonutProgress donut_profile;
    private int pStatus = 0;
    private int setMinValue = 0;
    private Handler handler = new Handler();
    private Calendar cal, cal1, calOpen;
    private String login = "";
    private static final String TAG = "MapActivity";

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private AutoCompleteTextView mSearchText;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private static final int PLACE_PICKER_REQUEST = 1;
    private Dialog autoSearchDialog;
    private boolean autoDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        login = getIntent().getExtras().getString("login");

        intiView();

    }

    private void intiView() {

        img_profile = findViewById(R.id.img_profile);
        img_back = findViewById(R.id.img_pro_back);
        btn_pro_update = findViewById(R.id.btn_pro_update);
        edt_pro_username = findViewById(R.id.edt_pro_username);
        edt_pro_dob = findViewById(R.id.edt_pro_dob);
        sp_pro_gender = findViewById(R.id.sp_pro_gender);
        edt_pro_country = findViewById(R.id.edt_pro_country);
        edt_pro_email = findViewById(R.id.edt_pro_email);
        edt_pro_mobile = findViewById(R.id.edt_pro_mobile);
        edt_pro_state = findViewById(R.id.edt_pro_state);
        edt_pro_city = findViewById(R.id.edt_pro_city);
        donut_profile = findViewById(R.id.donut_profile);

        edt_pro_city.setFocusable(false);
        edt_pro_dob.setFocusable(false);

        donut_profile.setText("");

        autoSearchDialog = new Dialog(ProfileScreen.this, R.style.Theme_AppCompat_Light);

        edt_pro_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutoSearchDialog();

            }
        });

        img_back.setOnClickListener(this);
        img_profile.setOnClickListener(this);
        btn_pro_update.setOnClickListener(this);

        userId = AppPreferences.getUserId(this);
        token = "Bearer " + AppPreferences.getApiToken(this);
        apiInterface = BaseUrl.getClient().create(ApiInterface.class);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        edt_pro_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog();
            }
        });

        if (AppUtils.isConnectionAvailable(this)) {
            if (countryResponse.size() == 0) {
                getCountryCode();
            } else {
                getUserProfile();
            }
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        ArrayList<String> spinerItem = new ArrayList<>();

        spinerItem.add("Gender");
        spinerItem.add("Male");
        spinerItem.add("Female");
        spinerItem.add("Other");

        ArrayAdapter<String> genderData = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinerItem);
        genderData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_pro_gender.setAdapter(genderData);

        disableSelection();

    }

    private void disableSelection() {
        edt_pro_email.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_pro_mobile.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_pro_country.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_pro_username.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_pro_dob.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_pro_city.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) { return false; }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) { return false; }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        edt_pro_state.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

            case R.id.img_profile:
                if (checkExternalStoragePermission()) {
                    openIntent();
                } else {
                    requestExternalStoragePermission();
                }
                break;
            case R.id.img_pro_back:
                onBackPressed();
                break;
            case R.id.btn_pro_update:
                if (edt_pro_username.getText() == null || edt_pro_username.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
                } else {
                    setMinValue = 0;
                    donut_profile.clearAnimation();
                    vaidateUpdate();
                }
                break;
        }
    }

    private void requestExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            openUtilityDialog(ProfileScreen.this, "External Storage permission is required. Please allow this permission in App Settings.");
        } else {
            ActivityCompat.requestPermissions(ProfileScreen.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1011);
        }
    }

    private boolean checkExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(ProfileScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void openIntent() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ProfileScreen.this,
                R.style.CustomAlertDialogStyle);
        dialog.setMessage("Take Profile Activity Picture");
        dialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, LOAD_FROM_GALLERY);
                        dialog.dismiss();
                    }
                });

        dialog.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkCameraPermission()) {
                            openCamera();
                        } else {
                            requestCameraPermission();
                        }
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_FROM_CAMERA:
                    try {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        Uri tempUri = getImageUri(getApplicationContext(), photo);
                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        File finalFile = new File(getRealPathFromURI(tempUri));
                        mCurrentPhotoPath = String.valueOf(finalFile);
                        setImage(mCurrentPhotoPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case LOAD_FROM_GALLERY:
                    if (data != null) {
                        mImageCaptureUri = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(mImageCaptureUri, filePathColumn,
                                null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        setImage(picturePath);
                    }
                    break;
            }
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOAD_FROM_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, LOAD_FROM_GALLERY);
                } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileScreen.this, R.style.DialogTheme);
                    alertDialog.setMessage("You Have To Give Permission From Your Device Setting To go in Setting Please Click on Settings Button");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    alertDialog.show();
                }
                break;
        }

    }

    @SuppressLint("NewApi")
    private void setImage(String picturePath) {
        finalPath = picturePath;
        image = picturePath;
        try {
            ExifInterface exif = new ExifInterface(picturePath);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, bounds);
            int srcWidth = bounds.outWidth;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = AppUtils.calculateInSampleSize(bounds, 150, 150);
            opts.inScaled = true;
            opts.inDensity = srcWidth;
            opts.inTargetDensity = 100 * bounds.inSampleSize;
            Bitmap bm = BitmapFactory.decodeFile(picturePath, opts);
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

            File file = new File(picturePath);
            OutputStream fOut = null;
            fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            Glide.with(this).load(picturePath).into(img_profile);
            if (picturePath != null) {

            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
    }

    private boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(ProfileScreen.this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileScreen.this, Manifest.permission.CAMERA)) {
            openUtilityDialog(ProfileScreen.this, "Camera permission is required. Please allow this permission in App Settings.");
        } else {
            ActivityCompat.requestPermissions(ProfileScreen.this, new String[]{Manifest.permission.CAMERA}, 1012);
        }
    }

    private void openUtilityDialog(final Context ctx, final String messageID) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(ctx, R.style.Theme_AppCompat_Light);
            dialog.setMessage(messageID);
            dialog.setCancelable(false);
            dialog.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void getUserProfile() {
        pd.show();
        Call<GetProfilePojo> pojoCall = apiInterface.getUserProfile(token, userId);
        pojoCall.enqueue(new Callback<GetProfilePojo>() {
            @Override
            public void onResponse(Call<GetProfilePojo> call, Response<GetProfilePojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(ProfileScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        setProfile(response);
//                        rv_profile.setAdapter(profileAdapter);
                        runSeekBar();
                    } else {
                        Toast.makeText(ProfileScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetProfilePojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(ProfileScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProfile(Response<GetProfilePojo> response) {
        if(response.body().getResponse().getImage_url().isEmpty() || response.body().getResponse().getImage_url() == null){
            setMinValue = setMinValue+1;
        }if(response.body().getResponse().getCity().isEmpty() || response.body().getResponse().getCity() == null){
            setMinValue = setMinValue+1;
        }if(response.body().getResponse().getCountry().isEmpty() || response.body().getResponse().getCountry() == null){
            setMinValue = setMinValue+1;
        }if(response.body().getResponse().getDateOfBirth().isEmpty() || response.body().getResponse().getDateOfBirth() == null){
            setMinValue = setMinValue+1;
        }if (response.body().getResponse().getEmail().isEmpty() || response.body().getResponse().getEmail() == null){
            setMinValue = setMinValue+1;
        }if(response.body().getResponse().getFullName().isEmpty() || response.body().getResponse().getFullName() == null){
            setMinValue = setMinValue+1;
        }if(response.body().getResponse().getGender().isEmpty() || response.body().getResponse().getGender() == null) {
            setMinValue = setMinValue + 1;
        }if(response.body().getResponse().getMobile().isEmpty() || response.body().getResponse().getMobile() == null){
            setMinValue = setMinValue+1;
        }if(response.body().getResponse().getStates().isEmpty() || response.body().getResponse().getStates() == null){
            setMinValue = setMinValue+1;
        }

        dob = response.body().getResponse().getDateOfBirth();

        String OLD_FORMAT = "dd/MM/yyyy";
        String NEW_FORMAT = "MM/dd/yyyy";

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        dob = sdf.format(d);

        edt_pro_dob.setText(dob);

        if (response.body().getResponse().getGender().equalsIgnoreCase("Male")) {
            sp_pro_gender.setSelection(1);
        } else if (response.body().getResponse().getGender().equalsIgnoreCase("Female")) {
            sp_pro_gender.setSelection(2);
        } else if (response.body().getResponse().getGender().equalsIgnoreCase("Other")) {
            sp_pro_gender.setSelection(3);
        } else {
            sp_pro_gender.setSelection(0);
        }

        edt_pro_username.setText(response.body().getResponse().getFullName());
//        edt_pro_gender.setText(response.body().getResponse().getGender());
        edt_pro_city.setText(response.body().getResponse().getCity());
        edt_pro_state.setText(response.body().getResponse().getStates());
        edt_pro_mobile.setText(response.body().getResponse().getMobile());
        edt_pro_email.setText(response.body().getResponse().getEmail());
        if(response.body().getResponse().getImage_url().isEmpty() || response.body().getResponse().getImage_url() == null){
            img_profile.setBackgroundResource(R.drawable.ic_person_gray_24dp);
        }else{
            Glide.with(getApplicationContext()).load(IMG_BASE_URL + response.body().getResponse().getImage_url())
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(img_profile);
            image = IMG_BASE_URL + response.body().getResponse().getImage_url();
        }
        country_code = response.body().getResponse().getCountry();
        for (int i = 0; i < countryResponse.size(); i++) {
            if (countryResponse.get(i).getCountries_id().equalsIgnoreCase(country_code)) {
                edt_pro_country.setText(countryResponse.get(i).getCountries_name());
            }
        }

        if(edt_pro_username.getText().toString().length() > 0){
            edt_pro_username.setClickable(false);
            edt_pro_username.setFocusable(false);
        }

        if(edt_pro_email.getText().toString().length() > 0){
            edt_pro_email.setClickable(false);
            edt_pro_email.setFocusable(false);
        }
    }

    private void vaidateUpdate(){
        if (image == null) {
            image = "";
        } else {
            if (finalPath != null && !finalPath.isEmpty()) {
                image = AppUtils.convertToBase64(finalPath);
                AppPreferences.setImageBase64(this, image);
            }else{
                image = AppPreferences.getImageBase64(this);
            }
        }
        username = edt_pro_username.getText().toString();
        mobile = edt_pro_mobile.getText().toString();
        email = edt_pro_email.getText().toString();
        city = edt_pro_city.getText().toString();
        state = edt_pro_state.getText().toString();
        country = edt_pro_country.getText().toString();
        dob = edt_pro_dob.getText().toString();

        for (int j = 0; j < countryResponse.size(); j++) {
            if (countryResponse.get(j).getCountries_name().equalsIgnoreCase(country)) {
                country_code = countryResponse.get(j).getCountries_id();
            }
        }

        gender = sp_pro_gender.getSelectedItem().toString();

        if(username.isEmpty()){
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
        }else if(mobile.isEmpty()){
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
        }else if(email.isEmpty()){
            Toast.makeText(this, "Please enter your email id", Toast.LENGTH_SHORT).show();
        }else if(city.isEmpty()){
            Toast.makeText(this, "Please enter your city", Toast.LENGTH_SHORT).show();
        }else if(dob.isEmpty()){
            Toast.makeText(this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
        }else {
            updateProfile();
        }
    }

    private void updateProfile() {
        pd.show();

        UpdateProfilePost updateProfilePost = new UpdateProfilePost(gender, mobile, email, city, state, image, country_code, dob, username);
        Call<UpdateProfilePojo> pojoCall = apiInterface.profileUpdate(token, userId, updateProfilePost);
        pojoCall.enqueue(new Callback<UpdateProfilePojo>() {
            @Override
            public void onResponse(Call<UpdateProfilePojo> call, Response<UpdateProfilePojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(ProfileScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals("200")) {
                        Toast.makeText(ProfileScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(ProfileScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateProfilePojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(ProfileScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
                    getUserProfile();
                } else {
                    Toast.makeText(ProfileScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(ProfileScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void dateDialog() {

        cal = Calendar.getInstance();
        cal1 = Calendar.getInstance();
        calOpen = Calendar.getInstance();
        cal.add(Calendar.YEAR, -100);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal1.add(Calendar.YEAR, -13);
        calOpen.set(1990, Calendar.JANUARY , 1);

        DatePickerDialog dialog = new DatePickerDialog(ProfileScreen.this ,R.style.Theme_AppCompat_Light_Dialog, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat sendFormat = new SimpleDateFormat("MM/dd/yyyy");
                String dateString = sendFormat.format(calendar.getTime());
                edt_pro_dob.setText(dateString);
            }
        }, calOpen.get(Calendar.YEAR), calOpen.get(Calendar.MONTH),
                calOpen.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(cal1.getTimeInMillis());
        dialog.show();
    }

    private void openAutoSearchDialog(){
        if(autoDialog){
            mGoogleApiClient.stopAutoManage(ProfileScreen.this);
            mGoogleApiClient.disconnect();
        }

        autoSearchDialog.setCanceledOnTouchOutside(false);
        autoSearchDialog.setContentView(R.layout.auto_complete_dialog);

        autoSearchDialog.show();

        mSearchText = autoSearchDialog.findViewById(R.id.input_Dialogsearch);
        autoSearchDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        autoDialog = true;

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

    }

    private void geoLocate() {

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void openKeyboard(){

    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d("TAG", "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d("TAG", "onResult: address: " + place.getAddress());
                String address = (String) place.getAddress();
                String[] str = address.split(", ");
                edt_pro_city.setText("");
                edt_pro_state.setText("");
                edt_pro_country.setText("");
                int addLength = str.length;
                if(addLength == 1){
                    edt_pro_city.setText(str[0]);
                    edt_pro_state.setText(str[0]);
                    edt_pro_country.setText("");
                    country_code = "0";
                }else if(addLength == 2){
                    edt_pro_city.setText(str[0]);
                    edt_pro_state.setText(str[0]);
                    edt_pro_country.setText(str[addLength-1]);
                    country_code = str[addLength-1];
                }else{
                    edt_pro_city.setText(str[0]);
                    edt_pro_state.setText(str[1]);
                    edt_pro_country.setText(str[addLength-1]);
                    country_code = str[addLength-1];
                }

                mPlace.setId(place.getId());
//                mPlace.setLatlng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());
            } catch (NullPointerException e) {
                Log.e("TAG", "onResult: NullPointerException: " + e.getMessage());
            }
            places.release();

            autoSearchDialog.dismiss();
            mGoogleApiClient.stopAutoManage(ProfileScreen.this);
            mGoogleApiClient.disconnect();
            autoDialog = false;
        }
    };

    private void runSeekBar() {

        if(setMinValue == 0){
            donut_profile.setDonut_progress("" + 9);
        }else{
            setMinValue = 9 - setMinValue;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (pStatus < setMinValue) {
                        pStatus += 1;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                donut_profile.setDonut_progress("" + pStatus);
                            }
                        });
                        try {
                            Thread.sleep(130);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    }

    @Override
    public void onBackPressed() {
        if(login == null){
            finish();
        }else{
           backValidation();
        }
    }

    private void backValidation(){
        String name = edt_pro_username.getText().toString();
        String dob = edt_pro_dob.getText().toString();
        String gender = sp_pro_gender.getSelectedItem().toString();
        String city = edt_pro_city.getText().toString();
        String mobile = edt_pro_mobile.getText().toString();
        String email = edt_pro_email.getText().toString();

        if(name.isEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }else if(dob.isEmpty()){
            Toast.makeText(this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
        }else if(gender.equalsIgnoreCase("gender")){
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
        }else if(city.isEmpty()){
            Toast.makeText(this, "Please enter your city", Toast.LENGTH_SHORT).show();
        }else if(mobile.isEmpty()){
            Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
        }else if(email.isEmpty()){
            Toast.makeText(this, "Please enter your email id", Toast.LENGTH_SHORT).show();
        }else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}