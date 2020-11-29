package com.cmc.web.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.cmc.web.beans.EBook;
import com.cmc.web.beans.EBookChapter;
import com.cmc.web.beans.EBookFolder;
import com.cmc.web.beans.EBookImage;
import com.cmc.web.config.EBookConfig;
import freemarker.template.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

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
        EBookFolder folder = generateBookFolder(name, author);
        generateBookMineType(folder.getEbookFullPath());
        generateMetaInf(folder.getEbookFullPath());
        generateOEBPS(folder.getEbookFullPath());
    }

    private static void generateOEBPS(String fullPath) {
        String oebpsDir = fullPath + File.separator + "OEBPS";
        FileUtil.mkdir(oebpsDir);
        FileUtil.mkdir(oebpsDir + File.separator + "Images");
        FileUtil.mkdir(oebpsDir + File.separator + "Text");

        Map coverDateMap=new HashMap();
        coverDateMap.put("coverFullName","cover00383.jpeg");
        coverDateMap.put("title","demo啊");
        FreemarkerUtil.generateFromTemplate("coverPage.ftl",coverDateMap,oebpsDir + File.separator + "Text" + File.separator + "coverPage.xhtml");


        Map chapterDataMap = new HashMap();
        List<EBookChapter> chapters=new LinkedList<>();
        chapters.add(EBookChapter.builder().title("title1").fullName("chapter1.html").build());
        chapters.add(EBookChapter.builder().title("title2").fullName("chapter2.html").build());
        chapters.add(EBookChapter.builder().fullName("chapter3.html").build());
        chapterDataMap.put("chapters",chapters);
        FreemarkerUtil.generateFromTemplate("bookIndex.ftl", chapterDataMap, oebpsDir + File.separator + "Text" + File.separator + "bookIndex.xhtml");

        EBook ebook = EBook.builder().creator("陈茂昌").date(LocalDate.now().toString()).identifier(UUID.fastUUID().toString()).title("众神的嘿嘿").build();
        generateOpf(oebpsDir, ebook);
        generateNcx(oebpsDir);
    }

    private static void generateNcx(String ncxDir) {
        FreemarkerUtil.generateFromTemplate("ncx.ftl", null, ncxDir + File.separator + "toc.ncx");
    }

    private static void generateOpf(String opfDir, EBook ebook) {
        Map dataMap = new LinkedHashMap();

        List<String> htmlPages = new LinkedList<>();
        htmlPages.add("page_001.xhtml");
        htmlPages.add("page_002.xhtml");
        htmlPages.add("page_003.xhtml");
        htmlPages.add("page_004.xhtml");

        List<EBookImage> images = new LinkedList<>();
        images.add(new EBookImage("demoImage001", ".jpeg"));
        images.add(new EBookImage("demoImage002", ".jpeg"));
        images.add(new EBookImage("demoImage003", ".jpeg"));
        images.add(new EBookImage("demoImage004", ".jpeg"));



        dataMap.put("ebook", ebook);
        dataMap.put("htmlPages", htmlPages);
        dataMap.put("images", images);
        dataMap.put("cover",EBookChapter.builder().fullName("coverPage.html").build());
        FreemarkerUtil.generateFromTemplate("opf.ftl", dataMap, opfDir + File.separator + "content.opf");


    }

    private static void generateMetaInf(String fullPath) {
        String metaInfDir = fullPath + File.separator + "META-INF";
        FileUtil.mkdir(metaInfDir);
        generateContainer(metaInfDir);
    }

    private static void generateContainer(String metaInfDir) {
        FreemarkerUtil.generateFromTemplate("container.ftl", null, metaInfDir + File.separator + "container.xml");
    }

    private static void generateBookMineType(String mimetypeDir) {
        FreemarkerUtil.generateFromTemplate("mimetype.ftl", null, mimetypeDir + File.separator + "/mimetype");
    }

    private static EBookFolder generateBookFolder(String name, String author) {
        EBookFolder folder = new EBookFolder(eBookGenerator.eBookConfig.getPath() + File.separator, name + "-" + author);
        return folder;
    }
}
