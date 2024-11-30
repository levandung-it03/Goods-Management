package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.general.ByIdDto;
import com.distributionsys.backend.dtos.request.*;
import com.distributionsys.backend.dtos.response.SimpleGoodsResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.dtos.utils.GoodsFilterRequest;
import com.distributionsys.backend.dtos.utils.WarehouseGoodsFilterRequest;
import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import com.distributionsys.backend.entities.sql.Goods;
import com.distributionsys.backend.entities.sql.Supplier;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.enums.PageEnum;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.repositories.*;
import com.distributionsys.backend.services.auth.JwtService;
import com.distributionsys.backend.services.redis.FluxedGoodsFromWarehouseService;
import com.distributionsys.backend.services.webflux.FluxedAsyncService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoodsService {
    FluxedGoodsFromWarehouseCrud fluxedGoodsFromWarehouseCrud;
    FluxedGoodsFromWarehouseService fluxedGoodsFromWarehouseService;
    ExportBillWarehouseGoodsRepository exportBillWarehouseGoodsRepository;
    ImportBillWarehouseGoodsRepository importBillWarehouseGoodsRepository;
    GoodsRepository goodsRepository;
    WarehouseGoodsRepository warehouseGoodsRepository;
    SupplierRepository supplierRepository;
    WarehouseGoodsRepository warehouseGoodsRepository;
    PageMappers pageMappers;
    JwtService jwtService;

    public TablePagesResponse<Goods> getGoodsPages(PaginatedTableRequest request) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(Goods.class);
        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Goods> repoRes = goodsRepository.findAll(pageableCf);
            return TablePagesResponse.<Goods>builder().data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
        }
        try {
            var goodsInfo = GoodsFilterRequest.buildFromFilterHashMap(request.getFilterFields());
            Page<Goods> repoRes = goodsRepository.findAllByGoodsFilterInfo(goodsInfo, pageableCf);
            return TablePagesResponse.<Goods>builder().data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
        } catch (NoSuchFieldException | IllegalArgumentException | NullPointerException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }

    public TablePagesResponse<WarehouseGoods> getFullInfoGoodsPagesByImportBill(PaginatedRelationshipRequest request) {
        Pageable pageableCf = pageMappers.relationshipPageRequestToPageable(request).toPageable(WarehouseGoods.class);
        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<WarehouseGoods> repoRes = importBillWarehouseGoodsRepository.findWarehouseGoodsAllByImportBillId(
                request.getId(), pageableCf);
            return TablePagesResponse.<WarehouseGoods>builder().data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
        }
        try {
            var warehouseGoodsInfo = WarehouseGoodsFilterRequest.buildFormFilterHashMap(request.getFilterFields());
            Page<WarehouseGoods> repoRes = importBillWarehouseGoodsRepository.findWarehouseGoodsAllByImportBillId(
                request.getId(), warehouseGoodsInfo, pageableCf);
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

    public Flux<List<FluxedGoodsFromWarehouse>> fluxGoodsToStreamQuantity(String accessToken,
                                                                          FluxGoodsFromWarehouseRequest request) {
        return fluxedAsyncService
            .prepareFluxedGoodsFromWarehouseStreaming(accessToken, request)
            .flatMapMany(userId ->
                Flux.interval(Duration.ofSeconds(2))
                    .publishOn(Schedulers.boundedElastic())
                    .map(tick -> fluxedGoodsFromWarehouseService.findAllByUserId(userId))
            );
    }

    public void addGoods(NewGoodsRequest request) {
        if (goodsRepository.existsByGoodsName(request.getGoodsName()))
            throw new ApplicationException(ErrorCodes.DUPLICATE_GOODS);
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        goodsRepository.save(Goods.builder()
            .goodsName(request.getGoodsName())
            .unitPrice(request.getUnitPrice())
            .supplier(supplier)
            .build());
    }

    public void updateGoods(UpdateGoodsRequest request) {
        if (exportBillWarehouseGoodsRepository.existsGoodsByGoodsId(request.getGoodsId())
            ||  importBillWarehouseGoodsRepository.existsGoodsByGoodsId(request.getGoodsId()))
            throw new ApplicationException(ErrorCodes.UPDATE_GOODS);
        Goods updatedGoods = goodsRepository.findById(request.getGoodsId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        if (goodsRepository.existsByGoodsNameIgnoreUpdatedCase(request.getGoodsName(), updatedGoods.getGoodsName()))
            throw new ApplicationException(ErrorCodes.DUPLICATE_GOODS);
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        goodsRepository.updateGoodsByGoodsInfo(Goods.builder()
            .goodsId(request.getGoodsId())
            .goodsName(request.getGoodsName())
            .unitPrice(request.getUnitPrice())
            .supplier(supplier)
            .build());
    }

    public void deleteGoods(ByIdDto request) {
        if (!goodsRepository.existsById(request.getId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);
        if (exportBillWarehouseGoodsRepository.existsGoodsByGoodsId(request.getId())
        ||  importBillWarehouseGoodsRepository.existsGoodsByGoodsId(request.getId()))
            throw new ApplicationException(ErrorCodes.UPDATE_GOODS);
        goodsRepository.deleteById(request.getId());
    }

    public List<Goods> getAllGoods() {
        return goodsRepository.findAll();
    }

    public void fluxGoodsQuantityPreparation(String accessToken, FluxGoodsFromWarehouseRequest request) {
        var email = jwtService.readPayload(accessToken).get("sub");
        var fluxedWarehouseGoods = warehouseGoodsRepository.findAllById(request.getGoodsFromWarehouseIds())
            .stream()
            .map(warehouseGoods -> FluxedGoodsFromWarehouse.builder()
                .id(UUID.randomUUID().toString())
                .userEmail(email)
                .goodsFromWarehouseId(warehouseGoods.getId())
                .currentQuantity(warehouseGoods.getCurrentQuantity())
                .build())
            .toList();
        fluxedGoodsFromWarehouseCrud.deleteAllByUserEmail(email); //--Clear all old-data if it's exists
        fluxedGoodsFromWarehouseCrud.saveAll(fluxedWarehouseGoods);
    }

    public void fluxGoodsQuantityCancellation(String accessToken) {
        fluxedGoodsFromWarehouseCrud.deleteAllByUserEmail(jwtService.readPayload(accessToken).get("sub"));
    }
}
