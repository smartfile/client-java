package smartfileapi;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BasicClient extends Client{

    private String key;
    private String pwd;

    private void save_credentials(String key, String pwd) throws SmartFileException {
        try {
            this.key = clean_token(key);
            this.pwd = clean_token(pwd);
        } catch (Exception e) {
            throw new SmartFileException("Please provide an API key and password. Use arguments or environment variables.", 0);
        }
    }

    private void save_from_file(String file) throws SmartFileException {
        String key = null;
        String pwd = null;
        try {
            File rcfile = new File(file);
            BufferedReader br = new BufferedReader(new FileReader(rcfile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                for (int i = 0; i < words.length; i++) {
                    if (words[i].equals("login")) {
                        key = words[i+1];
                    } else if (words[i].equals("password")) {
                        pwd = words[i+1];
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        save_credentials(key,pwd);
    }

    public BasicClient(String key, String pwd) throws SmartFileException {
        super();
        save_credentials(key,pwd);
    }

    public BasicClient(String netrcfile) throws SmartFileException {
        super();
        save_from_file(netrcfile);

    }

    public BasicClient() throws SmartFileException {
        super();
        if (System.getenv("SMARTFILE_API_KEY") != null && System.getenv("SMARTFILE_API_PASS") != null) {
         save_credentials(System.getenv("SMARTFILE_API_KEY"),System.getenv("SMARTFILE_API_PASS"));
        } else {
            save_from_file(System.getProperty("user.home") + "/.netrc");
        }

    }

    private String clean_token(String token) throws SmartFileException {
        if (token.length() < 30) {
            throw new SmartFileException("Too short",0);
        }
        return token;

    }

    protected Object do_request(String method, String url, Map<String,String> args, File[] files, String strArgs, OAuthConsumer consumer) throws IOException, SmartFileException {
        if (args == null)
            args = new HashMap<String, String>();
        args.put("key",this.key);
        args.put("pwd",this.pwd);
        return super.do_request(method,url,args,files,strArgs,consumer);
    }
}
