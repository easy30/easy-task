package com.cehome.task.console;

import com.cehome.task.annotation.EnableTimeTaskClient;
import com.cehome.task.annotation.EnableTimeTaskConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@RestController
@EnableTimeTaskConsole
@EnableTimeTaskClient
public class Application {
    public static void main(String[] args) {
        TableCreator.execute();
        SpringApplication.run(Application.class,args);
        System.out.println("ok");
    }

    @RequestMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("timeTask/list.htm");
    }
}
