package com.cehome.task.annotation;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({TimeTaskFactoryConfiguration.class,TimeTaskClientConfiguration.class})
@Documented
public @interface EnableTimeTaskClient {
}
