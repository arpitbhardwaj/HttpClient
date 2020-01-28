package com.ab.configuration;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Arpit Bhardwaj
 */
public class CookieDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        CookieManager cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        var httpClient = HttpClient.newBuilder().cookieHandler(cm).build();
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://www.google.com")).build();
        httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());

        System.out.println(cm.getCookieStore().getURIs());
        System.out.println(cm.getCookieStore().getCookies());
    }
}
