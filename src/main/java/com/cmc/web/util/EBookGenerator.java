package com.cmc.web.util;

import com.cmc.web.beans.EBookFolder;
import com.cmc.web.config.EBookConfig;
import freemarker.template.Version;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class EBookGenerator {
    @Autowired
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
        generateBookMineType();
    }

    private static void generateBookMineType() {
    }

    private static EBookFolder generateBookFolder(String path, String name, String author) {
        EBookFolder folder = new EBookFolder(path + "/", name + "-" + author);
        return folder;
    }
}
