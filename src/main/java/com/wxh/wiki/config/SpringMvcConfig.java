/*
package com.wxh.wiki.config;

import com.wxh.wiki.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

*/
/**
 * @author wxh
 * @date 2022-06-05 13:10
 *
 * logInterceptor这个类定义了拦截器，在SpringMvcConfig 配置，让这个拦截器生效。
 *//*

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                //排除登录请求拦截
                .addPathPatterns("/**").excludePathPatterns("/login");
    }
}
*/
