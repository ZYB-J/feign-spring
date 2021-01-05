package test.feignSpring;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

import java.util.Set;

import static feign.FeignException.errorStatus;

/**
 * Created by zhouyongbo on 2019/11/27.
 */
public class CommonErrorDecoder implements ErrorDecoder {

    private Set<Integer> statusCodeSet;
    private boolean isMatchStatusCode = true;

    public CommonErrorDecoder(Set<Integer> statusCodeSet) {
        this.statusCodeSet = statusCodeSet;
        this.isMatchStatusCode = true;
    }


    public CommonErrorDecoder() {
        this.isMatchStatusCode = false;
    }


    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = errorStatus(methodKey, response);
        if(isMatchStatusCode){
            int status = response.status();
            if (statusCodeSet.contains(status)) {//状态码重试
                return new RetryableException(
                        response.status(),
                        exception.getMessage(),
                        response.request().httpMethod(),
                        exception,
                        null,
                        response.request());
            }
        }
        return exception;
    }
}
