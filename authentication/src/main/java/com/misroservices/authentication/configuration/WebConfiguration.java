package com.misroservices.authentication.configuration;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfiguration {

    @Autowired
    private EurekaClient eurekaClient;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                List<String> path = new ArrayList<>();
                Applications applications = eurekaClient.getApplications();
                applications.getRegisteredApplications().forEach(application -> {
                    application.getInstances().forEach(instanceInfo -> {
                        path.add(instanceInfo.getHomePageUrl());
                    });
                });

                System.out.println("Register allowedOrigins: ");
                String[] paths = path.toArray(String[]::new);
                for (String s : paths) {
                    System.out.println(s);
                }

                registry.addMapping("/**")
                        .allowedMethods("*")
                        .allowedOrigins(path.toArray(String[]::new));
            }
        };
    }

}
