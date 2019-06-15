package com.madao.user.service;

import com.madao.api.Exception.ResultException;
import com.madao.api.dto.UserDTO;
import com.madao.api.entity.PostCategory;
import com.madao.api.entity.User;
import com.madao.api.enums.ErrorEnum;
import com.madao.api.enums.UserStateEnum;
import com.madao.api.form.UserLoginForm;
import com.madao.api.form.UserRegisterForm;
import com.madao.api.form.UserRegisterForm2;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.MD5Encoder;
import com.madao.api.utils.ResultUtil;
import com.madao.user.bean.Role;
import com.madao.user.bean.RoleExample;
import com.madao.user.bean.UserExample;
import com.madao.user.mapper.AuthorityMapper;
import com.madao.user.mapper.RoleMapper;
import com.madao.user.mapper.UserMapper;
import com.madao.user.utils.PhoneCodeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Value("${link}")
    private String validateLink;

    @Value("${code_effective_time}")
    private String effectiveTime;

    @Value("${code-send-interval}")
    private String interval;

    @Value("${redis-send-interval-prefix}")
    private String intervalPrefix;

    @Value("${redis-code-prefix}")
    private String codePrefix;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    private Long userDefaultRoleId = 1559117637452359280L;

    @Value("${userPrefix}")
    private String USER_PREFIX;

    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    //获取用户个人信息
    public User getUserInfoById(Long userId){
        User user =  userMapper.selectByPrimaryKey(userId);
        user.setPassword("");
        return user;
    }

    //用户登录，返回带有权限信息的用户对象
    public UserDTO loginValidate(UserLoginForm form){
        List<User> userList = null;
        //通过手机号或者邮箱
        int type = testUserAccountType(form.getAccount());
        if(type==-1){
            throw new ResultException(ErrorEnum.PASSWORDERROR);
        }

        UserExample userExample = null;
        UserExample.Criteria criteria = null;

        if(type==0){
            userExample = new UserExample();
            criteria = userExample.createCriteria();
            criteria.andPhoneEqualTo(form.getAccount());
        }else if(type==1){
            userExample = new UserExample();
            criteria = userExample.createCriteria();
            criteria.andEmailEqualTo(form.getAccount());
        }

        userList = userMapper.selectByExample(userExample);
        if(userList==null || userList.size()==0)
            throw new ResultException(ErrorEnum.PASSWORDERROR);

        User user = userList.get(0);
        String password = null;
        password = MD5Encoder.getEncryptedWithSalt(form.getPassword(), userList.get(0).getUserId().toString());
        if(!password.equals(userList.get(0).getPassword()))
            throw new ResultException(ErrorEnum.PASSWORDERROR);
        user.setPassword(null);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        List<String> authorityList = authorityMapper.getAuthorityListByRoleId(user.getRoleId());
        userDTO.setAuthorityList(authorityList);
        return userDTO;

    }

    //通过手机号码注册
    public User registerByPhone(UserRegisterForm form){
        //检查手机号码格式
        if (testUserAccountType(form.getPhone())!=0){
            throw new ResultException("手机号码不正确");
        }

        //检查手机号码是否已注册
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andPhoneEqualTo(form.getPhone());
        int count = userMapper.countByExample(example);
        if(count > 0){
            throw new ResultException("该账户已存在");
        }

        String code = stringRedisTemplate.opsForValue().get(form.getPhone() + "-" + codePrefix);
        //检查手机验证码
        if(!form.getCode().equals(code)){
            throw new ResultException(ErrorEnum.CODEERROE);
        }

        User user = new User();
        BeanUtils.copyProperties(form, user);
        user.setUserName(KeyUtil.genStringCode(6));
        user.setUserId(KeyUtil.genUniquKeyOnLong());
        user.setPassword(MD5Encoder.getEncryptedWithSalt(user.getPassword(), user.getUserId().toString()));
        user.setState(UserStateEnum.ACTIVE.getCode());
        user.setRoleId(userDefaultRoleId);
        userMapper.insertSelective(user);
        user.setPassword("");
        return user;
    }

    public static int testUserAccountType(String userAccount){
        if(Pattern.matches(REGEX_MOBILE, userAccount)){
            return 0;
        }else if(Pattern.matches(REGEX_EMAIL, userAccount)){
            return 1;
        }else{
            return -1;
        }
    }

    public User registerByEmail(UserRegisterForm2 form) {
        System.out.println("service.........................email");
        if(testUserAccountType(form.getEmail())!=1){
            throw new ResultException("邮箱格式不正确");
        }
        User user = new User();
        BeanUtils.copyProperties(form, user);
        user.setState(UserStateEnum.INVALIDATE.getCode());
        Long userId = KeyUtil.genUniquKeyOnLong();
        String password = null;
        password = MD5Encoder.getEncryptedWithSalt(form.getPassword(), userId.toString());
        user.setUserId(userId);
        user.setPassword(password);
        user.setUserName(KeyUtil.genStringCode(6));
        user.setRoleId(userDefaultRoleId);
        userMapper.insertSelective(user);
        user.setPassword("");
        return  user;

    }

    //发送邮件
    public void sendEmail(Long userId, String toEmail){
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件设置
        String validatePath = KeyUtil.genStringCode(100);

        String path = validateLink + "/"  + userId.toString() + "/" + validatePath;
        System.out.println(path);
        //将验证路径放入redis中，设置有效时间为3天
        stringRedisTemplate.opsForValue().set(userId.toString(), validatePath, 259200, TimeUnit.SECONDS);
        message.setSubject("注册验证");
        message.setText("您现在正在注册论坛帐号,点击链接 " + path +  "完成注册,如非本人操作请忽略此邮件, 链接三天内有效");
        message.setTo(toEmail);
        message.setFrom("13420110105@163.com");
        mailSender.send(message);
    }

    //检查用户名是否已被使用
    public boolean checkUserName(String userName){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        int count = userMapper.countByExample(example);
        System.out.println("count.." + count);
        if(count <= 0)
            return true;
        return false;
    }

    //用户验证
    public boolean userRegisterValidate(Long userId, String path) {
        //根据传入的路径在redis中查找
        String pathStr = stringRedisTemplate.opsForValue().get(userId.toString());
        if(pathStr!=null && path.equals(pathStr)){
            User user = new User();
            user.setUserId(userId);
            user.setState(UserStateEnum.ACTIVE.getCode());
            userMapper.updateByPrimaryKeySelective(user);
            return true;
        }
        return false;
    }

    //发送手机验证码
    public String sendPhoneCode(String account) {
        System.out.println(account);
        //检查是否到达重发短信的时间
        Long expire = stringRedisTemplate.getExpire(account + "-" + intervalPrefix);
        if(expire > 0) {
            throw new ResultException("请" + expire + "秒后重试");
        }

        String code = KeyUtil.genNumCode(6);
        System.out.println("验证码:------------------" + code);
        System.out.println(effectiveTime);
        System.out.println(interval);
        //发送验证码
//        boolean result = PhoneCodeUtils.sendMessage(account, code);
//        if(result==false){
//            throw new ResultException("发送未成功，请稍后重试");
//        }

        //将验证码放到redis中， 10分钟内有效
        stringRedisTemplate.opsForValue().set(account + "-" + codePrefix, code, Integer.parseInt(effectiveTime), TimeUnit.SECONDS);
        //设置两次发送短信的间隔
        stringRedisTemplate.opsForValue().set(account + "-" + intervalPrefix, code, Integer.parseInt(interval), TimeUnit.SECONDS);
        return code;
    }

    //设置用户名
    public void setNewUserName(Long userId, String userName) {
        boolean checkResult = checkUserName(userName);
        if(!checkResult){
            throw new ResultException("该用户名已存在");
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            throw new ResultException("用户帐号不存在");
        }
        user.setUserName(userName);
        userMapper.updateByPrimaryKeySelective(user);
        System.out.println(USER_PREFIX);
        redisTemplate.delete(USER_PREFIX + userId);
    }

    //设置用户头像
    public void setNewUserPic(Long userId, String picPath) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            throw new ResultException("用户帐号不存在");
        }
        user.setUserPic(picPath);
        userMapper.updateByPrimaryKeySelective(user);
        redisTemplate.delete(USER_PREFIX + userId);
    }

    //校对密码
    public void changeUserPassword(Long userId, String password, String newPassword) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            throw new ResultException("用户不存在");
        }
        String encodePassword = MD5Encoder.getEncryptedWithSalt(password, userId.toString());
        if(!encodePassword.equals(user.getPassword())){
            throw new ResultException("密码错误");
        }
        String newEncodePassword = MD5Encoder.getEncryptedWithSalt(newPassword, userId.toString());

        user.setPassword(newEncodePassword);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public void changeUserPassword(Long userId, String newPassword) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            throw new ResultException("用户不存在");
        }
        String encodePassword = MD5Encoder.getEncryptedWithSalt(newPassword, userId.toString());
        user.setPassword(encodePassword);
        userMapper.updateByPrimaryKeySelective(user);
    }
}
