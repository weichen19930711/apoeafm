package com.perficient.library;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import com.perficient.library.rpa.BaiduOcrUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LoginRpaBak {

//    public static final String loginUrl="http://1.85.55.147:7221/zcsb/?flag=false&logoutResult=true";
    public static final String loginUrl="https://sfrz.shaanxi.gov.cn/#/login";
    public static final int loginType=1;//1代表个人 2代表单位
    public static final String imgTempPath="D:/ocr_pic/";

    public static void main(String[] args) throws Exception {

        System.setProperty("webdriver.chrome.driver", "D:\\chrome\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        //全局设置
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS); //识别元素时的超时时间
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);//页面加载时的超时时间
        driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);//异步脚本的超时时间

        driver.get(loginUrl);
//        driver.manage().window().maximize();//最大化

        //切换到个人登录
//        WebElement switchPersonalLogin = driver.findElement(By.xpath("//*[@id=\"loginId1\"]"));//tab切换
//        switchPersonalLogin.click();

        try {
            WebElement personalLoginElementTab = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[1]/div/div/div/div/div[2]"));//tab切换
            WebElement unitLoginElementTab = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[1]/div/div/div/div/div[3]"));//tab切换
            WebElement pwdLoginElementRadio = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[1]/label[1]/span[2]"));
            WebElement phoneLoginElementRadio = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[1]/label[2]/span[2]"));
            WebElement loginKeyInput = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[2]/form/div[1]/div/div[1]/input"));
            WebElement loginPwdInput = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[2]/form/div[3]/div/div[1]/input"));
            WebElement captchaCodeImg = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[2]/form/div[4]/div[2]/img"));

            WebElement captchaInput = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[2]/form/div[4]/div[1]/div/div/div/input"));
            WebElement loginBtn = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[1]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/div[2]/form/div[7]/div/button"));

            scrollTo(loginBtn, driver);

            if (loginType == 1) {
                personalLoginElementTab.click();
            } else {
                unitLoginElementTab.click();
            }
            pwdLoginElementRadio.click();//密码登录
            loginKeyInput.sendKeys("wh8309");
            loginPwdInput.sendKeys("wh8309spv8");
            //生成验证码
            String captchaCode="";


            int m=0;
            do {
                    int i=0;
                    do {
                        captchaCodeImg.click();
                        byte[] screenshotByte = null;
                        screenshotByte = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);// 得到截图
                        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(screenshotByte));
                        String screenshotPath = imgTempPath + UUID.randomUUID().toString().replace("-", "") + ".png";
                        ImageIO.write(originalImage, "png", new File(screenshotPath));
                        //百度OCR对验证码识别
                        captchaCode = BaiduOcrUtils.getCaptchaCode(screenshotPath);
                        i++;
                    } while (StringUtils.isBlank(captchaCode) && i <= 20);
                    if(i>20){
                        System.out.println("警告，验证码识别错误超过20次，程序退出");
                        driver.close();
                        System.exit(0);
                    }else {
                        captchaInput.sendKeys(captchaCode);
                        loginBtn.click();
                        Thread.sleep(1000);
                        m++;
                    }

            }while ( check(driver, By.xpath("/html/body/div[3]")) && m<=10);

            if(m>10){
                System.out.println("警告，登录次数超过10次，程序退出");
                driver.close();
                System.exit(0);
            }else{

            }
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            driver.close();
        }


    }

    public static void scrollTo(WebElement element, WebDriver driver) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].scrollIntoView(false);", element);
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