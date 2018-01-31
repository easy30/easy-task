package com.cehome.task.console;

import com.cehome.task.annotation.EnableTimeTaskClient;
import com.cehome.task.annotation.EnableTimeTaskConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
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
        SpringApplication.run(Application.class,args);
    }

    @RequestMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("timeTask/list.htm");
    }
}
