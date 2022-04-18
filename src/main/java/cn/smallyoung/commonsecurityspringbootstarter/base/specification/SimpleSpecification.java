package cn.smallyoung.commonsecurityspringbootstarter.base.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * 描述:
 * 创建SimpleSpecification来实现Specification接口，
 * 并且根据条件生成Specification对象，因为在最后查询的时候需要这个对象
 * SimpleSpecification是核心类型，
 * 用来根据条件生成Specification对象，这个SimpleSpecification直接存储了具体的查询条件。
 */

public class SimpleSpecification<T> implements Specification<T> {

    private static final long serialVersionUID = 9101383631123058909L;
    /**
     * 查询的条件列表，是一组列表
     */
    private final List<SpecificationOperator> operators;

    SimpleSpecification(List<SpecificationOperator> operators) {
        this.operators = operators;
    }

    @Override
    public Predicate toPredicate(@Nullable Root<T> root, @Nullable CriteriaQuery<?> criteriaQuery, @Nullable CriteriaBuilder criteriaBuilder) {
        Predicate resultPre = null;
        if (operators != null) {
            Map<String, List<SpecificationOperator>> map = operators.stream().filter(s -> s != null && s.getOperator() != null).collect(Collectors.groupingBy(SpecificationOperator::getJoin));
            for (Map.Entry<String, List<SpecificationOperator>> entry : map.entrySet()) {
                Predicate predicate = null;
                for (SpecificationOperator so : entry.getValue()) {
                    Predicate p = so.getOperator().getCheckValue().test(so.getValue()) ? so.getOperator().getFun().apply(expression(root, criteriaQuery, so.getKey()), criteriaBuilder, so) : null;
                    if (p == null) {
                        continue;
                    }
                    if (entry.getKey().startsWith("AND")) {
                        predicate = (predicate != null && criteriaBuilder != null) ? criteriaBuilder.and(predicate, p) : p;
                    } else if (entry.getKey().startsWith("OR")) {
                        predicate = (predicate != null && criteriaBuilder != null) ? criteriaBuilder.or(predicate, p) : p;
                    }
                }
                if(predicate != null){
                    resultPre = ((resultPre != null && criteriaBuilder != null) ? criteriaBuilder.and(resultPre, predicate) : predicate);
                }
            }
        }
        return resultPre;
    }

    private Path<?> expression(@Nullable Root<T> root, @Nullable CriteriaQuery<?> criteriaQuery, String key) {
        if (root == null) {
            return null;
        }
        Path<?> expression;
        boolean distinct = false;
        String[] names = key.split("\\.");
        if (Collection.class.isAssignableFrom(root.get(names[0]).getJavaType())) {
            expression = root.join(names[0], JoinType.LEFT);
            distinct = true;
        } else {
            expression = root.get(names[0]);
        }
        for (int i = 1; i < names.length; i++) {
            if (Collection.class.isAssignableFrom(expression.get(names[i]).getJavaType())) {
                Join<?, ?> join = root.join(names[0], JoinType.LEFT);
                for (int j = 1; j < i; j++) {
                    join = join.join(names[j], JoinType.LEFT);
                }
                expression = join.join(names[i], JoinType.LEFT);
                distinct = true;
            } else {
                expression = expression.get(names[i]);
            }
        }
        if (criteriaQuery != null) {
            criteriaQuery.distinct(distinct);
        }
        return expression;
    }
}
