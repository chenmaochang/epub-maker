package com.cmc.web.controller;

import com.cmc.web.common.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {

    @GetMapping("grab")
    public AjaxResult<String> grabEbook(){
        AjaxResult<String> successResult=new AjaxResult<>();
        successResult.setData("老婆,睡觉没啊");
        return successResult;
    }

}
