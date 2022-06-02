package com.wxh.wiki.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

/**
 * @author wxh
 * <p>
 * SpringBootApplication里面有@ComponentScan注解，用来扫描包，只会扫描这个类所在包下面的子包
 * 启动类放在config包里，需要手动设置一下ComponentScan注解。不要写com，会扫描到第三方jar，出错难以排查！！！
 * 支持扫描多个包（{，，}）
 */
@ComponentScan("com.wxh")
@MapperScan("com.wxh.wiki.mapper")
@SpringBootApplication
public class WikiApplication {

    private static final Logger LOG = LoggerFactory.getLogger(WikiApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WikiApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("地址：\thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }

}
