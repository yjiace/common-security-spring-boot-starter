package cn.smallyoung.commonsecurityspringbootstarter;

import cn.smallyoung.commonsecurityspringbootstarter.component.RestAccessDeniedHandler;
import cn.smallyoung.commonsecurityspringbootstarter.component.RestAuthenticationEntryPoint;
import cn.smallyoung.commonsecurityspringbootstarter.component.RestLogoutHandler;
import cn.smallyoung.commonsecurityspringbootstarter.config.SpringSecurityAuditorAware;
import cn.smallyoung.commonsecurityspringbootstarter.config.WebSecurityConfig;
import cn.smallyoung.commonsecurityspringbootstarter.filter.JwtAuthenticationTokenFilter;
import cn.smallyoung.commonsecurityspringbootstarter.util.JwtConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * jwt 相关的自动装配
 * @author smallyoung
 * @date 2022/4/8
 */
@Slf4j
@Configuration
@ConditionalOnClass(SecurityAutoConfiguration.class)
@EnableConfigurationProperties(value = JwtConfig.class)
@Import({
        SpringSecurityAuditorAware.class
})
public class JwtAutoConfiguration {

    @Bean
    @ConditionalOnClass(UserDetailsService.class)
    public WebSecurityConfig webSecurityConfig(){
        return new WebSecurityConfig();
    }

    /**
     * 未登录或者token失效
     */
    @Bean
    @ConditionalOnClass({JwtConfig.class, WebSecurityConfig.class})
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    /**
     * 访问接口没有权限
     */
    @Bean
    @ConditionalOnClass({JwtConfig.class, WebSecurityConfig.class})
    public RestAccessDeniedHandler restAccessDeniedHandler() {
        return new RestAccessDeniedHandler();
    }

    /**
     * 退出成功处理器
     */
    @Bean
    @ConditionalOnClass({JwtConfig.class, WebSecurityConfig.class})
    public RestLogoutHandler restLogoutHandler() {
        return new RestLogoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnClass({JwtConfig.class, WebSecurityConfig.class})
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(){
        return new JwtAuthenticationTokenFilter();
    }

}
