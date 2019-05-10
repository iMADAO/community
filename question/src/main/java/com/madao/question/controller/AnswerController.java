package com.madao.question.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.AnswerCommentDTO;
import com.madao.api.dto.AnswerDTO;
import com.madao.api.entity.Answer;
import com.madao.api.entity.AnswerComment;
import com.madao.api.entity.AnswerContent;
import com.madao.api.entity.User;
import com.madao.api.enums.AgreeEnum;
import com.madao.api.service.UserService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.question.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AnswerController {

    @Autowired
    UserService userService;

    @Autowired
    private AnswerService answerService;
    @RequestMapping("/getAnswerContent")
    public ResultView getAnswerContentByQuestionId(@RequestBody Long answerId){
        if(answerId==null) {
            return ResultUtil.returnFail("该信息不存在");
        }
        List<AnswerContent> answerContentList = answerService.getAnswerContentByAnswerId(answerId);
        return ResultUtil.returnSuccess(answerContentList);
    }

    @RequestMapping("/testAnswer")
    public ResultView test(@RequestBody String aa){
        System.out.println("test " + aa);
        return ResultUtil.returnSuccess();
    }

    //用户点赞
    @RequestMapping(value="/answer/agree")
    ResultView agreeAnswer(@RequestParam("userId") Long userId, @RequestParam("answerId") Long answerId){
        try {
            int result = answerService.addAgreeOnAnswer(userId, answerId);
            System.out.println("result...." + result);
            if(result>0){
                return ResultUtil.returnSuccess(result);
            }
        }catch(ResultException e){
            return ResultUtil.returnFail(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
        return ResultUtil.returnSuccess();
    }

    @RequestMapping("/answer/cancelAgre")
    ResultView cancelAgreeAnswer(@RequestParam("userId") Long userId, @RequestParam("answerId") Long answerId){
        try {
            int result = answerService.cancelAgreeOnAnswer(userId, answerId);
            if(result>0){
                return ResultUtil.returnSuccess(result);
            }
        }catch(ResultException e){
            return ResultUtil.returnFail(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
        return ResultUtil.returnSuccess();
    }

    @RequestMapping("/answer/disagree")
    ResultView disagreeAnswer(@RequestParam("userId") Long userId, @RequestParam("answerId") Long answerId){
        try {
            int result = answerService.addDisagreeOnAnswer(userId, answerId);
            if(result>0){
                return ResultUtil.returnSuccess(result);
            }
        }catch(ResultException e){
            return ResultUtil.returnFail(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
        return ResultUtil.returnSuccess();
    }

    //获取用户在该回答是点赞还是不同意，用于在页面时确定按钮的状态
    @RequestMapping("/answer/agreeType")
    public ResultView getUserAgreeType(@RequestParam("userId") Long userId, @RequestParam("answerIdList") List<Long> answerIdList){
        List<Byte> result =  answerService.getAgreeTypeByUserId(userId, answerIdList);
        System.out.println(result);
        return ResultUtil.returnSuccess(result);
    }

    @RequestMapping("/answer/comment")
    public ResultView getAnswerComment(@RequestParam("answerId") Long answerId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<AnswerCommentDTO> answerCommentDTOList = answerService.getCommentByAnswerid(answerId);
            PageInfo pageInfo = new PageInfo(answerCommentDTOList);
            return ResultUtil.returnSuccess(pageInfo);
        }catch (Exception e){
            e.printStackTrace();
            ResultUtil.returnFail("请稍后重试");
        }
        return ResultUtil.returnSuccess();
    }
}
