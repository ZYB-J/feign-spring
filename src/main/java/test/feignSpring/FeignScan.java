package test.feignSpring;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模拟MapperScan Mybatis-Spring中来实现
 * Created by zhouyongbo on 2019/9/11.
 * 自定义注解 demo:
 * @FeignScan(basePackages = "test.feignService")
 * @Component //必须的
 * public class TestFeign {
 *
 * }
 * {@link Import} 相当于导入一个配置类，用于查找@{@link FeignClient}注解
 * {@link org.springframework.beans.factory.config.BeanDefinition}的入口
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(FeignScannerRegistrar.class)
public @interface FeignScan {
    /**
     * basePackages 下查找{@link org.springframework.beans.factory.config.BeanDefinition}
     * @return
     */
    String[] basePackages() default {};
}
