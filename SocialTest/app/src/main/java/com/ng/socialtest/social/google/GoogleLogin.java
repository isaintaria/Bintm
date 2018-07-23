package com.ng.socialtest.social.google;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.ng.socialtest.R;
import com.ng.socialtest.social.SocialLogin;
import com.ng.socialtest.social.impl.OnResponseListener;
import com.ng.socialtest.social.impl.ResultType;
import com.ng.socialtest.social.impl.SocialType;
import com.ng.socialtest.social.impl.UserInfoType;

import java.util.HashMap;
import java.util.Map;


public class GoogleLogin extends SocialLogin {
    private static final int REQUEST_CODE_SIGN_IN = 19629;
    private GoogleApiClient mGoogleApiClient;

    public GoogleLogin(AppCompatActivity activity, OnResponseListener onResponseListener) {
        super(activity, onResponseListener);
        FirebaseAuth.getInstance().signOut();
        GoogleConfig googleConfig = (GoogleConfig) getConfig(SocialType.GOOGLE);
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        if (googleConfig.isRequireEmail()) {
            builder.requestEmail();
        }
       // builder.requestIdToken("70642079280-213fah6vkivh6d0801n4vb5djbi27bgf.apps.googleusercontent.com ");

        GoogleSignInOptions gso = builder.build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity).enableAutoManage(activity /* FragmentActivity */,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            }
                        } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onLogin() {
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.clearDefaultAccountAndReconnect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
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
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.clearDefaultAccountAndReconnect();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GoogleLogin :: ", "Handled Login");


        if (result.isSuccess()) {
            Log.d("GoogleLogin :: ", "Success");
            GoogleSignInAccount account = result.getSignInAccount();
            Map<UserInfoType, String> userInfoMaps = new HashMap<>();
            userInfoMaps.put(UserInfoType.NAME, account.getDisplayName());
            userInfoMaps.put(UserInfoType.EMAIL, account.getEmail());
            userInfoMaps.put(UserInfoType.ID, account.getId());
            userInfoMaps.put(UserInfoType.PROFILE_PICTURE, account.getPhotoUrl().toString());
            userInfoMaps.put(UserInfoType.ACCESS_TOKEN, account.getIdToken());

            mGoogleApiClient.disconnect();
            responseListener.onResult(SocialType.GOOGLE, ResultType.SUCCESS, userInfoMaps);
        } else {
            responseListener.onResult(SocialType.GOOGLE, ResultType.FAILURE, null);
        }
    }
}
