package cn.smallyoung.commonsecurityspringbootstarter.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * @author SmallYoung
 * @date 2021/12/15
 */
@Data
@Configuration
@ConfigurationProperties("jwt")
public class JwtConfig {

    /**
     * 是否单点登录
     */
    private Boolean sso = false;

    /**
     * token请求头名称
     */
    private String tokenHead = "token";

    /**
     * redis中保存token的键名称
     */
    private String redisKey = "token_key_";

    /**
     * token过期时间（分钟）
     */
    private Integer expiration = 120;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 只要登录即可访问
     */
    private List<String> authcUrl = Arrays.asList("/logout", "/updatePassword");

    private String userName = "username";

    private String authorityName = "authorities";

    private String rememberName = "remember";

}
