package com.distributionsys.backend.services.webflux;

import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.repositories.FluxedGoodsFromWarehouseCrud;
import com.distributionsys.backend.services.redis.RedisFGFWHTemplateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FluxedAsyncService {
    RedisFGFWHTemplateService redisFGFWHTemplateService;
    FluxedGoodsFromWarehouseCrud fluxedGoodsFromWarehouseCrud;

    public void updateFluxedGoodsFromWarehouseQuantityInRedis(List<WarehouseGoods> list) {
        var updatedWhGoodsMap = new HashMap<Long, Long>();
        list.forEach(obj -> updatedWhGoodsMap.put(obj.getWarehouseGoodsId(), obj.getCurrentQuantity()));
        var prefixPattern = "*/" + FluxedGoodsFromWarehouse.GOODS_FROM_WAREHOUSE_ID + "_";
        var fluxedWarehouseGoodsMap = new HashMap<String, FluxedGoodsFromWarehouse>();
        list.forEach(obj -> fluxedWarehouseGoodsMap.putAll(redisFGFWHTemplateService
            .getAllDataByPattern(prefixPattern + obj.getWarehouseGoodsId())));
        fluxedWarehouseGoodsMap.forEach((key, value) -> {
            if (updatedWhGoodsMap.get(value.getGoodsFromWarehouseId()).equals(value.getCurrentQuantity()))
                return; //--Nothing changes, so stop working
            value.setCurrentQuantity(updatedWhGoodsMap.get(value.getGoodsFromWarehouseId()));
            try {
                fluxedGoodsFromWarehouseCrud.deleteById(key.split(":")[1]);
                fluxedGoodsFromWarehouseCrud.save(value);
            } catch (RuntimeException e) {
                throw new ApplicationException(ErrorCodes.UNAWARE_ERR);
            }
        });
    }
}
