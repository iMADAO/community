package com.madao.web.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.dto.AnswerDTO;
import com.madao.api.entity.FileUploadResult;
import com.madao.api.entity.User;
import com.madao.api.enums.AgreeEnum;
import com.madao.api.enums.ErrorEnum;
import com.madao.api.form.AnswerCommentAddForm;
import com.madao.api.form.AnswerContentForm;
import com.madao.api.form.AnswerForm;
import com.madao.api.form.QuestionForm;
import com.madao.api.service.QuestionService;
import com.madao.api.utils.FormErrorUtil;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;

    public static final String PICPATH_PREFIX = "http://localhost:8080/pic";
    public static final String PICLOCAL_PATH = "/usr/local/apache-tomcat-7.0.79/webapps/pic";


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
        for(AnswerDTO answerDTO: answerDTOList)
            System.out.println(answerDTO);
        request.setAttribute("answerDTOList", answerDTOList);
        return "question";
    }

    @ResponseBody
    @GetMapping("/question/testSession")
    public void testSession(){
        User user = questionService.testSession();
        System.out.println(user);

    }

    @GetMapping("/toQuestionInfo/{questionId}/{answerId}")
    public String toQuestionInfo(@PathVariable("questionId") Long questionId, @PathVariable("answerId") Long answerId, HttpServletRequest request){
        ResultView resultView = questionService.getQuestionDTOById(questionId, answerId);
        request.setAttribute("resultView", resultView);
        return "questionInfo";
    }

    @ResponseBody
    @GetMapping("/getQuestionInfo/{questionId}/{answerId}")
    public ResultView getQuestionInfo(@PathVariable("questionId") Long questionId, @PathVariable("answerId") Long answerId, HttpServletRequest request){
        ResultView resultView = questionService.getQuestionDTOById(questionId, answerId);
        return resultView;
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

    @ResponseBody
    @RequestMapping("/answer/collect/{answerId}/{operate}")
    public ResultView collectionAnswer(@PathVariable("answerId") Long answerId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        Object userObj = request.getSession().getAttribute("user");
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObj;
        if(userObj==null){
            return ResultUtil.returnFail("用户未登录,请登录后操作");
        }
        Long userId = Long.parseLong(map.get("userId").toString());
        return questionService.collectionAnswer(answerId, userId, operate);
    }

    @ResponseBody
    @RequestMapping("/answer/collect/getList")
    public ResultView getCollectFlagByAnswerIdList(@RequestParam("answerIdList") List<Long> answerIdList, HttpServletRequest request){
        Object userObject = request.getSession().getAttribute("user");
        if(userObject==null)
            return ResultUtil.returnSuccess();
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObject;
        Long userId = Long.parseLong(map.get("userId").toString());
        return questionService.getCollectFlagInList(answerIdList, userId);
    }

    @ResponseBody
    @PostMapping(value = "/answer/comment", consumes = "application/json")
    public ResultView addAnswerComment(@RequestBody @Valid AnswerCommentAddForm form, HttpServletRequest request){
        Object userObject = request.getSession().getAttribute("user");
        if(userObject==null)
            return ResultUtil.returnFail("用户未登录");
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObject;
        Long userId = Long.parseLong(map.get("userId").toString());
        return questionService.addAnswerComment(form.getAnswerId(), userId, form.getCommentContent());
    }

    @ResponseBody
    @GetMapping("/question/collect/{questionId}/{collectType}")
    public ResultView collectQuestion(@PathVariable("questionId") Long questionId, @PathVariable("collectType") Byte collectType, HttpServletRequest request){
        Object userObject = request.getSession().getAttribute("user");
        if(userObject==null)
            return ResultUtil.returnFail("用户未登录");
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObject;
        Long userId = Long.parseLong(map.get("userId").toString());
        return questionService.collectQuestion(questionId, userId, collectType);
    }


    @ResponseBody
    @RequestMapping("/pic/upload")
    public ResultView  fileUpload2(@RequestParam("file") CommonsMultipartFile file, HttpServletRequest request){
        String fileName = KeyUtil.genStringCode(10)+file.getOriginalFilename();
        File picFile = new File(PICLOCAL_PATH, fileName);
        //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
        try {
            file.transferTo(picFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.returnFail("请稍后重试");
        }
        String picPath = Paths.get(PICPATH_PREFIX, fileName).toString();
        return ResultUtil.returnSuccess(picPath);
    }

    @ResponseBody
    @RequestMapping("/pic/upload/2")
    public static ResultView upload(@RequestParam("file") MultipartFile imageFile, @RequestParam("num") Integer num) {
        System.out.println("Num....." +  num);
        if (imageFile.isEmpty()) {
            return null;
        }
        String filename = imageFile.getOriginalFilename();

        String ext= null;
        if(filename.contains(".")){
            ext = filename.substring(filename.lastIndexOf("."));
        }else{
            ext = "";
        }

        String nfileName = KeyUtil.genStringCode(10) + ext;
        File targetFile = Paths.get(PICLOCAL_PATH, nfileName).toFile();
        try {
            imageFile.transferTo(targetFile);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return ResultUtil.returnFail("请稍后重试");
        }

        String accessUrl =  PICPATH_PREFIX + "/" + nfileName;

        FileUploadResult result = new FileUploadResult();
        result.setImgPath(accessUrl);
        result.setNum(num);
        return ResultUtil.returnSuccess(result);
    }

    //用户添加回答
    @ResponseBody
    @PostMapping("/answer/{questionId}")
    public ResultView addAnswer(@RequestBody List<AnswerContentForm> form, @PathVariable("questionId") Long questionId, HttpServletRequest request){
        Object userObject = request.getSession().getAttribute("user");
        if(userObject==null)
            return ResultUtil.returnFail("用户未登录");
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) userObject;
        Long userId = Long.parseLong(map.get("userId").toString());
        AnswerForm answerForm = new AnswerForm(questionId, userId, form);
        return questionService.addAnswer(answerForm);
    }


    //测试用
    @ResponseBody
    @RequestMapping("/testRealPath")
    public void test(HttpServletRequest request){
        String realPath = request.getServletContext().getRealPath("/");
        String path = request.getServletPath();
        System.out.println(realPath);
        System.out.println(path);
    }

    @RequestMapping("/toTest")
    public String toTest(){
        return "test";
    }




}
