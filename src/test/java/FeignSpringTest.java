import feign.Feign;
import feign.FeignException;
import feign.form.FormEncoder;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import test.FeignConfiguration;
import test.bean.FaceRequestBean;
import test.bean.TestFeignRequestBean;
import test.bean.TestRequest;
import test.feignService.UserFigureFallbackFactory;
import test.feignService.UserFigureService;
import test.feignService.UserFigureService2;
import test.util.JsonHelper;

import java.lang.reflect.Field;
import java.util.Collections;

/**
 * Created by zhouyongbo on 2019/9/20.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FeignConfiguration.class})
@TestPropertySource("classpath:application.properties")
public class FeignSpringTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserFigureService userFigureService;

    @Test
    public void testSendRequestWithJson() throws Exception {
        TestRequest request = JsonHelper.GetObjByJson("{\n" +
                "\t\"bodyModelVersion\": 1.0,\n" +
                "\t\"faceModelVersion\": 1.0,\n" +
                "\t\"categoryId\": 5,\n" +
                "\t\"gender\": 2\n" +
                "}", TestRequest.class);
        TestFeignRequestBean result  = userFigureService.listHeadCategorys(request);
        String result2  = userFigureService.listHeadCategorys2(request);
        System.out.println(result);
        System.out.println(result2);
    }


    @Test
    public void testSendRequestWithFormData() throws Exception {
        TestFeignRequestBean bean = new TestFeignRequestBean();
        bean.setAge(111);
        bean.setNickName("joy");
        String result = userFigureService.test(bean);
        System.out.println("11111:"+result);
    }

    @Test
    public  void testFace() throws Exception {
        UserFigureService2 userFigureService = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new FormEncoder(new JacksonEncoder()))
                .target(UserFigureService2.class, "https://api-cn.faceplusplus.com");
        FaceRequestBean request = new FaceRequestBean();
        String result = userFigureService.test(request);
        System.out.println(result);
    }


    @Test
    public  void testFeign() throws Exception {
        UserFigureService userFigureService = HystrixFeign.builder()
                .client(new OkHttpClient())
                .encoder(new FormEncoder(new JacksonEncoder()))
                .target(UserFigureService.class, "https://a.j.cn",new UserFigureFallbackFactory());
        TestRequest request = JsonHelper.GetObjByJson("{\n" +
                "\t\"bodyModelVersion\": 1.0,\n" +
                "\t\"faceModelVersion\": 1.0,\n" +
                "\t\"categoryId\": 5,\n" +
                "\t\"gender\": 2\n" +
                "}", TestRequest.class);
        //userFigureService.listHeadCategorys(request)
        //System.out.println("result"+result);
    }


    FallbackFactory<UserFigureService> fallbackFactory = new FallbackFactory<UserFigureService>() {
        @Override
        public UserFigureService create(Throwable throwable) {
           return new UserFigureService() {
               @Override
               public TestFeignRequestBean listHeadCategorys(TestRequest params) {
                   return null;
               }

               @Override
               public String test(TestFeignRequestBean request) {
                   return null;
               }

               @Override
               public String listHeadCategorys2(TestRequest params) {
                   return null;
               }
           };
        }
    };

}