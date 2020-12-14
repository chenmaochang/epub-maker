package com.cmc.web.util;

import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RestTemplateUtil {


    @SneakyThrows
    public static void main(String[] args) {
        WebClient webClient = new WebClient();
        webClient.setJavaScriptTimeout(10000);
        HtmlPage page = webClient.getPage("https://www.wenshushu.cn/signin");
        TimeUnit.SECONDS.sleep(3);
        List<DomElement> changeMode = page.getByXPath("//*[@id=\"page_content\"]/div[1]/div[1]/ul/li[2]");
        DomElement changeModeButton = changeMode.get(0);
        HtmlPage loginPage = changeModeButton.click();
        TimeUnit.SECONDS.sleep(1);
        DomElement accountElement = loginPage.getFirstByXPath("//*[@id=\"page_content\"]/div[1]/div[2]/div[1]/div/div[1]/div[2]/input");
        accountElement.focus();
        HtmlPage accountPage = accountElement.click();
        TimeUnit.SECONDS.sleep(1);
        DomNodeList<DomElement> inputElement = accountPage.getElementsByTagName("input");
        HtmlInput accountInput = (HtmlInput) inputElement.get(0);
        accountInput.focus();
        accountInput.type("15521135818");
        TimeUnit.SECONDS.sleep(1);
        HtmlInput passwordInput = (HtmlInput) inputElement.get(1);
        passwordInput.focus();
        passwordInput.type("CHENMAOCHANG");
        DomElement submitElement = accountPage.getFirstByXPath("//*[@id=\"page_content\"]/div[1]/div[2]/div[1]/div/button/span");
        HtmlPage submitPage = submitElement.click();
        TimeUnit.SECONDS.sleep(1);
        ScriptResult result = submitPage.executeJavaScript("window.localStorage.getItem('login_token');");
        System.out.println("!!!!!!!!!!!!!!!!!!");
        System.out.println("script result: " + result.getJavaScriptResult().toString());
    }
}
