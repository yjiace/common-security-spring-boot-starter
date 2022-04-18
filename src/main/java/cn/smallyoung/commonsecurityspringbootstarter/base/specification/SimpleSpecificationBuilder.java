package cn.smallyoung.commonsecurityspringbootstarter.base.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 创建一个SimpleSpecificationBuilder来具体创建SimpleSpecification,
 * 这里为了方便调用简单进行了一下设计
 *
 * @author smallyoung
 * @create 2018-03-21 21:44
 */

public class SimpleSpecificationBuilder<T> {

    /**
     * 条件列表
     */
    private final List<SpecificationOperator> operators;

    /**
     * 构造，初始化
     * 根据Map封装Specification
     * 规定：Map键格式：join-operator-key了；例：AND_EQ_ID
     *
     * @param map 初始化条件
     */
    public SimpleSpecificationBuilder(Map<String, Object> map) {
        operators = new ArrayList<>();
        int keyLength = 3;
        for(Map.Entry<String, Object> entry : map.entrySet()){
            String[] keys = entry.getKey().split("_");
            if (keys.length == keyLength) {
                this.add(keys[2], map.get(entry.getKey()), SpecificationOperator.stringToOperator(keys[1]), keys[0]);
            }
        }
    }

    /**
     * 往list中填加条件
     *
     * @param key      键
     * @param operator 查询条件，如EQ
     * @param value    查询的值
     * @param join     查询方式 ，包括AND、OR
     * @return SimpleSpecificationBuilder
     */
    public SimpleSpecificationBuilder<T> add(String key, Object value, SpecificationOperator.Operator operator, String join) {
        SpecificationOperator so = new SpecificationOperator(key, value, operator, join);
        operators.add(so);
        return this;
    }

    /**
     * 触发SimpleSpecification并返回Specification
     */
    public Specification<T> getSpecification() {
        return new SimpleSpecification<>(operators);
    }
}
