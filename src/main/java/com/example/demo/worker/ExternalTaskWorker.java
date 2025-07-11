package com.example.demo.worker;

import com.example.demo.config.ApiUrlsProperties;
import com.example.demo.service.ApiService;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.spin.Spin;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExternalTaskWorker {

    private final ApiService apiService;
    private final ApiUrlsProperties properties;

    public ExternalTaskWorker(ApiService apiService, ApiUrlsProperties properties) {
        this.apiService = apiService;
        this.properties = properties;
    }

    @PostConstruct
    public void subscribeToTopic() {

        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl(properties.getCamunda())
                .asyncResponseTimeout(20000)
                .build();

        client.subscribe("taskTopic")
                .lockDuration(30000)
                .handler(new ExternalTaskHandler() {
                    @Override
                    public void execute(ExternalTask task, ExternalTaskService service) {
                        Object value = task.getVariable("someVariable");
                        int id = Integer.parseInt(value.toString());
                        try {
                            Map<String, Object> vars = apiService.callUserApi(id);
                            int statusCode = (int) vars.get("statusCode");

                            if (statusCode != 200) {
                                service.handleBpmnError(task, "ERR_END", "خطای سرویس", vars);
                            } else {

                                SpinJsonNode jsonNode = Spin.JSON(vars.get("Output"));

                                Map<String, Object> result = new HashMap<>();
                                result.put("email", jsonNode.prop("data").prop("email").stringValue());
                                result.put("last_name", jsonNode.prop("data").prop("last_name").stringValue());

                                service.complete(task, result);

                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            try {
                                service.handleFailure(task, "API failed", ex.getMessage(), 0, 0);
                            } catch (Exception nested) {
                                System.err.println("Error reporting failure: " + nested.getMessage());
                            }
                        }
                    }
                })
                .open();

        System.out.println("External task worker started.");
    }
}
