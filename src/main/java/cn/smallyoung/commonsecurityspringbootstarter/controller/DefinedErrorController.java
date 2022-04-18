package cn.smallyoung.commonsecurityspringbootstarter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SmallYoung
 * @date 2021/11/19
 */
@Slf4j
@RestController
public class DefinedErrorController extends AbstractErrorController {

    @Value("${spring.application.name:''}")
    private String applicationName;

    public DefinedErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }


//    @RequestMapping(value = "/error")
//    public Dict myError(HttpServletRequest request) {
//        Map<String, Object> errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE));
//        return Dict.create().set("code", 404).set("message", "Not Found").set("timestamp", LocalDateTime.now())
//                .set("path", errorAttributes.get("path")).set("source", applicationName);
//    }

}
