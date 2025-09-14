package kyyspring.springbasicstudy.biz.polling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollingRespository extends JpaRepository<PollingEntity, Long> {
}
