package com.markerhub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.markerhub.mapper")   //第三步：开启mapper接口扫描
                                      //添加该包扫描
public class VueadminJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(VueadminJavaApplication.class, args);
    }

}
