package cn.smallyoung.commonsecurityspringbootstarter.util;

import org.springframework.util.AntPathMatcher;

/**
 * @author SmallYoung
 * @date 2021/12/15
 */
public class AntPathMatcherUtil {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    public static boolean matches(String pattern, String path){
        return MATCHER.match(pattern,path);
    }
}
