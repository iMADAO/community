package com.madao.user;

import com.alibaba.fastjson.JSON;
import com.madao.api.entity.User;
import com.madao.api.utils.*;
import com.madao.user.bean.Authority;
import com.madao.user.bean.Role;
import com.madao.user.bean.UserExample;
import com.madao.user.mapper.AuthorityMapper;
import com.madao.user.mapper.RoleMapper;
import com.madao.user.mapper.UserMapper;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.commons.httpclient.HttpClient;


import java.security.Key;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApplicationTests {
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private AuthorityMapper authorityMapper;

	@Test
	public void contextLoads() {
		UserExample example = new UserExample();
		UserExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(1L);
		List<User> user = userMapper.selectByExample(example);
		System.out.println(user);
	}

	@Test
	public void testRedis(){
//		redisTemplate.opsForValue().set("abcd", "def", 1200L);
		stringRedisTemplate.opsForValue().set("abcd", "def", 1200, TimeUnit.SECONDS);
	}

	@Value("${code-send-interval}")
	private String interval;

	@Test
	public void test(){
//		String str = KeyUtil.genStringCode(100);
//		System.out.println(str);
//		System.out.println(interval);

		Long expire = redisTemplate.getExpire("abc");
		System.out.println(expire);
	}

	@Test
	public void test1(){
//		Long expire = redisTemplate.getExpire("abcd");
//		System.out.println(expire);
//		Long expire = stringRedisTemplate.getExpire("abcde");
//		System.out.println(expire);
//		System.out.println(3 * 60 * 60 * 24);
//		com.madao.api.entity.User user = (com.madao.api.entity.User) redisTemplate.opsForValue().get("user");
//		System.out.println(user);
		System.out.println(MD5Encoder.getEncryptedWithSalt("root123", "1"));
	}

	@Test
	public void test2(){
//		Role role = new Role();
//		role.setRoleId(KeyUtil.genUniquKeyOnLong());
//		role.setRoleName("管理员");
//		roleMapper.insertSelective(role);
//		Authority authority = new Authority();
//		authority.setAuthorityId(KeyUtil.genUniquKeyOnLong());
//		authority.setAuthorityName("admin-ban");
//		authorityMapper.insertSelective(authority);
//		List<Long> authorityIdList = authorityMapper.getAuthorityIdListByRoleId(1559117678814186859L);
//		authorityIdList.stream().forEach(System.out::println);
		List<String> authorityNameList = authorityMapper.getAuthorityListByRoleId(1559117678814186859L);
		authorityNameList.stream().forEach(System.out::println);
	}
	@Test
	public void testSend(){
		String smsCode= "1234";
		final String charset = "utf-8";
		String phone = "13420110105";
		String account = "N7911438";
		String pswd = "ChuangLan168";
		//请求地址请登录253云通讯自助通平台查看或者询问您的商务负责人获取
		String smsSingleRequestServerUrl = "http://smssh1.253.com/msg/send/json";
		String msg = "尊敬的用户，您好，您正在注册帐号,验证码为："+smsCode+"，若非本人操作请忽略此短信。";

		String report= "true";
		SmsSendRequest smsSingleRequest = new SmsSendRequest(account, pswd, msg, phone,report);
		String requestJson = JSON.toJSONString(smsSingleRequest);
		System.out.println("before request string is: " + requestJson);
		String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
		System.out.println("response after request result is :" + response);
		SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);
		System.out.println("response  toString is :" + smsSingleResponse);
	}


	@Test
	public void testSendCode2(){
		sendMessage("13420110105");
	}
	private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";


	public static int sendMessage(String phone){
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(Url);

		client.getParams().setContentCharset("GBK");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK");

		//生成的随机码
		int mobileCode = (int)((Math.random()*9+1)*100000);

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
				return mobileCode;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
