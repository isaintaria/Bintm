package com.ng.socialtest.social;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;


import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kakao.auth.KakaoSDK;
import com.ng.socialtest.social.facebook.FacebookConfig;
import com.ng.socialtest.social.impl.OnResponseListener;
import com.ng.socialtest.social.impl.SocialConfig;
import com.ng.socialtest.social.impl.SocialType;
import com.ng.socialtest.social.kakao.KakaoSDKAdapter;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * SocialLogin
 * Class: SocialLogin
 * Created by Pyxis on 2017-10-27.
 * <p>
 * Description:
 */

public abstract class SocialLogin {
    protected Activity activity;
    protected OnResponseListener responseListener;
    private static Map<SocialType, SocialConfig> availableTypeMap = new HashMap<>();
    private static Context mContext;
    private static ArrayList<SocialType> alreadyInitializedList = new ArrayList<>();

    public SocialLogin(Activity activity, OnResponseListener onResponseListener) {
        this.activity = activity;
        responseListener = onResponseListener;
    }

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void onLogin();

    public abstract void onDestroy();

    public abstract void logout();

    public abstract void logout(boolean clearToken);

    /**
     * Initialize SocialLogin
     *
     * @param context {@link Context} object, it will be Application Context.
     */
    public static void init(Context context) {
        if (context instanceof Activity || context instanceof Service) {
            throw new InvalidParameterException("Context must be Application Context, not Activity, Service Context.");
        }

        mContext = context;
        clear();
    }

    /**
     * Initialize SocialLogin with pre-configured AvailableTypeMap
     *
     * @param context
     * @param availableTypeMap
     */
    public static void init(Context context, Map<SocialType, SocialConfig> availableTypeMap) {
        if (context instanceof Activity || context instanceof Service) {
            throw new InvalidParameterException("Context must be Application Context, not Activity, Service Context.");
        }

        mContext = context;

        if (!availableTypeMap.isEmpty()) {
            SocialLogin.availableTypeMap = availableTypeMap;
            initializeSDK();
        }
    }

    /**
     * add SocialConfig to use
     *
     * @param socialType   {@link SocialType} object
     * @param socialConfig {@link SocialConfig} object
     */
    public static void addType(SocialType socialType, SocialConfig socialConfig) {
        if (mContext == null) {
            throw new RuntimeException("No context is available, please add context by declare SocialLogin.init(this) in Application class");
        }

        availableTypeMap.put(socialType, socialConfig);
        initializeSDK();
    }

    /**
     * remove SocialConfig in AvailableTypeMap
     *
     * @param socialType {@link SocialType} object
     */
    public static void removeType(SocialType socialType) {
        availableTypeMap.remove(socialType);
    }

    /**
     * clear AvailableTypeMap
     */
    public static void clear() {
        availableTypeMap.clear();
        alreadyInitializedList.clear();
    }

    protected static SocialConfig getConfig(SocialType type) {
        if (!availableTypeMap.containsKey(type)) {
            throw new RuntimeException(String.format("No config is available, please add proper config :: SocialType -> %s", type.name()));
        }
        return availableTypeMap.get(type);
    }

    private static void initializeSDK() {
        for (Map.Entry<SocialType, SocialConfig> entry : availableTypeMap.entrySet()) {
            if (alreadyInitializedList.contains(entry.getKey())) {
                return;
            }
            alreadyInitializedList.add(entry.getKey());
            switch (entry.getKey()) {
                case KAKAO:
                    initializeKakaoSDK();
                    break;
                case FACEBOOK:
                    initializeFacebookSDK((FacebookConfig) entry.getValue());
                    break;

            }
        }
    }

    private static void initializeKakaoSDK() {
        KakaoSDK.init(new KakaoSDKAdapter(mContext));
    }

    private static void initializeFacebookSDK(FacebookConfig config) {
        FacebookSdk.setApplicationId(config.getApplicationId());
        FacebookSdk.sdkInitialize(mContext);
        AppEventsLogger.activateApp(mContext);
    }


}
