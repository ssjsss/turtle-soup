package com.turtlesoup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.turtlesoup.mapper")
public class TurtleSoupApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurtleSoupApplication.class, args);
    }
}
