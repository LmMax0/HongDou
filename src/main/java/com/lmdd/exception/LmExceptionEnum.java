package com.lmdd.exception;

/** 自定义的异常枚举类
 * @author LM_MAX
 * @date 2022/2/22
 */
public enum LmExceptionEnum {
    /**
     * 用户名不能为空
     */
    NEED_USER_NAME(10001,"用户名不能为空"),
    /**
     * 密码不能为空
     */
    NEED_PASSWORD(10002,"密码不能为空"),
    USER_NAME_EXISTED(10003,"用户名已经存在"),
    INSERT_USER_FILED(10004,"插入用户失败"),
    SYSTEM_ERROR(20000,"系统异常");

    /**
     * 异常码
     */
    private Integer code;
    /**
     * 异常信息
     */
    private String msg;

    LmExceptionEnum() {
    }

    LmExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
