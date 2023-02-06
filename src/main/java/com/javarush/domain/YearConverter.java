package com.javarush.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Converter(autoApply = true)
public class YearConverter implements AttributeConverter<Year,Short> {
    @Override
    public Short convertToDatabaseColumn(Year attribute) {
        if (attribute!=null){
            return (short) attribute.getValue();
        }
        return null;
    }

    @Override
    public Year convertToEntityAttribute(Short dbData) {
if (dbData != null){
return Year.of(dbData);
}
   return null;
    }
}
