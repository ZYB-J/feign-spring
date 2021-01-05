import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtract;
import io.opentracing.propagation.TextMapInject;
import io.opentracing.tag.Tags;
import org.junit.Test;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.*;

/**
 * Created by zhouyongbo on 2019/10/24.
 */
public class OpentracingTest {
    MockTracer tracer = new MockTracer();
    @Test
    public void test() throws InterruptedException {
        // Initialize MockTracer with the default values.


// Create a new Span, representing an operation.
        MockSpan span = tracer.buildSpan("test").start();

// Add a tag to the Span.
        span.setTag(Tags.COMPONENT, "my-own-application");
        tracer.inject(span.context(), Format.Builtin.TEXT_MAP_INJECT, new TextMapInject() {
            @Override
            public void put(String key, String value) {
                key = "1111";
                value = "22222";
            }
        });
        op1();
// Finish the Span.
        span.finish();
// Analyze the saved Span.
     /*   System.out.println("Operation name = " + span.operationName());
        System.out.println("Start = " + span.startMicros());
        System.out.println("Finish = " + span.finishMicros());*/

// Inspect the Span's tags.
        Map<String, Object> tags = span.tags();
        System.out.println(tags);

        List<MockSpan> mockSpans = tracer.finishedSpans();
        for(MockSpan mockSpan:mockSpans){
            System.out.println("Operation name = " + mockSpan.operationName());
            System.out.println("Start = " + mockSpan.startMicros());
            System.out.println("Finish = " + mockSpan.finishMicros());
            System.out.println("diff = " + (mockSpan.finishMicros()-mockSpan.startMicros()));
            System.out.println("context = " + mockSpan.context());
            System.out.println("parentId = " +  mockSpan.parentId());
            System.out.println("parentId = " +  mockSpan.context().traceId());

        }
    }

    private void  op1() throws InterruptedException {
        SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT, new TextMapExtract() {
            @Override
            public Iterator<Map.Entry<String, String>> iterator() {
                return new ArrayList<Map.Entry<String, String>>().iterator();
            }
        });
        MockSpan span = tracer.buildSpan("op1").asChildOf(spanContext).start();
        Thread.sleep(1000);
        span.finish();
    }



}
