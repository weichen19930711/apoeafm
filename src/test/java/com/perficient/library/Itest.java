package com.perficient.library;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Itest {
    public static void main(String[] args) {

       System.setProperty("webdriver.chrome.driver", "D:\\chrome\\chromedriver.exe");


        WebDriver driver = new ChromeDriver();
        driver.get("https://www.baidu.com/");

        if(check(driver,By.xpath("//*[@id=\"s-top-left\"]"))){
            System.out.printf("存在");
        }else{
            System.out.printf("不存在");
        }


        String title = driver.getTitle();
        System.out.printf(title);

        driver.close();
    }

    //判断元素是否存在方法
    public static Boolean check(WebDriver driver,By seletor) {
        try {
            driver.findElement(seletor);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }
}
