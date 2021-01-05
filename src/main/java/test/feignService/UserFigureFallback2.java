package test.feignService;

import org.springframework.stereotype.Component;
import test.bean.TestFeignRequestBean;
import test.bean.TestRequest;

/**
 * Created by zhouyongbo on 2019/10/15.
 */
@Component
public class UserFigureFallback2 implements UserFigureService {
    @Override
    public TestFeignRequestBean listHeadCategorys(TestRequest params) {
        return null;
    }

    @Override
    public String test(TestFeignRequestBean request) {
        return "222222";
    }

    @Override
    public String listHeadCategorys2(TestRequest params) {
        return null;
    }
}
