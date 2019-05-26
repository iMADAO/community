package com.madao.web.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.dto.AnswerDTO;
import com.madao.api.entity.FileUploadResult;
import com.madao.api.entity.User;
import com.madao.api.enums.AgreeEnum;
import com.madao.api.enums.ErrorEnum;
import com.madao.api.form.AnswerCommentAddForm;
import com.madao.api.form.ContentForm;
import com.madao.api.form.AnswerForm;
import com.madao.api.form.QuestionForm;
import com.madao.api.service.QuestionService;
import com.madao.api.utils.FormErrorUtil;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;

    @Value("${PIC_PREFIX}")
    private String PICPATH_PREFIX;

    @Value("${PICLOCAL_PATH}")
    private String PICLOCAL_PATH;

    public static final int DEFAULT_SIZE = 5;


    @ResponseBody
    @PostMapping("/question")
    public ResultView addQuestion(@Validated QuestionForm form, BindingResult bindingResult, HttpServletRequest request){
        if(bindingResult.hasErrors()){
            throw new ResultException(ErrorEnum.PARAM_ERROR, FormErrorUtil.getFormErrors(bindingResult));
        }
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
        form.setUserId(user.getUserId());
        System.out.println(form + "-------");

        return questionService.addQuestion(form);
    }

    //获取可见的回答
    @GetMapping("/toQuestion")
    public String toQuestion(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", required = false) Integer pageSize){
        if(pageSize==null)
            pageSize = DEFAULT_SIZE;
        ResultView resultView = questionService.getQuestion(pageNum, pageSize);
        request.setAttribute("data", resultView);
//        List<AnswerDTO> answerDTOList = questionService.getQuestion();
//        for(AnswerDTO answerDTO: answerDTOList)
//            System.out.println(answerDTO);
//        request.setAttribute("answerDTOList", answerDTOList);
        return "question";
    }

    @ResponseBody
    @GetMapping("/question/testSession")
    public void testSession(){
        User user = questionService.testSession();
        System.out.println(user);

    }

    //获取问题的下一条回答
    @ResponseBody
    @RequestMapping("/question/answer/next")
    public ResultView getNextAnswer(@RequestParam("answerIdList") List<Long> answerIdList, @RequestParam("questionId") Long questionId, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        Long userId = user==null ? null : user.getUserId();
        try {
            System.out.println(answerIdList.size());
            ResultView resultView = questionService.getNextAnswer(answerIdList, questionId, userId);
            System.out.println(resultView);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }

    @ResponseBody
    @RequestMapping("/post/person/getList/{pageNum}/{pageSize}")
    public ResultView getPostByPerson(@PathVariable("pageNum")Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                return ResultUtil.returnFail();
            }
            ResultView resultView = questionService.getQuestionDTOByPerson(user.getUserId(), pageNum, pageSize);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }


    @GetMapping("/toQuestionInfo/{questionId}")
    public String toQuestionInfo(@PathVariable("questionId") Long questionId, @RequestParam(value="answerId", required = false) Long answerId, HttpServletRequest request){
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
    public ResultView getEntileAnswer(@PathVariable("answerId") Long answerId){
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
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
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
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
        return questionService.getUserAgreeType(userId, answerIdList);
    }

    @ResponseBody
    @RequestMapping("/answer/comment/{answerId}/{pageNum}/{pageSize}")
    public ResultView getAnswerComment(@PathVariable Long answerId, @PathVariable Integer pageNum, @PathVariable Integer pageSize){
        return questionService.getAnswerComment(answerId, pageNum, pageSize);
    }

    //用户对回答的收藏和取消收藏操作
    @ResponseBody
    @RequestMapping("/answer/collect/{answerId}/{operate}")
    public ResultView collectionAnswer(@PathVariable("answerId") Long answerId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
        return questionService.collectionAnswer(answerId, userId, operate);
    }

    //获取用户是否收藏回答
    @ResponseBody
    @RequestMapping("/answer/collect/getList")
    public ResultView getCollectFlagByAnswerIdList(@RequestParam("answerIdList") List<Long> answerIdList, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
        return questionService.getCollectFlagInList(answerIdList, userId);
    }

    //获取用户是否关注问题
    @ResponseBody
    @RequestMapping("/question/collect/state/{questionId}")
    public ResultView getQuestionCollectState(@PathVariable("questionId") Long questionId, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
        try{
            return questionService.getQuestionCollectState(questionId, userId);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @PostMapping(value = "/answer/comment", consumes = "application/json")
    public ResultView addAnswerComment(@RequestBody @Valid AnswerCommentAddForm form, HttpServletRequest request){
        System.out.println("add answer comment");
        System.out.println(form);
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
        return questionService.addAnswerComment(form.getAnswerId(), userId, form.getCommentContent());
    }

    //用户关注问题或者取消关注
    @ResponseBody
    @GetMapping("/question/collect/{questionId}/{collectType}")
    public ResultView collectQuestion(@PathVariable("questionId") Long questionId, @PathVariable("collectType") Byte collectType, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
        return questionService.collectQuestion(questionId, userId, collectType);
    }


    //上传图片
    @ResponseBody
    @RequestMapping("/pic/upload")
    public ResultView upload(@RequestParam("file") MultipartFile imageFile, @RequestParam(value = "num", required = false) Integer num) {
        if (imageFile.isEmpty()) {
            return ResultUtil.returnFail("未选择图片");
        }
        String filename = imageFile.getOriginalFilename();

        String ext= null;
        if(filename.contains(".")){
            ext = filename.substring(filename.lastIndexOf("."));
        }else{
            ext = "";
        }

        String nfileName = KeyUtil.genUniquKeyOnLong() + ext;
        Path picPath = Paths.get(PICLOCAL_PATH);
        if(Files.notExists(picPath)){
            try {
                System.out.println("creating path........." + picPath);
                Files.createDirectory(picPath);
            } catch (IOException e) {
                e.printStackTrace();
                return ResultUtil.returnFail("请稍后重试");
            }
        }
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
        result.setImgName(nfileName);
        return ResultUtil.returnSuccess(result);
    }

    //用户添加回答
    @ResponseBody
    @PostMapping("/answer/{questionId}")
    public ResultView addAnswer(@RequestBody List<ContentForm> form, @PathVariable("questionId") Long questionId, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return ResultUtil.returnFail("用户未登录");
        Long userId = user.getUserId();
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

    @ResponseBody
    @PutMapping("/answer/person/visible/{answerId}/{operate}")
    public ResultView operateAnswerByPerson(@PathVariable("answerId")Long answerId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        User  user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultUtil.returnFail("用户未登录");
        }
        try {
            ResultView resultView = questionService.operateAnswer(user.getUserId(), answerId, operate);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/answer/person/collected/{pageNum}/{pageSize}")
    public ResultView getAnswerListByUserCollected(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        User  user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultUtil.returnFail("用户未登录");
        }
        try {
            ResultView resultView = questionService.getQuestionDTOByPersonCollected(user.getUserId(), pageNum, pageSize);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }




}
