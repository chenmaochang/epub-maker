package com.cmc.web.util;

import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;

@Component
public class RestTemplateUtil {
    @Resource
    private RestTemplate restTemplate;
    private static RestTemplateUtil restTemplateUtil;

    private RestTemplateUtil() {
    }

    @PostConstruct
    public void init() {
        restTemplateUtil.restTemplate = this.restTemplate;
    }

    @SneakyThrows
    public static <T> T getRequest(String url, ParameterizedTypeReference<T> reference) {
        HttpEntity<String> requestEntity = new HttpEntity<>(null, buildHeaders());
        ResponseEntity<T> result = restTemplateUtil.restTemplate.exchange(new URI(url), HttpMethod.GET, requestEntity, reference);
        return result.getBody();
    }

    @SneakyThrows
    public static <T> T postRequest(String url, MultiValueMap<String, String> body, ParameterizedTypeReference<T> reference) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, buildHeaders());
        ResponseEntity<T> result = restTemplateUtil.restTemplate.exchange(new URI(url), HttpMethod.POST, requestEntity, reference);
        return result.getBody();
    }

    private static HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
