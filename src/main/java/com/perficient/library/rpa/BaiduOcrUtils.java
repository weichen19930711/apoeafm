package com.perficient.library.rpa;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.aip.ocr.AipOcr;

public class BaiduOcrUtils {
    // 设置APPID/AK/SK
    public static final String APP_ID = "10099304";
    public static final String API_KEY = "hyf64QZ8cyTR0FXwIXEu7MNq";
    public static final String SECRET_KEY = "GikPbWlHuqPFUdzSGireFq5BS5sSbLEB";

//	public static final String APP_ID = "22757326";
//	public static final String API_KEY = "8EaQxrVwfMHDLfTAqIP160dK";
//	public static final String SECRET_KEY = "fDbFHxvULestN4L24qVIhj2I4raovndd";

    // 王井山
//  public static final String APP_ID = "23483126";
//  public static final String API_KEY = "c5HtdWz3uIQ0kn15EmAVu3qv";
//  public static final String SECRET_KEY = "q0lGlQS94XIwGQSbjeNQIY3qLOeUGm3E";

    // 何强
//  public static final String APP_ID = "23485379";
//  public static final String API_KEY = "jNjOHBVXLGVnIulawG4RrkBF";
//  public static final String SECRET_KEY = "07dyn3sx8r7HG8R68SCrDwzay62uEhTG";




    public static String getCaptchaCode(String imgPath){
        String captchaCode="";
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");
        // 调用接口
        JSONObject res = client.basicGeneral(imgPath, options);
        JSONArray resArray = res.getJSONArray("words_result");
        if(resArray.length()>0){
            String words=resArray.getJSONObject(0).getString("words");
            words=words.replaceAll("[^a-zA-Z0-9]","");
            captchaCode=words;
        }

/*        for (int i = 0; i < resArray.length(); i++) {
            JSONObject wordsJsonObj = resArray.getJSONObject(i);
            String words = wordsJsonObj.getString("words");
            if(words.indexOf("请输入图文验证码")!=-1){
                System.out.println("words："+words);
                captchaCode = words.substring(words.length()-4);
                String reg = "(?i)^(?!([a-z]*|\\d*)$)[a-z\\d]+$";
                if(!captchaCode.matches(reg)){
                    captchaCode="";
                };
                break;
            }
        }*/
        System.out.println("生成验证码："+captchaCode);
        return captchaCode;
    }

    public static void main(String[] args) {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        String path = "D:/ocr_pic/1_fullByte.png";
        JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        System.out.println(res.toString(2));

    }



}
