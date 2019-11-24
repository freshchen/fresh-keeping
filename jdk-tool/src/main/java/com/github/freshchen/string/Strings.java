package com.github.freshchen.string;

/**
 * @author : freshchen
 * <P>Created on 2019-11-23 22:35 </p>
 **/
public class Strings {

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
