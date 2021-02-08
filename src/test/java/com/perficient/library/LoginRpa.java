package com.perficient.library;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import com.perficient.library.rpa.RandomUtil;
// import com.perficient.library.rpa.ScrollBarUtil;
import com.perficient.library.rpa.TtshituUtils;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
// import java.text.SimpleDateFormat;
// import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LoginRpa {

    public static final String loginUrl="http://1.85.55.147:7221/zcsb/?flag=false&logoutResult=true";
//    public static final String loginUrl="https://sfrz.shaanxi.gov.cn/#/login";
    public static final int loginType=1;//1代表个人 2代表单位
    public static final String imgTempPath="D:/ocr_pic/";

    public static void main(String[] args) {
        String loginAccount="410724198005197052";
        String loginPwd="zc123456";
        String recommendUnit="西安中洲电力设备有限公司";
        String authCode="s70";
        System.setProperty("webdriver.chrome.driver", "D:\\chrome\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        try {
            //全局设置
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //识别元素时的超时时间
            //设置等待时间为3秒，如果3秒页面没有全部加载出来，就会报错，如果小于30秒就全部加载出来了，剩下的时间将不再等待,继续下一步操作
            driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);//异步脚本的超时时间

            driver.get(loginUrl);
//            driver.manage().window().maximize();//最大化

            //切换到个人登录
            WebElement switchPersonalLogin = driver.findElement(By.xpath("//*[@id=\"loginId1\"]"));//tab切换
            switchPersonalLogin.click();

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
            loginKeyInput.sendKeys(loginAccount);
            loginPwdInput.sendKeys(loginPwd);
            //生成验证码
            String captchaCode="";
            int m=0;
            do {
                for (int i = 0; i < 10; i++) {
                    captchaCodeImg.click();
                    byte[] screenshotByte = null;
                    screenshotByte = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);// 得到截图
                    BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(screenshotByte));

                    int bufImageWidth=originalImage.getWidth();
                    int bufImageHeight=originalImage.getHeight();
                    int x=bufImageWidth/2+bufImageWidth/4+108;
                    int y=bufImageHeight/2+bufImageHeight/4/2+40;
                    int w=200;
                    int h=85;

                    BufferedImage captchaImage = originalImage.getSubimage(x, y, w,h);

                    String screenshotPath = imgTempPath + RandomUtil.getSerialNum() + ".png";
                    ImageIO.write(captchaImage, "png", new File(screenshotPath));
                    //百度OCR对验证码识别
                    captchaCode = TtshituUtils.getCaptchaCode(screenshotPath);
                    if (StringUtils.isNotBlank(captchaCode)&&captchaCode.length()==4) {
                        break;
                    }
                }
                if(StringUtils.isBlank(captchaCode)||captchaCode.length()!=4){
                    System.out.println("警告，验证码识别错误超过10次，程序退出");
                    return;
                }else {
                    captchaInput.sendKeys(captchaCode);
                    loginBtn.click();
                    Thread.sleep(1000);
                    m++;
                }
            }while (check(driver, By.className("ivu-message"))&&m<2);//因为3次有可能系统锁定，所以重试2次，如果登录不上，给与告警
            if(m>2){
                System.out.println("警告，登录次数超过2次，程序退出");
                return;
            }
            //登录成功，判断跳转到那个界面
            sleep(3000);
            driver.switchTo().frame("indexIframe");//切换到内容页面
            sleep(3000);
            //如果页面存在"推荐单位"和"本单位申报授权码",则跳转到"推荐单位选择"界面
            if(check(driver,By.xpath("//*[@id=\"b0100_name\"]")) && check(driver,By.xpath("//*[@id=\"sqm\"]"))){
                System.out.println("进入推荐单位选择页面...");
                // 获取当前窗口的句柄
                String parentWindowId = driver.getWindowHandle();
                System.out.println("driver.getTitle(): " + driver.getTitle());
                //选择推荐单位
                driver.findElement(By.xpath("//*[@id=\"b0100_name\"]")).click();
                sleep(3000);
                Set<String> allWindowsId = driver.getWindowHandles();
                // 获取所有的打开窗口的句柄
                for (String windowId : allWindowsId) {
                    if (driver.switchTo().window(windowId).getTitle().contains("推荐单位选择")) {
                        driver.switchTo().window(windowId);
                        break;
                    }
                }
                driver.switchTo().frame("fancybox-frame");//切换到推荐单位选择界面
                WebElement unitSearchInput = driver.findElement(By.xpath("//*[@id=\"b0105_id\"]"));
                unitSearchInput.sendKeys(recommendUnit);
                sleep(500);
                driver.findElement(By.xpath("//*[@id=\"formId\"]/table[1]/tbody/tr/td/input[1]")).click();//推荐单位查询
                sleep(3000);
                if(!check(driver,By.xpath("//*[@id=\"datagrid_tag_id\"]/tbody/tr[2]"))){//存在则代表查询有数据
                    System.out.println("推荐单位："+recommendUnit+" 不存在！");
                    return;
                }
                WebElement firstDatagridData = driver.findElement(By.xpath("//*[@id=\"datagrid_tag_id\"]/tbody/tr[2]"));
                Actions actions=new Actions(driver);
                actions.doubleClick(firstDatagridData).build().perform();  //对按钮进行双击
                sleep(3000);
                driver.findElement(By.xpath("//*[@id=\"dialogOkdialog0\"]")).click();

                sleep(2000);

                driver.switchTo().parentFrame().switchTo().frame("indexIframe");

                sleep(500);

                driver.findElement(By.xpath("//*[@id=\"sqm\"]")).sendKeys(authCode);

                sleep(1000);

                driver.findElement(By.xpath("//*[@id=\"btnRegist\"]")).click();

                sleep(3000);

            }
            //评审申报通知选择
            if(check(driver,By.xpath("//*[@id=\"queryinfo\"]"))){

                if(check(driver,By.xpath("//*[@id=\"dialogOkdialog0\"]"))){
                    driver.findElement(By.xpath("//*[@id=\"dialogOkdialog0\"]")).click();
                }

                //判断是工程类型的，还是其它类型的
                driver.findElement(By.xpath("//*[@id=\"queryinfo\"]/table[2]/tbody/tr[1]/td[2]/div[3]")).click();//工程类型单选选中
                sleep(500);
                driver.findElement(By.xpath("//*[@id=\"yh5003_id\"]")).clear();
                sleep(500);
                driver.findElement(By.xpath("//*[@id=\"yh5003_id\"]")).sendKeys("2020");//申报年度
                sleep(500);
                driver.findElement(By.xpath("//*[@id=\"yh6d01_id\"]")).sendKeys("中级");//评审申报通知名称
                sleep(500);
                driver.findElement(By.xpath("//*[@id=\"formId\"]/table[2]/tbody/tr/td/input[1]")).click();//查询
                sleep(3000);

                if(check(driver,By.xpath("//*[@id=\"dialogOkdialog0\"]"))){
                    driver.findElement(By.xpath("//*[@id=\"dialogOkdialog0\"]")).click();
                    System.out.println("没有查询到要申报的数据...");
                    return;
                }

                //获取查询结果
                WebElement declareLink=null;
                WebElement table= driver.findElement(By.xpath("//*[@id=\"datagrid_tag_id\"]"));
                List<WebElement> rows = table.findElements(By.tagName("tr"));
                for(WebElement row:rows){
                    List<WebElement> col = row.findElements(By.tagName("td"));
                    for(WebElement cell:col){
//                        System.out.print(cell.getText() + "\t");
                        String cellText=cell.getText();
                        if(cellText.contains("工商业联合会")){
                            List<WebElement> rowCellLinks=row.findElements(By.xpath("//*[@id=\"index__td\"]/a"));//”点击申报“链接
                            for(WebElement  cellLink:rowCellLinks){
                                    if(cellLink.getText().trim().equals("点击申报")){
                                        declareLink=cellLink;
                                        break;
                                    }
                            }
                            break;
                        }

                    }
//                    System.out.print("\n\t");
                }

                sleep(3000);

                if(declareLink!=null){
                    declareLink.click();//点击链接进行申报
                }

                sleep(3000);


//                driver.switchTo().parentFrame().switchTo().frame("indexIframe");


            }






