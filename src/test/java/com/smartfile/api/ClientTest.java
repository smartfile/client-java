package com.smartfile.api;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.Random;

public class ClientTest {

    private static Client client;
    private static String clientKey;
    private static String clientSecret;
    private static String apiUrl;

    private static String  responseToString(InputStream inputStream) throws IOException {
        if (inputStream == null)
                return " ";
        return IOUtils.toString(inputStream);
    }

    private static File getTestFile() throws IOException {
        File file = new File("test");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("test");
        writer.close();
        return file;
    }

    @BeforeClass
    public static void BeforeClass() throws Exception {
        clientKey = System.getProperty("clientKey");
        clientSecret = System.getProperty("clientSecret");
        apiUrl = System.getProperty("apiUrl");
    }

    @Before
    public void Before() throws Exception {
        client = new BasicClient(clientKey, clientSecret);
    }

    @Test
    public void testSetApiUrl() throws Exception {
        String url;
        if (apiUrl != null)
            url = apiUrl;
        else
            url = "app.smartfile.com";
        client.setApiUrl(url);
        assert client.getApiUrl().equals(url);
    }

    @Test
    public void testPing() throws Exception {
        String result = responseToString(client.get("/ping"));
        System.out.println("Ping test:");
        System.out.println(result);
        assert result.equals("{\"ping\": \"pong\"}");
    }

