package com.madao.api.service;

import com.madao.api.dto.AnswerDTO;
import com.madao.api.entity.Question;
import com.madao.api.entity.User;
import com.madao.api.form.QuestionForm;
import com.madao.api.utils.ResultView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@FeignClient(value = "question", fallbackFactory = QuestionServiceFallbackFactory.class)
public interface QuestionService {
    @PostMapping("/question")
    public ResultView addQuestion(QuestionForm form);

    @GetMapping("/testSession")
    public User testSession();

    @GetMapping("/getAnswer")
    public List<AnswerDTO> getQuestion();

    @RequestMapping(value = "/getAnswerContent")
    public ResultView getAnswerContentByQuestionId(Long answerId);

    @RequestMapping(value="/testAnswer")
    public ResultView testAnswer(String aa);

    @RequestMapping(value="/answer/agree")
    ResultView agreeAnswer(@RequestParam("userId") Long userId, @RequestParam("answerId") Long answerId);

    @RequestMapping("/answer/agreeType")
    public ResultView getUserAgreeType(@RequestParam("userId") Long userId, @RequestParam("answerIdList") List<Long> answerIdList);

    @RequestMapping("/answer/cancelAgre")
    ResultView cancelAgreeAnswer(@RequestParam("userId") Long userId, @RequestParam("answerId") Long answerId);

    @RequestMapping("/answer/disagree")
    ResultView disagreeAnswer(@RequestParam("userId") Long userId, @RequestParam("answerId") Long answerId);

    @RequestMapping("/answer/comment")
    public ResultView getAnswerComment(@RequestParam("answerId") Long answerId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);
}
