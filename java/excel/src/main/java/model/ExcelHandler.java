package model;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author darcy
 * @since 2020/02/23
 **/
@Value
public class ExcelHandler {

    @NotNull
    private Integer sheetIndex;
    @NotNull
    private Class<? extends ExcelRow> rowClass;
    @NotNull
    private Integer cellIndex;
    @NotNull
    private String fieldName;
    private Predicate<?> checker;
    private Function<?, ? extends ExcelRow> reader;
    private Function<?, ?> handler;
}
