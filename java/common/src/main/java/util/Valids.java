package util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author darcy
 * @since 2020/02/20
 **/
public class Valids {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    public static Boolean email(String email) {
        return StringUtils.isNoneBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean containsNull(Object... objects) {
        return Stream.of(objects).anyMatch(Objects::isNull);
    }

    private Valids() {
    }
}
