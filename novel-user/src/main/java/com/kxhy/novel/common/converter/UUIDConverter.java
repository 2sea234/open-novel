package com.kxhy.novel.common.converter;

import java.math.BigInteger;
import java.util.UUID;


/**
 * UUID转换器
 */
public class UUIDConverter {

    public UUIDConverter() {
        // 禁止实例化
    }

    /**
     * 将uuid转换成BigInteger
     * @param uuid uuid
     * @return BigInteger
     */
    public static BigInteger toBigInteger(UUID uuid) {

        BigInteger msb = BigInteger.valueOf(uuid.getMostSignificantBits());
        BigInteger lsb = BigInteger.valueOf(uuid.getLeastSignificantBits());

        // 处理负数情况
        if (msb.signum() < 0) {
            msb = msb.add(BigInteger.ONE.shiftLeft(64));
        }

        if (lsb.signum() < 0) {
            lsb = lsb.add(BigInteger.ONE.shiftLeft(64));
        }

        return msb.shiftLeft(64).or(lsb);
    }

    /**
     * 将BigInteger转换成uuid
     * @param bigInteger bigInteger
     * @return UUID
     */
    public static UUID toUUID(BigInteger bigInteger) {


        BigInteger mask64 = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

        BigInteger lsb = bigInteger.and(mask64);
        BigInteger msb = bigInteger.shiftRight(64).and(mask64);

        return new UUID(msb.longValue(), lsb.longValue());
    }

    /**
     * 通过生成的UUID获取一个随机的BigInteger
     * @return BigInteger
     */
    public static  BigInteger randomUUIDAsBigInteger() {
        return toBigInteger(UUID.randomUUID());
    }

}
