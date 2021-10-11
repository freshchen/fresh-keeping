package com.github.freshchen.keeping.dao.config;

import com.github.freshchen.keeping.common.lib.enums.IEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author darcy
 * @since 2021/10/10
 **/
public class GenericEnumTypeHandle<E extends IEnum> extends BaseTypeHandler<E> {

    private Class<E> enumType;

    private E[] enums;

    public GenericEnumTypeHandle(Class<E> type) {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.enumType = type;
        this.enums = type.getEnumConstants();
        if (Objects.isNull(this.enums)) {
            throw new IllegalArgumentException(type.getName() + " does not represent an enum type.");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, e.getValue());
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String s) throws SQLException {
        if (Objects.isNull(resultSet.getObject(s))) {
            return null;
        }
        int index = resultSet.getInt(s);
        return getByValue(index);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int i) throws SQLException {
        if (Objects.isNull(resultSet.getObject(i))) {
            return null;
        }
        int index = resultSet.getInt(i);
        return getByValue(index);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        if (Objects.isNull(callableStatement.getObject(i))) {
            return null;
        }
        int index = callableStatement.getInt(i);
        return getByValue(index);
    }


    private E getByValue(int value) {
        for (E e : enums) {
            if (e.getValue() == value) {
                return e;
            }
        }
        throw new IllegalArgumentException(enumType.getName() + "  unknown enumerated type value:" + value);
    }
}
