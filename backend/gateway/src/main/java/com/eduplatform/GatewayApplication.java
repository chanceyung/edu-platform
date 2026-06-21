package com.eduplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧教学平台 - 聚合启动入口。
 * 扫描所有业务模块包（com.eduplatform.*）。
 * 严禁在启动类写业务逻辑。
 */
@SpringBootApplication(scanBasePackages = "com.eduplatform")
@MapperScan("com.eduplatform.**.mapper")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
