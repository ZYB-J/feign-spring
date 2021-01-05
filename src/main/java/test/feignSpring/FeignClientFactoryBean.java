package test.feignSpring;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.form.FormEncoder;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashSet;

/**
 * Created by zhouyongbo on 2019/9/11.
 * {@link FeignClient} 注解接口的代理类
 */
public class FeignClientFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {


    public FeignClientFactoryBean() {

    }

    public FeignClientFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    private Class<T> mapperInterface;
    private String url;

    private Class<? extends T> fallback;
    private Class<? extends T> fallbackFactory;

    private ApplicationContext applicationContext;
    private String retryStatusCode;


    @Override
    public T getObject() {
        Feign.Builder builder;
        if (fallback != void.class || fallbackFactory != void.class) {
            builder = HystrixFeign.builder();
        } else {
            builder = Feign.builder();
        }
        builder.logger(new Slf4jLogger())
                .client(new OkHttpClient())
                .options(new Request.Options(10*1000,2*60*1000))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new CombineDecoder(new JacksonDecoder()));

        HashSet<Integer> statusCodeSet = new HashSet<>();
        if (StringUtils.isNotEmpty(retryStatusCode)) {//处理按状态码重试的情况
            String[] codes = retryStatusCode.split(",");
            for (String code : codes) {
                statusCodeSet.add(Integer.parseInt(code));
            }
            builder.retryer(new StatusCodeRetryer(statusCodeSet))
                    .errorDecoder(new CommonErrorDecoder(statusCodeSet));
        }else{
            builder.retryer(Retryer.NEVER_RETRY)
                    .errorDecoder(new CommonErrorDecoder());
        }
        return getTarget(builder);
    }


    private T getTarget(Feign.Builder builder) {
        if (builder instanceof HystrixFeign.Builder) {
            HystrixFeign.Builder hystrixFeignBuilder = (HystrixFeign.Builder) builder;
            if (fallback != void.class) {
                T fallbackObj = applicationContext.getBean(fallback);
                return hystrixFeignBuilder.target(this.mapperInterface, url, fallbackObj);
            } else {
                FallbackFactory<? extends T> fallbackFactoryObj = (FallbackFactory<? extends T>) applicationContext.getBean(fallbackFactory);
                return hystrixFeignBuilder.target(this.mapperInterface, url, fallbackFactoryObj);
            }
        } else {
            return builder.target(this.mapperInterface, url);
        }
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


    public Class<? extends T> getFallback() {
        return fallback;
    }

    public void setFallback(Class<? extends T> fallback) {
        this.fallback = fallback;
    }

    public Class<? extends T> getFallbackFactory() {
        return fallbackFactory;
    }

    public void setFallbackFactory(Class<? extends T> fallbackFactory) {
        this.fallbackFactory = fallbackFactory;
    }

    public String getRetryStatusCode() {
        return retryStatusCode;
    }

    public void setRetryStatusCode(String retryStatusCode) {
        this.retryStatusCode = retryStatusCode;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
