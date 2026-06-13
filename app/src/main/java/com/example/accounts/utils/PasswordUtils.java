package com.example.accounts.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.math.BigInteger;

/**
 * 密码安全工具类
 * <p>
 * 功能说明：
 * - 生成随机盐值（16字节，32位十六进制）
 * - SHA-256加盐哈希计算（密码+盐值拼接后哈希）
 * - 密码校验（输入密码与存储的哈希值比对）
 * </p>
 *
 * <p>设计要点：
 * - 所有方法均为静态方法，无状态，线程安全
 * - 盐值每次生成均不同，修改密码时强制重新生成
 * - 日志不输出密码、盐值、哈希等敏感信息
 * </p>
 */
public class PasswordUtils {

    private static final String TAG = "PasswordUtils";
    /**
     * 盐值字节长度
     */
    private static final int SALT_BYTE_LENGTH = 16;
    /**
     * 哈希算法名称
     */
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * 生成随机盐值
     * 使用SecureRandom生成16字节随机数，转为32位十六进制字符串
     *
     * @return 32位十六进制字符串表示的随机盐值
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return new BigInteger(1, salt).toString(16);
    }

    /**
     * 对密码进行SHA-256加盐哈希计算
     * 将明文密码与盐值拼接后进行SHA-256哈希，返回64位十六进制字符串
     *
     * @param password 明文密码
     * @param salt     随机盐值
     * @return 64位十六进制字符串表示的SHA-256哈希值；异常时返回null
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update((password + salt).getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            android.util.Log.e(TAG, "hashPassword: 计算失败", e);
            return null;
        }
    }

    /**
     * 校验密码是否正确
     * 将用户输入的明文密码与数据库中存储的盐值重新计算哈希，
     * 与存储的哈希值进行比对
     *
     * @param inputPassword 用户输入的明文密码
     * @param storedHash    数据库中存储的密码哈希值
     * @param storedSalt    数据库中存储的盐值
     * @return 密码正确返回true，否则返回false
     */
    public static boolean verifyPassword(String inputPassword, String storedHash, String storedSalt) {
        if (inputPassword == null || storedHash == null || storedSalt == null) {
            return false;
        }
        String computedHash = hashPassword(inputPassword, storedSalt);
        if (computedHash == null) {
            return false;
        }
        return computedHash.equals(storedHash);
    }
}
