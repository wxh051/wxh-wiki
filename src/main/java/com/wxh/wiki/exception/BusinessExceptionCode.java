package com.wxh.wiki.exception;

/**
 * @author wxh
 * @date 2022-06-09 15:25
 */
public enum BusinessExceptionCode {

    /**
     * 登录名已存在
     */
    USER_LOGIN_NAME_EXIST("登录名已存在"),
    ;

    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

