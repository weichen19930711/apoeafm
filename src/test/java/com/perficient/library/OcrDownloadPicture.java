package com.perficient.library;

import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;



public class OcrDownloadPicture {
    public static final String loginUrl="https://sfrz.shaanxi.gov.cn/#/login";


    public static void main(String[] args) throws Exception {

        System.setProperty("webdriver.chrome.driver", "D:\\chrome\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(loginUrl);
        driver.manage().window().maximize();
        WebElement imgInput = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[2]/form/div[4]/div[1]/div/div/div[1]/input"));
        WebElement ele = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[2]/form/div[4]/div[2]/img"));
        ele.click();
        Thread.sleep(2000);
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = ImageIO.read(screenshot);  // 读取截图

        ImageIO.write(fullImg, "jpg", new File("D:/ocr_pic/1_full.jpg"));
        // 得到页面元素
        org.openqa.selenium.Point point = imgInput.getLocation();
        // 得到长、宽
        int eleWidth = ele.getSize().getWidth();
        int eleHeight = ele.getSize().getHeight();

        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX()+100, point.getY()+150, eleWidth+100, eleHeight+100);
        ImageIO.write(eleScreenshot, "png", screenshot);
        // copy 把图片放对应的生成目录下
        File screenshotLocation = new File("D:/ocr_pic/1_1.jpg");
        FileUtils.copyFile(screenshot, screenshotLocation);
        driver.close();
    }
    }