package com.wxh.wiki.util;

import com.wxh.wiki.resp.UserLoginResp;

import java.io.Serializable;

/**
 * @author wxh
 * @date 2022-06-13 22:51
 */
public class LoginUserContext implements Serializable {

    private static ThreadLocal<UserLoginResp> user = new ThreadLocal<>();

    public static UserLoginResp getUser() {
        return user.get();
    }

    public static void setUser(UserLoginResp user) {
        LoginUserContext.user.set(user);
    }
}
