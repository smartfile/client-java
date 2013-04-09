package smartfileapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BasicClient extends Client{

    private String netrcfile;
    private String key;
    private String pwd;

    public BasicClient(String key, String pwd) throws SmartFileException{
        try {
            this.key = clean_token(key);
            this.pwd = clean_token(pwd);
        } catch (SmartFileException e) {
            throw new SmartFileException("Please provide an API key and password. Use arguments or environment variables.", 0);
        }


    }

    public BasicClient(Map<String,String> args) throws SmartFileException{
        netrcfile = args.get("netrcfile");
        args.remove("netrcfile");

    }

    private String clean_token(String token) throws SmartFileException {
        if (token.length() < 30) {
            throw new SmartFileException("Too short",0);
        }
        return token;

    }

    protected String do_request(String method, String url, Map<String,String> args) throws IOException {
        if (args == null)
            args = new HashMap<String, String>();
        args.put("key",this.key);
        args.put("pwd",this.pwd);
        return super.do_request(method,url,args);
    }
}
