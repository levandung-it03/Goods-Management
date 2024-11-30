package com.distributionsys.backend.services.webflux;

import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.repositories.FluxedGoodsFromWarehouseCrud;
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
    FluxedGoodsFromWarehouseCrud fluxedGoodsFromWarehouseCrud;

    public void updateFluxedGoodsFromWarehouseQuantityInRedis(List<WarehouseGoods> list) {
        var updatedWhGoodsMap = new HashMap<Long, Long>();
        list.forEach(obj -> updatedWhGoodsMap.put(obj.getId(), obj.getCurrentQuantity()));
        fluxedGoodsFromWarehouseCrud.saveAll(fluxedGoodsFromWarehouseCrud
            .findAllByGoodsFromWarehouseIdIn(list.stream().map(WarehouseGoods::getId).toList())
            .stream()
            .filter(fluxObj -> !updatedWhGoodsMap.get(fluxObj.getGoodsFromWarehouseId()).equals(fluxObj.getCurrentQuantity()))
            .peek(fluxObj -> fluxObj.setCurrentQuantity(updatedWhGoodsMap.get(fluxObj.getGoodsFromWarehouseId())))
            .toList());
    }
}