//            ScrollBarUtil.horizontaltoRight(driver);//滚动条水平居右
//
//            driver.manage().window().maximize();//最大化

            sleep(5000);

            //进入职称申报界面




            //已经填报过的进行编辑
            if(check(driver,By.xpath("//*[@id=\"formId\"]/div/div/div[2]"))) {
                WebElement editBtn = driver.findElement(By.xpath("//*[@id=\"formId\"]/div/div/div[2]"));
                editBtn.click();
            }
            sleep(2000);


            driver.switchTo().frame("Left");//切换到内容页面



            //基本信息填报
            WebElement jbxxMenu = driver.findElement(By.id("menu_jbxx"));
            jbxxMenu.click();
            sleep(200);

            driver.switchTo().parentFrame().switchTo().frame("Right");//切换到内容页面;

            Select applySkillSelect = new Select(driver.findElement(By.id("yh5005")));//申请专业
            applySkillSelect.selectByVisibleText("水利");
            sleep(200);
            Select skillNameSelect = new Select(driver.findElement(By.id("yh5018")));//专业名称
            skillNameSelect.selectByVisibleText("水利水电工程");
            sleep(2000);
            WebElement jobYearsInput = driver.findElement(By.id("yh5019"));
            jobYearsInput.clear();
            sleep(2000);
            jobYearsInput.sendKeys("9");
            sleep(2000);

            String birthDateStr="1978-07-29";
            String birthDateJs =  "document.getElementById('a0111_id').removeAttribute('readOnly');document.getElementById('a0111_id').setAttribute('value','"
                    + birthDateStr + "');";//先获取input标签的id，然后remove掉readOnly标签，最后把日期输入。
            ((JavascriptExecutor) driver).executeScript(birthDateJs);//出生日期
            sleep(2000);

            String jobDate="2012.02";
            String jobDateJs =  "document.getElementById('a0141').removeAttribute('readOnly');document.getElementById('a0141').setAttribute('value','"
                    + jobDate + "');";//先获取input标签的id，然后remove掉readOnly标签，最后把日期输入。
            ((JavascriptExecutor) driver).executeScript(jobDateJs);//参加工作日期
            sleep(500);

            sleep(300000);

            //编码单位
