import model.ExcelParser;
import model.ExcelRow;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Color;
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
     * e.g. 其中 checkers 可选
     * List<ExcelParser.Transfer> transfers = Arrays.asList(
     * ExcelParser.Transfer.builder().cellIndex(0).fieldName("name").build(),
     * ExcelParser.Transfer.builder().cellIndex(1).fieldName("price").transfer(s -> Integer.parseInt(s) * 100).build(),
     * ExcelParser.Transfer.builder().cellIndex(2).fieldName("age").build()
     * );
     * List<ExcelParser.Checker<MyRow>> checkers = Arrays.asList(
     * ExcelParser.Checker.<MyRow>builder().cellIndexs(Arrays.asList(0, 1)).errorMessage("我的错").checker(excelRow -> {
     * if (excelRow.getAge() + excelRow.getPrice() > 100) {
     * return false;
     * }
     * return true;
     * }).build()
     * );
     * ExcelParser<MyRow> build = ExcelParser.<MyRow>builder().sheetIndex(0).workbook(workbook).checkers(checkers).transfers(transfers).build();
     * List<MyRow> parse1 = Excels.parseToRows(build, MyRow.class);
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
     * 处理所有错误数据，并且写提示信息
     *
     * @param workbook
     * @param excelRows
     * @param <T>
     * @return 有错误返回 true
     */
    public static <T extends ExcelRow> boolean handlerErrorAndWriteErrorMessage(XSSFWorkbook workbook, List<T> excelRows) {
        List<ExcelRow> formatErrorList = excelRows.stream()
                .filter(ExcelRow::isHappenedError)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(formatErrorList)) {
            formatErrorList.stream().map(ExcelRow::getSheetId).distinct().forEach(sheetId -> {
                XSSFRow titleRow = workbook.getSheetAt(sheetId).getRow(0);
                if (titleRow == null) {
                    titleRow = workbook.getSheetAt(sheetId).createRow(0);
                }
                XSSFCell errorTitleCell = titleRow.createCell(titleRow.getPhysicalNumberOfCells());
                errorTitleCell.setCellType(CellType.STRING);
                XSSFCellStyle errorTitleStyle = workbook.createCellStyle();
                errorTitleStyle.cloneStyleFrom(errorTitleCell.getCellStyle());
                errorTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                errorTitleStyle.setAlignment(HorizontalAlignment.CENTER);
                errorTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                errorTitleStyle.setFillForegroundColor(new XSSFColor(Color.YELLOW));
                errorTitleStyle.setBorderBottom(BorderStyle.THIN);
                XSSFFont font = workbook.createFont();
                font.setFontName(errorTitleCell.getCellStyle().getFont().getFontName());
                font.setColor(new XSSFColor(Color.RED));
                font.setBold(true);
                errorTitleStyle.setFont(font);
                errorTitleCell.setCellStyle(errorTitleStyle);
                errorTitleCell.setCellValue("报错提示");
            });
            XSSFCellStyle errorMsgStyle = workbook.createCellStyle();
            errorMsgStyle.setBorderBottom(BorderStyle.THIN);
            errorMsgStyle.setAlignment(HorizontalAlignment.CENTER);
            formatErrorList.forEach(formatErr -> {
                XSSFRow row = workbook.getSheetAt(formatErr.getSheetId()).getRow(formatErr.getRowId());
                int physicalNumberOfCells = row.getPhysicalNumberOfCells();
                formatErr.getErrorCellIds().forEach(cellId -> {
                    XSSFCell errorDataCell = row.getCell(cellId);
                    XSSFCellStyle errorDataStyle = workbook.createCellStyle();
                    errorDataStyle.cloneStyleFrom(errorDataCell.getCellStyle());
                    errorDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    errorDataStyle.setFillForegroundColor(new XSSFColor(Color.YELLOW));
                    errorDataCell.setCellStyle(errorDataStyle);
                });
                workbook.getSheetAt(formatErr.getSheetId()).autoSizeColumn(physicalNumberOfCells);
                XSSFCell errorMsgCell = row.createCell(physicalNumberOfCells);
                errorMsgCell.setCellStyle(errorMsgStyle);
                errorMsgCell.setCellType(CellType.STRING);
                errorMsgCell.setCellValue(formatErr.getErrorMessage());
            });
            return true;
        }
        return false;
    }


    /**
     * 提供默认赋值方式
     *
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

