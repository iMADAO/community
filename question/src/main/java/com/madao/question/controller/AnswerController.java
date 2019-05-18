package com.madao.question.controller;

import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.AnswerCommentDTO;
import com.madao.api.entity.AnswerComment;
import com.madao.api.entity.AnswerContent;
import com.madao.api.form.AnswerForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.question.service.AnswerService;
import com.madao.question.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnswerController {

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CollectService collectService;
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
            return ResultUtil.returnSuccess(result);
        }catch(ResultException e){
            return ResultUtil.returnFail(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
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
        System.out.println("/answer/agreeType");
        List<Byte> result =  answerService.getAgreeTypeByUserId(userId, answerIdList);
        for(Byte b: result)
            System.out.println(b);
        System.out.println(result);
        return ResultUtil.returnSuccess(result);
    }

    //获取某个回答的评论
    @RequestMapping("/answer/comment")
    public ResultView getAnswerComment(@RequestParam("answerId") Long answerId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){

        try {
//            List<AnswerCommentDTO> answerCommentDTOList = answerService.getCommentByAnswerid(answerId, pageNum, pageSize);
            PageInfo pageInfo = answerService.getCommentByAnswerid(answerId, pageNum, pageSize);
            return ResultUtil.returnSuccess(pageInfo);
        }catch (Exception e){
            e.printStackTrace();
            ResultUtil.returnFail("请稍后重试");
        }
        return ResultUtil.returnSuccess();
    }

    //获取某个回答的评论
    @RequestMapping("/answer/comment/2")
    public ResultView getAnswerComment2(@RequestParam("answerId") Long answerId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){

        try {
            PageInfo<AnswerCommentDTO> pageInfo = answerService.getCommentByAnswerid2(answerId, pageNum, pageSize);
            return ResultUtil.returnSuccess(pageInfo);
        }catch (Exception e){
            e.printStackTrace();
            ResultUtil.returnFail("请稍后重试");
        }
        return ResultUtil.returnSuccess();
    }

    //对回答进行收藏或者取消收藏
    @RequestMapping("/answer/collect")
    public ResultView collectionAnswer(@RequestParam("answerId") Long answerId, @RequestParam("userId") Long userId, @RequestParam("operate") Byte operate){
        try {
            collectService.updateAnswerCollect(answerId, userId, operate);
        }catch (ResultException e){
            e.printStackTrace();
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
        return ResultUtil.returnSuccess();
    }

    //对一组回答判断用户是否已收藏
    @RequestMapping("/answer/collect/getList")
    public ResultView getCollectFlagInList(@RequestBody @RequestParam("answerIdList") List<Long> answerIdList, @RequestParam("userId") Long userId){
        List<Byte> resultList =  collectService.checkCollectInList(userId, answerIdList);
        return ResultUtil.returnSuccess(resultList);
    }

    @PostMapping("/answer/comment")
    public ResultView addAnswerComment(@RequestParam("answerId") Long answerId, @RequestParam("userId") Long userId, @RequestParam("commentContent") String commentContent){
        try{
            AnswerComment comment = answerService.addAnswerComment(answerId, userId, commentContent);
            return ResultUtil.returnSuccess(comment);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail("请稍后重试");
        }
    }

    @PostMapping("/answer/add")
    public ResultView addAnswer(@RequestBody AnswerForm form){
        try {
            answerService.addAnswer(form);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail("请稍后重试");
        }
        return ResultUtil.returnSuccess();
    }
}
