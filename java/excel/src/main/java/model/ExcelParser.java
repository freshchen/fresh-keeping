package model;

import lombok.Builder;
import lombok.Getter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author darcy
 * @since 2020/02/24
 **/
@Builder
@Getter
public class ExcelParser<T extends ExcelRow> {

    @NotNull
    private XSSFWorkbook workbook;

    /**
     * Excel 的第几个工作簿
     */
    @NotNull
    private Integer sheetIndex;

    /**
     * 数据转换器，只有配置了的列才会去读
     */
    @NotNull
    private Collection<Transfer> transfers;
    /**
     * 数据校验器
     */
    private Collection<Checker<T>> checkers;

    @Builder
    @Getter
    public static class Transfer {
        /**
         * 单元格的列
         */
        @NotNull
        private Integer cellIndex;
        /**
         * 实现了 ExcelRow 的对象的 字段名
         */
        @NotNull
        private String fieldName;
        /**
         * 自定义如何转换
         */
        private Function<String, ?> transfer;

    }

    @Builder
    @Getter
    public static class Checker<T extends ExcelRow> {
        /**
         * 检查的是excel中哪几行数据
         */
        @NotNull
        private Collection<Integer> cellIndexs;
        /**
         * 错误信息
         */
        @NotNull
        private String errorMessage;
        /**
         * 校验规则
         */
        @NotNull
        private Predicate<T> checker;

    }
}
