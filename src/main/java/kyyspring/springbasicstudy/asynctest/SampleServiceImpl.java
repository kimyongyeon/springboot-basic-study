package kyyspring.springbasicstudy.asynctest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class SampleServiceImpl implements SampleService {

    @Override
    public void asyncWork() {
        log.info("[asyncWork] thread = {}", Thread.currentThread().getName());
        Arrays.stream(Thread.currentThread().getStackTrace())
                .forEach(s -> log.info("{}", s));
        // ❌ 문제 지점 — self-invocation → 프록시 우회
        this.syncWork();
    }

    @Override
    public void syncWork() {
        log.info("[syncWork] thread = {}", Thread.currentThread().getName());
        Arrays.stream(Thread.currentThread().getStackTrace())
                .forEach(s -> log.info("{}", s));
    }
}
