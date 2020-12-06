package com.cmc.web.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.http.*;
import com.cmc.web.beans.EBook;
import com.cmc.web.beans.EBookChapter;
import com.cmc.web.beans.EBookImage;
import com.cmc.web.config.EBookConfig;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class ShouManHuaService {
    @Resource
    EBookConfig eBookConfig;
    private static final String DONMAN_SHOUMANHUA = "http://www.shoumanhua.com";

    @SneakyThrows
    public ResponseEntity<FileSystemResource> export(File file) {
        if (file == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }

    public List<String> checkEBook(String url) {
        if(!url.contains(DONMAN_SHOUMANHUA)){
            return Collections.emptyList();
        }
        Document doc = Jsoup.parse(HttpUtil.get(url));
        String chineseName = doc.selectFirst("h1").text();
        return checkName(chineseName);
    }

    public List<String> checkName(String name) {
        if (!FileUtil.isDirectory(eBookConfig.getPath())) {
            return null;
        }
        File[] files = FileUtil.ls(eBookConfig.getPath());
        List<String> res=new ArrayList<>();
        for (File file : files) {
            if(file.getName().contains(name)&&file.getName().contains("epub")){
                res.add(file.getName());
            }
        }
        return res;
    }

    public EBook grabManga(String url, String startStr, String endStr) {
        Document doc = Jsoup.parse(HttpUtil.get(url));
        String chineseName = doc.selectFirst("h1").text() + "-" + startStr + "~" + endStr;
        String title = PinyinUtil.getPinyin(chineseName);
        String creator = doc.selectFirst("#intro_l > div:nth-child(4) > span:nth-child(1)").text();
        EBook eBook = EBook.builder().creator(creator).title(title).chineseName(chineseName).identifier(UUID.fastUUID().toString()).date(LocalDate.now().toString()).build();
        String coverImgUrl = doc.selectFirst(".cy_info_cover").selectFirst(".pic").attr("src");
        eBook.setCover(EBookChapter.builder().images(Arrays.asList(EBookImage.builder().downloadUrl(coverImgUrl).build())).title(title + "_cover").chineseName(chineseName + "封面").build());
        List<Node> chapterNodes = doc.selectFirst("#mh-chapter-list-ol-0").childNodes();
        eBook.setChapters(traverseChapters(startStr, endStr, chapterNodes));
        log.info("-------grab-all-----done------");
        return eBook;
    }

    private List<EBookChapter> traverseChapters(String startStr, String endStr, List<Node> chapterNodes) {
        boolean touchStart = false;
        List<EBookChapter> chapters = new LinkedList<>();
        for (int i = chapterNodes.size() - 1; i >= 0; i--) {
            Node chapterNode = chapterNodes.get(i);
            if (chapterNode instanceof TextNode) {
                continue;
            }
            if (chapterNode.outerHtml().contains("<li>")) {
                Node chapterInfo = chapterNode.childNode(0);
                String chapterUrl = DONMAN_SHOUMANHUA + chapterInfo.attr("href");
                StringBuilder outHtml = new StringBuilder(chapterInfo.childNode(0).childNode(0).outerHtml());
                outHtml.setLength(14);
                String chapterName = outHtml.toString().trim();
                if (chapterName.equals(startStr) || touchStart) {
                    touchStart = true;
                    String firstPage = HttpUtil.get(chapterUrl);
                    Document firstPageDoc = Jsoup.parse(firstPage);
                    Elements scripts = firstPageDoc.select("script");
                    String base64;
                    EBookChapter chapter = EBookChapter.builder().chineseName(chapterName).title(PinyinUtil.getPinyin(chapterName)).build();
                    List<EBookImage> images = new LinkedList<>();
                    script:
                    for (int j = 0; j < scripts.size(); j++) {
                        String scriptStr = scripts.get(j).toString();
                        if (scriptStr.contains("qTcms_Cur=")) {
                            String[] base64Pre = scriptStr.split("qTcms_S_m_murl_e=\"");
                            String[] base64Suf = base64Pre[1].split("\";");
                            base64 = base64Suf[0];
                            String mangaUrlsStr = Base64.decodeStr(base64);
                            String bookMid = base64Suf[11].replace("\r\nvar qTcms_Pic_Cur_m_id=\"", "");
                            String bookMif = base64Suf[12].replace("\r\nvar qTcms_Pic_m_if=\"", "");
                            String bookIndexUrl = base64Suf[15].replace("\r\nvar qTcms_m_indexurl=\"", "");
                            String[] mangaUrls = mangaUrlsStr.split("[$]qingtiandy[$]");
                            for (int k = 0; k < mangaUrls.length; k++) {
                                images.add(EBookImage.builder().downloadUrl(getTheRealPicUrl(mangaUrls[k], bookMid, bookMif, bookIndexUrl)).build());
                            }
                            break script;
                        }
                    }
                    chapter.setImages(images);
                    chapters.add(chapter);
                    log.info("-------{}~done------", chapterName);
                    if (chapterName.equals(endStr)) {
                        break;
                    }
                }
                continue;
            }
        }
        return chapters;
    }

    @SneakyThrows
    public String getTheRealPicUrl(String beforeUrl, String bookMid, String bookMif, String bookIndexUrl) {
        if (beforeUrl.substring(0, 1).equals("/")) {
            return "http://img001.xznj120.com/" + beforeUrl;
        }
        if (!bookMif.equals("2")) {
            beforeUrl = beforeUrl.replace("?", "a1a1");
            beforeUrl = beforeUrl.replace("&", "a1a1");
            beforeUrl = beforeUrl.replace("&", "a1a1");
            beforeUrl = bookIndexUrl + "statics/pic/?p=" + URLEncoder.encode(beforeUrl, "UTF-8") + "&picid=" + bookMid;
        }
        if (!beforeUrl.startsWith("http")) {
            beforeUrl = DONMAN_SHOUMANHUA + beforeUrl;
        }
        if (beforeUrl.length() > (beforeUrl.lastIndexOf(".") + 4)) {
            HttpResponse response = HttpRequest.get(beforeUrl).header(Header.REFERER, DONMAN_SHOUMANHUA).execute();
            if (HttpStatus.HTTP_MOVED_TEMP == response.getStatus()) {
                beforeUrl = response.header(Header.LOCATION);
            }
        }
        if (!beforeUrl.startsWith("http")) {
            beforeUrl = DONMAN_SHOUMANHUA + beforeUrl;
        }
        return beforeUrl;
    }


}
