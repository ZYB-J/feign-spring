package test;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.feignService.UserFigureFallback;
import test.feignService.UserFigureService;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.*;

@RestController
@EnableAutoConfiguration
@ServletComponentScan
@ComponentScan
public class Example {


    @RequestMapping("/api/listHeadCategorys")
    Map<String, String> home() {
        Map<String, String> a = new HashMap<String,String>(){{
            put("1","上你号");
            put("2","我的号");
        }};
        throw new RuntimeException("测试异常");
    }

    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }

    @Bean
    public Tracer initTracer(){
        OkHttpSender sender = OkHttpSender.create("http://192.168.64.128:9411/api/v2/spans");
        AsyncReporter spanReporter = AsyncReporter.create(sender);

// If you want to support baggage, indicate the fields you'd like to
// whitelist, in this case "country-code" and "user-id". On the wire,
// they will be prefixed like "baggage-country-code"
        ExtraFieldPropagation.Factory propagationFactory = ExtraFieldPropagation.newFactoryBuilder(B3Propagation.FACTORY)
                .addPrefixedFields("baggage-", Arrays.asList("country-code", "user-id"))
                .build();

// Now, create a Brave tracing component with the service name you want to see in Zipkin.
//   (the dependency is io.zipkin.brave:brave)
        Tracing braveTracing = Tracing.newBuilder()
                .localServiceName("my-service")
                .propagationFactory(propagationFactory)
                .spanReporter(spanReporter)
                .build();

// use this to create an OpenTracing Tracer
        return BraveTracer.create(braveTracing);

    }

    @Bean
    public MeterRegistry initMeterRegistry(){
        MeterRegistry registry = new SimpleMeterRegistry();
        registry.config().commonTags("appName","chloe");
        return registry;
    }




}