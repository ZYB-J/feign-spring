package test.feignSpring;

/**
 * Created by zhouyongbo on 2019/9/20.
 * 常用的content-type header
 */
public interface ContentType {
    String JSON = "Content-Type: application/json";
    String URLENCODED = "Content-Type: application/x-www-form-urlencoded";
    String MULTIPART = "Content-Type: multipart/form-data";
}
