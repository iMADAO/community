package com.madao.web.controller;

import com.madao.api.dto.CollectDTO;
import com.madao.api.entity.User;
import com.madao.api.service.PostService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CollectController {
    @Autowired
    private PostService postService;
    @RequestMapping("/collect/get/post/{pageNum}/{pageSize}")
    public ResultView getUserCollectByType(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        try{
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                return ResultUtil.returnFail("用户未登录,请登录后重试");
            }
            Long userId = user.getUserId();
            ResultView resultView = postService.getPostCollect(userId, pageNum, pageSize);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }
}
