package com.perficient.library.rpa;;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
public class TtshituUtils {
    public static String getCaptchaCode(String imgPath) throws IOException {
        //用户名
        String username= "wh8309";
        //密码
        String password= "wh8309spv8";
        //验证码类型(默认数英混合),1:纯数字, 2:纯英文，3:数英混合：可空
        String typeid="3";
        //备注字段: 可以不写
        String remark="输出计算结果";
        InputStream inputStream=null;
        //你需要识别的1:图片地址，2:也可以是一个文件
        //1:这是远程url的图片地址
        //String url = "https://ningge.oss-cn-shanghai.aliyuncs.com/recordImage/0000008bd2134152aa5fad036a802a89.jpg";
        //URL u = new URL(url);
        //inputStream=u.openStream();
        //2:这是本地文件
        File needRecoImage=new File(imgPath);
        inputStream=new FileInputStream(needRecoImage);

        Map< String, String> data = new HashMap<String, String>();
        data.put("username",username);
        data.put("password", password);
        data.put("typeid", typeid);
        data.put("remark", remark);

        String resultString = Jsoup.connect("http://api.ttshitu.com/create.json")
                .data(data).data("image","test.jpg",inputStream)
                .ignoreContentType(true)
                .post().text();
        JSONObject jsonObject = JSONObject.parseObject(resultString);
        if (jsonObject.getBoolean("success")) {
            String result=jsonObject.getJSONObject("data").getString("result");
            System.out.println("识别成功结果为:"+result);
            return result;
        }else {
            System.out.println("识别失败原因为:"+jsonObject.getString("message"));
        }
        return "";
    }
}
