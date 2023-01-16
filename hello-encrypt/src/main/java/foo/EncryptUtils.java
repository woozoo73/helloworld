package foo;

import java.util.UUID;

/**
 * 암호화 도구의 더미 구현입니다.
 */
public abstract class EncryptUtils {

    public static String generateKey() {
        return "<" + UUID.randomUUID() + ">";
    }

    public static String decrypt(String source, String key) {
        return source.replaceAll(key, "");
    }

    public static String encrypt(String source, String key) {
        return key + source + key;
    }

    public static String valueAndKey(String source, String key) {
        return "p=" + encrypt(source, key) + "&q=" + key;
    }

}
