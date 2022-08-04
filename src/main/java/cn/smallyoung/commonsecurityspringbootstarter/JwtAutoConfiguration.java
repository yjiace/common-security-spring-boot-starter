package cn.smallyoung.commonsecurityspringbootstarter;

import cn.smallyoung.commonsecurityspringbootstarter.component.RestAccessDeniedHandler;
import cn.smallyoung.commonsecurityspringbootstarter.component.RestAuthenticationEntryPoint;
import cn.smallyoung.commonsecurityspringbootstarter.component.RestLogoutHandler;
import cn.smallyoung.commonsecurityspringbootstarter.config.SpringSecurityAuditorAware;
import cn.smallyoung.commonsecurityspringbootstarter.filter.JwtAuthenticationTokenFilter;
import cn.smallyoung.commonsecurityspringbootstarter.util.JwtConfig;
import cn.smallyoung.commonsecurityspringbootstarter.util.JwtTokenUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * jwt 相关的自动装配
 *
 * @author smallyoung
 * @date 2022/4/8
 */
@Log4j2
@Configuration
@ConditionalOnClass(SecurityAutoConfiguration.class)
@EnableConfigurationProperties(value = JwtConfig.class)
@Import({
        SpringSecurityAuditorAware.class
})
public class JwtAutoConfiguration {


    /**
     * 未登录或者token失效
     */
    @Bean
    @ConditionalOnClass({JwtConfig.class})
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    /**
     * 访问接口没有权限
     */
    @Bean
    @ConditionalOnClass({JwtConfig.class})
    public RestAccessDeniedHandler restAccessDeniedHandler() {
        return new RestAccessDeniedHandler();
    }

    /**
     * 退出成功处理器
     */
    @Bean
    @ConditionalOnClass({JwtConfig.class})
    public RestLogoutHandler restLogoutHandler() {
        return new RestLogoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnClass({JwtConfig.class})
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    @ConditionalOnClass({JwtConfig.class})
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }
}
