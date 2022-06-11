package com.wxh.wiki.util;

import java.io.Serializable;

/**
 * @author wxh
 * @date 2022-06-11 13:05
 * 上下文
 */
public class RequestContext implements Serializable {

    /**
     * 对线程本地变量的封装，方便对线程本地变量赋值或取值
     * 线程本地变量，只在当前线程有效，线程之间不会干扰。定义一个远程地址
     * 整个执行的路径，可以看作是一条线
     * 在接口入口获取IP，并给线程本地变量赋值；在中间如service层，获取线程本地变量IP
     */
    private static ThreadLocal<String> remoteAddr = new ThreadLocal<>();

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }

}
