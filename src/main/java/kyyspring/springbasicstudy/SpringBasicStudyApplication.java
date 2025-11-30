package kyyspring.springbasicstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBasicStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBasicStudyApplication.class, args);

//        WebClient webClient = WebClient.create();
//        Mono<String> stringMono = webClient.get()
//                .uri("http://localhost:8000/")
//                .exchangeToMono(response -> {
//                    if (response.statusCode().isError()) {
//                        // 에러 상태라면 bodyToMono로 에러 내용 읽고 Mono.error로 넘김
//                        return response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error: " + errorBody)));
//                    } else {
//                        // 정상 응답일 경우 body 읽음
//                        return response.bodyToMono(String.class);
//                    }
//                });
//        stringMono.subscribe(
//                response -> System.out.println("Response: " + response),
//                throwable -> {
//                    System.err.println("예외 메시지: " + throwable.getMessage());
//                    System.err.println("예외 클래스: " + throwable.getClass());
//                    throwable.printStackTrace();
//                }
//        );
//
//        // 단건
//        Mono<String> result = Mono.just("Hello World");
//
//        // 빈값
//        Mono.empty();
//
//        // 출력
//        result.subscribe(response -> System.out.println("Response: " + response));
//
//        // 다건
//        Flux<Integer> flux = Flux.just(1, 2, 3);
//
//        // 출력
//        flux.subscribe(response -> System.out.println("Response: " + response));


    }

}
