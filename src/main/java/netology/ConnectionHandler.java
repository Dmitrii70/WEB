package netology;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConnectionHandler implements Runnable {
    private final Socket SOCKET;
    private final Map<String, Map<String, Handler>> HANDLERS;

    public ConnectionHandler(Socket SOCKET, Map<String, Map<String, Handler>> HANDLERS) {
        this.SOCKET = SOCKET;
        this.HANDLERS = HANDLERS;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    @Override
    public void run() {
        try (
                final var IN = new BufferedInputStream(SOCKET.getInputStream());
                final var OUT = new BufferedOutputStream(SOCKET.getOutputStream());
        ) {
            final int LIMIT = 4096;
            IN.mark(LIMIT);
            final byte[] BUFFER = new byte[LIMIT];
            final int READ = IN.read(BUFFER);
            final byte[] REQUEST_LINE_DELIMETER = new byte[]{
                    '\r', '\n'
            };
            final int REQUEST_LINE_END = indexOf(BUFFER, REQUEST_LINE_DELIMETER, 0, READ);
            if (REQUEST_LINE_END == -1) {
                ResponseUtils.badRequest(OUT);
                return;
            }
            final String[] PARTS = new String(Arrays.copyOf(BUFFER, REQUEST_LINE_END)).split(" ");
            if (PARTS.length != 3) {
                ResponseUtils.badRequest(OUT);
                return;
            }
            if (PARTS[1].startsWith("/")) {
                ResponseUtils.badRequest(OUT);
            }

            RequestLine requestLine = new RequestLine(PARTS[0], PARTS[1], PARTS[2]);
            final byte[] HEADER_DELIMETER = new byte[]{'\r', '\n', '\r', '\n',};
            final int HEADER_START = REQUEST_LINE_END + REQUEST_LINE_DELIMETER.length;
            final int HEADER_END = indexOf(BUFFER, HEADER_DELIMETER, HEADER_START, READ);
            if (HEADER_END == -1) {
                ResponseUtils.badRequest(OUT);
                return;
            }
            IN.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
