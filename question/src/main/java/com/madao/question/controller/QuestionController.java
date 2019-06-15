package com.madao.question.controller;

import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.AnswerDTO;
import com.madao.api.dto.QuestionDTO;
import com.madao.api.dto.ReportDTO;
import com.madao.api.entity.Question;
import com.madao.api.form.QuestionForm;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@RestController
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    //添加问题
    @ResponseBody
    @PostMapping("/question")
    public ResultView addQuestion(@RequestBody QuestionForm form){
        System.out.println("addQuestion");
        Question question = null;
        try {
            question = questionService.addQuestion(form);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail("错误,请稍后重试");
        }
        return ResultUtil.returnSuccess(question);
    }

    //获取可见状态的回答
    @GetMapping("/getAnswer/visible")
    public ResultView getQuestion(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            PageInfo<AnswerDTO> resultPage = questionService.getQuestionPageVisible(pageNum, pageSize);
            return ResultUtil.returnSuccess(resultPage);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //获取下一条回答
    @ResponseBody
    @RequestMapping("/question/answer/next")
    ResultView getNextAnswer(@RequestParam(value = "answerIdList", required = false) List<Long> answerIdList, @RequestParam("questionId") Long questionId, @RequestParam(value = "userId", required = false) Long userId){
        if(answerIdList==null){
            answerIdList = new ArrayList<>();
        }
        try {
            AnswerDTO answerDTO = questionService.getNextAnswer(questionId, answerIdList);
            if(userId!=null){
                questionService.addAnswerState(answerDTO, userId);
            }
            System.out.println(answerDTO);
            if(answerDTO==null){
                return ResultUtil.returnFail("没有更多内容了");
            }
            return ResultUtil.returnSuccess(answerDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }

    //获取问题的详细信息，即问题的信息和一个回答的信息
    @RequestMapping("/question/info")
    ResultView getQuestionDTOById(@RequestParam("questionId") Long questionId, @RequestParam(value="answerId", required = false) Long answerId){
        try {
            QuestionDTO questionDTO = null;
            if(answerId!=null) {
                questionDTO = questionService.getQuestionDTO(questionId, answerId);
            }else{
                questionDTO = questionService.getQuestionDTOWithoutAnswer(questionId);
            }
            return ResultUtil.returnSuccess(questionDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail(e.getMessage());
        }
    }

    //问题关注
    @RequestMapping("/question/collect")
    public ResultView collectQuestion(@RequestParam("questionId") Long questionId, @RequestParam("userId") Long userId, @RequestParam("collectType") Byte collectType){
        try {
            questionService.collectQuestion(questionId, userId, collectType);
            return ResultUtil.returnSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail(e.getMessage());
        }
    }

    //获取用户是否关注问题
    @RequestMapping("/question/collect/state")
    public ResultView getQuestionCollectState(@PathParam("questionId") Long questionId, @RequestParam("userId")Long userId){
        try{
            boolean result = questionService.getUserCollectQuestionState(questionId, userId);
            return ResultUtil.returnSuccess(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail(e.getMessage());
        }
    }

    //用户获取个人发布的问题回答
    @RequestMapping("/question/person/getList")
    ResultView getQuestionDTOByPerson(@RequestParam("userId") Long userId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            PageInfo<AnswerDTO> answerDTOPageInfo = questionService.getQuestionByPerson(userId, pageNum, pageSize);
            return ResultUtil.returnSuccess(answerDTOPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //用户获取收藏的回答
    @RequestMapping("/answer/getList/person/collected")
    ResultView getQuestionDTOByPersonCollected(@RequestParam("userId") Long userId, @RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            PageInfo<AnswerDTO> answerDTOPageInfo = questionService.getQuestionByPersonCollected(userId, pageNum, pageSize);
            return ResultUtil.returnSuccess(answerDTOPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/answer/getList/allState")
    ResultView getQuestionDTOInAllState(@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            PageInfo<AnswerDTO> answerDTOPageInfo = questionService.getQuestionInAllState(pageNum, pageSize);
            return ResultUtil.returnSuccess(answerDTOPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/report/getList")
    ResultView getReportList(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try {
            PageInfo<ReportDTO> reportDTOPageInfo = questionService.getReportDTOList(pageNum, pageSize);
            return ResultUtil.returnSuccess(reportDTOPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/report/question")
    ResultView reportQuestion(@RequestParam("userId") Long userId, @RequestParam("questionId") Long questionId, @RequestParam("reason") String reason){
        try{
            questionService.reportQuestion(userId, questionId, reason);
            return ResultUtil.returnSuccess();
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return  ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @RequestMapping("/report/operate")
    ResultView reportOperate(@RequestParam("targetId") Long targetId, @RequestParam("type") Byte type, @RequestParam("operate") Byte operate){
        try {
            questionService.operateReport(targetId, type, operate);
            return ResultUtil.returnSuccess();
        }catch(ResultException e){
            return ResultUtil.returnFail(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //根据问题标题搜索问题，获取该问题点赞数最多的回答
    @RequestMapping("/question/search/byTitle")
    ResultView searchByQuestion(@RequestParam("searchContent") String searchContent, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        try{
            PageInfo<AnswerDTO> questionDTOList = questionService.getQuestionDTOLikeQuestionTitle(searchContent, pageNum, pageSize);
            return ResultUtil.returnSuccess(questionDTOList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

}
