package common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * [시험 학생용] SHA-256 해시 — ☆ TODO
 *
 * <pre>
 * 능력단위요소 2 / 수행준거 2.4 (보안)
 * 학습모듈 2-3 §2 표 2-14 SHA-256
 * </pre>
 *
 * <p><b>sha256(plain)</b>:</p>
 * <ol>
 *   <li>MessageDigest.getInstance("SHA-256")</li>
 *   <li>md.digest(plain.getBytes(StandardCharsets.UTF_8))</li>
 *   <li>byte[] → hex String (StringBuilder + String.format("%02x", b))</li>
 *   <li>리턴 길이는 64자</li>
 * </ol>
 *
 * <p>정답: ../EX_답/src/common/HashUtil.java</p>
 */
public class HashUtil {

    /** SHA-256 hex string */
    public static String sha256(String plain) {
    	// TODO: java.security.MessageDigest + UTF-8 + hex 변환
        if (plain == null) return null;
        // plain 값이 null 이라면 null을 줘라..
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // md라는 SHA-256 객체 생성 
            byte[] hash = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            // plain을 받아 바이트로 변환시켜 hash에 저장 - hash 값 생성
            StringBuilder sb = new StringBuilder(64);
            // 64자 길이를 담을수 있는 sb객체 생성
            for (byte b : hash) sb.append(String.format("%02x", b));
            // hash 값을 꺼내 16진수로 치환 후 sb에 추가
            return sb.toString();
            // sb를 string 타입으로 리턴한다.
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } // 마지막 예외처리
    }
}
