package kyyspring.springbasicstudy.rsaservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RsaKeyDto {
    String privateKey;
    String publicKey;
}
