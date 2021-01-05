package test.feignService;

import feign.Headers;
import feign.RequestLine;
import test.bean.TestFeignRequestBean;
import test.bean.TestRequest;
import test.feignSpring.ContentType;
import test.feignSpring.FeignClient;


/**
 * Created by zhouyongbo on 2019/9/9.
 */
@FeignClient(url="${url}",retryStatusCode = "500")
public interface UserFigureService {
    @RequestLine("POST /api/listHeadCategorys")
    //@Headers({ContentType.JSON})
    TestFeignRequestBean listHeadCategorys(TestRequest params);

    @RequestLine("POST /api/listHeadCategorys")
    @Headers(ContentType.URLENCODED)
    String test(TestFeignRequestBean request);


    @RequestLine("POST /api/listHeadCategorys")
    @Headers({ContentType.JSON})
    String listHeadCategorys2(TestRequest params);

}
