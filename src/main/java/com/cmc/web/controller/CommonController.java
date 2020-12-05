package com.cmc.web.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.http.HttpUtil;
import com.cmc.web.beans.EBook;
import com.cmc.web.beans.EBookChapter;
import com.cmc.web.beans.EBookImage;
import com.cmc.web.common.AjaxResult;
import com.cmc.web.util.EBookGenerator;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {
    private static final String DOMAN_SHOUMANHUA = "http://www.shoumanhua.com";

    @GetMapping("grab")
    public AjaxResult<String> grabEbook(@RequestParam("url") String url, @RequestParam(name = "startStr", required = true) String startStr, @RequestParam(name = "endStr", required = true) String endStr) {
        AjaxResult<String> successResult = new AjaxResult<>();
        successResult.setData("老婆,睡觉没啊");
        grabManga(url, startStr, endStr);
        return successResult;
    }

    private void grabManga(String url, String startStr, String endStr) {
        String html = HttpUtil.get(url);
        Document doc = Jsoup.parse(html);
        Element titleElement = doc.selectFirst("h1");
        String title = PinyinUtil.getPinyin(titleElement.text());
        Element creatorElement = doc.selectFirst("#intro_l > div:nth-child(4) > span:nth-child(1)");
        String creator = creatorElement.text();
        EBook eBook = EBook.builder().creator(creator).title(title).identifier(UUID.fastUUID().toString()).date(LocalDate.now().toString()).build();
        Element coverPicElement = doc.selectFirst(".cy_info_cover");
        Element imgPicElement = coverPicElement.selectFirst(".pic");
        String coverImgUrl = imgPicElement.attr("src");
        List<EBookImage> coverImages = new LinkedList<>();
        EBookImage coverImg = EBookImage.builder().downloadUrl(coverImgUrl).build();
        coverImages.add(coverImg);
        EBookChapter cover = EBookChapter.builder().images(coverImages).title(title + "_cover").build();
        eBook.setCover(cover);
        List<EBookChapter> chapters = new LinkedList<>();
        Element element = doc.selectFirst("#mh-chapter-list-ol-0");
        List<Node> chapterNodes = element.childNodes();
        boolean touchStart = false;
        for (int i = 0; i < chapterNodes.size(); i++) {
            Node chapterNode = chapterNodes.get(i);
            if (chapterNode.outerHtml().contains("<li>")) {
                Node chapterInfo = chapterNode.childNode(0);
                String chapterUrl = DOMAN_SHOUMANHUA + chapterInfo.attr("href");
                StringBuilder outHtml = new StringBuilder(chapterInfo.childNode(0).childNode(0).outerHtml());
                outHtml.setLength(14);
                String chapterName = outHtml.toString().trim();
                if (chapterName.equals(startStr) || touchStart) {
                    touchStart = true;
                    String firstPage = HttpUtil.get(chapterUrl);
                    Document firstPageDoc = Jsoup.parse(firstPage);
                    Elements scripts = firstPageDoc.select("script");
                    String base64;
                    EBookChapter chapter = EBookChapter.builder().title(PinyinUtil.getPinyin(chapterName)).build();
                    List<EBookImage> images = new LinkedList<>();
                    script:
                    for (int j = 0; j < scripts.size(); j++) {
                        String scriptStr = scripts.get(j).toString();
                        if (scriptStr.contains("qTcms_Cur=")) {
                            String[] base64Pre = scriptStr.split("qTcms_S_m_murl_e=\"");
                            if (base64Pre.length == 2) {
                                String[] base64Suf = base64Pre[1].split("\";");
                                if (base64Suf.length >= 2) {
                                    base64 = base64Suf[0];
                                    String mangaUrlsStr = Base64.decodeStr(base64);
                                    String[] mangaUrls = mangaUrlsStr.split("[$]qingtiandy[$]");
                                    for (int k = 0; k < mangaUrls.length; k++) {
                                        images.add(EBookImage.builder().downloadUrl(mangaUrls[k]).build());
                                    }
                                    break script;
                                }
                            }
                        }
                    }
                    chapter.setImages(images);
                    chapters.add(chapter);
                    System.out.println("-------done------" + chapterName);
                    if (chapterName.equals(endStr)) {
                        break;
                    }
                }
                continue;
            }
        }
        eBook.setChapters(chapters);
        System.out.println("--------all-----done------");

        EBookGenerator.generateEBook(eBook);
    }

}
