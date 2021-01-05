package test.feignSpring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhouyongbo on 2019/9/11.
 * 用于创建一个feign client 注解标识
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FeignClient {
    String url();
    Class<?> fallback() default void.class;
    Class<?> fallbackFactory() default void.class;

    /**
     * multiple http status code
     * delimiter comma ,for example: 403,500,321
     * @return
     */
    String retryStatusCode() default "";
}
