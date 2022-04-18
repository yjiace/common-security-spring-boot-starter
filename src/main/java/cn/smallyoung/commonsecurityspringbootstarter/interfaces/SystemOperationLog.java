package cn.smallyoung.commonsecurityspringbootstarter.interfaces;


import cn.smallyoung.commonsecurityspringbootstarter.base.BaseService;
import cn.smallyoung.commonsecurityspringbootstarter.enums.SysOperationLogWayEnum;

import java.lang.annotation.*;

/**
 * 记录编辑详细信息的标注
 *
 * @author smallyoung
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemOperationLog {

    /**
     * 查询模块
     */
    String module() default "";

    /**
     * 查询模块名称
     */
    String methods() default "";

    /**
     * 查询的bean名称
     */
    Class<?> serviceClass() default BaseService.class;

    /**
     * 查询单个详情的bean的方法
     */
    String queryMethod() default "";

    /**
     * 查询详情的参数类型
     */
    Class<?> parameterType() default Object.class;

    /**
     * 从页面参数中解析出要查询的id，
     * 如域名修改中要从参数中获取customerDomainId的值进行查询
     */
    String parameterKey() default "";

    /**
     * 操作方式，
     * RecordBeforeAndAfterChanges：记录变化；
     * RecordOnly：仅记录；
     * UserAfter：使用变化后的返回值；
     */
    SysOperationLogWayEnum way() default SysOperationLogWayEnum.RecordBeforeAndAfterChanges;
}
