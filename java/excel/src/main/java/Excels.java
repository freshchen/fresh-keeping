
import model.ExcelRow;
import model.ExcelParser;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.reflect.Field;
import java.util.Collection;
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

    static <T extends ExcelRow> List<T> parse(ExcelParser parser, Class<T> rowClass) {
        int sheetIndex = parser.getSheetIndex();
        XSSFSheet sheet = parser.getWorkbook().getSheetAt(sheetIndex);
        return IntStream.range(1, sheet.getPhysicalNumberOfRows()).mapToObj(rowIndex -> {
            XSSFRow row = sheet.getRow(rowIndex);
            try {
                T excelRow = rowClass.newInstance();
                excelRow.setSheetId(sheetIndex);
                excelRow.setRowId(rowIndex);
                parser.getTransfers().forEach(transfer -> {
                    XSSFCell cell = row.getCell(transfer.getCellIndex());
                    Optional.ofNullable(cell).ifPresent(c -> {
                        c.setCellType(CellType.STRING);
                        String value = c.getStringCellValue().trim();
                        try {
                            Field field = rowClass.getDeclaredField(transfer.getFieldName());
                            field.setAccessible(true);
                            Function<String, ?> transferOrDefault = Optional.<Function<String, ?>>ofNullable(transfer.getTransfer())
                                    .orElse(Excels.defaultTransfer(field.getType(), excelRow));
                            field.set(excelRow, transferOrDefault.apply(value));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            excelRow.getErrorCellIds().add(transfer.getCellIndex());
                        }
                    });
                });
                return excelRow;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("反射创建Excel对象失败", e);
            }
        }).collect(Collectors.toList());
    }

    private static <T extends ExcelRow> Function<String, ?> defaultTransfer(Class<?> fieldType, T excelRow) {
        return value -> {
            switch (fieldType.getSimpleName()) {
                case "Integer":
                    return Integer.valueOf(value);
                case "String":
                    return value;
                default:
                    return null;
            }
        };
    }

}

