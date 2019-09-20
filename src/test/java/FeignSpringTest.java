import feign.Feign;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import test.FeignConfiguration;
import test.bean.TestFeignRequestBean;
import test.bean.TestRequest;
import test.feignService.UserFigureService;
import test.util.JsonHelper;

/**
 * Created by zhouyongbo on 2019/9/20.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FeignConfiguration.class})
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
        String result = userFigureService.listHeadCategorys(request);
        System.out.println(result);
    }


    @Test
    public void testSendRequestWithFormData() throws Exception {
        TestFeignRequestBean bean = new TestFeignRequestBean();
        bean.setAge(111);
        bean.setNickName("joy");
        String result = userFigureService.test(bean);
        System.out.println(result);
    }

    @Test
    public  void testFeign() throws Exception {
        UserFigureService userFigureService = Feign.builder()
                .encoder(new FormEncoder(new JacksonEncoder()))
                .target(UserFigureService.class, "https://test.cn");
        TestRequest request = JsonHelper.GetObjByJson("{\n" +
                "\t\"bodyModelVersion\": 1.0,\n" +
                "\t\"faceModelVersion\": 1.0,\n" +
                "\t\"categoryId\": 5,\n" +
                "\t\"gender\": 2\n" +
                "}", TestRequest.class);
        String result = userFigureService.listHeadCategorys(request);
    }

}