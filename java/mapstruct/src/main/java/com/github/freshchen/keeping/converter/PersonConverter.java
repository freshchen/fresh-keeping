package com.github.freshchen.keeping.converter;

import com.github.freshchen.keeping.po.Address;
import com.github.freshchen.keeping.po.Person;
import com.github.freshchen.keeping.dto.AddressDTO;
import com.github.freshchen.keeping.dto.PersonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

/**
 * @author darcy
 * @since 2020/8/6
 **/
@Mapper(componentModel = "spring")
public interface PersonConverter {

    @Mapping(source = "name", target = "realName")
    @ValueMapping(source = "type", target = "type")
    PersonDTO toDTO(Person person);

    default AddressDTO toAddressDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity(address.getCity() + "++");
        addressDTO.setStreet(address.getStreet() + "++");
        return addressDTO;
    }

}
