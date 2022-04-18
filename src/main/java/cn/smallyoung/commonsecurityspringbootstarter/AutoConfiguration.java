package cn.smallyoung.commonsecurityspringbootstarter;

import cn.smallyoung.commonsecurityspringbootstarter.config.LocalDateTimeSerializerConfig;
import cn.smallyoung.commonsecurityspringbootstarter.controller.DefinedErrorController;
import cn.smallyoung.commonsecurityspringbootstarter.controller.LoggerController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 初始化自动装配
 *
 * @author smallyoung
 * @date 2022/4/8
 */
@Configuration
@ComponentScan("cn.smallyoung.commonsecurityspringbootstarter.component")
@Import({
        BaseRepositoryAutoConfiguration.class,
        JwtAutoConfiguration.class,
        LoggerController.class,
        DefinedErrorController.class,
        LocalDateTimeSerializerConfig.class
})
public class AutoConfiguration {

}
