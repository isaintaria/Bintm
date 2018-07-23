package com.ng.socialtest.model;

import com.ng.socialtest.social.impl.SocialType;

public class UserModel extends ModelBase {

    private String userName;
    private String photoUrl;
    private String email;
    private SocialType loginType;

    public UserModel() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SocialType getLoginType() {
        return loginType;
    }

    public void setLoginType(SocialType loginType) {
        this.loginType = loginType;
    }
}
