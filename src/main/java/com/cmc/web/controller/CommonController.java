package com.cmc.web.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpUtil;
import com.cmc.web.beans.EBook;
import com.cmc.web.beans.EBookChapter;
import com.cmc.web.common.AjaxResult;
import com.cmc.web.config.EBookConfig;
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
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {
    private static final String DOMAN_SHOUMANHUA = "http://www.shoumanhua.com";

    @GetMapping("grab")
    public AjaxResult<String> grabEbook(@RequestParam("url") String url, @RequestParam(name = "startStr", required = true) String startStr, @RequestParam(name = "endStr", required = true) String endStr) {
        AjaxResult<String> successResult = new AjaxResult<>();
        //EBookGenerator.generateEBook(EBook.builder().title("陈茂昌之书").creator("陈茂昌").build());
        successResult.setData("老婆,睡觉没啊");
        grabManga(url, startStr, endStr);
        return successResult;
    }

    private void grabManga(String url, String startStr, String endStr) {
        EBook eBook=EBook.builder().creator("吾峠呼世晴").title("鬼灭之刃").identifier(UUID.fastUUID().toString()).date(LocalDate.now().toString()).build();
        String html = HttpUtil.get(url);
        Document doc = Jsoup.parse(html);
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
                String bookName = outHtml.toString();
                if (bookName.equals(startStr) || touchStart) {
                    touchStart = true;
                    String firstPage = HttpUtil.get(chapterUrl);
                    Document firstPageDoc = Jsoup.parse(firstPage);
                    Elements scripts = firstPageDoc.select("script");
                    String base64 = null;
                    script:
                    for (int j = 0; j < scripts.size(); j++) {
                        String scriptStr = scripts.get(j).toString();
                        if (scriptStr.contains("qTcms_Cur=")) {
                            String[] base64Pre = scriptStr.split("qTcms_S_m_murl_e=\"");
                            if (base64Pre.length == 2) {
                                String[] base64Suf = base64Pre[1].split("\";");
                                if (base64Suf.length >= 2) {
                                    base64 = base64Suf[0];
                                    System.out.println(base64);
                                    String mangaUrlsStr=Base64.decodeStr(base64);
                                    String[] mangaUrls=mangaUrlsStr.split("[$]qingtiandy[$]");
                                    for(int k=0;k<mangaUrls.length;k++){
                                        System.out.println(mangaUrls[k]);
                                    }
                                    break script;
                                }
                            }
                        }
                    }

                    if (outHtml.equals(endStr)) {
                        break;
                    }
                }
                continue;

            }
        }


    }

}
