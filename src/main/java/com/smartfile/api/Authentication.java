package com.smartfile.api;

import oauth.signpost.OAuthConsumer;

class Authentication {
    public static final String AUTH_BASIC = "basic";
    public static final String AUTH_OAUTH = "oauth";

    private String type;

    private String basicKey;

    private String basicPwd;

    private OAuthConsumer oAuthConsumer;

    public Authentication(String basicKey, String basicPwd) {
        type = AUTH_BASIC;
        this.basicKey = basicKey;
        this.basicPwd = basicPwd;
    }

    public Authentication(OAuthConsumer oAuthConsumer) {
        type = AUTH_OAUTH;
        this.oAuthConsumer = oAuthConsumer;
    }

    public String getBasicKey() {
        return basicKey;
    }

    public String getBasicPwd() {
        return basicPwd;
    }

    public OAuthConsumer getoAuthConsumer() {
        return oAuthConsumer;
    }
}
