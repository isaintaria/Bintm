package com.ng.socialtest;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.ng.socialtest.social.SocialLogin;
import com.ng.socialtest.social.facebook.FacebookConfig;
import com.ng.socialtest.social.google.GoogleConfig;
import com.ng.socialtest.social.impl.SocialType;
import com.ng.socialtest.social.kakao.KakaoConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GlobalApplication extends Application {

    private static GlobalApplication instance;

    public static GlobalApplication getGlobalApplicationContext() {
        if (instance == null) {
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SocialLogin.init(this);

        // 각종 로그인 SDK 초기화 및 설정
        // 네이버는 LoginModule 내에서 SDK를 초기화 함

        KakaoConfig kakaoConfig = new KakaoConfig.Builder()
                .setRequireEmail()
                .setRequireNickname()
                .setRequireImage()
                .build();
        GoogleConfig googleConfig = new GoogleConfig.Builder()
                .setRequireEmail()
                .build();
        SocialLogin.addType(SocialType.KAKAO, kakaoConfig);
        SocialLogin.addType(SocialType.GOOGLE, googleConfig);

        FacebookConfig facebookConfig = new FacebookConfig.Builder()
                .setApplicationId("639176623083211")
                .setRequireEmail()
                .build();


        SocialLogin.addType(SocialType.FACEBOOK, facebookConfig);
     // MainActivity.getHashKey(this); // kakao SDK 는 프로그램의 HashKey를 요구해서 확인차 넣어둠


        printKeyHash();
        Log.d("Global ::","Init Complete");

    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("jk", "Exception(NameNotFoundException) : " + e);
        } catch (NoSuchAlgorithmException e) {
            Log.e("mkm", "Exception(NoSuchAlgorithmException) : " + e);
        }
    }

}
