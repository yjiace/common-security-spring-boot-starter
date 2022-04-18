package cn.smallyoung.commonsecurityspringbootstarter.base.specification;

import cn.hutool.core.convert.Convert;
import cn.smallyoung.commonsecurityspringbootstarter.funcion.Function3Parameter;
import lombok.Getter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 描述:
 *
 * @author smallyoung
 */
@Getter
public class SpecificationOperator {

    private final static String BETWEEN_SEPARATOR = "~";
    private final static String IN_SEPARATOR = ":";

    @Getter
    public enum Operator {
        /**
         * 相等
         */
        EQ(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.equal(expression, so.getValue())),
        /**
         * 不相等
         */
        NEQ(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.notEqual(expression, so.getValue())),
        /**
         * 空
         */
        NULL(o -> true, (expression, criteriaBuilder, so) -> criteriaBuilder.isNull(expression)),
        /**
         * 非空
         */
        NOTNULL(o -> true, (expression, criteriaBuilder, so) -> criteriaBuilder.isNotNull(expression)),
        /**
         * 模糊匹配
         **/
        LIKE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.like(expression, "%" + so.getValue() + "%")),
        /**
         * 左  模糊匹配
         **/
        LEFTLIKE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.like(expression, "%" + so.getValue())),
        /**
         * 右  模糊匹配
         **/
        RIGHTLIKE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.like(expression, so.getValue() + "%")),
        /**
         * 模糊查询 非
         */
        NOTLIKE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.notLike(expression, String.valueOf(so.getValue()))),
        /**
         * 大于
         **/
        GT(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.greaterThan(expression, (Comparable) so.getValue())),
        /**
         * 大于等于
         **/
        GE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.greaterThanOrEqualTo(expression, (Comparable) so.getValue())),
        /**
         * 小于
         **/
        LT(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.lessThan(expression, (Comparable) so.getValue())),
        /**
         * 小于等于
         **/
        LE(o -> !"".equals(o), (expression, criteriaBuilder, so) -> criteriaBuilder.lessThanOrEqualTo(expression, (Comparable) so.getValue())),
        /**
         * 区间
         **/
        BETWEEN(o -> !"".equals(o), (expression, criteriaBuilder, so) -> {
            if(so.getValue() instanceof Collection){
                List<?> list = Convert.convert(List.class, so.getValue());
                return criteriaBuilder.between(expression, (Comparable) list.get(0), (Comparable) list.get(1));
            }else if(String.valueOf(so.getValue()).split(BETWEEN_SEPARATOR).length == 2){
                String values = so.getValue().toString();
                Class<?> clazz = expression.getJavaType();
                return criteriaBuilder.between(expression, (Comparable) Convert.convert(clazz, values.split(BETWEEN_SEPARATOR)[0].trim()),
                        (Comparable) Convert.convert(clazz, values.split(BETWEEN_SEPARATOR)[1].trim()));
            }
            return null;
        }),
        /**
         * 包含
         */
        IN(o -> !"".equals(o), (expression, criteriaBuilder, so) -> {
            CriteriaBuilder.In in = criteriaBuilder.in(expression);
            if(so.getValue() instanceof Collection){
                Iterator iterator = ((Collection) so.getValue()).iterator();
                while (iterator.hasNext()){
                    in.value(iterator.next());
                }
            }else if (so.getValue() instanceof String){
                String inValues = so.getValue().toString();
                Class<?> clazz = expression.getJavaType();
                for (String str : inValues.split(IN_SEPARATOR)) {
                    in.value(Convert.convert(clazz, str));
                }
            }
            return in;
        });

        private final java.util.function.Predicate<Object> checkValue;

        private final Function3Parameter<Path, CriteriaBuilder, SpecificationOperator, Predicate> fun;

        Operator(java.util.function.Predicate<Object> checkValue, Function3Parameter<Path, CriteriaBuilder, SpecificationOperator, Predicate> fun) {
            this.checkValue = checkValue;
            this.fun = fun;
        }
    }

    /**
     * 连接的方式：and或者or
     */
    public String join;

    /**
     * 操作符的key，如查询时的name,id之类
     */
    private final String key;
    /**
     * 操作符的value，具体要查询的值
     */
    private final Object value;
    /**
     * 操作符，自己定义的一组操作符，用来方便查询
     */
    private final Operator operator;

    public SpecificationOperator(String key, Object value, Operator operator, String join) {
        this.key = key;
        this.value = value;
        this.operator = operator;
        this.join = join;
    }

    public static Operator stringToOperator(String data) {
        return Stream.of(Operator.values()).filter(operator -> operator.name().equals(data)).findFirst().orElse(Operator.EQ);
    }
}
