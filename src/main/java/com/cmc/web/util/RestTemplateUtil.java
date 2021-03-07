package com.cmc.web.util;

import com.cmc.web.dto.response.bilnn.BilnnResponse;
import com.cmc.web.dto.response.bilnn.SearchResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RestTemplateUtil {
    @Resource
    private RestTemplate restTemplate;
    private static RestTemplateUtil restTemplateUtil;

    private RestTemplateUtil() {
    }

    @PostConstruct
    public void init() {
        restTemplateUtil=this;
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
        List<String> cookies = reulst.getHeaders().get("Set-Cookie");
        RedisUtil.set("bilingCookie:" + account, cookies.stream().collect(Collectors.joining(";")));
        //List<String> cookies2 = JSON.parseObject(RedisUtil.get("bilingCookie").toString(), new TypeReference<List<String>>() {});
    }

    @SneakyThrows
    public static SearchResult searchBilnn(String account, String keyword) {
        HttpHeaders headers = new HttpHeaders();
        String cookieStr = (String) RedisUtil.get("bilingCookie:" + account);
        headers.add("Cookie", cookieStr);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<BilnnResponse<SearchResult>> result = restTemplateUtil.restTemplate.exchange(new URI("https://pan.bilnn.com/api/v3/file/search/keywords%2F" + keyword), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<BilnnResponse<SearchResult>>() {
        });
        if(result.getBody().getCode()==0){
            return result.getBody().getData();
        }
        log.error("查询返回失败,{}",result);
        return null;
    }

    public static void uploadBilnn(String account, String filePath) {
        File file = new File(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Cookie", (String) RedisUtil.get("bilingCookie:" + account));
        headers.add("Referer", "https://pan.bilnn.com/");
        headers.add("Content-Length", String.valueOf(file.length()));
        headers.add("x-path", "%2F");
        headers.add("x-filename", file.getName());
        HttpEntity<FileSystemResource> requestEntity = new HttpEntity<>(new FileSystemResource(file), headers);
        ResponseEntity<String> result = restTemplateUtil.restTemplate.exchange("https://pan.bilnn.com/api/v3/file/upload?chunk=0&chunks=1", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<String>() {
        });
        System.out.println(result);
    }

    @SneakyThrows
    public static void getDownloadLink(String account) {
        HttpHeaders headers = new HttpHeaders();
        String cookieStr = (String) RedisUtil.get("bilingCookie:" + account);
        headers.add("Cookie", cookieStr);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> reulst = restTemplateUtil.restTemplate.exchange(new URI("https://pan.bilnn.com/api/v3/file/download/YqQDdcv"), HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<String>() {
        });
        System.out.println(reulst.getBody());
    }
}
