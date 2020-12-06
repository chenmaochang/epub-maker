package com.cmc.web.controller;

import com.cmc.web.beans.EBook;
import com.cmc.web.common.AjaxResult;
import com.cmc.web.service.ShouManHuaService;
import com.cmc.web.service.EBookGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {
    @Resource
    ShouManHuaService shouManHuaService;
    @Resource
    EBookGenerator eBookGenerator;

    @GetMapping("grab")
    public AjaxResult<String> grabEbook(@RequestParam("url") String url, @RequestParam(name = "startStr", required = true) String startStr, @RequestParam(name = "endStr", required = true) String endStr) {
        AjaxResult<String> successResult = new AjaxResult<>();
        successResult.setData("老婆,睡觉没啊");
        EBook eBook = shouManHuaService.grabManga(url, startStr, endStr);
        eBookGenerator.generateEBook(eBook);
        return successResult;
    }


}
