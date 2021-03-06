package com.cmc.web.util;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Map;

public class FreemarkerUtil {


    @SneakyThrows
    public static void generateFromTemplate(String templateName, Map dataMap, String outputFileName) {
        if (FileUtil.exist(outputFileName)) {
            return;
        }
        Configuration configuration = new Configuration(new Version(2, 3, 30));
        configuration.setClassForTemplateLoading(FreemarkerUtil.class, "/templates");
        configuration.setTemplateLoader(new ClassTemplateLoader(FreemarkerUtil.class, "/templates"));
        configuration.setDefaultEncoding("UTF-8");
        Template template = configuration.getTemplate(templateName);
        File file = FileUtil.touch(outputFileName);
        @Cleanup
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        template.process(dataMap, out);
    }
}
