import model.ExcelParser;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExcelsTest {

    @Test
    public void parse() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook("src/test/java/test.xlsx");
//        List<ExcelParser.Transfer> transfers = Arrays.asList(ExcelParser.Transfer.builder().cellIndex(0).fieldName("name").build(), ExcelParser.Transfer.builder().cellIndex(1).fieldName("price").transfer(s -> Integer.parseInt(s) * 100).build(), ExcelParser.Transfer.builder().cellIndex(2).fieldName("age").build());
//        ExcelParser build1 = ExcelParser.builder().sheetIndex(0).workbook(workbook).transfers(transfers).build();
//        List<MyRow> parse1 = Excels.parseToRows(build1, MyRow.class);
//
//        ExcelParser build2 = ExcelParser.builder().sheetIndex(1).workbook(workbook).transfers(transfers).build();
//        List<MyRow> parse2 = Excels.parseToRows(build2, MyRow.class);

        List<ExcelParser.Transfer> transfers = Arrays.asList(
                ExcelParser.Transfer.builder().cellIndex(0).fieldName("name").build(),
                ExcelParser.Transfer.builder().cellIndex(1).fieldName("price").transfer(s -> Integer.parseInt(s) * 100).build(),
                ExcelParser.Transfer.builder().cellIndex(2).fieldName("age").build()
        );
        List<ExcelParser.Checker<MyRow>> checkers = Arrays.asList(
                ExcelParser.Checker.<MyRow>builder().cellIndexs(Arrays.asList(0, 1)).errorMessage("我的错").checker(excelRow -> {
                    if (excelRow.getAge() + excelRow.getPrice() > 100) {
                        return false;
                    }
                    return true;
                }).build()
        );
        ExcelParser<MyRow> build = ExcelParser.<MyRow>builder().sheetIndex(0).workbook(workbook).transfers(transfers).build();
        List<MyRow> parse1 = Excels.parseToRows(build, MyRow.class);
        System.out.println(parse1);
    }

}