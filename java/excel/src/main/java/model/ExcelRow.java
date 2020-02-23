package model;

import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

/**
 * @author darcy
 * @since 2020/02/23
 **/
@Data
public class ExcelRow {

    private Integer sheetId;
    private Integer rowId;
    private Set<Integer> errorCellIds = Sets.newHashSet();
    private String errorMessage;

    public Boolean isHappenedError() {
        return CollectionUtils.isNotEmpty(errorCellIds);
    }
}
