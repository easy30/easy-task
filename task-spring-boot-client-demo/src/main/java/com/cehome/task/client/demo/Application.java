package com.cehome.task.client.demo;

import com.cehome.task.annotation.EnableTimeTaskClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTimeTaskClient
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}
