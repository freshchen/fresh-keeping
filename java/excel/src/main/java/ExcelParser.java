import com.google.common.collect.Lists;
import javafx.util.Pair;
import model.ExcelHandler;
import model.ExcelRow;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
        Pair<Boolean, ?> handler(final Collection<? extends ExcelHandler> handlers);
    }

    private static Pair<Boolean, ?> process(XSSFWorkbook workbook, Collection<? extends ExcelHandler> excelHandlers) {
        List<ExcelRow> rows = Lists.newArrayList();
        Map<Integer, ? extends List<? extends ExcelHandler>> sheetIndexAndHandlersMap = excelHandlers.stream().collect(Collectors.groupingBy(ExcelHandler::getSheetIndex));
        sheetIndexAndHandlersMap.entrySet().stream()
                .map(entry -> {
                    int sheetIndex = entry.getKey();
                    List<? extends ExcelHandler> handlers = entry.getValue();
                    XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
                    IntStream.range(1, sheet.getPhysicalNumberOfRows()).forEach(rowIndex -> {
                        XSSFRow row = sheet.getRow(rowIndex);
                        handlers.stream().map(handler -> {
                            Class<? extends ExcelRow> rowClass = handler.getRowClass();
                            ExcelRow excelRow = null;
                            try {
                                excelRow = rowClass.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new IllegalStateException("反射创建Excel对象失败", e);
                            }
                            Optional.ofNullable(excelRow)
                                    .flatMap(r -> {
                                        r.setSheetId(sheetIndex);
                                        r.setRowId(rowIndex);
                                        XSSFCell cell = row.getCell(handler.getCellIndex());
                                        return Optional.ofNullable(cell);
                                    })
                                    .flatMap(c -> {
                                        c.setCellType(CellType.STRING);
                                        String value = c.getStringCellValue().trim();
                                        try {
                                            rowClass.getDeclaredField(handler.getFieldName());
                                        } catch (Exception e) {
                                            e
                                        }
                                    });
                        });

                        return null;
                    });
                });
        return null;
    });
        return null;
}

}
