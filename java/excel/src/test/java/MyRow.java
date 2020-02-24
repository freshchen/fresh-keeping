import lombok.Data;
import model.ExcelRow;

/**
 * @author darcy
 * @since 2020/02/24
 **/
@Data
public class MyRow extends ExcelRow {

    private String name;
    private Integer price;
    private int age;

}
