package test.meter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhouyongbo on 2019/12/19.
 */
@Component
public class RequestCounter {
    @Resource
    private MeterRegistry meterRegistry;

    private ConcurrentHashMap<String, Counter> totalCounterMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Counter> faildRepCounterMap = new ConcurrentHashMap<>();


    public Counter getTotalCounter(String uri){
        if(totalCounterMap.containsKey(uri)){
            return totalCounterMap.get(uri);
        }else{
            Counter counter = meterRegistry.counter(uri);
            totalCounterMap.put(uri,counter);
            return counter;
        }
    }

    public Counter getFaildReqCounter(String uri){
        if(faildRepCounterMap.containsKey(uri)){
            return faildRepCounterMap.get(uri);
        }else{
            Counter counter = meterRegistry.counter(uri);
            faildRepCounterMap.put(uri,counter);
            return counter;
        }
    }
}
