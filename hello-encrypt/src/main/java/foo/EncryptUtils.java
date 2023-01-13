package foo;

public abstract class EncryptUtils {

    public static String decrypt(String source, String key) {
        return source.replaceAll(key, "");
    }

    public static String encrypt(String source, String key) {
        return key + source + key;
    }

}
