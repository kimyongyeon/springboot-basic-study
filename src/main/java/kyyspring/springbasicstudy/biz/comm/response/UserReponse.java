package kyyspring.springbasicstudy.biz.comm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReponse {
    private String name;
    private int age;

    private String userId;
    private Profile profile;
    private List<Device> devices;
}
