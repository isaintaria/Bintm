package com.ng.socialtest.social.kakao;


import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.ng.socialtest.social.SocialLogin;
import com.ng.socialtest.social.impl.OnResponseListener;
import com.ng.socialtest.social.impl.ResultType;
import com.ng.socialtest.social.impl.SocialType;
import com.ng.socialtest.social.impl.UserInfoType;

import java.util.HashMap;
import java.util.Map;

/**
 * SocialLogin
 * Class: KakaoLogin
 * Created by Pyxis on 2017-10-27.
 * <p>
 * Description:
 */

public class KakaoLogin extends SocialLogin {
    private SessionCallback mSessionCallback;

    public KakaoLogin(Activity activity, OnResponseListener onResponseListener) {
        super(activity, onResponseListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLogin() {
        Session.getCurrentSession().removeCallback(mSessionCallback);
        mSessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mSessionCallback);
        if (!Session.getCurrentSession().checkAndImplicitOpen())
            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, activity);
    }

    @Override
    public void onDestroy() {
        if (mSessionCallback != null)
            Session.getCurrentSession().removeCallback(mSessionCallback);
    }

    @Override
    public void logout() {
        logout(false);
    }

    @Override
    public void logout(boolean clearToken) {
        if (Session.getCurrentSession().checkAndImplicitOpen())
            Session.getCurrentSession().close();
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d("SessionCallback", String.format("OpenFailed:: %s", exception != null ? exception.getMessage() : ""));
        }
    }

    private void requestMe() {
        KakaoConfig config = (KakaoConfig) getConfig(SocialType.KAKAO);
        UserManagement.getInstance().me(config.getRequestOptions(),new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                super.onFailure(errorResult);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("Login ::","Logout 되었음");
            }

            @Override
            public void onSuccess(MeV2Response result) {

                String id = String.valueOf(result.getId());
                String nickname = !TextUtils.isEmpty( result.getNickname()) ?  result.getNickname() : "";
                String email = result.getKakaoAccount().getEmail();
                String isEmailVerified = String.valueOf(result.getKakaoAccount().isEmailVerified());
                String profilePicture =  !TextUtils.isEmpty(result.getProfileImagePath()) ? result.getProfileImagePath() : "";
                String thumbnailPicture = !TextUtils.isEmpty(result.getThumbnailImagePath()) ? result.getThumbnailImagePath() : "";

                Map<UserInfoType, String> userInfoMap = new HashMap<>();
                userInfoMap.put(UserInfoType.ID, id);
                userInfoMap.put(UserInfoType.NAME, nickname);
                userInfoMap.put(UserInfoType.EMAIL, email);
                userInfoMap.put(UserInfoType.PROFILE_PICTURE, profilePicture);
                userInfoMap.put(UserInfoType.EMAIL_VERIFIED, String.valueOf(isEmailVerified));
                userInfoMap.put(UserInfoType.THUMBNAIL_IMAGE, thumbnailPicture);

                responseListener.onResult(SocialType.KAKAO, ResultType.SUCCESS, userInfoMap);
            }
        });
/*
        UserManagement.getInstance().me( )requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                responseListener.onResult(SocialType.KAKAO, ResultType.FAILURE, null);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                responseListener.onResult(SocialType.KAKAO, ResultType.FAILURE, null);
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                String id = String.valueOf(userProfile.getId());
                String nickname = !TextUtils.isEmpty(userProfile.getNickname()) ? userProfile.getNickname() : "";
                String email = userProfile.getEmail();
                String profilePicture = !TextUtils.isEmpty(userProfile.getProfileImagePath()) ? userProfile.getProfileImagePath() : "";
                String thumbnailPicture = !TextUtils.isEmpty(userProfile.getThumbnailImagePath()) ? userProfile.getThumbnailImagePath() : "";
                boolean isEmailVerified = userProfile.getEmailVerified();

                Map<UserInfoType, String> userInfoMap = new HashMap<>();
                userInfoMap.put(UserInfoType.ID, id);
                userInfoMap.put(UserInfoType.NICKNAME, nickname);
                userInfoMap.put(UserInfoType.EMAIL, email);
                userInfoMap.put(UserInfoType.PROFILE_PICTURE, profilePicture);
                userInfoMap.put(UserInfoType.EMAIL_VERIFIED, String.valueOf(isEmailVerified));
                userInfoMap.put(UserInfoType.THUMBNAIL_IMAGE, thumbnailPicture);

                responseListener.onResult(SocialType.KAKAO, ResultType.SUCCESS, userInfoMap);
            }

            @Override
            public void onNotSignedUp() {

            }
        }, config.getRequestOptions(), config.isSecureResource());*/
    }
}
