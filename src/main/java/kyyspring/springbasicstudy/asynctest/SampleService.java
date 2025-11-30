package kyyspring.springbasicstudy.asynctest;

import org.springframework.scheduling.annotation.Async;

public interface SampleService {
    @Async("taskExecutor")
    void asyncWork();

    void syncWork();
}
