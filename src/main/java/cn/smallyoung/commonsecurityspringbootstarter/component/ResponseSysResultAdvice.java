package cn.smallyoung.commonsecurityspringbootstarter.component;

import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import cn.smallyoung.commonsecurityspringbootstarter.interfaces.ResponseSysResult;
import cn.smallyoung.commonsecurityspringbootstarter.util.result.SysResult;
import cn.smallyoung.commonsecurityspringbootstarter.util.result.SysResultStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 */
@Log4j2
@RestControllerAdvice
public class ResponseSysResultAdvice implements ResponseBodyAdvice<Object> {

    @Value("${spring.application.name:''}")
    private String applicationName;

    private static final Class<? extends Annotation> ANNOTATION_TYPE = ResponseSysResult.class;

    @Override
    public boolean supports(MethodParameter returnType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ANNOTATION_TYPE) || returnType.hasMethodAnnotation(ANNOTATION_TYPE);
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, @Nullable MethodParameter returnType,  @Nullable MediaType selectedContentType,
                                  @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response) {
        if (body instanceof String) {
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(SysResult.success(body));
        }
        if (returnType != null && returnType.getMethod() != null
                && "java.lang.String".equals(returnType.getMethod().getReturnType().getName())) {
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(SysResult.success(body));
        }
        if (body instanceof SysResult) {
            return body;
        }
        return SysResult.success(body);
    }

    /**
     * 异常处理
     */
    @ExceptionHandler(value = Exception.class)
    public Dict handler(HttpServletRequest request, Exception e) {
        log.error("错误信息：",e);
        return Dict.create().set("source", applicationName)
                .set("path", request.getRequestURI()).set("timestamp", LocalDateTime.now())
                .set("code", SysResultStatus.INTERNAL_SERVER_ERROR.getCode()).set("message", e.getMessage());
    }

}
