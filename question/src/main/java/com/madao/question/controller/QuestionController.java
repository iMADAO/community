package com.madao.question.controller;

import com.madao.api.dto.AnswerDTO;
import com.madao.api.dto.QuestionDTO;
import com.madao.api.entity.Answer;
import com.madao.api.entity.Question;
import com.madao.api.entity.User;
import com.madao.api.enums.ErrorEnum;
import com.madao.api.enums.ResultEnum;
import com.madao.api.form.QuestionForm;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.question.service.QuestionService;
import org.apache.catalina.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class QuestionController {

    @Autowired
    private QuestionService questionService;

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

    @ResponseBody
    @GetMapping("/testSession")
    public User testSession(HttpServletRequest request){
        HttpSession session = request.getSession();
        Object user =  session.getAttribute("user");
        System.out.println(user);
        return null;

    }

    @GetMapping("/getAnswer")
    public List<AnswerDTO> getQuestion(){
        List<AnswerDTO> answerDTO =  questionService.getQuestion();
        return answerDTO;
    }


    @RequestMapping("/question/info")
    public ResultView getQuestionDTOById(@RequestParam("questionId") Long questionId, @RequestParam("answerId") Long answerId){
        try {
            QuestionDTO questionDTO = questionService.getQuestionDTO(questionId, answerId);
            return ResultUtil.returnSuccess(questionDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail(e.getMessage());
        }
    }

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
}
