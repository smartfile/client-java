package smartfileapi;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Client {

    protected String url = "";
    protected String version = "2.1";
    private final String API_URL = "https://vkovalenko.smartfile.com";
    private final String HTTP_USER_AGENT = String.format("SmartFile Java API client v%s",version);

    public Client() {
        String env_url = System.getenv("SMARTFILE_API_URL");
        if (env_url == null) {
            url = API_URL;
        } else {
            url = env_url;
        }
    }

    public Client(String url) {
        this.url = url;
    }

    public Client(String url, String version) {
        this.url = url;
        this.version = version;
    }

    //Get methods
    public Object get(String endpoint) throws SmartFileException {
        return request("get",endpoint,null,null,null,null);
    }

    public Object get(String endpoint, String id) throws SmartFileException {
        return request("get", endpoint, id, null,null,null);
    }

    public Object get(String endpoint, String id, Map<String,String> args) throws SmartFileException {
        return request("get", endpoint, id, args,null,null);
    }

    public Object get(String endpoint, String id, String args) throws SmartFileException {
        return request("get", endpoint, id, null,args,null);
    }

    //Post methods
    public Object post(String endpoint, String id, Map<String,String> args) throws SmartFileException {
        return request("post",endpoint,id,args,null,null);
    }

    public Object post(String endpoint, String id, File file) throws SmartFileException {
        File[] files = new File[1];
        files[0] = file;
        return request("post",endpoint,id,null,null,files);
    }

    public Object post(String endpoint, String id, File[] files) throws SmartFileException {
        return request("post",endpoint,id,null,null,files);
    }

    public Object post(String endpoint, String id, List<File> files) throws SmartFileException {
        File[] file = files.toArray(new File[files.size()]);
        return request("post",endpoint,id,null,null,file);
    }

    public Object post(String endpoint, String args) throws SmartFileException {
        return request("post",endpoint,null,null,args,null);
    }

    //Put methods
    public Object put(String endpoint) throws SmartFileException {
        return request("put",endpoint,null,null,null,null);
    }

    public Object put(String endpoint, String id) throws SmartFileException {
        return request("put",endpoint,id,null,null,null);
    }

    public Object put(String endpoint, String id, Map<String,String> args) throws SmartFileException {
        return request("put",endpoint,id,args,null,null);
    }

    public Object put(String endpoint, String id, String  args) throws SmartFileException {
        return request("put",endpoint,id,null,args,null);
    }

    //Delete methods
    public Object delete(String endpoint) throws SmartFileException {
        return request("delete",endpoint,null,null,null,null);
    }

    public Object delete(String endpoint, String id) throws SmartFileException {
        return request("delete",endpoint,id,null,null,null);
    }

    public Object delete(String endpoint, String id, Map<String,String> args) throws SmartFileException {
        return request("delete",endpoint,id,args,null,null);
    }

    public Object delete(String endpoint, String id, String  args) throws SmartFileException {
        return request("delete",endpoint,id,null,args,null);
    }



    private Object request(String method, String endpoint, String id, Map<String, String> args, String strArgs, File[] files) throws SmartFileException {

        if (args == null) {
            args = new HashMap<String, String>();
        }

        List<String> pathArray = new ArrayList<String>();
        pathArray.add("/api");
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
        args.put("User-Agent",HTTP_USER_AGENT);

        for (int trys = 0; trys < 3; trys++) {
            try {
                return do_request(method,url,args,files,strArgs,null);
            } catch (IOException e) {
                continue;
            } catch (SmartFileException e) {
                throw e;
            }

        }

        return "Failed 3 times".getBytes();
    }

    protected Object do_request(String method, String url, Map<String,String> args, File[] files, String strArgs, OAuthConsumer consumer) throws IOException, SmartFileException {
        String user_agent = args.get("User-Agent");

        String basic_auth = "";
        String key = "";
        String pwd = "";
        if (consumer == null) {
            key = args.get("key");
            pwd = args.get("pwd");
            basic_auth = new String(Base64.encodeBase64((key + ":" + pwd).getBytes()));
            args.remove("key");
            args.remove("pwd");
        }
        HttpClient client = new DefaultHttpClient();

        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,user_agent);
        args.remove("User-Agent");

        String params = "";

        if (!args.isEmpty()) {
            URIBuilder builder = new URIBuilder();
            for (Map.Entry<String,String> entry : args.entrySet()) {
                builder.setParameter(entry.getKey(),entry.getValue());
            }
            params = builder.toString();
        } else if (strArgs != null) {
            params = '?' + strArgs;
        }

        MultipartEntity request_file_entity = new MultipartEntity();
        HttpEntity request_entity = new BasicHttpEntity();
        if (method == "post") {
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    FileBody file_body = new FileBody(files[i]);
                    request_file_entity.addPart(files[i].getName(),file_body);
                }
            } else if (params.length() > 0) {
                request_entity = new StringEntity(params.substring(1),ContentType.create("application/x-www-form-urlencoded"));
                request_file_entity = null;
                params = "";
            }
        }

        HttpRequestBase request = new HttpGet();
        HttpPost request_post = new HttpPost();

        if (method == "get") {
            request = new HttpGet(url + params);
        } else if (method == "post") {
            request_post = new HttpPost(url + params);
            if (request_file_entity != null) {
                request_post.setEntity(request_file_entity);
            } else {
                request_post.setEntity(request_entity);
            }
        } else if (method == "put") {
            request = new HttpPut(url + params);
        } else if (method == "delete") {
            request = new HttpDelete(url + params);
        }

        try {
            if (consumer == null) {
                request.addHeader("Authorization", "Basic " + basic_auth);
                request_post.addHeader("Authorization", "Basic " + basic_auth);
            } else {
                consumer.sign(request);
                consumer.sign(request_post);
            }
            HttpResponse response;
            if (method == "post") {
                response = client.execute(request_post);
            } else {
                response = client.execute(request);
            }

            if (response.getHeaders("Location").length == 1) {
                args.clear();
                args.put("User-Agent",user_agent);
                args.put("key",key);
                args.put("pwd",pwd);
                return this.do_request("get", response.getHeaders("Location")[0].getValue(), args, null, null, consumer);
            }

            HttpEntity entity = response.getEntity();


            if (entity != null) {
                return entity.getContent();
            }
            return response.toString();
        } catch (IOException e) {
            throw e;
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
            return null;
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
            return null;
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
