package com.cmc.web.util;

import com.cmc.web.beans.EBookFolder;
import com.cmc.web.config.EBookConfig;
import freemarker.template.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class EBookGenerator {
    @Resource
    private EBookConfig eBookConfig;

    private static EBookGenerator eBookGenerator;

    @PostConstruct
    public void init() {
        eBookGenerator = this;
        eBookGenerator.eBookConfig = this.eBookConfig;
    }

    private EBookGenerator() {
    }

    public static void generateEBook(String name, String author) {
        EBookFolder folder = generateBookFolder(eBookGenerator.eBookConfig.getPath(), name, author);
        generateBookMineType(name + "-" + author);
    }

    private static void generateBookMineType(String bookName) {
        FreemarkerUtil.generateFromTemplate("mimetype.ftl", null, bookName + "/mimetype");
    }

    private static EBookFolder generateBookFolder(String path, String name, String author) {
        EBookFolder folder = new EBookFolder(path + "/", name + "-" + author);
        return folder;
    }
}
