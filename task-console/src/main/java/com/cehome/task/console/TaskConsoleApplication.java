package com.cehome.task.console;

import com.cehome.task.annotation.EnableTimeTaskClient;
import com.cehome.task.annotation.EnableTimeTaskConsole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@RestController
@EnableTimeTaskConsole
@EnableTimeTaskClient
public class TaskConsoleApplication {
    @Value("${language:en}")
    String language;
    public static void main(String[] args) {
        SpringApplication.run(TaskConsoleApplication.class, args);
    }

    @RequestMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("timeTask/list.htm");
    }


    @Bean
    public FilterRegistrationBean createFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Filter() {


            @Override
            public void init(FilterConfig filterConfig) throws ServletException {

            }

            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                servletRequest.setAttribute("language", language);
                filterChain.doFilter(servletRequest, servletResponse);
            }

            @Override
            public void destroy() {

            }
        });
        registration.addUrlPatterns("/*");
        //registration.setName("MyFilter");
        // registration.setOrder(1);
        return registration;
    }
}
