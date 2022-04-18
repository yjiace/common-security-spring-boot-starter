package cn.smallyoung.commonsecurityspringbootstarter.component;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.smallyoung.commonsecurityspringbootstarter.util.result.SysResultStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 当访问接口没有权限时，自定义的返回结果
 *
 * @author smallyoung
 * @data 2020/10/31
 */
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Value("${spring.application.name:''}")
    private String applicationName;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException e) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(JSONUtil.parse(Dict.create().set("source", applicationName)
                .set("path", request.getRequestURI()).set("timestamp", LocalDateTime.now())
                .set("code", SysResultStatus.FORBIDDEN).set("message", e.getMessage())));
        response.getWriter().flush();
    }
}
