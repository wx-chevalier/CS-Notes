package wx.toolkits.ds.string;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by apple on 15/12/2.
 */
public class StringGenerator {

    /**
     * @param length 字符串长度
     * @return 新的随机生成的字符串
     * @function 生成固定长度的字符串
     */
    public static String generateFixedLengthString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    @Test
    public void test_generateFixedLengthString() { //length表示生成字符串的长度
        System.out.println(StringGenerator.generateFixedLengthString(5));
    }

    /**
     * @param originStr 原始的字符串
     * @return
     * @function 反转字符串
     */
    public static String reverseString(String originStr) {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = originStr.length() - 1; i > -1; i--) {

            stringBuilder.append(originStr.charAt(i));
        }

        return stringBuilder.toString();

    }

    @Test
    public void test_reverseString() {

        Assert.assertEquals("bacd", reverseString("dcab"));

    }
}
