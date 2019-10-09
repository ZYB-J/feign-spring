package test.feignSpring;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyongbo on 2019/9/11.
 * 根据{@link FeignScan} 注解属性basePackages  扫描自定义注解{@link FeignClient}注释的接口 注册为{@link BeanDefinition}
 */

public class ClasspathFeignScanner extends ClassPathBeanDefinitionScanner {

    /**
     * 过滤不是没有被FeignClient 注解作用的接口
     */
    private Class<? extends Annotation> annotationClass ;

    private FeignClientFactoryBean feignClientFactoryBean = new FeignClientFactoryBean();

    public ClasspathFeignScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }


    public void registerFilters() {

        if (this.annotationClass != null) {
            this.addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
        }else{
            throw new IllegalArgumentException("feign标记接口为空:FeignClient");
        }
        addIncludeFilter(new AnnotationTypeFilter(FeignScan.class));


    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {

        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if(beanDefinitions.isEmpty()){
            logger.warn("no feign client definition");
        }else{
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }



    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        //GenericBeanDefinition definition;
        //AnnotatedBeanDefinition definition;
        ScannedGenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            BeanDefinition beanDefinition = holder.getBeanDefinition();
            if(beanDefinition instanceof AnnotatedBeanDefinition){
                logger.info("11111111111111111111111");
            }
            definition = (ScannedGenericBeanDefinition) holder.getBeanDefinition();

            if (logger.isDebugEnabled()) {
                logger.debug("Creating FeignClientFactoryBean with name '" + holder.getBeanName()
                        + "' and '" + definition.getBeanClassName() + "' mapperInterface");
            }
            AnnotationMetadata metadata = definition.getMetadata();
            System.out.println(FeignClient.class.getCanonicalName());
            System.out.println(FeignClient.class.getName());
            Map<String, Object> attributes = metadata.getAnnotationAttributes(FeignClient.class.getCanonicalName());
            String url = getUrl(attributes);
            logger.info("exec url:"+url);
            // the mapper interface is the original class of the bean
            // but, the actual class of the bean is MapperFactoryBean
            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); // 把注解的类名注入到feignClientFactoryBean 的构造方法
            definition.setBeanClass(this.feignClientFactoryBean.getClass());
            definition.getPropertyValues().add("url", url);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping MapperFactoryBean with name '" + beanName
                    + "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }


    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }


    private String resolve(String value) {
        Environment environment = getEnvironment();
        if (StringUtils.hasText(value)) {
            return environment.resolvePlaceholders(value);
        }
        return value;
    }

    private String getUrl(Map<String, Object> attributes) {
        String url = resolve((String) attributes.get("url"));
        if (StringUtils.hasText(url) && !(url.startsWith("${") && url.contains("}"))) {
            if (!url.contains("://")) {
                url = "http://" + url;
            }
            try {
                new URL(url);
            }
            catch (MalformedURLException e) {
                throw new IllegalArgumentException(url + " is malformed", e);
            }
        }
        return url;
    }
}
