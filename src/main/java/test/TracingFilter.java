package test;

import io.micrometer.core.instrument.MeterRegistry;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.feignService.UserFigureService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouyongbo on 2019/10/29.
 */
@WebFilter(urlPatterns={"/*"})
@Slf4j
public class TracingFilter implements Filter {

    @Resource
    private Tracer tracer;
    @Resource
    private MeterRegistry meterRegistry;
    @Autowired
    private UserFigureService[] userFigureServices;

    @PostConstruct
    public void init(){
        System.out.println(userFigureServices.length);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("TracingFilter start....");
        Map<String,String> map = new HashMap<>();
        Span span = null;
        if(servletRequest instanceof HttpServletRequest){
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()){
                String name = headerNames.nextElement();
                if(name.startsWith("x-b3-")){
                    String headerValue = request.getHeader(name);
                    map.put(name,headerValue);
                }

            }
            if(!map.isEmpty()){
                TextMapAdapter textMapAdapter = new TextMapAdapter(map);
                SpanContext context = tracer.extract(Format.Builtin.HTTP_HEADERS, textMapAdapter);
                String requestURI = request.getRequestURI();
                span = tracer.buildSpan(requestURI).asChildOf(context).start();
                span.log("filter request start");
                span.setTag("span.kind","server");
            }
        }
        try{
            filterChain.doFilter(servletRequest,servletResponse);
        }catch (Exception e){
            if(span != null){
                span.setTag("error",e.toString().substring(0,100));
            }
            throw e;
        }finally {
            if(span != null){
                span.finish();
            }
        }



    }
}
