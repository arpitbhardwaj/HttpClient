package com.ab.linkvalidator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Arpit Bhardwaj
 */
public class LinkValidatorSync {
    private static HttpClient httpClient;
    public static void main(String[] args) throws IOException {
        httpClient = HttpClient.newHttpClient();
        Files.lines(Path.of("urls.txt"))
                .map(LinkValidatorSync::validateLink)
                .forEach(System.out::println);

    }

    private static String validateLink(String link) {
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(link)).GET().build();

        try {
            HttpResponse<Void> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.discarding());
            return responseToString(httpResponse);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return String.format("%s -> %s",link,false);

        }
    }

    private static String responseToString(HttpResponse<Void> httpResponse) {
        int status = httpResponse.statusCode();
        boolean success = status >= 200 && status <= 299;
        return String.format("%s -> %s (status: %s)",httpResponse.uri(),success,status);
    }

}
