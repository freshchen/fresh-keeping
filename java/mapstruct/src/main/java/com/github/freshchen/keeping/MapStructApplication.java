package com.github.freshchen.keeping;

import com.github.freshchen.keeping.po.Address;
import com.github.freshchen.keeping.po.Person;
import com.github.freshchen.keeping.converter.PersonConverter;
import com.github.freshchen.keeping.dto.PersonDTO;
import com.github.freshchen.keeping.po.Type;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author darcy
 * @since 2020/8/6
 **/
@SpringBootApplication
public class MapStructApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext run = SpringApplication.run(MapStructApplication.class, args);

        PersonConverter personConverter = run.getBean(PersonConverter.class);
        Person person = Person.builder()
                .type(Type.ONE)
                .name("wang")
                .phone("1234567890")
                .phone("12345678901")
                .address(Address.builder().city("sh").street("1").build())
                .address(Address.builder().city("bj").street("2").build())
                .build();
        PersonDTO personDTO = personConverter.toDTO(person);
        // 默认是深拷贝的
        System.out.println(person);
        System.out.println(personDTO);


    }
}
