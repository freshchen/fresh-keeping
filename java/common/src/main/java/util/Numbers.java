package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author darcy
 * @since 2020/02/17
 **/
public class Numbers {

    /**
     * 元转分
     *
     * @param s
     * @return
     */
    public static BigDecimal yuanToCent(String s) {
        return yuanToCent(new BigDecimal(s));
    }

    /**
     * 分转元
     *
     * @param s
     * @return
     */
    public static BigDecimal centToYuan(String s) {
        return centToYuan(new BigDecimal(s));
    }

    /**
     * 元转分
     *
     * @param yuan
     * @return
     */
    public static BigDecimal yuanToCent(BigDecimal yuan) {
        if (yuan == null) {
            throw new NullPointerException("元转分，原始金额为空");
        }
        return yuan.multiply(BigDecimal.valueOf(100));
    }

    /**
     * 分转元
     *
     * @param cent
     * @return
     */
    public static BigDecimal centToYuan(BigDecimal cent) {
        if (cent == null) {
            throw new NullPointerException("分转元，原始金额为空");
        }
        return cent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
    }


    private Numbers() {
    }
}
