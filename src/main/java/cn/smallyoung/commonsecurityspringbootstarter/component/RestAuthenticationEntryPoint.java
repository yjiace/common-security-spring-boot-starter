package cn.smallyoung.commonsecurityspringbootstarter.component;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.smallyoung.commonsecurityspringbootstarter.util.result.SysResultStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 当未登录或者token失效访问接口时，自定义的返回结果
 *
 * @author smallyoung
 * @data 2020/10/31
 */

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Value("${spring.application.name:''}")
    private String applicationName;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(JSONUtil.parse(Dict.create().set("source", applicationName)
                .set("path", request.getRequestURI()).set("timestamp", LocalDateTime.now())
                .set("code", SysResultStatus.UNAUTHORIZED.getCode()).set("message", authException.getMessage())));
        response.getWriter().flush();
    }
}
