package com.microservice.ApiGateway.configuration;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route( "authentication-service",route->route
                        .path("/auth/**")
                        .filters(f -> f.rewritePath("/auth/(?<segment>.*)",
                                "/api/auth/${segment}"))
                        .uri("lb://AUTHENTICATION-SERVICE"))
                .route( "user-service",route->route
                        .path("/user/**")
                        .filters(f -> f.rewritePath("/user/(?<segment>.*)",
                                "/api/user/${segment}"))
                        .uri("lb://USER-SERVICE"))
                .build();
    }

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters, RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        for (RouteDefinition definition : definitions) {
            System.out.println("id: " + definition.getId()+ "  "+definition.getUri().toString());
        }
        definitions.forEach(routeDefinition -> {
            System.out.println(routeDefinition.getId());
            String name = routeDefinition.getId().replaceAll("-SERVICE", "");
            swaggerUiConfigParameters.addGroup(name);
            GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(name).build();
        });
        return groups;
    }

}
