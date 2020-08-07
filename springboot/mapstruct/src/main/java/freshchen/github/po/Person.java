package freshchen.github.po;

import lombok.*;

import java.util.List;

/**
 * @author darcy
 * @since 2020/8/6
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private String name;
    @Singular
    private List<String> phones;
    @Singular
    private List<Address> addresses;

    private Type type;

}
