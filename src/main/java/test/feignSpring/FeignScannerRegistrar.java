package test.feignSpring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyongbo on 2019/9/11.
 * 查看{@link FeignScan}
 *
 */
public class FeignScannerRegistrar implements ImportBeanDefinitionRegistrar , EnvironmentAware {
    private Class<? extends Annotation> annotationClass=FeignClient.class ;//只扫描带此注解的接口


    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(FeignScan.class.getName()));
        ClasspathFeignScanner scanner = new ClasspathFeignScanner(registry);
        scanner.setAnnotationClass(annotationClass);
        scanner.setEnvironment(environment);

        String[] var1 = annoAttrs.getStringArray("basePackages");
        List<String> basePackages = new ArrayList();
        for(String pkg :var1){
            if(StringUtils.hasText(pkg)){
                basePackages.add(pkg);
            }
        }
        scanner.registerFilters();//注册过滤器。用户过滤查找到的候选类
        /**
         * 扫描basePackages的包下的类，注册为{@link org.springframework.beans.factory.config.BeanDefinition}
         */
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
