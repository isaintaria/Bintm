package com.ng.socialtest.social.kakao;

import com.ng.socialtest.social.impl.SocialConfig;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * SocialLogin
 * Class: KakaoConfig
 * Created by Pyxis on 2017-10-27.
 * <p>
 * Description:
 */

public class KakaoConfig extends SocialConfig implements Serializable {
    private ArrayList<String> requestOptions;
    private boolean isSecureResource = false;

    private KakaoConfig(ArrayList<String> requestOptions, boolean isSecureResource) {
        this.requestOptions = requestOptions;
        this.isSecureResource = isSecureResource;
    }

    public ArrayList<String> getRequestOptions() {
        return requestOptions;
    }

    public boolean isSecureResource() {
        return isSecureResource;
    }

    public static class Builder {
        private boolean isRequireEmail = false;
        private boolean isRequireNickname = false;
        private boolean isSecureResource = false;
        private boolean isRequireImage = false;

        public Builder setRequireEmail() {
            isRequireEmail = true;
            return this;
        }

        public Builder setRequireNickname() {
            isRequireNickname = true;
            return this;
        }

        public Builder setSecureResource() {
            isSecureResource = true;
            return this;
        }

        public Builder setRequireImage() {
            isRequireImage = true;
            return this;
        }

        public KakaoConfig build() {
            ArrayList<String> requestOptions = new ArrayList<>();
            if (isRequireEmail) {
                requestOptions.add("kakao_account.email");
            }
            if (isRequireNickname) {
                requestOptions.add("properties.nickname");
            }
            if (isRequireImage) {
                requestOptions.add("properties.profile_image");
                requestOptions.add("properties.thumbnail_image");
            }

            return new KakaoConfig(requestOptions, isSecureResource);
        }
    }
}