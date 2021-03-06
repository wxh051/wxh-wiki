package com.wxh.wiki.config;

import com.wxh.wiki.interceptor.ActionInterceptor;
import com.wxh.wiki.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wxh
 * @date 2022-06-05 13:10
 *
 * 拦截类里定义了拦截器，在SpringMvcConfig 配置，让这个拦截器生效。
 * */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor loginInterceptor;

    @Autowired
    ActionInterceptor actionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                //不需要拦截的过滤掉
                .excludePathPatterns(
                        "/test/**",
                        "/redis/**",
                        "/user/login",
                        "/category/all",
                        "/ebook/list",
                        "/doc/all/**",
                        "/doc/vote/**",
                        "/doc/find-content/**",
                        "/ebook-snapshot/**",
                        "/ebook/upload/avatar",
                        "/file/**"
                );

        registry.addInterceptor(actionInterceptor)
                .addPathPatterns(
                        "/*/save",
                        "/*/delete/**",
                        "/*/reset-password");
    }

    //对静态资源的处理，所有访问/file的路径，都定位到D盘这个目录下
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/**").addResourceLocations("file:D:/Java/project/wiki/avatar/");
        // registry.addResourceHandler("/file/**").addResourceLocations("file:/root/file/wiki/");
    }
}
