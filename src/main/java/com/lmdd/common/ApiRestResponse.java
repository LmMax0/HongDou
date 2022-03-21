package com.lmdd.common;

import com.lmdd.exception.LmExceptionEnum;

/**
 * 返回通用对象
 * @author LM_MAX
 * @date 2022/2/22
 */
public class ApiRestResponse<T> {
    private Integer status;

    private String msg;

    private T data;

    private static final int OK_CODE = 10000;

    private static final String OK_MSG = "SUCCESS";

    public ApiRestResponse() {
        this(OK_CODE,OK_MSG);
    }

    public ApiRestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ApiRestResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static int getOkCode() {
        return OK_CODE;
    }

    public static String getOkMsg() {
        return OK_MSG;
    }

    @Override
    public String toString() {
        return "ApiRestResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static <T> ApiRestResponse<T> success(){
        return new ApiRestResponse<>();
    }

    public static <T>  ApiRestResponse<T> success(T result){
        ApiRestResponse<T> restResponse = new ApiRestResponse<>();
        restResponse.setData(result);
        return restResponse;
    }

    public static <T> ApiRestResponse<T> error(Integer code, String msg){
        return new ApiRestResponse<>(code,msg);
    }

    public static <T> ApiRestResponse<T> error(LmExceptionEnum ex){
        return new ApiRestResponse<>(ex.getCode(),ex.getMsg());
    }
}
