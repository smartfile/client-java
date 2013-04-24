package com.smartfile.api;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


/**
 * Implements Smartfile API client using OAuth1 authentication.
 * Provide credentials within constructor or environment variables.
 * <p>
 * Example of usage:<br />
 *
 * <code>
 * Scanner scan = new Scanner(System.in);<br />
 * OAuthClient api = new OAuthClient("***********","*************");<br />
 * api.get_request_token();<br />
 * System.out.println(api.get_authorization_url());<br />
 * api.get_access_token(scan.nextLine());<br />
 * </code>
 * </p>
 * Now your client is ready.
 * @see Client
 * Client for details of API call.
 */
public class OAuthClient extends Client {
    private OAuthToken client;

    private int trys;

    OAuthConsumer consumer;
    OAuthProvider provider;

    /**
     * Creates OAuth client using credentials stored at environment variables
     * SMARTFILE_CLIENT_TOKEN and SMARTFILE_CLIENT_SECRET
     */
    public OAuthClient() {
        super();
        this.client = new OAuthToken(getEnvVariable("SMARTFILE_CLIENT_TOKEN"),getEnvVariable("SMARTFILE_CLIENT_SECRET"));
    }

    /**
     * Creates OAuth client
     * @param client_token Client Token
     * @param client_secret Client Secret
     * @throws SmartFileException
     */
    public OAuthClient(String client_token, String client_secret) throws SmartFileException {
        super();
        this.client = new OAuthToken(client_token,client_secret);
    }

    /**
     * Retrieves request token
     * @throws SmartFileException
     */
    public void get_request_token() throws SmartFileException{
        this.get_request_token(OAuth.OUT_OF_BAND);
    }

    /**
     * Retrieves request token to a specific callback
     * @param callback Callback url
     * @throws SmartFileException
     */
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

    /**
     * Returns an URL where Verifier must be obtained
     * @return URL where Verifier must be obtained
     * @throws SmartFileException
     */
    public String get_authorization_url() throws SmartFileException {
        if (consumer.getToken() == null)
            throw new SmartFileException("You must obtain a request token to request " +
                    "and access token. Use get_request_token()" +
                    "first.",0);
        return url + "/oauth/authorize/?oauth_token=" + consumer.getToken();
    }

    /**
     * Retrieves access token
     * @param verifier Verifier obtained at authorization page or to callback
     * @throws SmartFileException
     */
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
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
        }
    }

    protected InputStream do_request(String method, String url, Map<String,String> args, File[] files, String strArgs, OAuthConsumer consumer) throws IOException, SmartFileException {
        return super.do_request(method,url,args,files,strArgs,this.consumer);
    }
}

