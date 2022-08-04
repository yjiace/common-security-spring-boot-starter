package cn.smallyoung.commonsecurityspringbootstarter.config;

import lombok.extern.log4j.Log4j2;
import cn.smallyoung.commonsecurityspringbootstarter.util.UserUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author smallyoung
 */
@Log4j2
@Configuration
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(UserUtil.getCurrentAuditor());
    }

}
