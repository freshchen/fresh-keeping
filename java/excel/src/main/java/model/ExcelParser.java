package model;

import lombok.Builder;
import lombok.Getter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    private XSSFWorkbook workbook;

    /**
     * Excel 的第几个工作簿
     */
    private Integer sheetIndex;

    /**
     * 数据转换器，只有配置了的列才会去读
     */
    private Collection<Transfer> transfers;
    /**
     * 数据校验器 可选
     */
    private Collection<Checker<T>> checkers;

    @Builder
    @Getter
    public static class Transfer {
        /**
         * 单元格的列
         */

        private Integer cellIndex;
        /**
         * 实现了 ExcelRow 的对象的 字段名
         */
        private String fieldName;
        /**
         * 自定义如何转换，可选
         */
        private Function<String, ?> transfer;

    }

    @Builder
    @Getter
    public static class Checker<T extends ExcelRow> {
        /**
         * 检查的是excel中哪几行数据
         */
        private Collection<Integer> cellIndexs;
        /**
         * 错误信息
         */
        private String errorMessage;
        /**
         * 校验规则
         */
        private Predicate<T> checker;

    }
}
