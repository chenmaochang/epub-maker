package com.cmc.web.controller;

import com.cmc.web.beans.EBook;
import com.cmc.web.common.AjaxResult;
import com.cmc.web.config.EBookConfig;
import com.cmc.web.util.EBookGenerator;
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
        EBookGenerator.generateEBook(EBook.builder().title("陈茂昌之书").creator("陈茂昌").build());
        successResult.setData("老婆,睡觉没啊");
        return successResult;
    }

}
