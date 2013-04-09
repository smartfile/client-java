package smartfileapi;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Client {

    private String url = "http://app.smartfile.com/";
    private String version = "2.1";
    private boolean throttle_wait = true;
    private final String API_URL = "https://app.smartfile.com/";
    //private final String THROTTLE_PATTERN = re.compile('^.*; next=([\d\.]+) sec$');
    private final String HTTP_USER_AGENT = String.format("SmartFile Java API client v%s",version);

    public Client() {
    }

    public Client(String url) {
        this.url = url;
    }

    public Client(String url, String version) {
        this.url = url;
        this.version = version;
    }

    public Client(String url, String version, boolean throttle_wait) {
        this.url = url;
        this.version = version;
        this.throttle_wait = throttle_wait;
    }

    public String get(String endpoint) {
        return request("get",endpoint,null,null);

    }

    public String get(String endpoint, String id, Map<String, String> args) {
        return request("get",endpoint,id,args);

    }

    private String request(String method, String endpoint, String id, Map<String, String> args) {

        if (args == null) {
            args = new HashMap<String, String>();
        }

        List<String> pathArray = new ArrayList<String>();
        pathArray.add("api");
        pathArray.add(version);
        pathArray.add(endpoint);
        if (id != null) {
            pathArray.add(id);
        }
        String path = StringUtils.join(pathArray,'/');
        if (!path.substring(path.length() - 1).equals("/")) {
            path = path + '/';
        }
        path = path.replaceAll("//","/");
        String url = this.url + path;
        args.put("headers","");
        args.put("User-Agent",HTTP_USER_AGENT);

        for (int trys = 0; trys < 3; trys++) {
            try {
                return do_request(method,url,args);
            } catch (IOException e) {
                continue;
            }

        }

        return "Failed 3 times";
    }

    protected String do_request(String method, String url, Map<String,String> args) throws IOException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,args.get("User-Agent"));
        String basic_auth = new String(Base64.encodeBase64((args.get("key") + ":" + args.get("pwd")).getBytes()));
        HttpRequestBase request = new HttpGet();
        if (method == "get") {
            request = new HttpGet(url);
            request.addHeader("Authorization", "Basic " + basic_auth);
        } else if (method == "post") {

        } else if (method == "put") {

        } else if (method == "delete") {

        }
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
            return response.toString();
        } catch (IOException e) {
            throw e;
        }
    }
}
