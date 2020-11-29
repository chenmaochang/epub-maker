package com.cmc.web.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;

public class RestTemplateUtil {


    public static void main(String[] args) {
//        String fileUrl = "http://img001.wlbaike.com/upload2/7633/2020/10-09/20201009022936924731_small.jpg";
//
//        long size = HttpUtil.downloadFile(fileUrl, FileUtil.touch("/new-book/img.jpg"));
//        System.out.println("Download size: " + size);
        String html=HttpUtil.get("http://www.shoumanhua.com/tuili/7633/553191.html?p=1");
        System.out.println();
    }
}
