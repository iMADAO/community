package com.madao.web.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.dto.CollectDTO;
import com.madao.api.dto.UserDTO;
import com.madao.api.entity.User;
import com.madao.api.enums.OperateEnum;
import com.madao.api.enums.ReportStateEnum;
import com.madao.api.service.PostService;
import com.madao.api.service.QuestionService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CollectController {
    @Autowired
    private PostService postService;

    @Autowired
    private QuestionService questionService;

    private String authorityResult = "authorityResult";

    @RequestMapping("/collect/get/post/{pageNum}/{pageSize}")
    public ResultView getUserCollectByType(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        try{
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            ResultView resultView = postService.getPostCollect(userId, pageNum, pageSize);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }

    @RequestMapping("/admin/get/report/getList")
    public ResultView getReportList(HttpServletRequest request, @RequestParam(value="pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        try{
            UserDTO user = checkUserLogin(request);
            checkAdminAuthority(request);
            ResultView resultView = questionService.getReportList(pageNum, pageSize);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }


    @RequestMapping("/admin/ban/operate/toCancel")
    public ResultView toCancelReport(@RequestParam("type") Byte type, @RequestParam("targetId") Long targetId){
        try {
            ResultView resultView = questionService.reportOperate(targetId, type, ReportStateEnum.DENY.getCode());
            System.out.println(resultView);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @PutMapping("/admin/ban/operate/toBan")
    public ResultView toDenyReport(@RequestParam("type") Byte type, @RequestParam("targetId") Long targetId){
        try {
            ResultView resultView = questionService.reportOperate(targetId, type, ReportStateEnum.FINISH.getCode());
            System.out.println(resultView);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }


    private UserDTO checkUserLogin(HttpServletRequest request){
        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        if(user==null){
            throw new ResultException("用户未登录");
        }
        return user;
    }

    private void checkAdminAuthority(HttpServletRequest request){
        System.out.println(request);
        boolean result = (boolean) request.getAttribute(authorityResult);
        if(result==false){
            throw new ResultException("权限不足,无法访问");
        }
    }
}
