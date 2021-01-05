import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.propagation.TextMapInject;
import io.opentracing.tag.Tags;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyongbo on 2019/10/28.
 */
public class ZipkinOpentracingTest {

    private Tracer tracer;

    @Before
    public void initZipkin(){
        // Configure a reporter, which controls how often spans are sent
//   (the dependency is io.zipkin.reporter2:zipkin-sender-okhttp3)
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
         this.tracer = BraveTracer.create(braveTracing);

// You can later unwrap the underlying Brave Api as needed
        //braveTracer = this.tracer.unwrap();
    }



    @Test
    public void testZipkin() throws InterruptedException, IOException {
        Span span = tracer.buildSpan("getCatgory").start();

        SpanContext context = span.context();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("X-B3-TraceId", context.toTraceId());
        map.put("X-B3-SpanId", context.toSpanId());
        map.put("X-B3-Sampled", "1");
        TextMapAdapter carrier = new TextMapAdapter(map);
        String url = "http://localhost:8080/api/listHeadCategorys";
        span.setTag("http.url",url);
        span.setTag("span.kind","client");
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\n" +
                "\t\"bodyModelVersion\": 1.0,\n" +
                "\t\"faceModelVersion\": 1.0,\n" +
                "\t\"categoryId\": 5,\n" +
                "\t\"gender\": 2\n" +
                "}";
        RequestBody body = RequestBody.create(json,JSON);

        Request.Builder builder = new Request.Builder();
        Set<String> keys = map.keySet();
        for(String key:keys){
            builder.addHeader(key, map.get(key));
        }
        Request request = builder.url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
            Tags.HTTP_STATUS.set(span,response.code());
            Tags.ERROR.set(span,true);
            span.log("1111");
            //span.log("error");
        }
        tracer.inject(context, Format.Builtin.HTTP_HEADERS,carrier);
        try (Scope scope = tracer.scopeManager().activate(span)) {
            // Do things.
        } catch(Exception ex) {
            Tags.ERROR.set(span, true);
            //span.log(Map.of(Fields.EVENT, "error", Fields.ERROR_OBJECT, ex, Fields.MESSAGE, ex.getMessage()));
        } finally {
            span.finish();
        }
        Thread.sleep(1000);
       //throw  new RuntimeException("111111");
    }
}
