package cn.smallyoung.commonsecurityspringbootstarter.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author SmallYoung
 * @date 2021/12/15
 */
@Data
@Configuration
@ConfigurationProperties("jwt")
public class JwtConfig {

    private Boolean sso = false;

    private String tokenHead = "token";

    private String redisKey = "token_key_";

    private Integer expiration;

    private String publicKey;

    private String privateKey;

    private List<String> authcUrl;

}
