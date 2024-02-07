package netology;

import java.nio.file.Path;

public class RequestLine {
    private final String METHOD;
    private final String PATH;
    private final String VERSION;

    public RequestLine(String METHOD, String PATH, String VERSION) {
        this.METHOD = METHOD;
        this.PATH = PATH;
        this.VERSION = VERSION;
    }

    public String getMethod() {
        return METHOD;
    }

    public String getPath() {
        return PATH;
    }

    public String getVersion() {
        return VERSION;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method='" + METHOD + '\'' +
                ", path='" + PATH + "\'" +
                ", version='" + VERSION + "\'" +
                '}';
    }
}
