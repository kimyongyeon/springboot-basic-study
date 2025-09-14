package kyyspring.springbasicstudy.biz.polling;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PollingResponse {

    @JsonProperty("polling_id")
    private Long id;
    @JsonProperty("polling_name")
    private String name;
    @JsonProperty("polling_phone")
    private String phone;
}
