package com.distributionsys.backend.entities.utils;

import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.enums.PageEnum;
import com.distributionsys.backend.exceptions.ApplicationException;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageObject {
    private final int pageSize = PageEnum.SIZE.getSize();
    Integer page;
    String sortedField;
    Integer sortedMode;

    private final HashMap<Integer, Direction> SORTING_DIRECTION = new HashMap<>(Map.ofEntries(
        Map.entry(1, Direction.ASC),
        Map.entry(-1, Direction.DESC)
    ));

    public Pageable toPageable(Class<?> rootClass) {
        if (Objects.isNull(this.sortedField) || this.sortedField.isEmpty())
            return PageRequest.of(page - 1, pageSize);

        if (Objects.isNull(this.sortedMode))
            this.sortedMode = 1;

        StringBuilder builtSortedField = new StringBuilder();
        verifyAndBuildSortingFieldRecursion(this.sortedField, builtSortedField, new StringBuilder(), rootClass);

        if (builtSortedField.isEmpty())
            throw new ApplicationException(ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);

        return PageRequest.of(page - 1, pageSize, SORTING_DIRECTION.get(sortedMode),
            builtSortedField.toString());
    }

    /**
     * Example: From BillOfReceivingGoods Table (seeing goods of receiving-bill), sorting "supplierName" of this goods
     * Reason: Because this entity is just a relationship-entity, it won't be shown separated and will be shown in
     * Input: supplierName, emptyResult, new StringBuilder(), BillOfReceivingGoods.class
     * Output: BillOfReceivingGoods.goods.supplier.supplierName
     * */
    public void verifyAndBuildSortingFieldRecursion(String inp, StringBuilder res, StringBuilder directoryCls,
                                                    Class<?> rootClass) {
        for (Field field: rootClass.getDeclaredFields()) {
            if (field.getType().isAnnotationPresent(Entity.class)) {
                directoryCls
                    .append(Character.toLowerCase(field.getName().charAt(0)))   //--Lower Case first letter of Entity
                    .append(field.getName().substring(1))   //--Rest letters of Entity
                    .append(".");   //--Entities separator
                //--Recursive
                verifyAndBuildSortingFieldRecursion(inp, res, directoryCls, field.getType());
            }
            if (inp.equals(field.getName()))
                res.append(directoryCls.append(field.getName()));
            if (!res.isEmpty())  break;
        }
    }

    public static String javaFieldToSqlColumn(String field) {
        StringBuilder result = new StringBuilder();
        for (char c: field.toCharArray()) {
            if (c >= 65 && c <= 90) result.append("_").append(c + 32);
            else result.append(c);
        }
        return result.toString();
    }
}
