package util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;


/**
 * @author darcy
 * @since 2020/02/17
 **/
public class DateTimes {

    /**
     * Java8的时间日期转换成 Joda 的
     *
     * @param time
     * @return
     */
    public static DateTime toJodaTime(OffsetDateTime time) {
        return time == null ? null : new DateTime(time.toInstant().toEpochMilli());
    }

    /**
     * Joda 时间转 Java8 时间
     *
     * @param dateTime
     * @return
     */
    public static OffsetDateTime fromJodaTime(DateTime dateTime) {
        return dateTime == null ? null : fromLong(dateTime.getMillis());
    }

    /**
     * 按照毫秒转换成 Java8 时间
     *
     * @param mills
     * @return
     */
    public static OffsetDateTime fromLong(Long mills) {
        return new Timestamp(mills).toInstant().atZone(systemDefault()).toOffsetDateTime();
    }

    /**
     * java8区间判断
     *
     * @param dateTime
     * @param start
     * @param end
     * @return
     */
    public static boolean between(OffsetDateTime dateTime, OffsetDateTime start, OffsetDateTime end) {
        return dateTime.isAfter(start) && dateTime.isBefore(end);
    }

    /**
     * jodatime 区间判断
     *
     * @param dateTime
     * @param start
     * @param end
     * @return
     */
    public static boolean between(DateTime dateTime, DateTime start, DateTime end) {
        return dateTime.isAfter(start) && dateTime.isBefore(end);
    }

    /**
     * ISO_LOCAL_DATE 格式字符串转 OffsetDateTime, 默认时间设为'00:00'
     *
     * @param text
     * @return
     */
    public static OffsetDateTime fromIsoDate(String text) {
        return LocalDate.parse(text, ISO_LOCAL_DATE).atTime(LocalTime.MIN).atZone(systemDefault()).toOffsetDateTime();
    }

    /**
     * 格式化时间
     *
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String format(OffsetDateTime dateTime, String pattern) {
        Optional.ofNullable(dateTime).orElseThrow(() -> new NullPointerException("格式化时间 时间为空"));
        if (StringUtils.isBlank(pattern)) {
            throw new NullPointerException("格式化时间 格式为空");
        }

        if (ZoneOffset.UTC.equals(dateTime.getOffset())) {
            return dateTime.toInstant()
                    .atOffset(ZoneOffset.ofTotalSeconds(OffsetDateTime.now().getOffset().getTotalSeconds()))
                    .format(DateTimeFormatter.ofPattern(pattern));
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern(pattern));
        }
    }

    private DateTimes() {
    }
}
