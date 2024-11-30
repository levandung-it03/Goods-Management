package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.general.ByIdDto;
import com.distributionsys.backend.dtos.request.*;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.SimpleGoodsResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import com.distributionsys.backend.entities.sql.Goods;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.GoodsService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoodsControllers {
    GoodsService goodsService;

    @GetMapping("/user/v1/get-goods-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Goods>>> getGoodsPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_FULL_INFO_GOODS_PAGES,
            goodsService.getGoodsPages(request));
    }

    @GetMapping("/user/v1/get-full-info-goods-pages-by-import-bill")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<WarehouseGoods>>> getFullInfoGoodsPagesByImportBill(
        @Valid PaginatedRelationshipRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_FULL_INFO_GOODS_PAGES_BY_IP_BILL,
            goodsService.getFullInfoGoodsPagesByImportBill(request));
    }

    @GetMapping("/user/v1/get-simple-goods-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<SimpleGoodsResponse>>> getSimpleGoodsPages(
        @Valid SimpleGoodsRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SIMPLE_GOODS_PAGES,
            goodsService.getSimpleGoodsPages(request));
    }

    @PostMapping("/user/v1/flux-goods-quantity-preparation")
    public ResponseEntity<ApiResponseObject<Void>> fluxGoodsQuantityPreparation(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody FluxGoodsFromWarehouseRequest request) {
        goodsService.fluxGoodsQuantityPreparation(accessToken, request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.PREPARE_FLUX_GOODS_QUANTITY);
    }

    @PostMapping("/user/v1/flux-goods-quantity-cancellation")
    public ResponseEntity<ApiResponseObject<Void>> fluxGoodsQuantityCancellation(
        @RequestHeader("Authorization") String accessToken) {
        goodsService.fluxGoodsQuantityCancellation(accessToken);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CANCEL_FLUX_GOODS_QUANTITY);
    }

    @GetMapping(value = "/user/v1/flux-goods-quantity", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<FluxedGoodsFromWarehouse>> fluxGoodsToStreamQuantity(
        @RequestHeader("Authorization") String accessToken) {
        return goodsService.fluxGoodsToStreamQuantity(accessToken);
    }

    @PostMapping("/user/v1/add-goods")
    public ResponseEntity<ApiResponseObject<Void>> addGoods(@Valid @RequestBody NewGoodsRequest request) {
        goodsService.addGoods(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.ADD_GOODS);
    }

    @PutMapping("/user/v1/update-goods")
    public ResponseEntity<ApiResponseObject<Void>> updateGoods(@Valid @RequestBody UpdateGoodsRequest request) {
        goodsService.updateGoods(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_GOODS);
    }

    @PostMapping("/user/v1/delete-goods")
    public ResponseEntity<ApiResponseObject<Void>> deleteGoods(@Valid @RequestBody ByIdDto request) {
        goodsService.deleteGoods(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_GOODS);
    }
}
