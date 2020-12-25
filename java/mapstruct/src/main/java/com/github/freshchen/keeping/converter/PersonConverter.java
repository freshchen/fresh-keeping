package com.github.freshchen.keeping.converter;

import com.github.freshchen.keeping.dto.AddressAllDTO;
import com.github.freshchen.keeping.dto.PersonAllDTO;
import com.github.freshchen.keeping.dto.PersonDTO;
import com.github.freshchen.keeping.po.Address;
import com.github.freshchen.keeping.po.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ValueMapping;

/**
 * @author darcy
 * @since 2020/8/6
 **/
@Mapper(componentModel = "spring")
public interface PersonConverter {

    /**
     * 指定 realName 如何赋值不影响 name 的正常赋值
     *
     * @param person
     * @return
     */
    @Mapping(source = "name", target = "realName")
    @ValueMapping(source = "type", target = "type")
    PersonAllDTO toAllDTO(Person person);

    @Mapping(source = "name", target = "realName")
    @ValueMapping(source = "type", target = "type")
    PersonDTO toDTO(Person person);

    @Mapping(source = "personName", target = "personName")
    AddressAllDTO toDTO(Address address, String personName);

    void update(Address from, @MappingTarget Address to);

}
