package model;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author darcy
 * @since 2020/02/24
 **/
@Builder
@Getter
public class ExcelParser {

    @NotNull
    private XSSFWorkbook workbook;
    @NotNull
    private Integer sheetIndex;
    @Valid
    private Collection<Transfer> transfers;

    @Builder
    @Getter
    public static class Transfer {
        @NotNull
        private Integer cellIndex;
        @NotNull
        private String fieldName;

        private Function<String, ?> transfer;

    }
}
