package com.ab.configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;

/**
 * @author Arpit Bhardwaj
 */
public class Security {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {

        SSLParameters parameters = new SSLParameters(
                new String[]{ "TLSv1.2"},
                new String[]{ "TLS_AES_128_GCM_SHA256"}
        );
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(SSLContext.getDefault())
                .sslParameters(parameters)
                .proxy(
                        ProxySelector.of(new InetSocketAddress("proxyserver.com",8080))
                        //ProxySelector.getDefault()
                )
                .authenticator(new Authenticator(){
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("username","password".toCharArray());
                        //return super.getPasswordAuthentication();
                    }
                })
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://www.google.com")).build();
        httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());

    }
}
