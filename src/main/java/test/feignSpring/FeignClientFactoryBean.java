package test.feignSpring;

import feign.Feign;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by zhouyongbo on 2019/9/11.
 * {@link FeignClient} 注解接口的代理类
 */
public class FeignClientFactoryBean<T> implements FactoryBean<T> {



    public FeignClientFactoryBean(){

    }

    public FeignClientFactoryBean(Class<T> mapperInterface){
        this.mapperInterface = mapperInterface;
    }

    private Class<T> mapperInterface;
    private String url;



    @Override
    public T getObject(){
        //FeignClient annotation = AnnotationUtils.findAnnotation(this.mapperInterface, FeignClient.class);
        //String url = annotation.url();
        return Feign.builder()
                .logger(new Slf4jLogger())
                .encoder(new FormEncoder(new JacksonEncoder()))
                .target(this.mapperInterface, url);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
