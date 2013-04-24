package com.smartfile.api;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class OAuthTokenTest {

    static OAuthToken test;

    @BeforeClass
    public static void runBeforeClass() {
        test = new OAuthToken("token","secret");
    }

    @Test
    public void testGetToken() throws Exception {
        assert test.getToken().equals("token");
    }

    @Test
    public void testGetSecret() throws Exception {
        assert test.getSecret().equals("secret");
    }

    @Test
    public void testSetToken() throws Exception {
        test.setToken("newToken");
        assert test.getToken().equals("newToken");
    }

    @Ignore
    @Test
    public void testSetSecret() throws Exception {
        test.setSecret("newSecret");
        assert test.getSecret().equals("newSecret");
    }
}
