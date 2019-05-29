package com.madao.web.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.dto.CollectDTO;
import com.madao.api.dto.UserDTO;
import com.madao.api.entity.User;
import com.madao.api.service.PostService;
import com.madao.api.service.QuestionService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CollectController {
    @Autowired
    private PostService postService;

    @Autowired
    private QuestionService questionService;
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

    @RequestMapping("/admin/report/getList")
    public ResultView getReportList(HttpServletRequest request, @RequestParam(value="pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        try{
            UserDTO user = checkUserLogin(request);
            checkAdminAuthority(user);
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

    private UserDTO checkUserLogin(HttpServletRequest request){
        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        if(user==null){
            throw new ResultException("用户未登录");
        }
        return user;
    }
    private void checkAdminAuthority(UserDTO user){
        //todo 检查管理员权限
    }
}
