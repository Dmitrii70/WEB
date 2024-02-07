package netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService POOL = Executors.newFixedThreadPool(64);
    private final Map<String, Map<String, Handler>> HANDLERS = new ConcurrentHashMap<>();

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try (final var socket = serverSocket.accept()) {
                    var connectionHandler = new ConnectionHandler(socket, HANDLERS);
                    POOL.execute(connectionHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        Map<String, Handler> map = new ConcurrentHashMap<>();
        if (HANDLERS.containsKey(method)) {
            map = HANDLERS.get(method);
        }
        map.put(path, handler);
        HANDLERS.put(method, map);
    }
}

