package kyyspring.springbasicstudy.rsaservice;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class RsaUtil {

    // 1. RSA 키 쌍(공개키, 개인키) 생성
    public static Map<String, String> createKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // 키 사이즈 2048비트 (권장)
            KeyPair keyPair = keyPairGenerator.genKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            Map<String, String> keys = new HashMap<>();
            // 프론트로 보낼 공개키 (Base64 인코딩)
            keys.put("publicKey", Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            // 서버에 저장할 개인키 (Base64 인코딩) - 실제로는 세션이나 DB에 임시 저장해야 함
            keys.put("privateKey", Base64.getEncoder().encodeToString(privateKey.getEncoded()));

            return keys;
        } catch (Exception e) {
            throw new RuntimeException("키 생성 실패", e);
        }
    }

    // 2. 복호화 로직 (개인키 사용)
    public static String decrypt(String encryptedText, String stringPrivateKey) {
        try {
            // Base64 String 개인키를 PrivateKey 객체로 변환
            byte[] privateKeyBytes = Base64.getDecoder().decode(stringPrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // 복호화 수행
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 암호문은 Base64로 오므로 디코딩 후 복호화
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, "UTF-8");

        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }
}