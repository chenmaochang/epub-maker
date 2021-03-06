package com.cmc.web.util;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HtmlUnitUtil {
    private static final String TOKEN_KEY = "wenshushuToken:%s";

    @SneakyThrows
    public static void loginWenShuShu(String account, String password) {
        WebClient webClient = new WebClient();
        webClient.setJavaScriptTimeout(10000);
        HtmlPage page = webClient.getPage("https://www.wenshushu.cn/signin");
        webClient.waitForBackgroundJavaScript(3000);
        List<DomElement> changeMode = page.getByXPath("//*[@id=\"page_content\"]/div[1]/div[1]/ul/li[2]");
        DomElement changeModeButton = changeMode.get(0);
        HtmlPage loginPage = changeModeButton.click();
        webClient.waitForBackgroundJavaScript(1000);
        DomElement accountElement = loginPage.getFirstByXPath("//*[@id=\"page_content\"]/div[1]/div[2]/div[1]/div/div[1]/div[2]/input");
        accountElement.focus();
        HtmlPage accountPage = accountElement.click();
        webClient.waitForBackgroundJavaScript(1000);
        DomNodeList<DomElement> inputElement = accountPage.getElementsByTagName("input");
        HtmlInput accountInput = (HtmlInput) inputElement.get(0);
        accountInput.focus();
        accountInput.type(account);
        webClient.waitForBackgroundJavaScript(1000);
        HtmlInput passwordInput = (HtmlInput) inputElement.get(1);
        passwordInput.focus();
        passwordInput.type(password);
        DomElement submitElement = accountPage.getFirstByXPath("//*[@id=\"page_content\"]/div[1]/div[2]/div[1]/div/button/span");
        HtmlPage submitPage = submitElement.click();
        webClient.waitForBackgroundJavaScript(1000);
        ScriptResult tokenResult = submitPage.executeJavaScript("window.localStorage.getItem('login_token');");
        RedisUtil.set(String.format(TOKEN_KEY, account), tokenResult.getJavaScriptResult().toString());
    }

    @SneakyThrows
    public static void loginWithToken(String account) {
        WebClient webClient = new WebClient();
        webClient.setJavaScriptTimeout(10000);
        HtmlPage page = webClient.getPage("https://www.wenshushu.cn/");
        webClient.waitForBackgroundJavaScript(3000);
        page.executeJavaScript("window.localStorage.clear();window.localStorage.setItem('login_token','" + getWenShuShuTokenFromRedis(account) + "')");
        webClient.waitForBackgroundJavaScript(500);
        page.refresh();
        webClient.waitForBackgroundJavaScript(3000);
        HtmlPage drivePage = webClient.getPage("https://www.wenshushu.cn/drive/");
        webClient.waitForBackgroundJavaScript(3000);
        DomNodeList<DomElement> inputElement=drivePage.getElementsByTagName("input");
        if(inputElement.size()>0){
            HtmlInput searchInput = (HtmlInput)inputElement.get(0);
            searchInput.focus();
            webClient.waitForBackgroundJavaScript(500);
            searchInput.type("阿里云认证证书");
            webClient.waitForBackgroundJavaScript(500);
            DomElement searchButton=drivePage.getFirstByXPath("//*[@id=\"page_content\"]/div/div/div/div[2]/div[2]/div/div/div/div[1]/div/div[1]/div[2]/div[1]/div/span/span/i[2]");
            searchButton.click();
            webClient.waitForBackgroundJavaScript(2000);
            DomElement checkButton=drivePage.getFirstByXPath("//*[@id=\"page_content\"]/div/div/div/div[2]/div[2]/div/div/div/div[2]/div/div[1]/div/div/div[1]/div/div/div/div[1]/div/div[1]/span/i");
            checkButton.focus();
            webClient.waitForBackgroundJavaScript(200);
            checkButton.click();
            webClient.waitForBackgroundJavaScript(500);
            drivePage.getFirstByXPath("//*[@id=\"page_content\"]/div/div/div/div[2]/div[2]/div/div/div/div[1]/div/div[1]/div[1]/div/div/div[1]");
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        }
    }

    @SneakyThrows
    public static void uploadFileWithToken(String account) {
        WebClient webClient = new WebClient();
        webClient.setJavaScriptTimeout(10000);
        HtmlPage page = webClient.getPage("https://www.wenshushu.cn/");
        webClient.waitForBackgroundJavaScript(3000);
        page.executeJavaScript("window.localStorage.clear();window.localStorage.setItem('login_token','" + getWenShuShuTokenFromRedis(account) + "')");
        webClient.waitForBackgroundJavaScript(500);
        page.refresh();
        webClient.waitForBackgroundJavaScript(3000);
        HtmlPage drivePage = webClient.getPage("https://www.wenshushu.cn/drive/");
        webClient.waitForBackgroundJavaScript(3000);
        DomNodeList<DomElement> inputElement=drivePage.getElementsByTagName("input");
        if(inputElement.size()>0){
            HtmlInput searchInput = (HtmlInput)inputElement.get(0);
            searchInput.focus();
            webClient.waitForBackgroundJavaScript(500);
            searchInput.type("阿里云认证证书");
            webClient.waitForBackgroundJavaScript(500);
            DomElement searchButton=drivePage.getFirstByXPath("//*[@id=\"page_content\"]/div/div/div/div[2]/div[2]/div/div/div/div[1]/div/div[1]/div[2]/div[1]/div/span/span/i[2]");
            searchButton.click();
            webClient.waitForBackgroundJavaScript(2000);
            DomElement checkButton=drivePage.getFirstByXPath("//*[@id=\"page_content\"]/div/div/div/div[2]/div[2]/div/div/div/div[2]/div/div[1]/div/div/div[1]/div/div/div/div[1]/div/div[1]/span/i");
            checkButton.focus();
            webClient.waitForBackgroundJavaScript(200);
            checkButton.click();
            webClient.waitForBackgroundJavaScript(500);
            drivePage.getFirstByXPath("//*[@id=\"page_content\"]/div/div/div/div[2]/div[2]/div/div/div/div[1]/div/div[1]/div[1]/div/div/div[1]");
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        }
    }

    private static String getWenShuShuTokenFromRedis(String account) {
        return RedisUtil.get(String.format(TOKEN_KEY, account)).toString();
    }


}
