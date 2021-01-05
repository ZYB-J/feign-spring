package test.feignService;

import feign.Headers;
import feign.RequestLine;
import test.bean.FaceRequestBean;
import test.feignSpring.ContentType;


/**
 * Created by zhouyongbo on 2019/9/9.
 */
public interface UserFigureService2 {
    @RequestLine("POST /facepp/v1/face/thousandlandmark")
    @Headers(ContentType.URLENCODED)
    String test(FaceRequestBean request);

}
