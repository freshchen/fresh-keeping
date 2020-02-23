
import model.ExcelHandler;
import model.ExcelRow;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * @author darcy
 * @since 2020/02/23
 **/
public class ExcelParser {

    public static Handler workBook(XSSFWorkbook workbook) {
        Optional.ofNullable(workbook).orElseThrow(NullPointerException::new);
        return handlers -> {
            Optional.ofNullable(handlers).orElseThrow(NullPointerException::new);
            return process(workbook, handlers);
        };
    }

    @FunctionalInterface
    private interface Handler {
        List<Pair<Integer, List<ExcelRow>>> handler(final Collection<ExcelHandler<ExcelRow>> handlers);
    }

    private static List<Pair<Integer, List<ExcelRow>>> process(XSSFWorkbook workbook, Collection<ExcelHandler<ExcelRow>> excelHandlers) {
        return excelHandlers.stream().map(handler -> {
            int sheetIndex = handler.getSheetIndex();
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            List<ExcelRow> excelRows = IntStream.range(1, sheet.getPhysicalNumberOfRows()).mapToObj(rowIndex -> {
                XSSFRow row = sheet.getRow(rowIndex);
                @NotNull Class<ExcelRow> rowClass = handler.getRowClass();
                try {
                    ExcelRow excelRow = rowClass.newInstance();
                    excelRow.setSheetId(sheetIndex);
                    excelRow.setRowId(rowIndex);
                    handler.getTransfers().forEach(transfer -> {
                        XSSFCell cell = row.getCell(transfer.getCellIndex());
                        Optional.ofNullable(cell).ifPresent(c -> {
                            c.setCellType(CellType.STRING);
                            String value = c.getStringCellValue().trim();
                            try {
                                Field field = rowClass.getDeclaredField(transfer.getFieldName());
                                field.setAccessible(true);
//                                field.set(excelRow, transfer.getTransfer().apply(value));
                                field.set(excelRow, value);
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
            handler.getResults().addAll(excelRows);
            return Pair.of(handler.getSheetIndex(), handler.getResults());
        }).collect(Collectors.toList());
    }

}

