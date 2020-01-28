package com.ab.advanced;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

import static java.net.http.WebSocket.*;

/**
 * @author Arpit Bhardwaj
 */
public class WebSocket {
    private static final int msgCount = 5;
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch receiveLatch = new CountDownLatch(msgCount);

        CompletableFuture<java.net.http.WebSocket> webSocketCompletableFuture = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .buildAsync(URI.create("ws://echo.websocket.org"), new EchoListener(receiveLatch));

        webSocketCompletableFuture.thenAccept(webSocket -> {
            webSocket.request(msgCount);
            for (int i = 0; i < msgCount; i++) {
                webSocket.sendText("Message " + i, true);
            }
        });

        receiveLatch.await();
    }

    private static class EchoListener implements Listener {
        CountDownLatch receiveLatch;
        public EchoListener(CountDownLatch receiveLatch) {
            this.receiveLatch = receiveLatch;
        }

        @Override
        public void onOpen(java.net.http.WebSocket webSocket) {
            System.out.println("Web Socket Opened");
        }

        @Override
        public CompletionStage<?> onText(java.net.http.WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("onText " + data);
            receiveLatch.countDown();
            return null;
        }
    }
}
