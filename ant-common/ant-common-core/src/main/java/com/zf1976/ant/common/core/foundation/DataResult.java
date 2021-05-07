package com.zf1976.ant.common.core.foundation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.power.common.util.DateTimeUtil;
import com.zf1976.ant.common.core.util.RequestUtils;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * 请求响应对象
 *
 * @author ant
 * @since 2020/5/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@SuppressWarnings("rawtypes")
public class DataResult<T> implements Serializable {

    /**
     * 响应是否成功
     */
    private Boolean success;

    /**
     * 响应码
     */
    private Integer status;

    /**
     * 错误代码
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int errCode;

    /**
     * 错误消息
     */
    private String errMsg;

    /**
     * 正常消息
     */
    private String message;

    /**
     * 错误详情
     */
    private String errDetail;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 处理时间
     */
    private String timestamp;

    /**
     * 路径
     */
    private String path;


    /**
     * 响应带数据的成功消息
     *
     * @param message 消息
     * @param <E>  E
     * @return 响应对象
     */
    public static <E> DataResult<E> success(String message) {
        DataResult<E> vo = new DataResult<>();
        vo.setMessage(message);
        vo.setSuccess(true);
        vo.setStatus(200);
        vo.setPath(getUri());
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 响应带数据的成功消息
     *
     * @param data 数据
     * @param <E>  E
     * @return 响应对象
     */
    public static <E> DataResult<E> success(E data) {
        DataResult<E> vo = new DataResult<>();
        vo.setData(data);
        vo.setSuccess(true);
        vo.setStatus(200);
        vo.setPath(getUri());
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 响应成功消息
     *
     * @param <E> E
     * @param sign param
     * @return 响应对象
     */
    public static <E> DataResult<E> success(@Nullable Void sign) {
        DataResult<E> vo = new DataResult<>();
        vo.setSuccess(true);
        vo.setStatus(200);
        vo.setPath(getUri());
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 响应成功消息
     *
     * @param <E> E
     * @return 响应对象
     */
    public static <E> DataResult<E> success() {
        DataResult<E> vo = new DataResult<>();
        vo.setSuccess(true);
        vo.setStatus(200);
        vo.setPath(getUri());
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 返回失败消息
     * @return 响应对象
     */
    public static <E> DataResult fail() {
        return fail((String) null);
    }

    /**
     * 返回失败消息
     * @param errMsg 失败消息
     * @return 响应对象
     */
    public static <E> DataResult fail(String errMsg) {
        return fail(500, errMsg);
    }

    /**
     * 返回失败消息
     * @param errMsg 错误消息
     * @param errCode 错误码
     * @return 响应对象
     */
    public static <E> DataResult fail(int errCode, String errMsg) {
        DataResult<E> vo = new DataResult<>();
        vo.setSuccess(false);
        vo.setErrCode(errCode);
        vo.setErrMsg(errMsg);
        vo.setData(null);
        vo.setPath(getUri());
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 返回失败消息
     *
     * @param httpStatus status
     * @return /
     */
    public static <E> DataResult fail(HttpStatus httpStatus) {
        DataResult<E> vo = new DataResult<>();
        vo.setSuccess(false);
        vo.setErrCode(httpStatus.value());
        vo.setErrMsg(httpStatus.getReasonPhrase());
        vo.setData(null);
        vo.setPath(getUri());
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 获取请求路径
     *
     * @return uri
     */
    public static String getUri() {
        return RequestUtils.getRequest().getRequestURI();
    }

}