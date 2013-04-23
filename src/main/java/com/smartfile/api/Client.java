package com.smartfile.api;


import oauth.signpost.OAuthConsumer;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Superclass which provides all api calls.
 * <p>Example of Usage:<br />
 * First you need to create instance of one of <code>Client</code> subclasses. For example let's assume that
 * we are using basic authentication:<br />
 * <code>
 *     BasicClient api = new BasicClient("client_key","client_secret");
 * </code>
 * <br />
 * If you API url is different from "https://app.smartfile.com", you should call <br />
 * <code>
 *     api.setApiUrl("yours url here");
 * </code>
 * <br />
 * Now we can call endpoints:<br />
 * <code>
 *     System.out.println(IOUtils.toString(api.get("/ping")));
 * </code>
 * <br />
 * We use <a href="http://commons.apache.org/proper/commons-io/">Apache IOUtils</a> to easily convert InputStream to string.<br />
 * The default response format is json. If you want to change it, you can send "format" parameter:<br />
 * <code>
 *     System.out.println(IOUtils.toString(api.get("/ping","/","format=xml)));
 * </code>
 * <br />
 * If you need to send couple of different parameters, it is more convenient to use Map:<br />
 * <code>
 *     Map<String,String> arg = new HashMap<String, String>();<br />
 *     arg.put("param1","value1");<br />
 *     arg.put("param2","value2");<br />
 *     System.out.println(IOUtils.toString(api.get("/ping","/",arg)));<br />
 * </code>
 * <br />
 * File upload.<br />
 * <code>
 *     File file = new File("file.txt");<br />
 *     api.post("/path/data","/",file);<br />
 * </code>
 * You can also upload multiple files at once, just pass the array or List.
 * <br />
 * <br />
 * File download.<br />
 * <code>
 *     InputStream in = api.get("/path/data/","file.txt");<br />
 *     out = new FileOutputStream("file.txt");<br />
 *     int c;<br />
 *      while ((c = in.read()) != -1) {<br />
 *      out.write(c);<br />
 *     }<br />
 * </code>
 * <br />
 * </p>
 */
public abstract class Client {

    protected String url = "";
    protected String version = "2.1";
    private final String API_URL = "https://app.smartfile.com";
    private final String HTTP_USER_AGENT = String.format("SmartFile Java API client v%s",version);

    /**
     * Use this method to set you own API url stored at environment variable SMARTFILE_API_URL
     * instead of default "https://app.smartfile.com"
     */
    public void setApiUrl() {
        setApiUrl(System.getenv("SMARTFILE_API_URL"));
    }

    /**
     * Use this method to set you own API url
     * instead of default "https://app.smartfile.com"
     * @param url Yours API url
     */
    public void setApiUrl(String url) {
        this.url = url;
    }

    protected Client() {
        String env_url = System.getenv("SMARTFILE_API_URL");
        if (env_url == null) {
            url = API_URL;
        } else {
            url = env_url;
        }
    }

    protected Client(String url) {
        this.url = url;
    }

    protected Client(String url, String version) {
        this.url = url;
        this.version = version;
    }

    //Get methods

    /**
     * Executes get request to the endpoint with no params.
     * @param endpoint The endpoint the request sent to
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream get(String endpoint) throws SmartFileException {
        return request("get",endpoint,null,null,null,null);
    }

    /**
     * Executes get request to the endpoint and id with no params.
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream get(String endpoint, String id) throws SmartFileException {
        return request("get", endpoint, id, null,null,null);
    }

    /**
     * Executes get request to the endpoint and id with params as <code>Map&lt;String, String&gt; </code>
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param args Key-Value pairs of request params
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream get(String endpoint, String id, Map<String,String> args) throws SmartFileException {
        return request("get", endpoint, id, args,null,null);
    }

    /**
     * Executes get request to the endpoint and id with params as <code>String</code>
     * Use it when you need few params with same name, like when copying or moving objects
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param args String of http request params like: "param1=value1&param2=value2"
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream get(String endpoint, String id, String args) throws SmartFileException {
        return request("get", endpoint, id, null,args,null);
    }

    //Post methods

    /**
     * Executes post request to the endpoint and id with params as <code>Map&lt;String, String&gt; </code>
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param args Key-Value pairs of request params
     * @return InputStream server response.
     * @throws SmartFileException
     */
    public InputStream post(String endpoint, String id, Map<String,String> args) throws SmartFileException {
        return request("post",endpoint,id,args,null,null);
    }

    /**
     * Executes post request to the endpoint and id with params as <code>String</code>
     * Use it when you need few params with same name, like when copying or moving objects
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param args String of http request params like: "param1=value1&param2=value2"
     * @return InputStream server response.
     * @throws SmartFileException
     */
    public InputStream post(String endpoint,String id, String args) throws SmartFileException {
        return request("post",endpoint,id,null,args,null);
    }

    /**
     * Executes post request to the endpoint and id with file in request body.
     * Use it when you need to upload single file
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param file File to upload
     * @return InputStream server response.
     * @throws SmartFileException
     */
    public InputStream post(String endpoint, String id, File file) throws SmartFileException {
        File[] files = new File[1];
        files[0] = file;
        return request("post",endpoint,id,null,null,files);
    }

    /**
     * Executes post request to the endpoint and id with files in request body.
     * Use it when you need to upload multiple files.
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param files File[] array to upload.
     * @return InputStream server response.
     * @throws SmartFileException
     */
    public InputStream post(String endpoint, String id, File[] files) throws SmartFileException {
        return request("post",endpoint,id,null,null,files);
    }

    /**
     * Executes post request to the endpoint and id with files in request body.
     * Use it when you need to upload multiple files.
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param files List&lt;File&gt; array to upload.
     * @return InputStream server response.
     * @throws SmartFileException
     */
    public InputStream post(String endpoint, String id, List<File> files) throws SmartFileException {
        File[] file = files.toArray(new File[files.size()]);
        return request("post",endpoint,id,null,null,file);
    }

    //Put methods
    /**
     * Executes put request to the endpoint with no params.
     * @param endpoint The endpoint the request sent to
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream put(String endpoint) throws SmartFileException {
        return request("put",endpoint,null,null,null,null);
    }

    /**
     * Executes put request to the endpoint and id with no params.
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream put(String endpoint, String id) throws SmartFileException {
        return request("put",endpoint,id,null,null,null);
    }

    /**
     * Executes put request to the endpoint and id with params as <code>Map&lt;String, String&gt; </code>
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param args Key-Value pairs of request params
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream put(String endpoint, String id, Map<String,String> args) throws SmartFileException {
        return request("put",endpoint,id,args,null,null);
    }

    /**
     * Executes put request to the endpoint and id with params as <code>String</code>
     * Use it when you need few params with same name, like when copying or moving objects
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param args String of http request params like: "param1=value1&param2=value2"
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream put(String endpoint, String id, String  args) throws SmartFileException {
        return request("put",endpoint,id,null,args,null);
    }

    //Delete methods
    /**
     * Executes delete request to the endpoint with no params.
     * @param endpoint The endpoint the request sent to
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream delete(String endpoint) throws SmartFileException {
        return request("delete",endpoint,null,null,null,null);
    }

    /**
     * Executes delete request to the endpoint and id with no params.
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream delete(String endpoint, String id) throws SmartFileException {
        return request("delete",endpoint,id,null,null,null);
    }

    /**
     * Executes delete request to the endpoint and id with params as <code>Map&lt;String, String&gt; </code>
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param args Key-Value pairs of request params
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream delete(String endpoint, String id, Map<String,String> args) throws SmartFileException {
        return request("delete",endpoint,id,args,null,null);
    }

    /**
     * Executes delete request to the endpoint and id with params as <code>String</code>
     * Use it when you need few params with same name, like when copying or moving objects
     * @param endpoint The endpoint the request sent to
     * @param id API id
     * @param args String of http request params like: "param1=value1&param2=value2"
     * @return InputStream. You should convert it to File if you expect result to be a file
     * or to String otherwise.
     * @throws SmartFileException
     */
    public InputStream delete(String endpoint, String id, String  args) throws SmartFileException {
        return request("delete",endpoint,id,null,args,null);
    }



    private InputStream request(String method, String endpoint, String id, Map<String, String> args, String strArgs, File[] files) throws SmartFileException {

        if (args == null)
            args = new HashMap<String, String>();

        List<String> pathArray = new ArrayList<String>();
        pathArray.add("/api");
        pathArray.add(version);
        pathArray.add(endpoint);
        if (id != null)
            pathArray.add(id);
        String path = StringUtils.join(pathArray, '/');
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
        return null;
    }

    protected InputStream do_request(String method, String url, Map<String,String> args, File[] files, String strArgs, OAuthConsumer consumer) throws IOException, SmartFileException {
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

        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, user_agent);
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
                request_entity = new StringEntity(params.substring(1), ContentType.create("application/x-www-form-urlencoded"));
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
                try {
                    consumer.sign(request);
                } catch (NullPointerException e) {
                    consumer.sign(request_post);
                }
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
            return null;
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
