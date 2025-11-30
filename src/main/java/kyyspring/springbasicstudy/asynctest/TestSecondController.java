package kyyspring.springbasicstudy.asynctest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSecondController {

    private final SampleService sampleService;

    public TestSecondController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @GetMapping("/api/guest/test")
    public String test() {
        sampleService.asyncWork();
        return "OK";
    }
}
