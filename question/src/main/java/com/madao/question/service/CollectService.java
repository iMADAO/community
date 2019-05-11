package com.madao.question.service;

import com.madao.api.Exception.ResultException;
import com.madao.api.entity.Collect;
import com.madao.api.enums.CollectTypeEnum;
import com.madao.api.enums.OperateEnum;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultView;
import com.madao.question.bean.CollectExample;
import com.madao.question.mapper.CollectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectService {
    @Autowired
    private CollectMapper collectMapper;

    public void updateAnswerCollect(Long answerId, Long userId, Byte operate) {
        CollectExample example =  new CollectExample();
        CollectExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTypeEqualTo(CollectTypeEnum.ANSWER.getCode());
        criteria.andTargetIdEqualTo(answerId);
        //取消操作
        if(operate.equals(OperateEnum.CANCEL.getCode())){
            collectMapper.deleteByExample(example);
        }else if(operate.equals(OperateEnum.OPERATE.getCode())){
            int count = collectMapper.countByExample(example);
            if(count>0){
                throw new ResultException("该操作已完成");
            }
            Collect collect = new Collect(userId, answerId, CollectTypeEnum.ANSWER.getCode());
            collect.setCollectId(KeyUtil.genUniquKeyOnLong());
            collectMapper.insertSelective(collect);
        }

    }

    public List<Byte> checkCollectInList(Long userId, List<Long> answerIdList) {
        List<Byte> resultList =  new ArrayList<>(answerIdList.size());
        for(Long answerId: answerIdList){
            CollectExample collectExample = new CollectExample();
            CollectExample.Criteria criteria = collectExample.createCriteria();
            criteria.andUserIdEqualTo(userId);
            criteria.andTargetIdEqualTo(answerId);
            criteria.andTypeEqualTo(CollectTypeEnum.ANSWER.getCode());
            int count = collectMapper.countByExample(collectExample);
            if(count > 0){
                resultList.add(OperateEnum.OPERATE.getCode());
            }else{
                resultList.add(OperateEnum.CANCEL.getCode());
            }
        }
        return resultList;
    }
}
