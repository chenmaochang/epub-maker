package com.cmc.web.controller;

import cn.hutool.http.HttpStatus;
import com.cmc.web.beans.EBook;
import com.cmc.web.common.AjaxResult;
import com.cmc.web.config.CloudStorageConfig;
import com.cmc.web.config.EBookConfig;
import com.cmc.web.service.EBookGenerator;
import com.cmc.web.service.ShouManHuaService;
import com.cmc.web.util.HtmlUnitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {
    @Resource
    ShouManHuaService shouManHuaService;
    @Resource
    EBookGenerator eBookGenerator;
    @Resource
    EBookConfig eBookConfig;
    @Resource
    CloudStorageConfig cloudStorageConfig;
    @Resource
    private RestTemplate restTemplate;

    @GetMapping("test")
    public String test() {
        //upload
        /*HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Cookie", "token=bFI_Ll713vX1AkwbAM6Qpzb2CctTfImHGxx8TUMqZSovm6fx14N2zSR6fg2BmF9_;cloudreve-session=MTYwOTIzMTMzNHxOd3dBTkVsSlF6TktUVE5LVUZwRFUwTXlTVFJDUVZGTFIweE5XalExVlZVMlNqUlNURnBYTlZVeVJUZFFXbGswU1RSSU5FMHlWRkU9fJ-E4jn-O7BHLuge0CyfYj072U9Z_u3vlilHfuXoLCh8");
        headers.add("Referer","https://pan.bilnn.com/");
        headers.add("Content-Length","3376680");
        headers.add("x-path","%2F");
        headers.add("x-filename","newxtgs.exe");
        HttpEntity<FileSystemResource> requestEntity = new HttpEntity<>(new FileSystemResource("D:/123.cab"), headers);
        ResponseEntity<String> reulst=restTemplate.exchange("https://pan.bilnn.com/api/v3/file/upload?chunk=0&chunks=1", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<String>() {
        });*/

        //
        /*HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<String> requestEntity = new HttpEntity<>("{\"userName\":\"chenmaochang@qq.com\",\"Password\":\"chenmaochang\",\"captchaCode\":\"\"}", headers);
        ResponseEntity<String> reulst=restTemplate.exchange("https://pan.bilnn.com/api/v3/user/session", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<String>() {
        });
        System.out.println(reulst.getHeaders());
        List<String> cookies=reulst.getHeaders().get("Set-Cookie");*/

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> reulst=restTemplate.exchange("https://pan.bilnn.com/api/v3/user/session", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<String>() {
        });
        System.out.println(reulst.getHeaders());
        List<String> cookies=reulst.getHeaders().get("Set-Cookie");


        return "123";
    }

    @GetMapping("index")
    public ModelAndView indexHtml() {
        return new ModelAndView("index");
    }

    @GetMapping("grab")
    public AjaxResult<String> grabEbook(@RequestParam("url") String url, @RequestParam(name = "startStr", required = true) String startStr, @RequestParam(name = "endStr", required = true) String endStr) {
        AjaxResult<String> successResult = new AjaxResult<>();
        successResult.setData("已经开始啦,迟些去看看进度吧");
        EBook eBook = shouManHuaService.grabManga(url, startStr, endStr);
        eBookGenerator.generateEBook(eBook);
        return successResult;
    }

    @GetMapping("check")
    public AjaxResult<List<String>> checkGrabProgress(@RequestParam("url") String url) {
        AjaxResult<List<String>> successResult = new AjaxResult<>();
        successResult.setData(shouManHuaService.checkEBook(url));
        successResult.setCode(HttpStatus.HTTP_OK);
        successResult.setMsg("查找完成,请加上[http://域名/common/file/download?fileName=文件名]进行访问");
        return successResult;
    }

    @GetMapping("file/download")
    public ResponseEntity<FileSystemResource> getFile(@RequestParam String fileName) throws FileNotFoundException {
        File file = new File(eBookConfig.getPath() + File.separator + fileName);
        if (file.exists()) {
            return shouManHuaService.export(file);
        }
        return null;
    }


}
