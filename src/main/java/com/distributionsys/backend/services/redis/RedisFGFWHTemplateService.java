package com.distributionsys.backend.services.redis;

import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import com.distributionsys.backend.repositories.FluxedGoodsFromWarehouseCrud;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisFGFWHTemplateService {
    RedisTemplate<String, FluxedGoodsFromWarehouse> redisFGFWHTemplate;
    FluxedGoodsFromWarehouseCrud fluxedGoodsFromWarehouseCrud;

    public void saveData(String key, FluxedGoodsFromWarehouse value) {
        redisFGFWHTemplate.opsForValue().set(key, value);
    }

    public FluxedGoodsFromWarehouse getData(String pattern) {
        return redisFGFWHTemplate.opsForValue().get(pattern);
    }

    public void deleteData(String pattern) {
        Set<String> keys = redisFGFWHTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            // Delete all matching keys
            redisFGFWHTemplate.delete(keys);
            System.out.println("Deleted keys: " + keys);
        } else {
            System.out.println("No keys found for pattern: " + pattern);
        }
    }

    public Map<String, FluxedGoodsFromWarehouse> getAllDataByPattern(String pattern) {
        Set<String> keys = redisFGFWHTemplate.keys(pattern);
        Map<String, FluxedGoodsFromWarehouse> result = new HashMap<>();

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                var value = fluxedGoodsFromWarehouseCrud.findById(key.split(":")[1]).orElseThrow();
                result.put(key, value);
            }
        }
        else  log.info("No keys found for pattern: {}", pattern);
        return result;
    }
}
