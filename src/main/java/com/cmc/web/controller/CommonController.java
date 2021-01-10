package com.cmc.web.controller;

import cn.hutool.http.HttpStatus;
import com.cmc.web.beans.EBook;
import com.cmc.web.common.AjaxResult;
import com.cmc.web.config.CloudStorageConfig;
import com.cmc.web.config.EBookConfig;
import com.cmc.web.service.EBookGenerator;
import com.cmc.web.service.ShouManHuaService;
import com.cmc.web.util.RestTemplateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
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

    @SneakyThrows
    @GetMapping("test")
    public String test() {
        RestTemplateUtil.loginBilnn("chenmaochang@qq.com","chenmaochang");
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
