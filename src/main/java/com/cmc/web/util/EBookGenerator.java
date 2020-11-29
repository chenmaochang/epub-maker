package com.cmc.web.util;

import cn.hutool.core.io.FileUtil;
import com.cmc.web.beans.EBook;
import com.cmc.web.beans.EBookChapter;
import com.cmc.web.beans.EBookFolder;
import com.cmc.web.beans.EBookImage;
import com.cmc.web.config.EBookConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
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

    public static void generateEBook(EBook eBook) {
        EBookFolder folder = generateBookFolder(eBook.getTitle(), eBook.getCreator());
        generateBookMineType(folder.getEbookFullPath());
        generateMetaInf(folder.getEbookFullPath());
        generateOEBPS(eBook, folder.getEbookFullPath());
    }

    private static void generateOEBPS(EBook eBook, String fullPath) {
        String oebpsDir = fullPath + File.separator + "OEBPS";
        String imagesDir = oebpsDir + File.separator + "Images";
        String textDir = oebpsDir + File.separator + "Text";
        makeOebpsDir(oebpsDir, imagesDir, textDir);
        generateBookCoverPage(eBook, textDir);
        generateBookIndexPage(eBook, oebpsDir);
        generateOpf(oebpsDir, eBook);
        generateNcx(oebpsDir, eBook);
        generateImages(imagesDir, eBook);
        generateChapterHtml(oebpsDir, eBook);
    }

    private static void generateBookIndexPage(EBook eBook, String oebpsDir) {
        Map chapterDataMap = new HashMap();
        chapterDataMap.put("chapters", eBook.getChapters());
        FreemarkerUtil.generateFromTemplate(eBookGenerator.eBookConfig.getTemplateBookIndex(), chapterDataMap, oebpsDir + File.separator + "Text" + File.separator + "bookIndex.xhtml");
    }

    private static void makeOebpsDir(String oebpsDir, String imagesDir, String textDir) {
        FileUtil.mkdir(oebpsDir);
        FileUtil.mkdir(imagesDir);
        FileUtil.mkdir(textDir);
    }

    private static void generateBookCoverPage(EBook eBook, String textDir) {
        Map coverDateMap = new HashMap();
        coverDateMap.put("coverFullName", eBook.getCover().getFullName());
        coverDateMap.put("title", eBook.getTitle());
        FreemarkerUtil.generateFromTemplate(eBookGenerator.eBookConfig.getTemplateCoverPage(), coverDateMap, textDir + File.separator + "coverPage.xhtml");
    }

    private static void generateChapterHtml(String textDir, EBook eBook) {
        List<EBookChapter> chapters = eBook.getChapters();
        for (int i = 0; i < chapters.size(); i++) {
            EBookChapter chapter = chapters.get(i);
            Map contentDataMap = new HashMap();
            contentDataMap.put("images", chapter.getImages());
            contentDataMap.put("title", chapter.getTitle());
            FreemarkerUtil.generateFromTemplate(eBookGenerator.eBookConfig.getTemplateContentPage(), contentDataMap, textDir + File.separator + ("contentPage_" + i + ".xhtml"));
        }
    }

    private static void generateImages(String imagesDir, EBook eBook) {
    }

    private static void generateNcx(String oebpsDir, EBook eBook) {
        Map ncxDataMap = new HashMap();
        ncxDataMap.put("eBook", eBook);
        ncxDataMap.put("chapters", eBook.getChapters());
        FreemarkerUtil.generateFromTemplate(eBookGenerator.eBookConfig.getTemplateNcx(), ncxDataMap, oebpsDir + File.separator + "toc.ncx");
    }

    private static void generateOpf(String oebpsDir, EBook ebook) {
        Map dataMap = new LinkedHashMap();
        dataMap.put("ebook", ebook);
        dataMap.put("htmlPages", ebook.getChapters());
        dataMap.put("images", collectImageFromChapters(ebook.getChapters()));
        dataMap.put("cover", ebook.getCover());
        FreemarkerUtil.generateFromTemplate(eBookGenerator.eBookConfig.getTemplateOpf(), dataMap, oebpsDir + File.separator + "content.opf");
    }

    private static List<EBookImage> collectImageFromChapters(List<EBookChapter> chapters) {
        List<EBookImage> images = new LinkedList<>();
        for (int i = 0; i < chapters.size(); i++) {
            EBookChapter chapter = chapters.get(i);
            images.addAll(chapter.getImages());
        }
        return images;
    }

    private static void generateMetaInf(String fullPath) {
        String metaInfDir = fullPath + File.separator + "META-INF";
        FileUtil.mkdir(metaInfDir);
        generateContainer(metaInfDir);
    }

    private static void generateContainer(String metaInfDir) {
        FreemarkerUtil.generateFromTemplate(eBookGenerator.eBookConfig.getTemplateContainer(), null, metaInfDir + File.separator + "container.xml");
    }

    private static void generateBookMineType(String mimetypeDir) {
        FreemarkerUtil.generateFromTemplate(eBookGenerator.eBookConfig.getTemplateMimetype(), null, mimetypeDir + File.separator + "/mimetype");
    }

    private static EBookFolder generateBookFolder(String name, String author) {
        return new EBookFolder(eBookGenerator.eBookConfig.getPath() + File.separator, name + "-" + author);
    }
}
