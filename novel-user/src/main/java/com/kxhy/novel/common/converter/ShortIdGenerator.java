package com.kxhy.novel.common.converter;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ShortIdGenerator {

    // 字符集用于生成可读的ID
    private static final String BASE32_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // 用于将数字转换为字符
    private static final char[] BASE32_ARRAY = BASE32_CHARS.toCharArray();

    // 用于生成随机数
    private final SecureRandom secureRandom = new SecureRandom();
    // 用于生成计数器
    private final Random random = new Random();
    // 用于生成计数器
    private final AtomicLong counter = new AtomicLong(1000000000L); // 起始值10亿



    /**
     * 生成带时间戳的可读ID （如：USER_241021_123456）
     * @param prefix 前缀
     * @return 可读ID
     */

    public String generateReadableId(String prefix) {
        // 生成时间戳部分
        String timestamp = String.valueOf(System.currentTimeMillis() % 1000000);
        // 生成随机数部分
        String randomPart = String.valueOf(secureRandom.nextInt(1000000));

        return prefix + "_" + timestamp + "_" + randomPart;
    }

    /**
     * 转数字Base32
     * @param number 数字
     * @return Base32字符串
     */
    private String toBase32(long number) {

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int index  = (int) (number % 32);
            result.append(BASE32_ARRAY[index]);
            number = number / 32;
        }
        return result.reverse().toString();
    }

    /**
     * 生成BigInteger类型的短ID
     * @return 短ID
     */
    public BigInteger generateShortBigIntegerId() {
        return BigInteger.valueOf(generateNumericId());
    }

    /**
     * 生成Base32类型的短ID
     * @return 短ID
     */
    public String generateBase32Id() {
        long id = generateNumericId();
        return toBase32(id);
    }

    /**
     * 生成唯一的ID（纯数字10-12位）
     * @return  ID
     */
    public long generateNumericId() {

        // 生成时间戳部分
        long timestampPart = System.currentTimeMillis() % 1000000000L;

        // 生成随机数部分
        long randomPart = random.nextInt(1000000);

        // 生成计数器部分
        long counterPart = counter.incrementAndGet() % 1000000;

        // 组合ID
        return timestampPart * 10000000 + randomPart % 1000 + counterPart % 1000;
    }

}
