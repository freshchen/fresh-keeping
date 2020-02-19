package util;

public class Strings {

    public static String capitalizeFirstLetter(String s) {

        if (Character.isLowerCase(s.charAt(0))) {
            char[] chars = s.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return String.valueOf(chars);
        }
        return s;
    }
}
