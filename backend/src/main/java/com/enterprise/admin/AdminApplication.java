package com.enterprise.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 企业管理后台系统启动类
 */
@SpringBootApplication
@MapperScan("com.enterprise.admin.mapper")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("\n" +
                "============================================\n" +
                "  企业管理后台系统启动成功！\n" +
                "  访问地址: http://localhost:8080/api\n" +
                "  默认账号: admin / 123456\n" +
                "============================================\n");
    }
}
