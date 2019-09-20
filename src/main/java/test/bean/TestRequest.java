package test.bean;

import lombok.Data;

/**
 * Created by zhouyongbo on 2019/9/9.
 */
@Data
public class TestRequest {
    private int gender;
    private long categoryId;
    private double bodyModelVersion;
    private double faceModelVersion;
}
