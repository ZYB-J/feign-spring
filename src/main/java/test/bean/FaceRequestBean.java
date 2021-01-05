package test.bean;

import lombok.Data;

/**
 * Created by zhouyongbo on 2019/9/20.
 */
@Data
public class FaceRequestBean {
     private String api_key="";
     private String api_secret="";
     private String return_landmark = "";
     private String image_base64="";

}
