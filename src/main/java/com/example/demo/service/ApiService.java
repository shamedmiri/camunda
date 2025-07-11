package com.example.demo.service;

import com.example.demo.config.ApiUrlsProperties;
import com.example.demo.error.ErrorMessagesProperties;
import org.camunda.spin.Spin;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiService {
    @Autowired
    private ErrorMessagesProperties errorMessages;

    private final HttpClient httpClient;
    private final ApiUrlsProperties urls;

    public ApiService(HttpClient httpClient, ApiUrlsProperties urls) {
        this.httpClient = httpClient;
        this.urls = urls;
    }

    public Map<String, Object> callUserApi(int id) throws Exception {
        return callApi(urls.getUser(),id);
    }

    private Map<String, Object> callApi(String url, int id) throws Exception {
        Map<String, Object> result = new HashMap<>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+id))
                .header("accept", "application/json")
                .header("x-api-key","reqres-free-v1")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        result.put("statusCode", statusCode);

        if (statusCode == 200) {
            String responseBody = response.body();
          //  SpinJsonNode jsonNode = Spin.JSON(responseBody);
            result.put("Output", responseBody);

        } else {
            result.put("ErrorMessage",errorMessages.get("GENERAL_ERROR"));
        }

        return result;
    }
}