//            String unitCode="195";
//            String unitName="长安大学";
//            ((JavascriptExecutor) driver).executeScript("document.getElementById(\"yh4300\").type=\"text\";");
//            sleep(2000);
//            driver.findElement(By.xpath("//*[@id=\"yh4300\"]")).sendKeys(unitCode);
//            sleep(200);
//            String unitNameJs =  "document.getElementById('yh4300_name').removeAttribute('readOnly');document.getElementById('yh4300_name').setAttribute('value','"
//                    + unitName + "');";//先获取input标签的id，然后remove掉readOnly标签，最后把日期输入。
//            ((JavascriptExecutor) driver).executeScript(unitNameJs);//编码单位

//            //现职称
//            String nowSkillPostCode="4631";
//            String nowSkillPostName="船长(中级)";
//            ((JavascriptExecutor) driver).executeScript("document.getElementById(\"yh5022\").type=\"text\";");
//            sleep(2000);
//            driver.findElement(By.xpath("//*[@id=\"yh5022\"]")).sendKeys(nowSkillPostCode);
//            String owSkillPostJs =  "document.getElementById('yh5022_name').removeAttribute('readOnly');document.getElementById('yh5022_name').setAttribute('value','"
//                    + nowSkillPostName + "');";//先获取input标签的id，然后remove掉readOnly标签，最后把日期输入。
//            ((JavascriptExecutor) driver).executeScript(owSkillPostJs);//现职称


            WebElement jbxxSaveBtn = driver.findElement(By.xpath("//*[@id=\"btnSave\"]"));
            jbxxSaveBtn.click();
            sleep(1000);

            WebElement dialogOk = driver.findElement(By.xpath("//*[@id=\"dialogOkdialog0\"]"));
            dialogOk.click();
            sleep(200);

            sleep(3000);

            //开始上传照片
            driver.switchTo().parentFrame().switchTo().frame("Left");//切换到内容页面;


            WebElement photoMenu = driver.findElement(By.xpath("//*[@id=\"menu_photo\"]/font"));
            photoMenu.click();
            sleep(500);

            //开始上传照片
            driver.switchTo().parentFrame().switchTo().frame("Right");//切换到内容页面;

            String filePath = "D:/a.jpg";
            driver.findElement(By.xpath("//*[@id=\"imgId\"]")).sendKeys(filePath);
            driver.findElement(By.xpath("//*[@id=\"btnSave\"]")).click();
            sleep(1000);

            dialogOk = driver.findElement(By.xpath("//*[@id=\"dialogOkdialog0\"]"));
            dialogOk.click();
            sleep(200);


//            driver.switchTo().parentFrame();
//            driver.switchTo().parentFrame();
//            driver.switchTo().parentFrame();
//            driver.switchTo().defaultContent();
//
//            WebElement exitBtn = driver.findElement(By.className("to_right font_1 font_exit"));
//            exitBtn.click();




            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            driver.close();
        }


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

    public static void scrollTo(WebElement element, WebDriver driver) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].scrollIntoView(false);", element);
    }



    public static void sleep(int x){
        try{
            Thread.sleep(x);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }



}