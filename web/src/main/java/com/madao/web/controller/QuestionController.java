package com.madao.web.controller;

import com.madao.api.Exception.ResultException;
import com.madao.api.dto.UserDTO;
import com.madao.api.entity.FileUploadResult;
import com.madao.api.enums.AgreeEnum;
import com.madao.api.form.AnswerCommentAddForm;
import com.madao.api.form.ContentForm;
import com.madao.api.form.AnswerForm;
import com.madao.api.form.QuestionForm;
import com.madao.api.service.QuestionService;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        try {
            UserDTO user = checkUserLogin(request);
            form.setUserId(user.getUserId());

            return questionService.addQuestion(form);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //获取可见的回答
    @GetMapping("/toQuestion")
    public String toQuestion(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", required = false) Integer pageSize){
        if(pageSize==null)
            pageSize = DEFAULT_SIZE;
        try {
            ResultView resultView = questionService.getQuestion(pageNum, pageSize);
            request.setAttribute("data", resultView);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "question";
    }

    //获取问题的下一条回答
    @ResponseBody
    @RequestMapping("/question/answer/next")
    public ResultView getNextAnswer(@RequestParam("answerIdList") List<Long> answerIdList, @RequestParam("questionId") Long questionId, HttpServletRequest request){
        try {
            UserDTO user = (UserDTO) request.getSession().getAttribute("user");
            Long userId = user==null ? null : user.getUserId();
            System.out.println(answerIdList.size());
            ResultView resultView = questionService.getNextAnswer(answerIdList, questionId, userId);
            System.out.println(resultView);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }

    }

    //获取用户个人所有状态的回答
    @ResponseBody
    @RequestMapping("/post/person/getList/{pageNum}/{pageSize}")
    public ResultView getPostByPerson(@PathVariable("pageNum")Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        try {
            UserDTO user = (UserDTO) request.getSession().getAttribute("user");
            if (user == null) {
                return ResultUtil.returnFail();
            }
            ResultView resultView = questionService.getQuestionDTOByPerson(user.getUserId(), pageNum, pageSize);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
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
        try {
            ResultView resultView = questionService.getQuestionDTOById(questionId, answerId);
            return resultView;
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @GetMapping("/getEntileAnswer/{answerId}")
    public ResultView getEntileAnswer(@PathVariable("answerId") Long answerId){
        try {
            return questionService.getAnswerContentByQuestionId(answerId);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @GetMapping("/web/testAnswer")
    public ResultView testAnswer(){
        try {
            return questionService.testAnswer("Madao");
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @GetMapping("/answer/agree/{type}")
    public ResultView agreeAnswerL(Long answerId, @PathVariable Byte type, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            System.out.println(userId);
            System.out.println(answerId);
            System.out.println(type);
            if (type.equals(AgreeEnum.AGREE.getCode())) {
                System.out.println("agree");
                return questionService.agreeAnswer(userId, answerId);
            } else if (type.equals(AgreeEnum.DEFAULT.getCode())) {
                System.out.println("default");
                return questionService.cancelAgreeAnswer(userId, answerId);
            } else if (type.equals(AgreeEnum.DISAGREE.getCode())) {
                System.out.println("disagree");
                return questionService.disagreeAnswer(userId, answerId);
            }
            return ResultUtil.returnSuccess();
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/answer/agreeType")
    public ResultView getUserAgreeType(@RequestParam("answerIdList") List<Long> answerIdList, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            return questionService.getUserAgreeType(userId, answerIdList);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/answer/comment/{answerId}/{pageNum}/{pageSize}")
    public ResultView getAnswerComment(@PathVariable Long answerId, @PathVariable Integer pageNum, @PathVariable Integer pageSize){
        try {
            return questionService.getAnswerComment(answerId, pageNum, pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //用户对回答的收藏和取消收藏操作
    @ResponseBody
    @RequestMapping("/answer/collect/{answerId}/{operate}")
    public ResultView collectionAnswer(@PathVariable("answerId") Long answerId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            return questionService.collectionAnswer(answerId, userId, operate);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //获取用户是否收藏回答
    @ResponseBody
    @RequestMapping("/answer/collect/getList")
    public ResultView getCollectFlagByAnswerIdList(@RequestParam("answerIdList") List<Long> answerIdList, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            return questionService.getCollectFlagInList(answerIdList, userId);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //获取用户是否关注问题
    @ResponseBody
    @RequestMapping("/question/collect/state/{questionId}")
    public ResultView getQuestionCollectState(@PathVariable("questionId") Long questionId, HttpServletRequest request){
        try{
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            return questionService.getQuestionCollectState(questionId, userId);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @PostMapping(value = "/answer/comment", consumes = "application/json")
    public ResultView addAnswerComment(@RequestBody @Valid AnswerCommentAddForm form, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            return questionService.addAnswerComment(form.getAnswerId(), userId, form.getCommentContent());
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //用户关注问题或者取消关注
    @ResponseBody
    @GetMapping("/question/collect/{questionId}/{collectType}")
    public ResultView collectQuestion(@PathVariable("questionId") Long questionId, @PathVariable("collectType") Byte collectType, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            return questionService.collectQuestion(questionId, userId, collectType);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
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
        try {
            UserDTO user = checkUserLogin(request);
            Long userId = user.getUserId();
            AnswerForm answerForm = new AnswerForm(questionId, userId, form);
            return questionService.addAnswer(answerForm);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //用户对个人的回答选择可见还是不可见
    @ResponseBody
    @PutMapping("/answer/person/visible/{answerId}/{operate}")
    public ResultView operateAnswerByPerson(@PathVariable("answerId")Long answerId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            ResultView resultView = questionService.operateAnswer(user.getUserId(), answerId, operate);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    //用户获取收藏的回答
    @ResponseBody
    @RequestMapping("/answer/person/collected/{pageNum}/{pageSize}")
    public ResultView getAnswerListByUserCollected(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            ResultView resultView = questionService.getQuestionDTOByPersonCollected(user.getUserId(), pageNum, pageSize);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }


    //用户举报问题
    @ResponseBody
    @RequestMapping("/post/report/question/{questionId}")
    public ResultView reportQuestion(@PathVariable("questionId") Long questionId, @RequestParam("reason")String reason,HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            return questionService.reportQuestion(user.getUserId(), questionId, reason);
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/answer/admin/getList/{pageSize}/{pageNum}")
    public ResultView getAnswerInAllState(@PathVariable("pageSize") Integer pageSize, @PathVariable("pageNum") Integer pageNum, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            checkAdminAuthority(user);
            ResultView resultView = questionService.getQuestionDTOInAllState(pageNum, pageSize);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/answer/admin/ban/{answerId}/{operate}")
    public ResultView adminBanAnswer(@PathVariable("answerId") Long answerId, @PathVariable("operate") Byte operate, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            checkAdminAuthority(user);
            ResultView resultView = questionService.operateBanAnswer(answerId, operate);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }

    @ResponseBody
    @RequestMapping("/question/report/{answerId}")
    public ResultView reportAnswer(@PathVariable("answerId") Long answerId, @RequestParam("reason") String reason, HttpServletRequest request){
        try {
            UserDTO user = checkUserLogin(request);
            ResultView resultView = questionService.reportAnswer(user.getUserId(), answerId, reason);
            return resultView;
        }catch (ResultException e){
            System.out.println(e.getMessage());
            return ResultUtil.returnFail(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }


    @RequestMapping("/question/search")
    public String searchQuestion(@RequestParam("searchContent") String searchContent, @RequestParam(value="pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value="pageSize", defaultValue = "5")Integer pageSize, HttpServletRequest request){
        try {
            ResultView resultView = questionService.searchByQuestion(searchContent, pageNum, pageSize);
            request.setAttribute("data", resultView);
            request.setAttribute("searchContent", searchContent);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "question";
    }

    @ResponseBody
    @RequestMapping("/question/search/1")
    public ResultView searchQuestion1(@RequestParam("searchContent") String searchContent, @RequestParam(value="pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value="pageSize", defaultValue = "5")Integer pageSize, HttpServletRequest request){
        try {
            ResultView resultView = questionService.searchByQuestion(searchContent, pageNum, pageSize);
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
    private void checkAdminAuthority(UserDTO user){
        //todo 检查管理员权限
    }
}
