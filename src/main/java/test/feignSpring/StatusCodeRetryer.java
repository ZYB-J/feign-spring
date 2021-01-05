package test.feignSpring;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * Created by zhouyongbo on 2019/11/27.
 */
@Slf4j
public class StatusCodeRetryer extends Retryer.Default {
    private Set<Integer> statusCodeSet;
    public StatusCodeRetryer(Set<Integer> statusCodeSet){
        super(100,500,5);
        this.statusCodeSet = statusCodeSet;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        int status = e.status();
        log.info("response status is"+status);
        if(!statusCodeSet.contains(status)){
            throw  e;
        }
        super.continueOrPropagate(e);
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
