package smartfileapi;

import java.util.Map;

public class BasicClient extends Client{

    private String netrcfile;
    private String key;
    private String pwd;

    public BasicClient(String key, String pwd, Map<String,String> args) throws SmartFileException{

        netrcfile = args.get("netrcfile");
        args.remove("netrcfile");

        try {
            this.key = clean_token(key);
            this.pwd = clean_token(pwd);
        } catch (SmartFileException e) {
            throw new SmartFileException("Please provide an API key and password. Use arguments or environment variables.", 0);
        }


    }

    private String clean_token(String token) throws SmartFileException {

        if (token.length() < 30) {
            throw new SmartFileException("Too short",0);
        }
        return token;

    }
}
