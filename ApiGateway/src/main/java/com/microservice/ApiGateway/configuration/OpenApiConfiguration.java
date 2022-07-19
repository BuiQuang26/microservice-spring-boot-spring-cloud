package com.microservice.ApiGateway.configuration;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters, RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        for (RouteDefinition definition : definitions) {
            System.out.println("id: " + definition.getId()+ "  "+definition.getUri().toString());
        }
        definitions.forEach(routeDefinition -> {
            if(routeDefinition.getId().contains("-openapi")){
                System.out.println(routeDefinition.getId());
                String name = routeDefinition.getId();
                swaggerUiConfigParameters.addGroup(name);
                GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(name).build();
            }
        });
        return groups;
    }

}
