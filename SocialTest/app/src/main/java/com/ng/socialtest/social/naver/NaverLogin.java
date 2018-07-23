package com.ng.socialtest.social.naver;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ng.socialtest.social.SocialLogin;
import com.ng.socialtest.social.impl.OnResponseListener;
import com.ng.socialtest.social.impl.ResultType;
import com.ng.socialtest.social.impl.SocialType;
import com.ng.socialtest.social.impl.UserInfoType;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * SocialLogin
 * Class: NaverLogin
 * Created by Pyxis on 2017-10-27.
 * <p>
 * Description:
 */

public class NaverLogin extends SocialLogin {
    private OAuthLogin authLogin = OAuthLogin.getInstance();
    private OAuthLoginHandler mOAuthLoginHandler;
    private static String CLIENT_ID = "Ozj5ODozVr_1IxszDUuD";
    private static String CLIENT_SECRET =  "CLnhS1pAhU";
    private static String CLIENT_NAME = "Social Test";

    public NaverLogin(Activity activity, OnResponseListener onResponseListener) {
        super(activity, onResponseListener);

        NaverConfig naverConfig = new NaverConfig.Builder()
                .setAuthClientId(CLIENT_ID)
                .setAuthClientSecret(CLIENT_SECRET)
                .setClientName(CLIENT_NAME)
                .build();
        mOAuthLoginHandler = new NaverLoginHandler();
        authLogin.showDevelopersLog(true);
        authLogin.init(activity, naverConfig.getAuthClientId(), naverConfig.getAuthClientSecret(), naverConfig.getClientName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onLogin() {
        authLogin.startOauthLoginActivity(activity, mOAuthLoginHandler);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void logout() {
        logout(false);
    }
    @Override
    public void logout(boolean clearToken) {
        if (clearToken) {
            OAuthLogin.getInstance().logoutAndDeleteToken(activity);
        } else {
            OAuthLogin.getInstance().logout(activity);
        }
    }
    private class NaverLoginHandler extends OAuthLoginHandler {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = authLogin.getAccessToken(activity);
                String authHeader = String.format("Bearer %s", accessToken);
                new RequestProfile().execute(authHeader);
            }
        }
    }

    private class RequestProfile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://openapi.naver.com/v1/nid/me")
                        .addHeader("Authorization", strings[0])
                        .build();
                Response response = client.newCall(request).execute();

                return response.body().string();
            } catch (IOException e) {
                responseListener.onResult(SocialType.NAVER, ResultType.FAILURE, null);
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (TextUtils.isEmpty(s)) {
                responseListener.onResult(SocialType.NAVER, ResultType.FAILURE, null);
                return;
            }
            JsonElement jsonElement = new JsonParser().parse(s);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject responseObject = jsonObject.getAsJsonObject("response");
            //JSONObject jsonObject = RichUtils.createJSONObject(s);
            //JSONObject responseObject = RichUtils.getJSONObject(jsonObject, "response");

            Map<UserInfoType, String> userInfoMap = new HashMap<>();

            String id = responseObject.get("id") != null ? responseObject.get("id").getAsString() : "";
            String name = responseObject.get("name") != null ? responseObject.get("name").getAsString() : "";
            String email = responseObject.get("email") != null ? responseObject.get("email").getAsString() : "";
            String nickname = responseObject.get("nickname") != null ? responseObject.get("nickname").getAsString() : "";
            String gender = responseObject.get("gender") != null ? responseObject.get("gender").getAsString() : "";
            String age = responseObject.get("age") != null ? responseObject.get("age").getAsString() : "";
            String birthday = responseObject.get("birthday") != null ?  responseObject.get("birthday").getAsString() : "";
            String profileImage = responseObject.get("profile_image") != null ? responseObject.get("profile_image").getAsString() : "";

            userInfoMap.put(UserInfoType.ID, id);
            userInfoMap.put(UserInfoType.NAME, name);
            userInfoMap.put(UserInfoType.EMAIL, email);
            userInfoMap.put(UserInfoType.NICKNAME, nickname);
            userInfoMap.put(UserInfoType.GENDER, gender);
            userInfoMap.put(UserInfoType.PROFILE_PICTURE, profileImage);
            userInfoMap.put(UserInfoType.AGE, age);
            userInfoMap.put(UserInfoType.BIRTHDAY, birthday);

            responseListener.onResult(SocialType.NAVER, ResultType.SUCCESS, userInfoMap);
        }
    }
}
