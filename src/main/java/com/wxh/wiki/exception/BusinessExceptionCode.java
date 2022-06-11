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
    LOGIN_USER_ERROR("用户名不存在或密码错误"),
    VOTE_REPEAT("您已点赞过"),
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

