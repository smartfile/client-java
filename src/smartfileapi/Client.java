package smartfileapi;

public class Client {

    private String url = "";
    private String version = "2.1";
    private boolean throttle_wait = true;

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
}
