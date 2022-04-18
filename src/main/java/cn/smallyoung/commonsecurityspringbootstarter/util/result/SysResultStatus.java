package cn.smallyoung.commonsecurityspringbootstarter.util.result;

import lombok.Getter;

/**
 * @author smallyoung
 */

@Getter
public enum SysResultStatus {

    /**
     * 请求成功
     */
    SUCCESS(200, "OK"),

    /**
     * 400
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * 401
     */
    UNAUTHORIZED(401, "Unauthorized"),

    /**
     * 403
     */
    FORBIDDEN(403, "Forbidden"),


    /**
     * 系统错误
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");


    /** 业务异常码 */
    private final Integer code;
    /** 业务异常信息描述 */
    private final String message;

    SysResultStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
