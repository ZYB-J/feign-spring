import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import test.bean.TestRequest;

/**
 * Created by zhouyongbo on 2019/11/15.
 */
@Slf4j
public class ReactorTest {
    WebClient webClient = WebClient.create("http://localhost:8080111");

    @Test
    public void test() throws InterruptedException {

        Mono<String> testRequest = findById(1L);
        testRequest.subscribe(x-> System.out.println(x));
        Thread.sleep(10000);
    }


    public Mono<String> findById(Long userId) {
        return webClient
                .get()
                .uri("/api/listHeadCategorys")
                //.accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(e -> {
                    log.info("11111111111111111111111111111111111");
                    System.out.println("11111111111111111");
                    return e.isError();
                }, clientResponse -> {
                    System.out.println("1111111111111111111111111111");
                    return Mono.error(new RuntimeException("1111"));
                })
                .bodyToMono(String.class)
                .doOnError(x -> {
                    System.out.println("exception erro:");
                });
    }


    @Test
    public void testRetrieve4xx() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.github.v3+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .build();
        WebClient.ResponseSpec responseSpec = webClient.method(HttpMethod.GET)
                .uri("/user/repos?sort={sortField}&direction={sortDirection}",
                        "updated", "desc")
                .retrieve();
        Mono<String> mono = responseSpec
                .onStatus(e ->e.isError(), resp -> {
                    System.out.println("444444444444444444444444444444");
                    return Mono.error(new RuntimeException(resp.statusCode().value() + " : " + resp.statusCode().getReasonPhrase()));
                })
                .bodyToMono(String.class)
                .doOnError(WebClientResponseException.class, err -> {
                    System.out.println("1111111111111111111111111111");
                    throw new RuntimeException(err.getMessage());
                })
                .onErrorReturn("fallback");
        String result = mono.block();

    }


    @Test
    public void testLogger(){
        Logger logger = LoggerFactory.getLogger(ReactorTest.class);
        logger.info("1111");
    }
}