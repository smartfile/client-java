package smartfileapi;

import java.net.*;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Enumeration;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;

public class SmartFileAPI {
    // These constants are needed to access the API.
    private static String API_URL = "http://vkovalenko.smartfile.com/api/1";
    private static String API_KEY = "fSEgN3SdzVvfwbOUuv2WUtLO4Gtrmj";
    private static String API_PWD = "06hKBxYpN3pHYRquOjlzbeki9kzXj2";

    // This function does the bulk of the work by performing
    // the HTTP request and raising an exception for any HTTP
    // status code that does not signify success for the operation.
    private static void httpRequest(String uri, Hashtable<String, String> data, String method) throws Exception {
        // We use the XML format because Java has no native JSON decoder.
        String url = String.format("%s%s?format=xml", API_URL, uri);
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication (API_KEY, API_PWD.toCharArray());
            }
        });
        URL opener = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)opener.openConnection();
        conn.addRequestProperty("User-Agent", "Java SmartFile API Sample Client");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setRequestMethod(method);
        if (method.equals("POST")) {
            conn.setDoOutput(true);
            String pre = "";
            StringBuilder post = new StringBuilder();
            Enumeration keys = data.keys();
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                String value = URLEncoder.encode(data.get(key), "utf-8");
                post.append(String.format("%s%s=%s", pre, key, value));
                pre = "&";
            }
            conn.setRequestProperty("Content-Length", String.valueOf(post.length()));
            OutputStreamWriter requestStream = new OutputStreamWriter(conn.getOutputStream());
            requestStream.write(post.toString());
            requestStream.flush();
            requestStream.close();
        }
        int status = conn.getResponseCode();
        if ((method.equals("GET") && status == 200) ||
            (method.equals("POST") && status == 201) ||
            (method.equals("PUT") && status == 200) ||
            (method.equals("DELETE") && status == 204))
            // Success, return.
            return;
        // Non-success status code, parse message and throw an Exception.
        BufferedReader responseStream = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        responseStream.mark(1024);
        StringBuffer responseString = new StringBuffer(1024);
        char[] buf = new char[1024];
        int bytes = 0;
        while((bytes = responseStream.read(buf)) != -1) {
            responseString.append(String.valueOf(buf, 0, bytes));
        }
        String message = responseString.toString();
        try {
            responseStream.reset();
            XMLReader xr = XMLReaderFactory.createXMLReader();
            XmlMessageParser handler = new XmlMessageParser();
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);
            xr.parse(new InputSource(responseStream));
            message = handler.getMessage();
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            responseStream.close();
        }
        throw new SmartFileException(message, status);
    }

    // This function makes the User add API call. It uses the httpRequest
    // function to handle the transport. Additional API calls could be supported
    // simply by writing additional wrappers that create the data Hashtable and
    // use httpRequest to do the grunt work.
    public static void CreateUser(String fullname, String username, String password, String email) throws Exception {
        Hashtable<String, String> data = new Hashtable();
        data.put("name", fullname);
        data.put("username", username);
        data.put("password", password);
        data.put("email", email);
        httpRequest("/users/add/", data, "POST");
    }

    public static void DeleteUser(String username) throws Exception {
        httpRequest("/users/delete/" + username, null, "DELETE");
    }
}
