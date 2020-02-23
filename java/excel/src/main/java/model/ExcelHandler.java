package model;

import com.google.common.collect.Lists;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Function;

/**
 * @author darcy
 * @since 2020/02/23
 **/
@Value
public class ExcelHandler<T extends ExcelRow> {

    @NotNull
    private Integer sheetIndex;

    @NotNull
    private Class<T> rowClass;

    @Valid
    private List<Transfer> transfers;

    private List<T> results = Lists.newArrayList();

    @Value
    public static class Transfer {
        @NotNull
        private Integer cellIndex;
        @NotNull
        private String fieldName;
        private Function<String, ?> transfer;
        private Function<?, ?> handler;
    }

}
