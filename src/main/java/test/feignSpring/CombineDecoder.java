package test.feignSpring;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouyongbo on 2019/10/21.
 */
public class CombineDecoder implements Decoder {

    Map<String,Decoder> decoders = new HashMap<>();
    public CombineDecoder(Decoder decoder){
        decoders.put("default",new Default());
        decoders.put("other",decoder);
    }

    @Override
    public Object decode(Response response, Type type) throws  IOException {
        Decoder decoder;
       if(String.class.equals(type) || byte[].class.equals(type) || void.class.equals(type)){
            decoder = decoders.get("default");
       }else{
           decoder = decoders.get("other");
       }
        return decoder.decode(response,type);
    }
}
