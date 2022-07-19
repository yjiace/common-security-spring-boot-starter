package cn.smallyoung.commonsecurityspringbootstarter.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author smallyoung
 * @date 2022/7/19
 */
public class RequestUtils {

    private final static int IP_LENGTH = 15;

    private final static String LOCALHOST = "0:0:0:0:0:0:0:1";

    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static String getIp() {
        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            return getIp(request);
        }
        return null;
    }

    public static String getIp(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (isBlankIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (isBlankIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (isBlankIp(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (isBlankIp(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (isBlankIp(ip)) {
                ip = request.getRemoteAddr();
            }
            // 使用代理，则获取第一个IP地址
            if (!isBlankIp(ip) && ip.length() > IP_LENGTH) {
                ip = ip.split(",")[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (LOCALHOST.equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    private static boolean isBlankIp(String str) {
        return str == null || str.trim().isEmpty() || "unknown".equalsIgnoreCase(str);
    }
}
