package com.cmc.web.util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RestTemplateUtil {


    public static void main(String[] args) {
        try (final WebClient webClient = new WebClient()) {
            webClient.setJavaScriptTimeout(10000);
            HtmlPage page = webClient.getPage("https://www.wenshushu.cn/signin");
            TimeUnit.SECONDS.sleep(3);
            List<DomElement> changeMode = page.getByXPath("//*[@id=\"page_content\"]/div[1]/div[1]/ul/li[2]");
            DomElement changeModeButton = changeMode.get(0);
            HtmlPage loginPage = changeModeButton.click();
            TimeUnit.SECONDS.sleep(1);
            DomElement accountElement = loginPage.getFirstByXPath("//*[@id=\"page_content\"]/div[1]/div[2]/div[1]/div/div[1]/div[2]/input");
            accountElement.focus();
            accountElement.click();
            TimeUnit.SECONDS.sleep(1);
            loginPage.pressAccessKey('1');
            loginPage.pressAccessKey('5');
            loginPage.pressAccessKey('5');
            loginPage.pressAccessKey('2');
            loginPage.pressAccessKey('1');
            loginPage.pressAccessKey('1');
            loginPage.pressAccessKey('3');
            loginPage.pressAccessKey('5');
            loginPage.pressAccessKey('8');
            loginPage.pressAccessKey('1');
            loginPage.pressAccessKey('8');
            DomElement passwordElement = loginPage.getFirstByXPath("//*[@id=\"page_content\"]/div[1]/div[2]/div[1]/div/div[2]/div[2]/input");
            accountElement.focus();
            passwordElement.click();
            TimeUnit.SECONDS.sleep(1);
            loginPage.pressAccessKey('C');
            loginPage.pressAccessKey('H');
            loginPage.pressAccessKey('E');
            loginPage.pressAccessKey('N');
            loginPage.pressAccessKey('M');
            loginPage.pressAccessKey('A');
            loginPage.pressAccessKey('O');
            loginPage.pressAccessKey('C');
            loginPage.pressAccessKey('H');
            loginPage.pressAccessKey('A');
            loginPage.pressAccessKey('N');
            loginPage.pressAccessKey('G');
            DomElement loginButtonElement = loginPage.getFirstByXPath("//*[@id=\"page_content\"]/div[1]/div[2]/div[1]/div/button");
            HtmlPage redirectedPage=loginButtonElement.click();
            TimeUnit.SECONDS.sleep(3);
            System.out.println(redirectedPage.getTextContent());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
