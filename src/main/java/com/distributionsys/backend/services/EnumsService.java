package com.distributionsys.backend.services;

import com.distributionsys.backend.enums.Gender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnumsService {

    public List<Map<String, String>> getAllGenders() {
        return Gender.getAllGenders().stream().map(gender -> Map.of(
            "raw", gender.toString(),
            "id", gender.getGenderId().toString()
        )).toList();
    }
}
