package com.madao.api.service;

import com.madao.api.dto.AnswerDTO;
import com.madao.api.entity.User;
import com.madao.api.form.AnswerForm;
import com.madao.api.form.QuestionForm;
import com.madao.api.utils.ResultView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@FeignClient(value = "question", fallbackFactory = QuestionServiceFallbackFactory.class)
public interface QuestionService {
    @PostMapping("/question")
    public ResultView addQuestion(QuestionForm form);

    @GetMapping("/testSession")
    public User testSession();

    @GetMapping("/getAnswer/visible")
    public ResultView getQuestion(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);

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

    @RequestMapping("/answer/collect")
    public ResultView collectionAnswer(@RequestParam("answerId") Long answerId, @RequestParam("userId") Long userId, @RequestParam("operate") Byte operate);

    @RequestMapping("/answer/collect/getList")
    ResultView getCollectFlagInList(@RequestParam("answerIdList") List<Long> answerIdList, @RequestParam("userId") Long userId);

    @PostMapping("/answer/comment")
    ResultView addAnswerComment(@RequestParam("answerId") Long answerId, @RequestParam("userId") Long userId, @RequestParam("commentContent") String commentContent);

    @RequestMapping("/question/info")
    ResultView getQuestionDTOById(@RequestParam("questionId") Long questionId, @RequestParam(value="answerId", required = false) Long answerId);

    @RequestMapping("/question/collect")
    ResultView collectQuestion(@RequestParam("questionId") Long questionId, @RequestParam("userId") Long userId, @RequestParam("collectType") Byte collectType);

    @PostMapping("/answer/add")
    public ResultView addAnswer(@RequestBody AnswerForm form);

    @RequestMapping("/question/collect/state")
    public ResultView getQuestionCollectState(@PathParam("questionId") Long questionId, @RequestParam("userId")Long userId);

    @RequestMapping("/question/answer/next")
    ResultView getNextAnswer(@RequestParam("answerIdList") List<Long> answerIdList, @RequestParam("questionId") Long questionId, @RequestParam(value = "userId", required = false) Long userId);

    @RequestMapping("/question/person/getList")
    ResultView getQuestionDTOByPerson(@RequestParam("userId") Long userId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);

    @RequestMapping("/question/person/operate")
    ResultView operateAnswer(@RequestParam("userId")Long userId, @RequestParam("answerId")Long answerId, @RequestParam("operate")Byte operate);

    @RequestMapping("/answer/getList/person/collected")
    ResultView getQuestionDTOByPersonCollected(@RequestParam("userId") Long userId, @RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize);
}
