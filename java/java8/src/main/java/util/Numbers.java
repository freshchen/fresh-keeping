package util;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @author darcy
 * @since 2020/02/17
 **/
public class Numbers {

    /**
     * 字符串转为分为单位的价格，即保留两位小数
     *
     * @param s
     * @return
     */
    public static int stringToPrice(String s) throws ParseException {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(0);
        String value = format.format(format.parse(s).doubleValue() * 100);
        return Double.valueOf(value).intValue();
    }

    public static void main(String[] args) throws ParseException, IOException {

        InputStream inputStream = new FileInputStream("D:\\workarea\\GIT\\personal\\code\\common-util\\src\\main\\java\\1.txt");
        try(InputStream inputStream1 = inputStream) {
            System.out.println(inputStream1.read());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(inputStream.read());
        Numbers.stringToPrice("11.11");
    }
}
