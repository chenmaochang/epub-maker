package com.cmc.web.util;

import freemarker.template.Configuration;
import freemarker.template.Version;

import java.util.Map;

public class FreemarkerUtil {
    public static void generateFromTemplate(String templateName, Map dataMap,String outputFileName){
        Configuration configuration = new Configuration(new Version("2.3.30"));
        configuration.setDefaultEncoding("UTF-8");
    }
}
