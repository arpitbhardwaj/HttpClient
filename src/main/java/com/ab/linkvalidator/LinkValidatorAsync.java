package com.ab.linkvalidator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Arpit Bhardwaj
 */
public class LinkValidatorAsync {
    private static HttpClient httpClient;
    public static void main(String[] args) throws IOException {
        httpClient = HttpClient.newHttpClient();
        /*Files.lines(Path.of("urls.txt"))
                .map(LinkValidatorAsync::validateLink)
                .forEach(System.out::println);*/

        List<CompletableFuture<String>> collectCompletableFutureList = Files.lines(Path.of("urls.txt"))
                .map(LinkValidatorAsync::validateLink)
                .collect(Collectors.toList());
        collectCompletableFutureList.stream()
                .map(CompletableFuture::join)
                .forEach(System.out::println);

    }

    private static CompletableFuture<String> validateLink(String link) {
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(link)).GET().build();

        /*try {
            HttpResponse<Void> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.discarding());
            return responseToString(httpResponse);
        } catch (IOException | InterruptedException e) {
            return String.format("%s -> %s",link,false);
        }*/
        CompletableFuture<HttpResponse<Void>> httpResponseCompletableFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.discarding());
        CompletableFuture<String> stringCompletableFuture = httpResponseCompletableFuture.thenApply(LinkValidatorAsync::responseToString)
                .exceptionally(throwable -> String.format("Exception Occurred : %s -> %s", link, false));
        return stringCompletableFuture;
    }

    private static String responseToString(HttpResponse<Void> httpResponse) {
        int status = httpResponse.statusCode();
        boolean success = status >= 200 && status <= 299;
        return String.format("%s -> %s (status: %s)",httpResponse.uri(),success,status);
    }

}
