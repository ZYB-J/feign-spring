package test;

import feign.Feign;
import feign.form.ContentType;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Configuration;
import test.feignService.UserFigureService;
import test.feignSpring.FeignScan;

/**
 * Created by zhouyongbo on 2019/9/9.
 */
@FeignScan(basePackages = "test.feignService")
@Configuration
public class FeignConfiguration {

}
