package cn.smallyoung.commonsecurityspringbootstarter.config;

import lombok.extern.slf4j.Slf4j;
import cn.smallyoung.commonsecurityspringbootstarter.util.UserUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author smallyoung
 */
@Slf4j
@Configuration
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(UserUtil.getCurrentAuditor());
    }

}
