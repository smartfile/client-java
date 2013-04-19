package smartfileapi;


import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.PlainTextMessageSigner;
import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class OAuthClient extends Client {
    private OAuthToken client;
    private OAuthToken access;
    private OAuthToken request;

    private int trys;

    OAuthConsumer consumer;
    OAuthProvider provider;

    public OAuthClient() {
        super();
        this.client = new OAuthToken(System.getenv("SMARTFILE_CLIENT_TOKEN"),System.getenv("SMARTFILE_CLIENT_SECRET"));
        this.access = new OAuthToken(System.getenv("SMARTFILE_ACCESS_TOKEN"),System.getenv("SMARTFILE_ACCESS_SECRET"));
    }

    public OAuthClient(String client_token, String client_secret) throws SmartFileException {
        super();
        this.client = new OAuthToken(client_token,client_secret);
    }

    public OAuthClient(String client_token, String client_secret, String access_token, String access_secret) throws SmartFileException {
        super();
        this.client = new OAuthToken(client_token,client_secret);
        this.access = new OAuthToken(access_token,access_secret);
    }

    public void get_request_token() throws SmartFileException{
        this.get_request_token(OAuth.OUT_OF_BAND);
    }

    public void get_request_token(String callback) throws SmartFileException {
        consumer = new CommonsHttpOAuthConsumer(client.getToken(),client.getSecret());
        consumer.setMessageSigner(new PlainTextMessageSigner());
        provider = new CommonsHttpOAuthProvider(url + "/oauth/request_token/",
                                                url + "/oauth/access_token/",
                                                url + "/oauth/authorize/");
        provider.setOAuth10a(true);
        try {
            provider.retrieveRequestToken(consumer, callback);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            trys++;
            if (trys < 10) {
                get_request_token(callback);
                trys = 0;
            } else {
                e.printStackTrace();
            }
        }

    }

    public String get_authorization_url() throws SmartFileException {
        if (request == null)
            throw new SmartFileException("You must obtain a request token to request " +
                    "and access token. Use get_request_token()" +
                    "first.",0);
        return url + "/oauth/authorize/?oauth_token=" + request.getSecret();
    }

    public void get_access_token(String verifier) throws SmartFileException{
        try{
            provider.setOAuth10a(true);
            provider.retrieveAccessToken(consumer, verifier);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            trys++;
            if (trys < 10) {
                get_access_token(verifier);
                trys = 0;
            } else {
                throw new SmartFileException("Verifier is incorrect. Try again.", 0);
            }
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
        }
    }

    protected Object do_request(String method, String url, Map<String,String> args, File[] files, String strArgs, OAuthConsumer consumer) throws IOException, SmartFileException {
        return super.do_request(method,url,args,files,strArgs,this.consumer);
    }
}
