package com.cmc.web.util;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Map;

public class FreemarkerUtil {


    @SneakyThrows
    public static void generateFromTemplate(String templateName, Map dataMap, String outputFileName) {
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDirectoryForTemplateLoading(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "templates"));
        configuration.setDefaultEncoding("UTF-8");
        Template template = configuration.getTemplate(templateName);
        File file = FileUtil.touch(outputFileName);
        @Cleanup
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        template.process(dataMap, out);
    }
}
