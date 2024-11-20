package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.request.SimpleGoodsRequest;
import com.distributionsys.backend.dtos.response.SimpleGoodsResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.dtos.utils.WarehouseGoodsFilterRequest;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.enums.PageEnum;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.repositories.GoodsRepository;
import com.distributionsys.backend.repositories.WarehouseGoodsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoodsService {
    GoodsRepository goodsRepository;
    WarehouseGoodsRepository warehouseGoodsRepository;
    PageMappers pageMappers;

    public TablePagesResponse<WarehouseGoods> getWarehouseGoodsPages(PaginatedTableRequest request) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(WarehouseGoods.class);
        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<WarehouseGoods> repoRes = warehouseGoodsRepository.findAll(pageableCf);
            return TablePagesResponse.<WarehouseGoods>builder().data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
        }
        try {
            var warehouseGoodsInfo = WarehouseGoodsFilterRequest.builderFormFilterHashMap(request.getFilterFields());
            Page<WarehouseGoods> repoRes = warehouseGoodsRepository.findAllByWarehouseGoodsFilterInfo(warehouseGoodsInfo,
                pageableCf);
            return TablePagesResponse.<WarehouseGoods>builder().data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
        } catch (NoSuchFieldException | IllegalArgumentException | NullPointerException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }

    public TablePagesResponse<SimpleGoodsResponse> getSimpleGoodsPages(SimpleGoodsRequest request) {
        var repoRes = goodsRepository.findAllSimpleGoodsInfoByGoodsName(request.getGoodsName(),
            request.getPage() - 1, PageEnum.SIZE.getSize());
        return TablePagesResponse.<SimpleGoodsResponse>builder()
            .data(repoRes.stream().map(SimpleGoodsResponse::buildFromRepoResponseObjArr).toList())
            .totalPages(null)   //--Simple table with "< >" pagination-bar.
            .currentPage(request.getPage())
            .build();
    }
}
