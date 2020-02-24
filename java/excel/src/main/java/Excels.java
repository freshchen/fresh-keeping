
import model.ExcelParser;
import model.ExcelRow;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * @author darcy
 * @since 2020/02/23
 **/
public class Excels {

    /**
     * 把 Excel 的转换成需要的对象集合
     * 案例参考 ExcelsTest.parse()
     *
     * @param parser
     * @param rowClass
     * @param <T>
     * @return
     */
    public static <T extends ExcelRow> List<T> parseToRows(final ExcelParser<T> parser, Class<T> rowClass) {
        int sheetIndex = parser.getSheetIndex();
        XSSFSheet sheet = parser.getWorkbook().getSheetAt(sheetIndex);
        // 跳过标题行，从第二行开始加载
        return IntStream.range(1, sheet.getPhysicalNumberOfRows()).mapToObj(rowIndex -> {
            XSSFRow row = sheet.getRow(rowIndex);
            try {
                T excelRow = rowClass.newInstance();
                excelRow.setSheetId(sheetIndex);
                excelRow.setRowId(rowIndex);
                // 解析一行中所有配置的列
                parser.getTransfers().forEach(transfer -> {
                    XSSFCell cell = row.getCell(transfer.getCellIndex());
                    Optional.ofNullable(cell).ifPresent(c -> {
                        c.setCellType(CellType.STRING);
                        String valueInExcel = c.getStringCellValue().trim();
                        try {
                            Field field = rowClass.getDeclaredField(transfer.getFieldName());
                            field.setAccessible(true);
                            // 如果没有指定怎么解析，就读默认值
                            Function<String, ?> transferOrDefault = Optional.<Function<String, ?>>ofNullable(transfer.getTransfer())
                                    .orElse(Excels.defaultTransfer(field.getType(), excelRow));
                            Object value = transferOrDefault.apply(valueInExcel);
                            field.set(excelRow, value);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            excelRow.getErrorCellIds().add(transfer.getCellIndex());
                        }
                    });
                });
                if (excelRow.isHappenedError()) {
                    excelRow.setErrorMessage("Excel数据错误");
                }
                // 如果指定了校验器，且校验不够就记录错误
                Optional.ofNullable(parser.getCheckers()).ifPresent(checkers -> {
                    checkers.forEach(checker -> {
                        if (!checker.getChecker().test(excelRow)) {
                            excelRow.getErrorCellIds().addAll(checker.getCellIndexs());
                            String errorMessage = checker.getErrorMessage() + Optional.ofNullable(excelRow.getErrorMessage()).map(s -> s + ",").orElse("");
                            excelRow.setErrorMessage(errorMessage);
                        }
                    });
                });
                return excelRow;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("反射创建Excel对象失败", e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 提供默认赋值方式
     * @param fieldType
     * @param excelRow
     * @param <T>
     * @return
     */
    private static <T extends ExcelRow> Function<String, ?> defaultTransfer(Class<?> fieldType, T excelRow) {
        return value -> {
            switch (fieldType.getSimpleName()) {
                case "Integer":
                case "int":
                    return Integer.valueOf(value);
                case "Double":
                case "double":
                    return Double.valueOf(value);
                case "Float":
                case "float":
                    return Float.valueOf(value);
                case "Long":
                case "long":
                    return Long.valueOf(value);
                default:
                    return value;
            }
        };
    }

    private Excels() {
    }

}

