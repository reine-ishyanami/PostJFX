package com.reine.postjfx;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author reine
 */
public class HttpTests {

    @Test
    void testGet() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8888/image/list"))
                .GET()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept((res) -> {
                    if (res.statusCode() == 200) System.out.println(res.body());
                })
                .join();
    }
}