    @Test
    public void testPathInfo() throws Exception {
        String result = responseToString(client.get("/path","/info"));
        System.out.println("Path info:");
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testFileUpload() throws Exception {
        String result = responseToString(client.post("/path", "/data", getTestFile()));
        System.out.println("File upload:");
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testFileDownload() throws Exception {
        InputStream inputStream = client.get("/path/data","test");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String result = reader.readLine();
        System.out.println("File download: \n" + result);
        assert  result.equals("test");
    }

    @Test
    public void testProgress() throws Exception {
        System.out.println("Proggress test:");
        client.addHeader("X-Upload-UUID","9f1466d1-dc3b-4e86-a625-f4b2ce1c1a10");
        System.out.println(responseToString(client.post("/path", "/data", getTestFile())));
        String result = responseToString(client.get("/path/progress","9f1466d1-dc3b-4e86-a625-f4b2ce1c1a10"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testAccessPath() throws Exception {
        String result = responseToString(client.get("/access/path","/test"));
        System.out.println("Access path test:");
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testSearch() throws Exception {
        client.post("/path", "/data", getTestFile());
        String result = responseToString(client.get("/search/path","","keywords=test"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testWatchPath() throws Exception {
        System.out.println("Watch path test:");
        System.out.println("\tStart watch root:");
        String result = responseToString(client.post("/watch", "/paths", "path=%2F"));
        System.out.println(result);
        System.out.println("\tWatch list:");
        result = responseToString(client.get("/watch","/paths"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testMkdir() throws Exception {
        System.out.println("Test mkdir:");
        String result = responseToString(client.put("/path/oper/mkdir/","TestDir"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testMove() throws Exception {
        System.out.println("Test move");
        client.put("/path/oper/mkdir/","TestDir");
        client.post("/path", "/data", getTestFile());
        String result = responseToString(client.post("/path/oper/move","","src=%2Ftest&dst=%2FTestDir"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testRename() throws Exception {
        System.out.println("Test rename");
        client.put("/path/oper/mkdir/", "TestDir");
        Random r = new Random();
        Integer str = new Integer(r.nextInt(1024));
        String result = responseToString(client.post("/path/oper/rename","","src=%2FTestDir&dst=%2FTest" + str.toString()));
        assert result.length() > 0;
    }

    @Test
    public void testCopy() throws Exception {
        System.out.println("Test copy");
        client.put("/path/oper/mkdir/","TestDir");
        client.post("/path", "/data", getTestFile());
        String result = responseToString(client.post("/path/oper/copy","","src=%2Ftest&dst=%2FTestDir"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testRemove() throws Exception {
        System.out.println("Test remove:");
        client.put("/path/oper/mkdir/","TestDir");
        String result = responseToString(client.post("/path/oper/remove","","path=%2FTestDir"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testCheckSum() throws Exception {
        System.out.println("Test checksum:");
        client.post("/path", "/data", getTestFile());
        String result = responseToString(client.post("/path/oper/checksum","","path=%2Ftest&algorithm=md5"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testComperss() throws Exception {
        System.out.println("Test compress:");
        client.post("/path", "/data", getTestFile());
        String result = responseToString(client.post("/path/oper/compress","","path=%2Ftest&dst=%2F&name=test.zip"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testUser() throws Exception {
        System.out.println("User test:");
        Random r = new Random();
        Integer str = new Integer(r.nextInt(1024));
        String result = responseToString(client.post("/user","","name=boba" + str.toString() + "&username=bobafett" + str.toString() + "&email=boba.fett" + str.toString() + "@example.com&password=secret" + str.toString()));
        System.out.println(result);
        result = responseToString(client.get("/user"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testRole() throws Exception {
        System.out.println("Role test:");
        String result = responseToString(client.get("/role"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testAccessUser() throws Exception {
        System.out.println("Access user test:");
        Random r = new Random();
        Integer str = new Integer(r.nextInt(1024));
        client.put("/path/oper/mkdir/","TestDir");
        client.post("/user","","name=boba" + str.toString() + "&username=bobafett" + str.toString() + "&email=boba.fett" + str.toString() + "@example.com&password=secret" + str.toString());
        String result = responseToString(client.post("/access","/user","user=bobafett" + str.toString() + "&path=%2FTestDir&read=true&list=true&remove=true&write=true"));
        System.out.println(result);
        result = responseToString(client.get("/access/user"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testSearchUser() throws Exception {
        System.out.println("Search user test:");
        Random r = new Random();
        Integer str = new Integer(r.nextInt(1024));
        client.post("/user","","name=boba" + str.toString() + "&username=bobafett" + str.toString() + "&email=boba.fett" + str.toString() + "@example.com&password=secret" + str.toString());
        String result = responseToString(client.get("/search/path","","keywords=bobafett" + str.toString()));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testWatch() throws Exception {
        System.out.println("Watch test:");
        Random r = new Random();
        Integer str = new Integer(r.nextInt(1024));
        System.out.println("\tStart watch user:");
        client.post("/user","","name=boba" + str.toString() + "&username=bobafett" + str.toString() + "&email=boba.fett" + str.toString() + "@example.com&password=secret" + str.toString());
        String result = responseToString(client.post("/watch/users/", "", "user=bobafett" + str.toString()));
        System.out.println(result);
        System.out.println("\tWatch list:");
        result = responseToString(client.get("/watch/users/",""));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testGroup() throws Exception {
        System.out.println("Group test:");
        String result = responseToString(client.get("/group"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testGroupAccess() throws Exception {
        System.out.println("Access group test:");
        client.put("/path/oper/mkdir/","TestDir");
        String result = responseToString(client.post("/access","/group","group=Users&path=%2FTestDir&read=true&list=true&remove=true&write=true"));
        System.out.println(result);
        result = responseToString(client.get("/access/group"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testSearchGroup() throws Exception {
        System.out.println("Search group test:");
        String result = responseToString(client.get("/search/group","","keywords=Users"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testGroupWatch() throws Exception {
        System.out.println("Watch group test:");
        System.out.println("\tStart watch group:");
        String result = responseToString(client.post("/watch/groups/", "", "group=Users"));
        System.out.println(result);
        System.out.println("\tWatch list:");
        result = responseToString(client.get("/watch/groups/",""));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testPreferences() throws Exception {
        System.out.println("Preferences test:");
        String result = responseToString(client.get("/pref"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testSitePreferences() throws Exception {
        System.out.println("Preferences site test:");
        String result = responseToString(client.post("/pref","/site","name=max-per-page&value=10"));
        System.out.println(result);
        result = responseToString(client.get("/pref","/site"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testUserPreferences() throws Exception {
        System.out.println("Preferences user test:");
        String result = responseToString(client.post("/pref","/user","name=max-per-page&value=10"));
        System.out.println(result);
        result = responseToString(client.get("/pref","/user"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testSession() throws Exception {
        System.out.println("Session test:");
        String result = responseToString(client.get("/session"));
        System.out.println(result);
        assert result.length() > 0;
    }

    @Test
    public void testActivity() throws Exception {
        System.out.println("Activity test:");
        String result = responseToString(client.get("/activity"));
        System.out.println(result);
        assert result.length() > 0;
    }










}
