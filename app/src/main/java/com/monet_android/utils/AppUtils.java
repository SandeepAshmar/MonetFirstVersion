package com.monet_android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.monet_android.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppUtils {

    public static AlertDialog d;

    public static boolean isEmpty(EditText etText) {
        return !((etText.getText().toString().trim().length() == 0)
                || etText.getText().toString() == null
                || etText.getText().toString().equals("null"));
    }

    public static String toInitCapitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    // validate email address
    public static boolean isEmailValid(EditText email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(
                email.getText().toString()).matches();
    }

    public static boolean isConnectionAvailable(Context ctx) {
        ConnectivityManager mManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mManager.getActiveNetworkInfo();
        return (mNetworkInfo != null) && (mNetworkInfo.isConnected());
    }

    // display a no Internet toast if no connectivity is available
    public static void noInternetDialog(Context ctx) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx, R.style.Theme_AppCompat_Light_Dialog);
        dialog.setMessage("Please check your internet connection and try again later.");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        d = dialog.create();
        d.show();
    }

    // display a message dialog with custom message
    public static void openUtilityDialog(final Context ctx, final String message) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx, R.style.Theme_AppCompat_Light_Dialog);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        d = dialog.create();
        d.show();
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void hideKeyboard(View view, Context ctx) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(View view, Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

    }

    public static String initCapString(String input) {
        if (input == null || input.trim().toString().isEmpty())
            return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static void requestFocus(View view) {
        if (view != null) {
            view.getParent().requestChildFocus(view, view);
            view.requestFocus();
        }
    }

    public static void showToastShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackBarLong(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v, v.getContext());
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public static String formatDate(int y, int m, int d, String formatTo) {
        String formattedDateStr = null;
        String formatStr = "dd/MM/yyyy";
        if (formatTo == null || formatTo.trim().isEmpty()) {
            formatTo = formatStr;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(formatTo);
        formattedDateStr = sdf.format(date);

        return formattedDateStr == null ? "" : formattedDateStr;
    }

    public static String dateFormat(String dateString, String inputDateFormat, String outputDateFormat) {
        String formattedDate = dateString;
        DateFormat preDateFormat = new SimpleDateFormat(inputDateFormat);
        DateFormat postDateFormat = new SimpleDateFormat(outputDateFormat);
        Date date = null;
        try {
            date = preDateFormat.parse(dateString);
            formattedDate = postDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return formattedDate;
        }
    }

    public static String convertToBase64(String imagePath) {
        if (imagePath == null || imagePath == "") {
            return "";
        }

        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);

        return encodedImage;

    }

    public static String getByteArrayFromImageURL(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return null;
    }

    public static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER, model = Build.MODEL;
        return model.startsWith(manufacturer) ? capitalizePhrase(model) :
                capitalizePhrase(manufacturer) + " " + model;
    }

    private static String capitalizePhrase(String s) {
        if (s == null || s.length() == 0)
            return s;
        else {
            StringBuilder phrase = new StringBuilder();
            boolean next = true;
            for (char c : s.toCharArray()) {
                if (next && Character.isLetter(c) || Character.isWhitespace(c))
                    next = Character.isWhitespace(c = Character.toUpperCase(c));
                phrase.append(c);
            }
            return phrase.toString();
        }
    }

    public static String getAndroidDeviceId(Context context) {
        if (context != null) {
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return "";
    }


    public static String replaceString(String fullString, String strReplaceWith) {

        return fullString.replace(strReplaceWith, "");
    }


    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                boolean cache = deleteDir(dir);
                Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" +
                        " DELETED *******************");
                Log.e("After pressing Exit ", "cache memory clear result is::  " +
                        cache);
            }
        } catch (Exception e) {
// TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return true;
    }
}
