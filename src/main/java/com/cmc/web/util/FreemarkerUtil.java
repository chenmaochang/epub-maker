package com.cmc.web.util;

import cn.hutool.core.io.FileUtil;
import com.cmc.web.config.EBookConfig;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.Map;

@Component
public class FreemarkerUtil {
    @Resource
    private EBookConfig eBookConfig;

    private static FreemarkerUtil eBookGenerator;

    @PostConstruct
    public void init() {
        eBookGenerator = this;
        eBookGenerator.eBookConfig = this.eBookConfig;
    }

    private FreemarkerUtil() {
    }

    @SneakyThrows
    public static void generateFromTemplate(String templateName, Map dataMap, String outputFileName) {
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDirectoryForTemplateLoading(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX+"templates"));
        configuration.setDefaultEncoding("UTF-8");
        Template template = configuration.getTemplate(templateName);
        File file = FileUtil.touch(eBookGenerator.eBookConfig.getPath() + File.separator + outputFileName);
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        template.process(dataMap, out);
    }
}
