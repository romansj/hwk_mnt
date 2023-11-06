package com.jromans.hwk.configuration.mapping;


import com.jromans.hwk.configuration.exceptions.MyViolation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ConstraintViolationMapper {

    public static List<MyViolation> getViolations(ConstraintViolationException e) {
        var myViolationList = new ArrayList<MyViolation>();
        for (var violation : e.getConstraintViolations()) { // using Exception always getting Set<?>, otherwise passing Set<DTO> to Set<?> causes incompatible "T vs ?" type error
            myViolationList.add(getMyViolation(violation));
        }

        return myViolationList;
    }

    @NotNull
    private static MyViolation getMyViolation(ConstraintViolation<?> violation) {
        return new MyViolation(violation.getPropertyPath().toString(), violation.getMessage());
    }


}
