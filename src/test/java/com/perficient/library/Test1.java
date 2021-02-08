package com.perficient.library;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

public class Test1 {
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

        //创建一个时间戳,防止验证码图片文件重名
        String timestamp = System.currentTimeMillis()+"";

        //寻找验证码容器
//        WebElement ele = driver.findElement(By.id("验证码容器ID"));

            byte[] screenshotByte = null;
        screenshotByte = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);// 得到截图

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(screenshotByte));
        ImageIO.write(originalImage, "png", new File("D:/ocr_pic/1_fullByte.png"));


        //创建一个快照
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

        //读取截图
        BufferedImage fullImg = ImageIO.read(screenshot);

        //获取页面上元素的位置
        org.openqa.selenium.Point point= ele.getLocation();

        //获取元素宽高
        int eleWidth= ele.getSize().getWidth();
        int eleHeight= ele.getSize().getHeight();

        //裁剪整个页面截图只得到元素截图
        BufferedImage eleScreenshot= originalImage.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
        ImageIO.write(eleScreenshot, "png", screenshot);

        //将验证码截图保存到本地
        File screenshotLocation = new File("E:/"+timestamp+".jpg");
        FileUtils.copyFile(screenshot, screenshotLocation);

        driver.close();

    }

}
