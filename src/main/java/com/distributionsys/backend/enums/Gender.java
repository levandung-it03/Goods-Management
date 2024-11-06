package com.distributionsys.backend.enums;

import com.distributionsys.backend.exceptions.ApplicationException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Gender {
    MALE(1),
    FEMALE(0)
    ;
    final Integer genderId;
    Gender(int genderId) {
        this.genderId = genderId;
    }

    public static Gender getByGenderId(Integer genderId) throws ApplicationException{
        for (Gender gender: Gender.values())
            if (genderId.equals(gender.getGenderId()))
                return gender;
        throw new ApplicationException(ErrorCodes.INVALID_GENDER_ID);
    }

    public static List<Gender> getAllGenders() {
        return Arrays.stream(Gender.values()).toList();
    }
}
