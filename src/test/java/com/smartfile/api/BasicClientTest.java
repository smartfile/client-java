package com.smartfile.api;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class BasicClientTest {
    BasicClient client;

    @Test
    public void testAuth() throws Exception {
        client = new BasicClient(System.getProperty("clientKey"),System.getProperty("clientSecret"));
        assert !IOUtils.toString(client.get("/whoami")).equals("{\"detail\": " +
                "\"You do not have permission to access this resource. You may need to login or otherwise authenticate the request.\"}");
    }
}
