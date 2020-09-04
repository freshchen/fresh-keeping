package util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author darcy
 * @since 2020/9/4
 **/
public class Nones {

    public static boolean is(final CharSequence s) {
        return StringUtils.isBlank(s);
    }

    public static boolean is(final Collection<?> coll) {
        return CollectionUtils.isEmpty(coll);
    }

    public static boolean is(final Map<?, ?> coll) {
        return MapUtils.isEmpty(coll);
    }

    public static boolean is(final Optional<?> opt) {
        if (!opt.isPresent()) {
            return false;
        }
        Object o = opt.get();
        if (o instanceof CharSequence) {
            return is((CharSequence) o);
        } else if (o instanceof Collection) {
            return is((Collection<?>) o);
        } else if (o instanceof Map) {
            return is((Map<?, ?>) o);
        } else if (o instanceof Optional) {
            return !((Optional<?>) o).isPresent();
        }
        return false;
    }


    private Nones() {
    }
}
