package netology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    private static final List<String> validPaths = List.of("/index.html",
            "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js",
            "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public static void main(String[] args) {
        var server = new Server();

        for (String validPath : validPaths) {
            server.addHandler("GET", validPath, ((request, responseStream) -> {
                try {
                    final Path filePath = Path.of(".", "public", request.getRequestLine().getPath());
                    byte[] content = Files.readAllBytes(filePath);
                    ResponseUtils.responseOK(filePath, content, responseStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }
        server.addHandler("GET", "/classic.html",
                (request, responseStream) -> {
                    try {
                        final Path filePath = Path.of(".", "public");
                        String fileContent = Files.readString(filePath);
                        byte[] content = fileContent
                                .replace("{time}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME))
                                .getBytes();
                        ResponseUtils.responseOK(filePath, content, responseStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        server.listen(9999);
    }
}





