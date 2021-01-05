package test;

import feign.Feign;
import feign.form.ContentType;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import test.feignService.UserFigureFallback;
import test.feignService.UserFigureService;
import test.feignSpring.FeignScan;

import javax.annotation.PostConstruct;

/**
 * Created by zhouyongbo on 2019/9/9.
 */
@FeignScan(basePackages = "test.feignService")
//@Configuration
@ComponentScan
public class FeignConfiguration {


}
