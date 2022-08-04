package cn.smallyoung.commonsecurityspringbootstarter.component;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.smallyoung.commonsecurityspringbootstarter.util.JwtConfig;
import cn.smallyoung.commonsecurityspringbootstarter.util.UserUtil;
import cn.smallyoung.commonsecurityspringbootstarter.util.result.SysResultStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 退出成功处理器
 *
 * @author SmallYoung
 * @date 2021/2/3
 */
@Log4j2
public class RestLogoutHandler implements LogoutSuccessHandler {

    @Value("${spring.application.name:''}")
    private String applicationName;
    @Resource
    private JwtConfig jwtConfig;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String token = request.getHeader(jwtConfig.getTokenHead());
        if (token != null) {
            String username = UserUtil.getCurrentAuditor();
            if (username != null) {
                redisTemplate.delete(jwtConfig.getRedisKey() + applicationName + ":" + username);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().println(JSONUtil.parse(Dict.create().set("source", applicationName).set("timestamp", LocalDateTime.now())
                        .set("code", SysResultStatus.SUCCESS.getCode()).set("message", "退出成功")));
                response.getWriter().flush();
            }
        }
    }
}
