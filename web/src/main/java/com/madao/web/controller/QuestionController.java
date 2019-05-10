package com.madao.web.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.dto.AnswerDTO;
import com.madao.api.entity.AnswerContent;
import com.madao.api.entity.Question;
import com.madao.api.entity.User;
import com.madao.api.enums.AgreeEnum;
import com.madao.api.enums.ErrorEnum;
import com.madao.api.form.QuestionForm;
import com.madao.api.service.QuestionService;
import com.madao.api.service.UserService;
import com.madao.api.utils.FormErrorUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;


    @ResponseBody
    @PostMapping("/question")
    public ResultView addQuestion(@Validated QuestionForm form, BindingResult bindingResult, HttpServletRequest request){
        if(bindingResult.hasErrors()){
            throw new ResultException(ErrorEnum.PARAM_ERROR, FormErrorUtil.getFormErrors(bindingResult));
        }
        HttpSession session = request.getSession();
        Object userObject = session.getAttribute("user");
        if(userObject==null){
            return ResultUtil.returnFail("用户未登录");
        }
        User user = new User();
        BeanUtils.copyProperties(userObject, user);
        form.setUserId(user.getUserId());
        System.out.println(form + "-------");

        return questionService.addQuestion(form);
    }

    @GetMapping("/toQuestion")
    public String toQuestion(HttpServletRequest request){
        List<AnswerDTO> answerDTOList = questionService.getQuestion();
        request.setAttribute("answerDTOList", answerDTOList);
        return "question";
    }

    @ResponseBody
    @GetMapping("/question/testSession")
    public void testSession(){
        User user = questionService.testSession();
        System.out.println(user);

    }

    @GetMapping("/toQuestionInfo")
    public void toQuestionInfo(Long questionId){
//        Question question = questionService.addQuestion();
    }

    @ResponseBody
    @GetMapping("/getEntileAnswer/{answerId}")
    public ResultView getEntileAnswer(@PathVariable Long answerId){
        return questionService.getAnswerContentByQuestionId(answerId);
    }

    @ResponseBody
    @GetMapping("/web/testAnswer")
    public ResultView testAnswer(){
        return questionService.testAnswer("Madao");
    }

    @ResponseBody
    @GetMapping("/answer/agree/{type}")
    public ResultView agreeAnswerL(Long answerId, @PathVariable Byte type, HttpServletRequest request){
        Object userObject =  request.getSession().getAttribute("user");
        if(userObject==null){
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObject;
        Long userId =  Long.parseLong(map.get("userId").toString());
        System.out.println(userId);
        System.out.println(answerId);
        System.out.println(type);
        if(type.equals(AgreeEnum.AGREE.getCode())) {
            System.out.println("agree");
            return questionService.agreeAnswer(userId, answerId);
        }else if(type.equals(AgreeEnum.DEFAULT.getCode())){
            System.out.println("default");
            return questionService.cancelAgreeAnswer(userId, answerId);
        }else if(type.equals(AgreeEnum.DISAGREE.getCode())){
            System.out.println("disagree");
            return questionService.disagreeAnswer(userId, answerId);
        }
        return ResultUtil.returnSuccess();
    }

    @ResponseBody
    @RequestMapping("/answer/agreeType")
    public ResultView getUserAgreeType(@RequestParam("answerIdList") List<Long> answerIdList, HttpServletRequest request){
        Object userObject = request.getSession().getAttribute("user");
        if(userObject==null)
            return ResultUtil.returnSuccess();
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObject;
        Long userId = Long.parseLong(map.get("userId").toString());
        return questionService.getUserAgreeType(userId, answerIdList);
    }

    @ResponseBody
    @RequestMapping("/answer/comment/{answerId}/{pageNum}/{pageSize}")
    public ResultView getAnswerComment(@PathVariable Long answerId, @PathVariable Integer pageNum, @PathVariable Integer pageSize){
        return questionService.getAnswerComment(answerId, pageNum, pageSize);
    }

}
