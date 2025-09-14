package kyyspring.springbasicstudy.biz.polling;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PollingService {

    private final PollingRespository pollingRespository;

    public PollingEntity savePolling(PollingEntity pollingEntity) {
        return pollingRespository.save(pollingEntity);
    }

    public PollingEntity getPolling(Long id) {
        return pollingRespository.findById(id).orElse(null);
    }
}
