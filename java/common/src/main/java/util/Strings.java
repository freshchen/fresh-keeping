package util;

/**
 * @author darcy
 * @since 2020/02/17
 **/
public class Strings {

    /**
     * 首字母大写
     *
     * @param s
     * @return
     */
    public static String capitalizeFirstLetter(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            char[] chars = s.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return String.valueOf(chars);
        }
        return s;
    }

    private Strings() {
    }
}
