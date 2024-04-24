package com.cm.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;

/**
 * description : 通信模块websocket路由转发规则
 *
 * @author kunlunrepo
 * date :  2024-04-24 14:39
 */
@Slf4j
@Component
public class WsRouteGlobalFilter implements GlobalFilter, Ordered {

    /**
     * websocket协议
     */
    private static final String WS_PROT = "ws://";

    /**
     * cm路由前缀
     */
    private static final String CM_PREFIX = "/ws";

    /**
     * cm路由标识
     */
    private static final String CM_ROUTE_ID = "cm-server";

    /**
     * cm默认路径
     */
    private static final String CM_DEFAULT_PATH = "ws://cm-server";

    /**
     * 自定义配置和管理路由规则
     */
    @Bean
    public RouteLocator cmWebsocketRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
        routes.route(CM_ROUTE_ID, // 路由标识 (无实质意义)
                r -> r.path(CM_PREFIX) // 截获路径
                        .uri(CM_DEFAULT_PATH)) // 转发至默认路径 (为了触发filter)
                .build();
        return routes.build();
    }

    /**
     * 过滤规则
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求
        ServerHttpRequest request = exchange.getRequest();
        // 获取请求路径
        String path = request.getPath().toString();
        // 原路由
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        // 判断是否是websocket请求
        if (path.startsWith(CM_PREFIX)) {
            // TODO 根据规则获取服务端地址

            // 转发路径
            String[] IPS = {
                    "127.0.0.1:9010",
                    "127.0.0.1:9011",
                    "127.0.0.1:9012"
            };
            Random random = new Random();
            int index = random.nextInt(IPS.length);
            String forwardPath = WS_PROT + IPS[index] + path;
            // 处理请求参数
            handleParams(forwardPath, request);
            URI forwardUri = URI.create(forwardPath);
            log.info("websocket转发路径:{}", forwardUri);
            // 转发请求
            ServerHttpRequest forwardRequest = request.mutate().uri(forwardUri).build();

            Route newRoute = Route.async()
                    .asyncPredicate(route.getPredicate())
                    .filters(route.getFilters())
                    .id(route.getId())
                    .order(route.getOrder())
                    .uri(forwardUri)
                    .build();
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, newRoute);
            // 继续执行
            return chain.filter(exchange.mutate().request(forwardRequest).build());
        }
        // 继续执行
        return chain.filter(exchange);
    }

    /**
     * 加载顺序
     */
    @Override
    public int getOrder() {
        return 1;
    }

    /**
     * 向path拼接参数
     */
    private String handleParams(String path, ServerHttpRequest request) {
        // 获取请求头中的参数
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        // 使用&拼接参数
        StringJoiner stringJoiner = new StringJoiner("&");
        // 遍历请求头中参数
        Set<Map.Entry<String, List<String>>> entries = queryParams.entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            stringJoiner.add(key + "=" + value.get(0));
        }
        try {
            String encode = URLEncoder.encode(stringJoiner.toString(), "UTF-8");
            return path + "?" + encode;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
