package com.cmc.web.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import com.cmc.web.beans.EBook;
import com.cmc.web.beans.EBookChapter;
import com.cmc.web.beans.EBookFolder;
import com.cmc.web.beans.EBookImage;
import com.cmc.web.config.EBookConfig;
import com.cmc.web.repository.EBookChapterRepository;
import com.cmc.web.repository.EBookFolderRepository;
import com.cmc.web.repository.EBookImageRepository;
import com.cmc.web.repository.EBookRepository;
import com.cmc.web.util.FreemarkerUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Component
@Slf4j
public class EBookGenerator {
    @Resource
    private EBookConfig eBookConfig;
    @Resource
    EBookFolderRepository eBookFolderRepository;
    @Resource
    EBookRepository eBookRepository;
    @Resource
    EBookImageRepository eBookImageRepository;
    @Resource
    EBookChapterRepository eBookChapterRepository;

    @SneakyThrows
    @Async
    public void generateEBook(EBook eBook) {
        EBookFolder folder = generateBookFolder(eBook.getChineseName(), eBook.getCreator());
        generateBookMineType(folder.calculateEbookFullPath());
        generateMetaInf(folder.calculateEbookFullPath());
        generateOEBPS(eBook, folder.calculateEbookFullPath());
        zipEpub(folder);
        persistent2DB(folder, eBook);
    }

    @SneakyThrows
    private void persistent2DB(EBookFolder folder, EBook eBook) {
        folder.setEBook(eBook);
        eBookFolderRepository.save(eBookFolderRepository.findOne(Example.of(folder, ExampleMatcher.matching().withIgnorePaths("ebook"))).orElseGet(() -> folder));
    }

    @Deprecated
    private void deleteOldBook(Example bookExample) {
        List<EBook> allBooksMatch = eBookRepository.findAll(bookExample);
        if (allBooksMatch.size() > 0) {
            EBook book2Delete = allBooksMatch.get(0);
            book2Delete.getChapters().parallelStream().forEach(chapter -> {
                eBookImageRepository.deleteAll(chapter.getImages());
            });
            eBookImageRepository.deleteAll(book2Delete.getCover().getImages());
            eBookChapterRepository.delete(book2Delete.getCover());
            eBookChapterRepository.deleteAll(book2Delete.getChapters());
            eBookRepository.delete(book2Delete);
        }
    }

    private void zipEpub(EBookFolder folder) {
        String epubFile = folder.calculateEbookFullPath() + ".epub";
        if (!FileUtil.exist(epubFile)) {
            ZipUtil.zip(folder.calculateEbookFullPath() + "/", epubFile, false);
        }
    }

    private void generateOEBPS(EBook eBook, String fullPath) {
        String oebpsDir = fullPath + File.separator + "OEBPS";
        String imagesDir = oebpsDir + File.separator + "Images";
        String textDir = oebpsDir + File.separator + "Text";
        makeOebpsDir(oebpsDir, imagesDir, textDir);
        generateBookCoverPage(eBook, textDir, imagesDir);
        generateChapterHtml(eBook, textDir, imagesDir);
        generateBookIndexPage(eBook, textDir);
        generateOpf(eBook, oebpsDir);
        generateNcx(eBook, oebpsDir);
    }

    private void generateBookIndexPage(EBook eBook, String oebpsDir) {
        Map chapterDataMap = new HashMap();
        chapterDataMap.put("chapters", eBook.getChapters());
        FreemarkerUtil.generateFromTemplate(eBookConfig.getTemplateBookIndex(), chapterDataMap, oebpsDir + File.separator + File.separator + "bookIndex.xhtml");
    }

    private void makeOebpsDir(String oebpsDir, String imagesDir, String textDir) {
        FileUtil.mkdir(oebpsDir);
        FileUtil.mkdir(imagesDir);
        FileUtil.mkdir(textDir);
    }

    private void generateBookCoverPage(EBook eBook, String textDir, String imageDir) {
        Map coverDateMap = new HashMap();
        downloadChapterImages(imageDir, eBook.getCover());
        eBook.getCover().setFullName(eBook.getCover().getImages().get(0).getFullName());
        coverDateMap.put("coverFullName", eBook.getCover().getFullName());
        coverDateMap.put("title", eBook.getTitle());
        FreemarkerUtil.generateFromTemplate(eBookConfig.getTemplateCoverPage(), coverDateMap, textDir + File.separator + "coverPage.xhtml");
    }

