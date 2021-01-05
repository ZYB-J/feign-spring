package test.feignService;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import test.bean.TestFeignRequestBean;
import test.bean.TestRequest;

import javax.annotation.Resource;

/**
 * Created by zhouyongbo on 2019/10/15.
 */
@Component
@Slf4j
public class UserFigureFallbackFactory implements FallbackFactory<UserFigureService> {

    @Override
    public UserFigureService create(Throwable throwable) {
        log.error("fallbackFactory is:",throwable);
        return new UserFigureService() {
            @Override
            public TestFeignRequestBean listHeadCategorys(TestRequest params) {
                return null;
            }

            @Override
            public String test(TestFeignRequestBean request) {
                return "shit";
            }

            @Override
            public String listHeadCategorys2(TestRequest params) {
                return null;
            }
        };
    }

}
