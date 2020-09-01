package helper;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author darcy
 * @since 2020/8/31
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryHelper<R> {

    /**
     * 如果搜索条件不为空，查询结果结果为空，返回 true
     */
    @Getter
    private Boolean shouldFastReturn;

    private R result;

    public static <T, R> QueryHelper<R> of(T queryParameter, Function<T, R> queryFunction) {
        QueryHelper<R> queryHelper = new QueryHelper<>();
        boolean queryParameterIsNone = _isNone(queryParameter);
        if (queryParameterIsNone) {
            queryHelper.shouldFastReturn = false;
            return queryHelper;
        }
        R result = queryFunction.apply(queryParameter);
        queryHelper.result = result;
        queryHelper.shouldFastReturn = _isNone(result);
        return queryHelper;
    }

    public R resultOrElse(R other) {
        return Optional.ofNullable(this.result).orElse(other);
    }

    private static <T> boolean _isNone(T o) {
        if (o instanceof CharSequence) {
            return StringUtils.isBlank((CharSequence) o);
        } else if (o instanceof Collection) {
            return CollectionUtils.isEmpty((Collection<?>) o);
        } else if (o instanceof Optional) {
            return !((Optional<?>) o).isPresent();
        } else if (o instanceof Map) {
            return MapUtils.isEmpty((Map<?, ?>) o);
        }
        return Objects.isNull(o);
    }

}
