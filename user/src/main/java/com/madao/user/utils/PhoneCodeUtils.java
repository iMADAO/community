package com.madao.user.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class PhoneCodeUtils {
    private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
    public static boolean sendMessage(String phone, String mobileCode){
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(Url);

        client.getParams().setContentCharset("GBK");
        method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK");

        //填写内容
        String content = new String("您的验证码是：" + mobileCode + "。请不要把验证码泄露给其他人。");

        NameValuePair[] data = {//提交短信
                new NameValuePair("account", "C45404859"), //查看用户名是登录用户中心->验证码短信->产品总览->APIID
                new NameValuePair("password", "7152279677a7b9b251d7c1d9fe3dc47d"),  //查看密码请登录用户中心->验证码短信->产品总览->APIKEY
                //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
                new NameValuePair("mobile", phone),
                new NameValuePair("content", content),
        };
        method.setRequestBody(data);

        try { //发送短信
            client.executeMethod(method);

            String SubmitResult =method.getResponseBodyAsString();

            //System.out.println(SubmitResult);

            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();

            String code = root.elementText("code"); //当返回值为2时表示提交成功
            String msg = root.elementText("msg");   //返回信息，提交成功后为提交成功
            String smsid = root.elementText("smsid"); //当提交成功后生成流水号

            System.out.println("短信code==="+code);
            System.out.println("短信msg==="+msg);
            System.out.println("短信smsid==="+smsid);

            if("2".equals(code)){
                System.out.println("短信提交成功");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
