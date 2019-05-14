package com.madao.post.bean;

import java.util.ArrayList;
import java.util.List;

public class SegmentContentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SegmentContentExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andContentIdIsNull() {
            addCriterion("content_id is null");
            return (Criteria) this;
        }

        public Criteria andContentIdIsNotNull() {
            addCriterion("content_id is not null");
            return (Criteria) this;
        }

        public Criteria andContentIdEqualTo(Long value) {
            addCriterion("content_id =", value, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdNotEqualTo(Long value) {
            addCriterion("content_id <>", value, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdGreaterThan(Long value) {
            addCriterion("content_id >", value, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("content_id >=", value, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdLessThan(Long value) {
            addCriterion("content_id <", value, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdLessThanOrEqualTo(Long value) {
            addCriterion("content_id <=", value, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdIn(List<Long> values) {
            addCriterion("content_id in", values, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdNotIn(List<Long> values) {
            addCriterion("content_id not in", values, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdBetween(Long value1, Long value2) {
            addCriterion("content_id between", value1, value2, "contentId");
            return (Criteria) this;
        }

        public Criteria andContentIdNotBetween(Long value1, Long value2) {
            addCriterion("content_id not between", value1, value2, "contentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdIsNull() {
            addCriterion("segment_id is null");
            return (Criteria) this;
        }

        public Criteria andSegmentIdIsNotNull() {
            addCriterion("segment_id is not null");
            return (Criteria) this;
        }

        public Criteria andSegmentIdEqualTo(Long value) {
            addCriterion("segment_id =", value, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdNotEqualTo(Long value) {
            addCriterion("segment_id <>", value, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdGreaterThan(Long value) {
            addCriterion("segment_id >", value, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("segment_id >=", value, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdLessThan(Long value) {
            addCriterion("segment_id <", value, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdLessThanOrEqualTo(Long value) {
            addCriterion("segment_id <=", value, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdIn(List<Long> values) {
            addCriterion("segment_id in", values, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdNotIn(List<Long> values) {
            addCriterion("segment_id not in", values, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdBetween(Long value1, Long value2) {
            addCriterion("segment_id between", value1, value2, "segmentId");
            return (Criteria) this;
        }

        public Criteria andSegmentIdNotBetween(Long value1, Long value2) {
            addCriterion("segment_id not between", value1, value2, "segmentId");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Byte value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Byte value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Byte value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Byte value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Byte value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Byte> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Byte> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Byte value1, Byte value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andContentOrderIsNull() {
            addCriterion("content_order is null");
            return (Criteria) this;
        }

        public Criteria andContentOrderIsNotNull() {
            addCriterion("content_order is not null");
            return (Criteria) this;
        }

        public Criteria andContentOrderEqualTo(Integer value) {
            addCriterion("content_order =", value, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderNotEqualTo(Integer value) {
            addCriterion("content_order <>", value, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderGreaterThan(Integer value) {
            addCriterion("content_order >", value, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderGreaterThanOrEqualTo(Integer value) {
            addCriterion("content_order >=", value, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderLessThan(Integer value) {
            addCriterion("content_order <", value, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderLessThanOrEqualTo(Integer value) {
            addCriterion("content_order <=", value, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderIn(List<Integer> values) {
            addCriterion("content_order in", values, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderNotIn(List<Integer> values) {
            addCriterion("content_order not in", values, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderBetween(Integer value1, Integer value2) {
            addCriterion("content_order between", value1, value2, "contentOrder");
            return (Criteria) this;
        }

        public Criteria andContentOrderNotBetween(Integer value1, Integer value2) {
            addCriterion("content_order not between", value1, value2, "contentOrder");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}