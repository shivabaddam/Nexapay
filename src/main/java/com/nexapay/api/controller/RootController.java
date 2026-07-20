package com.nexapay.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Tag(name = "Discovery", description = "API root and endpoint index")
public class RootController {

    private static final String API_ROOT = "/api";

    // Resolved lazily: the handler mapping is only fully populated once every
    // controller has been registered, which happens after this bean is created.
    private final ObjectProvider<RequestMappingHandlerMapping> handlerMapping;

    public RootController(@Qualifier("requestMappingHandlerMapping")
                          ObjectProvider<RequestMappingHandlerMapping> handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @GetMapping("/")
    @Operation(summary = "Redirect to the API index")
    public ResponseEntity<Void> root() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(API_ROOT))
                .build();
    }

    @GetMapping(API_ROOT)
    @Operation(summary = "List the available API endpoints")
    public Map<String, Object> index() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "Nexapay Banking API");
        body.put("version", "1.0.0");
        body.put("documentation", Map.of(
                "swaggerUi", "/swagger-ui/index.html",
                "openApi", "/v3/api-docs"
        ));
        body.put("endpoints", endpoints());
        return body;
    }

    private List<Map<String, String>> endpoints() {
        List<Map<String, String>> endpoints = new ArrayList<>();

        handlerMapping.getObject().getHandlerMethods().forEach((info, handler) -> {
            for (String path : pathsOf(info)) {
                if (!path.equals(API_ROOT) && !path.startsWith(API_ROOT + "/")) {
                    continue;
                }
                for (String method : methodsOf(info)) {
                    endpoints.add(describe(method, path, summaryOf(handler)));
                }
            }
        });

        endpoints.sort(Comparator.comparing((Map<String, String> e) -> e.get("path"))
                .thenComparing(e -> e.get("method")));
        return endpoints;
    }

    private Set<String> pathsOf(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() != null) {
            return info.getPathPatternsCondition().getPatternValues();
        }
        if (info.getPatternsCondition() != null) {
            return info.getPatternsCondition().getPatterns();
        }
        return Set.of();
    }

    private Set<String> methodsOf(RequestMappingInfo info) {
        Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
        if (methods.isEmpty()) {
            return Set.of("GET");
        }
        Set<String> names = new LinkedHashSet<>();
        methods.forEach(method -> names.add(method.name()));
        return names;
    }

    private String summaryOf(HandlerMethod handler) {
        Operation operation = handler.getMethodAnnotation(Operation.class);
        return operation != null ? operation.summary() : "";
    }

    private Map<String, String> describe(String method, String path, String summary) {
        Map<String, String> endpoint = new LinkedHashMap<>();
        endpoint.put("method", method);
        endpoint.put("path", path);
        endpoint.put("summary", summary);
        return endpoint;
    }
}
