package common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-256 해시 유틸 (비밀번호 단방향 해시) — ★ 정답
 *
 * <pre>
 * 학습모듈 2-3 §2 (데이터 암호화 — 표 2-14 SHA-256/384/512)
 * 인터페이스설계서.md §6 보안 — pwd_hash CHAR(64)
 * </pre>
 *
 * <p>SHA-256 hex string = 64자. 평문 비밀번호 절대 저장 금지.</p>
 */
public class HashUtil {

    /** SHA-256 hex string */
    public static String sha256(String plain) {
        if (plain == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