    private void generateChapterHtml(EBook eBook, String textDir, String imageDir) {
        downloadChaptersImages(imageDir, eBook.getChapters());
        List<EBookChapter> chapters = eBook.getChapters();
        for (int i = 0; i < chapters.size(); i++) {
            EBookChapter chapter = chapters.get(i);
            Map contentDataMap = new HashMap();
            contentDataMap.put("images", chapter.getImages());
            contentDataMap.put("title", chapter.getTitle());
            String fullName = "contentPage_" + i + ".xhtml";
            chapter.setFullName(fullName);
            FreemarkerUtil.generateFromTemplate(eBookConfig.getTemplateContentPage(), contentDataMap, textDir + File.separator + fullName);
        }
    }

    private void generateNcx(EBook eBook, String oebpsDir) {
        Map ncxDataMap = new HashMap();
        ncxDataMap.put("eBook", eBook);
        ncxDataMap.put("chapters", eBook.getChapters());
        FreemarkerUtil.generateFromTemplate(eBookConfig.getTemplateNcx(), ncxDataMap, oebpsDir + File.separator + "toc.ncx");
    }

    private void generateOpf(EBook ebook, String oebpsDir) {
        Map dataMap = new LinkedHashMap();
        dataMap.put("ebook", ebook);
        dataMap.put("htmlPages", ebook.getChapters());
        dataMap.put("images", collectImageFromChapters(ebook.getChapters()));
        dataMap.put("cover", ebook.getCover());
        FreemarkerUtil.generateFromTemplate(eBookConfig.getTemplateOpf(), dataMap, oebpsDir + File.separator + "content.opf");
    }

    private List<EBookImage> collectImageFromChapters(List<EBookChapter> chapters) {
        List<EBookImage> images = new LinkedList<>();
        for (int i = 0; i < chapters.size(); i++) {
            EBookChapter chapter = chapters.get(i);
            images.addAll(chapter.getImages());
        }
        return images;
    }

    private void generateMetaInf(String fullPath) {
        String metaInfDir = fullPath + File.separator + "META-INF";
        FileUtil.mkdir(metaInfDir);
        generateContainer(metaInfDir);
    }

    private void generateContainer(String metaInfDir) {
        FreemarkerUtil.generateFromTemplate(eBookConfig.getTemplateContainer(), null, metaInfDir + File.separator + "container.xml");
    }

    private void generateBookMineType(String mimetypeDir) {
        FreemarkerUtil.generateFromTemplate(eBookConfig.getTemplateMimetype(), null, mimetypeDir + File.separator + "/mimetype");
    }

    private EBookFolder generateBookFolder(String name, String author) {
        EBookFolder folder = new EBookFolder(null, eBookConfig.getPath() + "/", name + "-" + author, null);
        return folder;
    }

    private void downloadChaptersImages(String path, List<EBookChapter> chapters) {
        for (int i = 0; i < chapters.size(); i++) {
            EBookChapter chapter = chapters.get(i);
            downloadChapterImages(path, chapter);
        }
    }

    private void downloadChapterImages(String path, EBookChapter chapter) {
        List<EBookImage> images = chapter.getImages();
        for (int j = 0; j < images.size(); j++) {
            EBookImage image = images.get(j);
            String suffix = getFileSuffixFromUrl(image.getDownloadUrl());
            image.setFullName(chapter.getTitle() + "_" + j + suffix);
            image.setSuffix(suffix);
            try {
                String filePath = path + File.separator + image.getFullName();
                if (FileUtil.exist(filePath)) {
                    return;
                }
                HttpUtil.downloadFile(image.getDownloadUrl(), FileUtil.touch(filePath), 8000);
                Thumbnails.of(filePath).outputQuality(0.5F).keepAspectRatio(true).toFile(filePath);
            } catch (Exception e) {
                log.error("下载出错-{},{},{}", path, image.getDownloadUrl(), j, e);
            }
        }
    }


    private static String getFileSuffixFromUrl(String url) {
        return url.substring(url.lastIndexOf("."));
    }
}
