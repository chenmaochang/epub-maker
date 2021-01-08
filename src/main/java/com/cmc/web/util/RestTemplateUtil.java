package com.cmc.web.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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


    /**
     * @param account  chenmaochang@qq.com
     * @param password chenmaochang
     */
    public static void loginBilnn(String account, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<String> requestEntity = new HttpEntity<>("{\"userName\":\"" + account + "\",\"Password\":\"" + password + "\",\"captchaCode\":\"\"}", headers);
        ResponseEntity<String> reulst = restTemplateUtil.restTemplate.exchange("https://pan.bilnn.com/api/v3/user/session", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<String>() {
        });
        System.out.println(reulst.getHeaders());
        List<String> cookies = reulst.getHeaders().get("Set-Cookie");
        RedisUtil.set("bilingCookie", JSON.toJSONString(cookies));
        //List<String> cookies2 = JSON.parseObject(RedisUtil.get("bilingCookie").toString(), new TypeReference<List<String>>() {});
    }

    @SneakyThrows
    public static void search(String account) {
        HttpHeaders headers = new HttpHeaders();
        List<String> cookies = JSON.parseObject(RedisUtil.get("bilingCookie").toString(), new TypeReference<List<String>>() {
        });
        String cookieStr = cookies.stream().collect(Collectors.joining(";"));
        headers.add("Cookie", cookieStr);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> reulst = restTemplateUtil.restTemplate.exchange(new URI("https://pan.bilnn.com/api/v3/file/search/keywords%2FMuMu"), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<String>() {
        });
        System.out.println(reulst.getBody());
    }
}
