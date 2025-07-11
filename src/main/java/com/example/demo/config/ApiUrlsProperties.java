package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.urls")
public class ApiUrlsProperties {

    private String user;
    private String posts;
private String camunda;

    public String getCamunda() {
        return camunda;
    }

    public void setCamunda(String camunda) {
        this.camunda = camunda;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }
}
