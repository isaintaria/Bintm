package com.ng.socialtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.ng.socialtest.model.UserModel;
import com.ng.socialtest.social.facebook.FacebookLogin;
import com.ng.socialtest.social.google.GoogleLogin;
import com.ng.socialtest.social.impl.OnResponseListener;
import com.ng.socialtest.social.impl.ResultType;
import com.ng.socialtest.social.impl.SocialType;
import com.ng.socialtest.social.impl.UserInfoType;
import com.ng.socialtest.social.kakao.KakaoLogin;
import com.ng.socialtest.social.naver.NaverLogin;

import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private Context mContext;
    private KakaoLogin kakaoModule;
    private GoogleLogin googleModule;
    private NaverLogin naverModule;
    private FacebookLogin facebookModule;

    private Button buttonKakao;
    private Button buttonGoogle;
    private Button buttonNaver;
    private Button buttonLogout;
    private Button buttonFacebook;

    SocialType loginType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();

        UiInit();
        ModuleInit();
    }

    private void ModuleInit() {
        kakaoModule = new KakaoLogin(this, new OnResponseListener() {
            @Override
            public void onResult(SocialType social, ResultType result, Map<UserInfoType, String> resultMap) {
                if( result == ResultType.SUCCESS )
                {
            //        Log.d("Login ::", resultMap.get(UserInfoType.NICKNAME));

                    // TODO : 빈틈 서버에 토큰 전송
                    // 조회 요청


                    // TODO : IF OK
 //                 if(Success)
                    {
                        doLogin(resultMap,SocialType.KAKAO);
                    }

                }
            }
        });
        googleModule = new GoogleLogin(this, new OnResponseListener() {
            @Override
            public void onResult(SocialType social, ResultType result, Map<UserInfoType, String> resultMap) {
                // Log.d("Login :: Google", resultMap.get(UserInfoType.NAME));
                if( result == ResultType.SUCCESS )
                {
                    doLogin(resultMap,SocialType.GOOGLE);
                }
            }
        });
        naverModule = new NaverLogin(this, new OnResponseListener() {
            @Override
            public void onResult(SocialType social, ResultType result, Map<UserInfoType, String> resultMap) {
                if( result == ResultType.SUCCESS)
                {
                    doLogin(resultMap,SocialType.NAVER);
                }
            }
        });
        facebookModule = new FacebookLogin(this, new OnResponseListener() {
            @Override
            public void onResult(SocialType social, ResultType result, Map<UserInfoType, String> resultMap) {
                Log.d("Login :: Facebook", resultMap.get(UserInfoType.NAME));
                doLogin(resultMap,SocialType.FACEBOOK);
            }
        });
    }

    private void UiInit() {
        buttonNaver = (Button)findViewById(R.id.button_naver);
        buttonGoogle = (Button)findViewById(R.id.button_google);
        buttonLogout = (Button)findViewById(R.id.button_Logout);
        buttonKakao = (Button)findViewById(R.id.button_Kakao);
        buttonFacebook = (Button)findViewById(R.id.button_facebook);

        buttonGoogle.setOnClickListener( v -> {
            googleModule.onLogin();
            loginType = SocialType.GOOGLE;
        });
        buttonKakao.setOnClickListener( v -> {
            kakaoModule.onLogin();
            loginType = SocialType.KAKAO;
        });
        buttonNaver.setOnClickListener( v -> {
            naverModule.onLogin();
            loginType = SocialType.NAVER;
        });
        buttonFacebook.setOnClickListener( v -> {
            facebookModule.onLogin();
            loginType = SocialType.FACEBOOK;
        });
        buttonLogout.setOnClickListener( v -> {
            kakaoModule.logout();
            googleModule.logout();
            naverModule.logout();
            facebookModule.logout();
        });
    }

    private void doLogout(){
    }

    private void doLogin(Map<UserInfoType, String> resultMap, SocialType type) {
  //      Log.d("Login :: Google", resultMap.get(UserInfoType.NAME));
        Bundle bundle = new Bundle();
        UserModel userModel = new UserModel();
        userModel.setEmail( resultMap.get(UserInfoType.EMAIL));
        userModel.setLoginType( type );
        userModel.setPhotoUrl( resultMap.get(UserInfoType.PROFILE_PICTURE));
        userModel.setUserName( resultMap.get(UserInfoType.NAME));
        bundle.putSerializable("UserModel",userModel);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtras( bundle );
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        kakaoModule.onDestroy();
        googleModule.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch( loginType )
        {
            case KAKAO:
                kakaoModule.onActivityResult(requestCode,resultCode,data);
                break;
            case GOOGLE:
                googleModule.onActivityResult(requestCode,resultCode,data);
                break;
            case FACEBOOK:
                facebookModule.onActivityResult(requestCode,resultCode,data);
                break;
        }

    }


}