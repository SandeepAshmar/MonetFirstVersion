package com.monet_android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.monet_android.R;
import com.monet_android.utils.AppUtils;

public class TellAFriendScreen extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_friend_back;
    private RelativeLayout rl_facebook, rl_whatsapp, rl_email, rl_txt;
    private ShareButton fb_share;
    private String link = "https://play.google.com/store/apps/details?id=com.monet_android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tell_afriend_screen);

        fb_share = findViewById(R.id.fb_share);
        rl_facebook = findViewById(R.id.rl_facebook);
        rl_whatsapp = findViewById(R.id.rl_whatsapp);
        rl_txt = findViewById(R.id.rl_txt);
        rl_email = findViewById(R.id.rl_email);
        img_friend_back = findViewById(R.id.img_friend_back);

        fb_share.setOnClickListener(this);
        rl_facebook.setOnClickListener(this);
        rl_whatsapp.setOnClickListener(this);
        rl_email.setOnClickListener(this);
        img_friend_back.setOnClickListener(this);
        rl_txt.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.fb_share:
                shareWithFb();
                break;
            case R.id.rl_facebook:
                if(AppUtils.isConnectionAvailable(this)){
                    fb_share.performClick();
                }else{
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_whatsapp:
                shareWithWhatsapp();
                break;
            case R.id.rl_email:
                if(AppUtils.isConnectionAvailable(this)){
                shareWithEmail();
                }else{
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_friend_back:
                onBackPressed();
                break;
            case R.id.rl_txt:
                sendSMS();
                break;
        }
    }

    private void sendSMS() {
        try{
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            sendIntent.putExtra("sms_body"  , "Your Friend just invited you to Monet Networks. Download with this link: "+link);
            startActivity(sendIntent);
        }catch (Exception e){
            Toast.makeText(this, "Oops! something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareWithFb(){
        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentDescription("Your Friend just invited you to Monet Networks. Download with this link:")
                .setContentUrl(Uri.parse(link))
                .build();
        fb_share.setShareContent(shareLinkContent);
    }

    private void shareWithWhatsapp(){
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Your Friend just invited you to Monet Networks. Download with this link: "+link);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareWithEmail(){
        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.putExtra(Intent.EXTRA_SUBJECT, "Your Friend just invited you to Monet Networks");
        intent.putExtra(Intent.EXTRA_TEXT, "Your Friend just invited you to Monet Networks. Download with this link: "+link);
        intent.setData(Uri.parse("mailto:")); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);
    }
}
