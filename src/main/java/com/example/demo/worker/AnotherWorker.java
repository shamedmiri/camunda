package com.example.demo.worker;

import com.example.demo.config.ApiUrlsProperties;
import com.example.demo.service.AnotherApiService;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.Map;

@Component
public class AnotherWorker {
    private final ApiUrlsProperties properties;
    private final AnotherApiService apiService;

    public AnotherWorker(ApiUrlsProperties properties, AnotherApiService apiService) {
        this.properties = properties;
        this.apiService = apiService;
    }

    @PostConstruct
    public void subscribe() {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl(properties.getCamunda())
                .asyncResponseTimeout(20000)
                .build();

        client.subscribe("anotherTaskTopic") // تاپیک جدید در BPMN
                .lockDuration(30000)
                .handler((externalTask, externalTaskService) -> {
                    String userName = externalTask.getVariable("lastName");
                    String email = externalTask.getVariable("email");
                    String password = "Password";
                    try {
                        Map<String, Object> variables = apiService.callUserApi(userName, email, password);
                        int statusCode = (int) variables.get("statusCode");

                        if (statusCode == 200 || statusCode == 201) {
                            externalTaskService.complete(externalTask, variables);
                        } else {
                            externalTaskService.handleBpmnError(externalTask, "ERR_END", "خطای سرویس", variables);

                        }
                    } catch (Exception e) {
                        externalTaskService.handleFailure(externalTask, "API error", e.getMessage(), 0, 0);
                    }
                })
                .open();
    }
}
