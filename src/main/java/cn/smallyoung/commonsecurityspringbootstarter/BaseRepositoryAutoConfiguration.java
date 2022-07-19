package cn.smallyoung.commonsecurityspringbootstarter;

import cn.smallyoung.commonsecurityspringbootstarter.aspect.SysOperationLogAspect;
import cn.smallyoung.commonsecurityspringbootstarter.base.BaseRepository;
import cn.smallyoung.commonsecurityspringbootstarter.controller.SysOperationLogController;
import cn.smallyoung.commonsecurityspringbootstarter.service.SysOperationLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * hibernate 自动装配
 *
 * @author smallyoung
 * @date 2022/4/8
 */
@Configuration
@ConditionalOnClass(BaseRepository.class)
@Import({
        SysOperationLogAspect.class,
        SysOperationLogService.class,
        SysOperationLogController.class
})
public class BaseRepositoryAutoConfiguration {

}
