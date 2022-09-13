package com.microservice.ApiGateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class Custom2Filter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String gateway = String.valueOf(exchange.getRequest().getHeaders().get("token"));

        System.out.println(gateway);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
