package com.monet_android.connection;

import com.monet_android.modle.countryCode.CountryPojo;
import com.monet_android.modle.forgotPassword.ForgotPassPojo;
import com.monet_android.modle.forgotPassword.ForgotPassPost;
import com.monet_android.modle.getCampDetails.GetCampDetails_Pojo;
import com.monet_android.modle.getCampignFlow.GetCampFlowPojo;
import com.monet_android.modle.getProfile.GetProfilePojo;
import com.monet_android.modle.lastWatchVideoList.LastWatchVideoPojo;
import com.monet_android.modle.leaderboard.LeaderBoardPojo;
import com.monet_android.modle.login.LoginPojo;
import com.monet_android.modle.login.LoginPost;
import com.monet_android.modle.newContent.NewPojo;
import com.monet_android.modle.popularContent.PopularPojo;
import com.monet_android.modle.question.Question_Pojo;
import com.monet_android.modle.radarChart.RadarMyPojo;
import com.monet_android.modle.reaction.ReactionPost;
import com.monet_android.modle.reaction.ReactionResponse;
import com.monet_android.modle.register.RegisterPojo;
import com.monet_android.modle.register.RegisterPost;
import com.monet_android.modle.registerOtp.RegisterOtpPojo;
import com.monet_android.modle.registerOtp.RegisterOtpPost;
import com.monet_android.modle.saveFeedbackPreDetails.SaveFeedBack_Pojo;
import com.monet_android.modle.setPassword.SetPasswordPojo;
import com.monet_android.modle.setPassword.SetPasswordPost;
import com.monet_android.modle.social.SocialPojo;
import com.monet_android.modle.social.SocialPost;
import com.monet_android.modle.staging.StagingPojo;
import com.monet_android.modle.survay.SurvayPojo;
import com.monet_android.modle.survay.SurvayPost;
import com.monet_android.modle.updateProfile.UpdateProfilePojo;
import com.monet_android.modle.updateProfile.UpdateProfilePost;
import com.monet_android.modle.xpcp.XpCpPojo;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    /*Login user*/
    @Headers("Content-Type: application/json")
    @POST("login")
    Call<LoginPojo> loginUser(@Body LoginPost loginPost);

    /*Register user*/
    @Headers("Content-Type: application/json")
    @POST("register")
    Call<RegisterPojo> registerUser(@Body RegisterPost registerPost);

    /*sociallogin*/
    @Headers("Content-Type: application/json")
    @POST("social")
    Call<SocialPojo> socialLogin(@Body SocialPost socialPost);

    /*send otp*/
    @Headers("Content-Type: application/json")
    @POST("sendotp")
    Call<ForgotPassPojo> sendOtp(@Body ForgotPassPost forgotPassPost);

    @Headers("Content-Type: application/json")
    @POST("sendotp")
    Call<RegisterOtpPojo> sendOtpRegister(@Body RegisterOtpPost forgotPassPost);

    /*reset password*/
    @FormUrlEncoded
    @POST("setpassword")
    Call<SetPasswordPojo> setPassword(@Field("user_email")String user_email, @Field("user_password")String user_password);

    /*get new content*/
    @GET("getcontents/0/15/1")
    Call<NewPojo> getNewContent(@Header("Authorization") String token);

    /*get popular content*/
    @GET("getcontents/16/15/1")
    Call<PopularPojo> getPopularContent(@Header("Authorization") String token);

    /*get user profile*/
    @Headers("Content-Type: application/json")
    @GET("getUserInfo/{user_id}")
    Call<GetProfilePojo> getUserProfile(@Header("Authorization") String token,@Path("user_id") String s);

    /*Update profile and change password*/
    @Headers("Content-Type: application/json")
    @POST("profile/{user_id}")
    Call<UpdateProfilePojo> profileUpdate(@Header("Authorization") String token,@Path("user_id") String s,@Body UpdateProfilePost updateProfilePost);

    /*save feedback pre details*/
    @Headers("Content-Type: application/json")
    @POST("savefeedbackpredetails")
    Call<SaveFeedBack_Pojo> saveFeedBack(@Header("Authorization") String token, @Query("data") JSONObject jsonObject);

    /*get camp details*/
    @Headers("Content-Type: application/json")
    @GET("campaignDetails/{cam_id}")
    Call<GetCampDetails_Pojo> getCampDetails(@Header("Authorization") String token,@Path("cam_id") String cam_id);

    /*get pre and post questions*/
    @Headers("Content-Type: application/json")
    @GET("getsurveyquestions/{cam_id}/{PreorPost}")
    Call<Question_Pojo> getQuestioList(@Header("Authorization") String token, @Path("cam_id") String cam_id, @Path("PreorPost") String pre_or_post);

    /*Submit reaction part*/
    @Headers("Content-Type: application/json")
    @POST("savereaction")
    Call<ReactionResponse> submitReactionPart(@Header("Authorization") String token, @Body ReactionPost reactionPost);

    /*get country code*/
    @Headers("Content-Type: application/json")
    @GET("getCountry")
    Call<CountryPojo> getCountryCode();

    /*get leader board*/
    @Headers("Content-Type: application/json")
    @GET("leaderboard")
    Call<LeaderBoardPojo> getLeaderBoard(@Header("Authorization") String token);

    /*save survay answer*/
    @Headers("Content-Type: application/json")
    @POST("savesurveyanswers")
    Call<SurvayPojo> submitSurvayAns(@Header("Authorization") String token, @Body SurvayPost survayPost);

    /*get camp flow*/
    @Headers("Content-Type: application/json")
    @POST("getCampaign")
    Call<GetCampFlowPojo> getCampFlow(@Header("Authorization") String token, @Query("cmp_id") String cmp_id);

    /*get camp flow*/
    @Headers("Content-Type: application/json")
    @POST("feedbackStage")
    Call<StagingPojo> sendStagingData(@Header("Authorization") String token, @Query("campaignId") String cmp_id, @Query("jsonData") JSONObject jsonData);

    /*get radar chart value*/
    @Headers("Content-Type: application/json")
    @GET("getemotion")
    Call<RadarMyPojo> getLastVideoChart(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("gethallfame")
    Call<RadarMyPojo> getHallOfFame(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("getxpcp")
    Call<XpCpPojo> getXpCp(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("lastwatchvideo")
    Call<LastWatchVideoPojo> getLastVideoWatchList(@Header("Authorization") String token);
}

