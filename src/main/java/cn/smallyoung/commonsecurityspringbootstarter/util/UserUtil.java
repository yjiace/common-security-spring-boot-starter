package cn.smallyoung.commonsecurityspringbootstarter.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 获取当前的登录用户id
 * @author SmallYoung
 * @date 2021/10/25
 */
@Slf4j
public class UserUtil {

    public static String getCurrentAuditor(){
        try {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return authentication != null ? authentication.getName() : "-1";
        }catch (Exception e){
            return "-1";
        }
    }
}
