package cn.smallyoung.commonsecurityspringbootstarter.util.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author smallyoung
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysResult<T> implements Serializable {

    private static final long serialVersionUID = -3937755350175676600L;

    private Integer code;

    private String message;

    private T data;

    private SysResult() {
    }

    private SysResult(SysResultStatus resultStatus, T data) {
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
        this.data = data;
    }

    private SysResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> SysResult<T> result(SysResultStatus resultStatus, T data) {
        return new SysResult<>(resultStatus, data);
    }

    public static SysResult<Void> success() {
        return result(SysResultStatus.SUCCESS, null);
    }

    public static <T> SysResult<T> success(T data) {
        return result(SysResultStatus.SUCCESS, data);
    }

    public static SysResult<Void> failure() {
        return result(SysResultStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> SysResult<T> failure(String msg) {
        return new SysResult<>(SysResultStatus.INTERNAL_SERVER_ERROR.getCode(), msg, null);
    }


}
